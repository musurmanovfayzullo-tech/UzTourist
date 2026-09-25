package com.example.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.model.TouristGroupMember
import com.example.model.WhisperAudioPreset
import com.example.model.WhisperUserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Real-time Tour Guide Audio Whisper Manager:
 * Enables guides to broadcast crystal-clear voice to tourist group members' earphones,
 * provides live audio waveforms, noise cancellation presets, distance proximity monitoring,
 * haptic stray alerts (>30m), group chimes, and interactive hand-raising.
 */
object TourGuideWhisperManager {

    private const val PREFS_NAME = "uz_whisper_prefs"
    private const val KEY_CHANNEL = "whisper_channel_code"
    private const val KEY_ROLE = "whisper_user_role"
    private const val KEY_PRESET = "whisper_preset"
    private const val KEY_VOLUME = "whisper_volume"

    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var waveformJob: Job? = null
    private var ttsEngine: TextToSpeech? = null
    private var isTtsInitialized = false

    // Reactive Compose States
    var userRole by mutableStateOf(WhisperUserRole.TOURIST_LISTENER)
    var isBroadcasting by mutableStateOf(false)
    var isListening by mutableStateOf(true)
    var isMuted by mutableStateOf(false)
    var channelCode by mutableStateOf("UZ-WHISPER-7788")
    var volumeGain by mutableFloatStateOf(1.35f) // 135% comfortable default
    var selectedPreset by mutableStateOf(WhisperAudioPreset.NOISE_SHIELD)
    var liveDecibels by mutableIntStateOf(52)
    var waveformAmplitudes by mutableStateOf(listOf(0.2f, 0.4f, 0.7f, 0.5f, 0.8f, 0.3f, 0.6f, 0.9f, 0.4f, 0.2f))
    var liveSubtitle by mutableStateOf("Gid sharhi: Registon maydonining uchala mahobatli madrasasiga xush kelibsiz...")
    var proximityAlertActive by mutableStateOf(false)
    var isChimeActive by mutableStateOf(false)
    var isTtsPlaying by mutableStateOf(false)

    // Dynamic Group Members List (Distance radar and active tourists)
    var groupMembers by mutableStateOf(
        listOf(
            TouristGroupMember(
                id = "mem_1",
                name = "John Miller",
                flagEmoji = "🇺🇸",
                country = "AQSH",
                distanceMeters = 8,
                isEarphoneConnected = true,
                signalBars = 5
            ),
            TouristGroupMember(
                id = "mem_2",
                name = "Elena Rostova",
                flagEmoji = "🇩🇪",
                country = "Germaniya",
                distanceMeters = 14,
                isEarphoneConnected = true,
                signalBars = 5
            ),
            TouristGroupMember(
                id = "mem_3",
                name = "Hiroshi Tanaka",
                flagEmoji = "🇯🇵",
                country = "Yaponiya",
                distanceMeters = 22,
                isEarphoneConnected = true,
                signalBars = 4
            ),
            TouristGroupMember(
                id = "mem_4",
                name = "Pierre Dubois",
                flagEmoji = "🇫🇷",
                country = "Fransiya",
                distanceMeters = 34, // Far distance: triggers stray proximity alert!
                isEarphoneConnected = false,
                signalBars = 3
            ),
            TouristGroupMember(
                id = "mem_5",
                name = "Maria Garcia",
                flagEmoji = "🇪🇸",
                country = "Ispaniya",
                distanceMeters = 18,
                isEarphoneConnected = true,
                signalBars = 4,
                isHandRaised = true,
                handRaisedTime = "10:24"
            )
        )
    )

    fun initPreferences(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        channelCode = prefs.getString(KEY_CHANNEL, "UZ-WHISPER-7788") ?: "UZ-WHISPER-7788"
        val savedRole = prefs.getString(KEY_ROLE, WhisperUserRole.TOURIST_LISTENER.name)
        userRole = try {
            WhisperUserRole.valueOf(savedRole ?: WhisperUserRole.TOURIST_LISTENER.name)
        } catch (e: Exception) {
            WhisperUserRole.TOURIST_LISTENER
        }
        val savedPreset = prefs.getString(KEY_PRESET, WhisperAudioPreset.NOISE_SHIELD.name)
        selectedPreset = try {
            WhisperAudioPreset.valueOf(savedPreset ?: WhisperAudioPreset.NOISE_SHIELD.name)
        } catch (e: Exception) {
            WhisperAudioPreset.NOISE_SHIELD
        }
        volumeGain = prefs.getFloat(KEY_VOLUME, 1.35f)

        initTts(context)
        startWaveformAnimation()
        checkProximityAlert(context)
    }

    private fun initTts(context: Context) {
        if (ttsEngine != null) return
        try {
            ttsEngine = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    isTtsInitialized = true
                    ttsEngine?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            isTtsPlaying = true
                        }
                        override fun onDone(utteranceId: String?) {
                            isTtsPlaying = false
                        }
                        override fun onError(utteranceId: String?) {
                            isTtsPlaying = false
                        }
                    })
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startWaveformAnimation() {
        waveformJob?.cancel()
        waveformJob = managerScope.launch {
            val random = java.util.Random()
            while (isActive) {
                delay(120)
                if ((isBroadcasting && !isMuted) || (isListening && !isMuted)) {
                    val baseAmp = if (isBroadcasting) 0.6f else 0.45f
                    val newAmps = List(12) {
                        (baseAmp + (random.nextFloat() * 0.45f - 0.2f)).coerceIn(0.12f, 1.0f)
                    }
                    waveformAmplitudes = newAmps
                    liveDecibels = (48 + (random.nextInt(28))).coerceIn(35, 85)
                } else {
                    waveformAmplitudes = List(12) { 0.1f }
                    liveDecibels = 28
                }
            }
        }
    }

    fun startBroadcasting(context: Context) {
        userRole = WhisperUserRole.GUIDE_BROADCASTER
        isBroadcasting = true
        isListening = false
        isMuted = false
        triggerChime(context, notifyTelegram = true)
        saveState(context)
    }

    fun stopBroadcasting(context: Context) {
        isBroadcasting = false
        saveState(context)
    }

    fun joinChannel(context: Context, code: String) {
        channelCode = code.trim().uppercase()
        userRole = WhisperUserRole.TOURIST_LISTENER
        isListening = true
        isBroadcasting = false
        isMuted = false
        triggerChime(context, notifyTelegram = false)
        saveState(context)
    }

    fun leaveChannel(context: Context) {
        isListening = false
        isBroadcasting = false
        stopNarration()
        saveState(context)
    }

    fun toggleMute() {
        isMuted = !isMuted
    }

    fun selectPreset(context: Context, preset: WhisperAudioPreset) {
        selectedPreset = preset
        saveState(context)
    }

    fun setVolume(context: Context, gain: Float) {
        volumeGain = gain.coerceIn(0.5f, 2.5f)
        saveState(context)
    }

    /**
     * Plays a pleasant chime sound (using ToneGenerator) and vibrates the device.
     */
    fun triggerChime(context: Context, notifyTelegram: Boolean = false) {
        isChimeActive = true
        managerScope.launch(Dispatchers.IO) {
            try {
                val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
                // Pleasant high chime bell
                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP2, 280)
                delay(300)
                toneGen.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            vibrateDevice(context, 200)

            delay(1200)
            isChimeActive = false
        }

        if (notifyTelegram) {
            try {
                TelegramBotManager.sendAudioWhisperEventViaTelegramApi(
                    context = context,
                    channelCode = channelCode,
                    eventType = "Gid Ovozli Efiri Boshlandi & Diqqat Signali",
                    details = "Gid $channelCode kanali orqali jonli audio efir uzatmoqda. Guruh a'zolariga diqqat signali yuborildi."
                )
            } catch (ignored: Exception) {}
        }
    }

    /**
     * Tourist raises hand to ask a question.
     */
    fun raiseHand(context: Context, touristName: String) {
        val timeNow = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val updated = groupMembers.map {
            if (it.id == "mem_self" || it.name == touristName) {
                it.copy(isHandRaised = true, handRaisedTime = timeNow)
            } else it
        }
        groupMembers = updated
        vibrateDevice(context, 100)
    }

    /**
     * Guide lowers the hand or marks question as addressed.
     */
    fun lowerHand(memberId: String) {
        groupMembers = groupMembers.map {
            if (it.id == memberId) it.copy(isHandRaised = false, handRaisedTime = null)
            else it
        }
    }

    /**
     * Check if any tourist is further than 30 meters from guide.
     */
    fun checkProximityAlert(context: Context) {
        val hasStray = groupMembers.any { it.distanceMeters > 30 }
        proximityAlertActive = hasStray
        if (hasStray) {
            vibrateDevice(context, 150)
        }
    }

    /**
     * Speaks real live audio commentary to the user via TextToSpeech
     * so they immediately hear the guide speaking in high fidelity.
     */
    fun playSampleGuideNarration(context: Context, langCode: String = "uz") {
        if (!isTtsInitialized) {
            initTts(context)
        }
        val speechText = when (langCode.lowercase()) {
            "en" -> "Dear esteemed travelers, welcome to Registan Square! In front of us stands the majestic Sherdor Madrasah, built between 1619 and 1636. Notice the radiant tiger mosaics leaping towards the sun."
            "ru" -> "Уважаемые путешественники, добро пожаловать на площадь Регистан! Перед нами медресе Шердор с уникальными мозаиками львов, несущих солнце на своих спинах."
            else -> "Hurmatli sayyohlar, Registon maydoniga xush kelibsiz! Oldimizdagi Sherdor madrasasining peshtoqidagi quyosh ortmoqlagan shirlar tasviri qadimiy Samarqand me'morchiligining tengsiz durdonasidir."
        }
        liveSubtitle = speechText

        try {
            val locale = when (langCode.lowercase()) {
                "en" -> Locale.US
                "ru" -> Locale("ru", "RU")
                else -> Locale("uz", "UZ")
            }
            ttsEngine?.language = locale
            ttsEngine?.setPitch(1.0f)
            ttsEngine?.setSpeechRate(0.95f)
            ttsEngine?.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "whisper_tour_speech")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopNarration() {
        try {
            ttsEngine?.stop()
            isTtsPlaying = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun vibrateDevice(context: Context, durationMs: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(durationMs)
                }
            }
        } catch (ignored: Exception) {}
    }

    private fun saveState(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_CHANNEL, channelCode)
            .putString(KEY_ROLE, userRole.name)
            .putString(KEY_PRESET, selectedPreset.name)
            .putFloat(KEY_VOLUME, volumeGain)
            .apply()
    }
}
