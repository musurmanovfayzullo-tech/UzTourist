package com.example.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.core.content.ContextCompat
import java.util.Locale

/**
 * Hands-Free Continuous Speech Recognition Manager for AR Historical Guide.
 * Keeps microphone active, converts voice queries into text, and feeds them into Gemini AI.
 */
class ArSpeechRecognitionManager(
    private val context: Context,
    private val onQuestionDetected: (String) -> Unit,
    private val onPartialSpeech: (String) -> Unit = {},
    private val onRmsChanged: (Float) -> Unit = {},
    private val onListeningStateChanged: (Boolean) -> Unit = {}
) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private var speechRecognizer: SpeechRecognizer? = null
    private var isEnabled = true
    private var isListening = false
    private var isTtsSpeaking = false
    private var restartRunnable: Runnable? = null

    init {
        initRecognizer()
    }

    private fun initRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.w("ArSpeechRecognizer", "SpeechRecognizer not available on this device")
            return
        }

        mainHandler.post {
            try {
                speechRecognizer?.destroy()
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(createListener())
                }
            } catch (e: Exception) {
                Log.e("ArSpeechRecognizer", "Error creating speech recognizer: ${e.message}")
            }
        }
    }

    private fun createListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isListening = true
                onListeningStateChanged(true)
            }

            override fun onBeginningOfSpeech() {
                // User started talking
            }

            override fun onRmsChanged(rmsdB: Float) {
                onRmsChanged(rmsdB)
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                isListening = false
                onListeningStateChanged(false)
            }

            override fun onError(error: Int) {
                isListening = false
                onListeningStateChanged(false)
                Log.d("ArSpeechRecognizer", "Speech error code: $error")

                // Schedule restart if recognizer is supposed to stay active and TTS isn't speaking
                scheduleRestart(delayMs = when (error) {
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> 600L
                    SpeechRecognizer.ERROR_NO_MATCH,
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> 300L
                    else -> 1000L
                })
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                onListeningStateChanged(false)

                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val recognizedText = matches?.firstOrNull()?.trim()

                if (!recognizedText.isNullOrBlank()) {
                    Log.i("ArSpeechRecognizer", "Recognized Question: $recognizedText")
                    onPartialSpeech("")
                    onQuestionDetected(recognizedText)
                }

                // If user didn't speak anything or after handling, restart listening if appropriate
                scheduleRestart(delayMs = 400L)
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val partialText = matches?.firstOrNull()?.trim()
                if (!partialText.isNullOrBlank()) {
                    onPartialSpeech(partialText)
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    fun setTtsSpeaking(speaking: Boolean) {
        isTtsSpeaking = speaking
        if (speaking) {
            cancelScheduledRestart()
            stopListeningInternal()
        } else {
            // Speech output finished, resume voice listening for questions
            scheduleRestart(delayMs = 500L)
        }
    }

    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
        if (enabled) {
            startListening()
        } else {
            cancelScheduledRestart()
            stopListeningInternal()
        }
    }

    fun startListening() {
        if (!isEnabled || isTtsSpeaking) return

        val hasRecordPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasRecordPermission) {
            Log.w("ArSpeechRecognizer", "RECORD_AUDIO permission not granted")
            return
        }

        cancelScheduledRestart()

        mainHandler.post {
            try {
                if (speechRecognizer == null) {
                    initRecognizer()
                }

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                    putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
                    // Support Uzbek, English, Russian
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toString())
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "uz-UZ")
                }

                speechRecognizer?.startListening(intent)
                isListening = true
                onListeningStateChanged(true)
            } catch (e: Exception) {
                Log.e("ArSpeechRecognizer", "Error starting listening: ${e.message}")
                scheduleRestart(delayMs = 1500L)
            }
        }
    }

    private fun stopListeningInternal() {
        mainHandler.post {
            try {
                speechRecognizer?.stopListening()
            } catch (e: Exception) {
                Log.e("ArSpeechRecognizer", "Error stopping listening: ${e.message}")
            } finally {
                isListening = false
                onListeningStateChanged(false)
            }
        }
    }

    private fun scheduleRestart(delayMs: Long) {
        if (!isEnabled || isTtsSpeaking) return
        cancelScheduledRestart()

        restartRunnable = Runnable {
            if (isEnabled && !isTtsSpeaking) {
                startListening()
            }
        }
        mainHandler.postDelayed(restartRunnable!!, delayMs)
    }

    private fun cancelScheduledRestart() {
        restartRunnable?.let {
            mainHandler.removeCallbacks(it)
            restartRunnable = null
        }
    }

    fun destroy() {
        cancelScheduledRestart()
        mainHandler.post {
            try {
                speechRecognizer?.destroy()
                speechRecognizer = null
            } catch (e: Exception) {
                Log.e("ArSpeechRecognizer", "Error destroying speech recognizer: ${e.message}")
            }
        }
    }
}
