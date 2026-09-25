package com.example.util

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import com.example.model.AppLanguage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

enum class StudioEqualizerPreset(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val pitchMultiplier: Float,
    val speedMultiplier: Float
) {
    STUDIO_BARITONE(
        id = "baritone",
        title = "Hujjatli Bariton",
        subtitle = "Chuqur, vazmin va salobatli kino ovozi",
        iconEmoji = "🎙️",
        pitchMultiplier = 0.98f,
        speedMultiplier = 0.98f
    ),
    CRYSTAL_CLEAR(
        id = "clear",
        title = "Ultra HD Tiniqlik",
        subtitle = "Yorqin, jarangdor va ravon diksiya",
        iconEmoji = "💎",
        pitchMultiplier = 1.06f,
        speedMultiplier = 0.98f
    ),
    HISTORICAL_HALL(
        id = "heritage",
        title = "Oltin Meros",
        subtitle = "Salmoqli akademik sharh",
        iconEmoji = "🏛️",
        pitchMultiplier = 0.84f,
        speedMultiplier = 0.88f
    ),
    SMART_DYNAMIC(
        id = "dynamic",
        title = "Chaqqon Gid",
        subtitle = "Tezkor, zamonaviy va jonli",
        iconEmoji = "⚡",
        pitchMultiplier = 1.02f,
        speedMultiplier = 1.05f
    )
}

enum class VoicePersona(
    val id: String,
    val title: String,
    val subtitle: String,
    val pitch: Float,
    val speechRate: Float,
    val iconEmoji: String,
    val gender: String
) {
    PROFESSIONAL_MALE(
        id = "prof_male",
        title = "Salobatli Davlat Suxandoni",
        subtitle = "Chuqur vazmin bariton, nufuzli hujjatli film diktori",
        pitch = 0.82f,
        speechRate = 0.88f,
        iconEmoji = "🎙️",
        gender = "Erkak"
    ),
    WARM_FEMALE(
        id = "warm_female",
        title = "Malika Karimova",
        subtitle = "Mayin, tiniq va jarangdor",
        pitch = 1.18f,
        speechRate = 0.95f,
        iconEmoji = "🌸",
        gender = "Ayol"
    ),
    ACADEMIC_HISTORIAN(
        id = "academic",
        title = "Tarixchi Professor",
        subtitle = "Salmoqli va sirli ohang",
        pitch = 0.78f,
        speechRate = 0.85f,
        iconEmoji = "🏛️",
        gender = "Erkak"
    ),
    NEURAL_AI(
        id = "neural_ai",
        title = "AI Smart Gid",
        subtitle = "Ultra HD zamonaviy talaffuz",
        pitch = 1.02f,
        speechRate = 1.02f,
        iconEmoji = "✨",
        gender = "Universal"
    );

    companion object {
        fun fromId(id: String): VoicePersona {
            return entries.firstOrNull { it.id == id } ?: PROFESSIONAL_MALE
        }
    }
}

data class SystemVoiceInfo(
    val name: String,
    val localeName: String,
    val quality: Int,
    val isNetwork: Boolean
)

class ArAudioNarratorManager(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var isCurrentlySpeaking = false
    private var onSpeechStatusListener: ((isSpeaking: Boolean, progress: Float) -> Unit)? = null
    var onWordPositionListener: ((charStart: Int, charEnd: Int, progress: Float) -> Unit)? = null
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

    var currentPersona: VoicePersona = VoicePersona.PROFESSIONAL_MALE
    var currentPreset: StudioEqualizerPreset = StudioEqualizerPreset.STUDIO_BARITONE
    var customPitch: Float? = null
    var customSpeed: Float? = null
    var selectedVoiceName: String? = null
    var isAmbientMusicEnabled: Boolean = true
    var currentSpokenOriginalText: String = ""
    var activeEngineLanguageCode: String = "uz"

    private val narratorScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressTickerJob: Job? = null

    val isGoogleEngineAvailable: Boolean by lazy {
        try {
            val pm = context.packageManager
            pm.getPackageInfo("com.google.android.tts", 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    init {
        initializeEngine()
    }

    private fun initializeEngine() {
        try {
            // Prioritize Google Speech Services if installed for neural quality
            if (isGoogleEngineAvailable) {
                tts = TextToSpeech(context.applicationContext, this, "com.google.android.tts")
            } else {
                tts = TextToSpeech(context.applicationContext, this)
            }
        } catch (e: Exception) {
            Log.e("ArAudioNarrator", "Error initializing preferred TTS engine: ${e.message}")
            try {
                tts = TextToSpeech(context.applicationContext, this)
            } catch (e2: Exception) {
                Log.e("ArAudioNarrator", "Fallback TTS init failed: ${e2.message}")
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            configureAudioAttributes()
            setupUtteranceListener()
            setLanguage(AppLanguage.currentLanguage)
        } else {
            Log.e("ArAudioNarrator", "TTS initialization failed with status: $status")
        }
    }

    private fun configureAudioAttributes() {
        tts?.let { engine ->
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
            engine.setAudioAttributes(audioAttributes)
        }
    }

    private fun setupUtteranceListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                isCurrentlySpeaking = true
                onSpeechStatusListener?.invoke(true, 0.05f)
            }

            override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                if (currentSpokenOriginalText.isNotEmpty()) {
                    val progress = (start.toFloat() / currentSpokenOriginalText.length).coerceIn(0f, 1f)
                    onSpeechStatusListener?.invoke(true, progress)
                    onWordPositionListener?.invoke(start, end, progress)
                }
            }

            override fun onDone(utteranceId: String?) {
                isCurrentlySpeaking = false
                progressTickerJob?.cancel()
                if (isAmbientMusicEnabled) {
                    AmbientSoundSynthesizer.stop()
                }
                onSpeechStatusListener?.invoke(false, 1.0f)
                onWordPositionListener?.invoke(currentSpokenOriginalText.length, currentSpokenOriginalText.length, 1.0f)
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                isCurrentlySpeaking = false
                progressTickerJob?.cancel()
                if (isAmbientMusicEnabled) {
                    AmbientSoundSynthesizer.stop()
                }
                onSpeechStatusListener?.invoke(false, 0f)
                onWordPositionListener?.invoke(0, 0, 0f)
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                Log.e("ArAudioNarrator", "TTS playback error $errorCode for $utteranceId")
                isCurrentlySpeaking = false
                progressTickerJob?.cancel()
                if (isAmbientMusicEnabled) {
                    AmbientSoundSynthesizer.stop()
                }
                onSpeechStatusListener?.invoke(false, 0f)
                onWordPositionListener?.invoke(0, 0, 0f)
            }
        })
    }

    fun setLanguage(lang: AppLanguage) {
        if (!isInitialized || tts == null) return

        val primaryLocale = when (lang) {
            AppLanguage.UZ -> Locale.forLanguageTag("uz-UZ")
            AppLanguage.EN -> Locale.US
            AppLanguage.RU -> Locale.forLanguageTag("ru-RU")
            AppLanguage.DE -> Locale.GERMANY
            AppLanguage.FR -> Locale.FRANCE
            AppLanguage.ES -> Locale.forLanguageTag("es-ES")
            AppLanguage.TR -> Locale.forLanguageTag("tr-TR")
            AppLanguage.ZH -> Locale.SIMPLIFIED_CHINESE
            AppLanguage.JA -> Locale.JAPAN
        }

        try {
            var res = tts?.setLanguage(primaryLocale)
            if (res != TextToSpeech.LANG_MISSING_DATA && res != TextToSpeech.LANG_NOT_SUPPORTED) {
                activeEngineLanguageCode = primaryLocale.language
            } else {
                val fallbackLocale = when (lang) {
                    AppLanguage.UZ -> Locale.forLanguageTag("uz")
                    AppLanguage.EN -> Locale.ENGLISH
                    AppLanguage.RU -> Locale.forLanguageTag("ru")
                    AppLanguage.DE -> Locale.GERMAN
                    AppLanguage.FR -> Locale.FRENCH
                    AppLanguage.ES -> Locale.forLanguageTag("es")
                    AppLanguage.TR -> Locale.forLanguageTag("tr")
                    AppLanguage.ZH -> Locale.CHINESE
                    AppLanguage.JA -> Locale.JAPANESE
                }
                res = tts?.setLanguage(fallbackLocale)
                if (res != TextToSpeech.LANG_MISSING_DATA && res != TextToSpeech.LANG_NOT_SUPPORTED) {
                    activeEngineLanguageCode = fallbackLocale.language
                } else if (lang == AppLanguage.UZ) {
                    // Fallback to Turkish or Russian for best phonetic pronunciation
                    val trRes = tts?.setLanguage(Locale.forLanguageTag("tr-TR"))
                    if (trRes != TextToSpeech.LANG_MISSING_DATA && trRes != TextToSpeech.LANG_NOT_SUPPORTED) {
                        activeEngineLanguageCode = "tr"
                    } else {
                        val ruRes = tts?.setLanguage(Locale.forLanguageTag("ru-RU"))
                        if (ruRes != TextToSpeech.LANG_MISSING_DATA && ruRes != TextToSpeech.LANG_NOT_SUPPORTED) {
                            activeEngineLanguageCode = "ru"
                        } else {
                            tts?.setLanguage(Locale.ENGLISH)
                            activeEngineLanguageCode = "en"
                        }
                    }
                } else {
                    tts?.setLanguage(Locale.ENGLISH)
                    activeEngineLanguageCode = "en"
                }
            }

            // Select highest fidelity voice (support HD Neural voices)
            tts?.voices?.let { voices ->
                if (selectedVoiceName != null) {
                    val matching = voices.find { it.name == selectedVoiceName }
                    if (matching != null) {
                        tts?.voice = matching
                        return
                    }
                }

                val matchingLangVoices = voices.filter { voice ->
                    voice.locale.language == primaryLocale.language ||
                    (primaryLocale.language == "uz" && (voice.locale.language == "tr" || voice.locale.language == "ru"))
                }

                // Prioritize high-quality male voices with solemn baritone timbre (e.g. Google Neural / Wavenet / Studio Male)
                val bestVoice = matchingLangVoices.maxWithOrNull(
                    compareBy<Voice> { if (it.name.contains("male", true) && !it.name.contains("female", true)) 3 else 0 }
                        .thenBy { it.quality }
                        .thenBy { if (it.name.contains("neural", true) || it.name.contains("studio", true) || it.name.contains("wavenet", true) || it.name.contains("network", true)) 2 else 0 }
                        .thenBy { if (!it.isNetworkConnectionRequired) 1 else 0 }
                )
                if (bestVoice != null) {
                    tts?.voice = bestVoice
                }
            }
        } catch (e: Exception) {
            Log.e("ArAudioNarrator", "Failed to set language: ${e.message}")
        }
    }

    /**
     * Cleans up text and adapts phonetics according to active speech engine language.
     * Keeps narration articulate and natural without robotic distortions.
     */
    fun cleanTextForNaturalSpeech(rawText: String, lang: AppLanguage = AppLanguage.currentLanguage): String {
        val naturalText = UzbekSpeechPhonetics.prepareTextForVoice(rawText)
        if (lang == AppLanguage.UZ) {
            return when (activeEngineLanguageCode) {
                "tr" -> UzbekSpeechPhonetics.adaptForTurkishEngine(naturalText)
                "ru" -> UzbekSpeechPhonetics.latinToCyrillic(naturalText)
                else -> naturalText
            }
        }
        return rawText
    }

    /**
     * Speaks audio guide text with chosen persona timbre, pitch, and optional Silk Road ambient background
     */
    fun speak(
        text: String,
        lang: AppLanguage = AppLanguage.currentLanguage,
        speechRate: Float = (customSpeed ?: currentPersona.speechRate) * currentPreset.speedMultiplier,
        pitch: Float = (customPitch ?: currentPersona.pitch) * currentPreset.pitchMultiplier,
        onStatus: ((isSpeaking: Boolean, progress: Float) -> Unit)? = null
    ) {
        this.onSpeechStatusListener = onStatus
        this.currentSpokenOriginalText = text

        // Boost stream volume if suppressed
        try {
            audioManager?.let { am ->
                val currentVol = am.getStreamVolume(AudioManager.STREAM_MUSIC)
                val maxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                if (currentVol < (maxVol * 0.75f)) {
                    am.setStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        (maxVol * 0.88f).toInt(),
                        AudioManager.FLAG_SHOW_UI
                    )
                }
            }
        } catch (e: Exception) {
            Log.w("ArAudioNarrator", "Could not adjust audio stream volume: ${e.message}")
        }

        if (!isInitialized || tts == null) {
            onStatus?.invoke(true, 0.2f)
            return
        }

        setLanguage(lang)
        tts?.setSpeechRate(speechRate.coerceIn(0.6f, 1.6f))
        tts?.setPitch(pitch.coerceIn(0.6f, 1.6f))
        tts?.stop()
        progressTickerJob?.cancel()

        // Start soothing ambient instruments in background
        if (isAmbientMusicEnabled) {
            AmbientSoundSynthesizer.start()
        }

        val smoothedText = cleanTextForNaturalSpeech(text, lang)
        val utteranceId = "audio_narrator_${System.currentTimeMillis()}"

        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
            putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC)
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }

        val words = text.split("\\s+".toRegex()).filter { it.isNotBlank() }
        val wordCount = words.size.coerceAtLeast(1)
        val estimatedDurationMs = ((wordCount / (2.6f * speechRate.coerceAtLeast(0.5f))) * 1000L).toLong().coerceIn(3000L, 120000L)
        val startTime = System.currentTimeMillis()

        try {
            tts?.speak(smoothedText, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
            isCurrentlySpeaking = true

            // Smooth progressive timeline updates every 50ms for synced on-screen subtitles
            progressTickerJob = narratorScope.launch {
                while (isCurrentlySpeaking) {
                    val elapsed = System.currentTimeMillis() - startTime
                    val progress = (elapsed.toFloat() / estimatedDurationMs).coerceIn(0f, 0.99f)
                    val charPos = (progress * text.length).toInt().coerceIn(0, text.length)
                    onSpeechStatusListener?.invoke(true, progress)
                    onWordPositionListener?.invoke(charPos, charPos, progress)
                    delay(50)
                }
            }
        } catch (e: Exception) {
            Log.e("ArAudioNarrator", "Error in speak: ${e.message}")
        }
    }

    fun getAvailableVoices(): List<SystemVoiceInfo> {
        return try {
            tts?.voices?.map {
                SystemVoiceInfo(
                    name = it.name,
                    localeName = it.locale.displayName,
                    quality = it.quality,
                    isNetwork = it.isNetworkConnectionRequired
                )
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun openTtsSettings() {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val altIntent = Intent("com.android.settings.TTS_SETTINGS").apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(altIntent)
            } catch (e2: Exception) {
                Log.e("ArAudioNarrator", "Could not open TTS settings: ${e2.message}")
            }
        }
    }

    fun openGoogleTtsPlayStore() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=com.google.android.tts")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val webIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.tts")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(webIntent)
            } catch (e2: Exception) {
                Log.e("ArAudioNarrator", "Could not open Google TTS page: ${e2.message}")
            }
        }
    }

    fun openInstallTtsData() {
        try {
            val intent = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            openTtsSettings()
        }
    }

    fun stop() {
        try {
            progressTickerJob?.cancel()
            tts?.stop()
            if (isAmbientMusicEnabled) {
                AmbientSoundSynthesizer.stop()
            }
            isCurrentlySpeaking = false
            onSpeechStatusListener?.invoke(false, 0f)
            onWordPositionListener?.invoke(0, 0, 0f)
        } catch (e: Exception) {
            Log.e("ArAudioNarrator", "Error stopping: ${e.message}")
        }
    }

    fun isSpeaking(): Boolean {
        return try {
            tts?.isSpeaking ?: isCurrentlySpeaking
        } catch (e: Exception) {
            isCurrentlySpeaking
        }
    }

    fun shutdown() {
        try {
            progressTickerJob?.cancel()
            narratorScope.cancel()
            tts?.stop()
            tts?.shutdown()
            AmbientSoundSynthesizer.stop()
        } catch (e: Exception) {
            Log.e("ArAudioNarrator", "Error in shutdown: ${e.message}")
        }
    }
}
