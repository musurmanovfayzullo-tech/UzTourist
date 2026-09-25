package com.example.util

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Manages the active in-app SOS emergency session:
 * - Prevents the app from exiting / minimizing to external apps.
 * - Starts a 5-minute (300 seconds) live countdown timer.
 * - Dispatches live GPS & tourist details to Telegram Bot via background HTTP API.
 * - Displays reassurance status in the user's active language.
 */
object SosSessionManager {
    const val TOTAL_COUNTDOWN_SECONDS = 300 // 5 minutes

    var isSosActive by mutableStateOf(false)
    var showActiveDialog by mutableStateOf(false)
    var remainingSeconds by mutableIntStateOf(TOTAL_COUNTDOWN_SECONDS)

    var touristName by mutableStateOf("Hurmatli Sayyoh")
    var touristPhone by mutableStateOf("+998 91 033 04 60")
    var latitude by mutableStateOf(41.3111)
    var longitude by mutableStateOf(69.2797)
    var addressEstimate by mutableStateOf("Joylashuv aniqlanmoqda...")
    var emergencyReason by mutableStateOf("Tezkor yordam va transport kerak")
    var isTelegramDispatched by mutableStateOf(false)

    private var countdownJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    fun startSos(
        context: Context,
        name: String,
        phone: String,
        lat: Double,
        lon: Double,
        address: String,
        reason: String
    ) {
        touristName = name.ifBlank { "Hurmatli Sayyoh" }
        touristPhone = phone.ifBlank { "+998 91 033 04 60" }
        latitude = lat
        longitude = lon
        addressEstimate = address.ifBlank { "GPS koordinatalar orqali" }
        emergencyReason = reason.ifBlank { "Tezkor yordam va transport kerak" }

        remainingSeconds = TOTAL_COUNTDOWN_SECONDS
        isSosActive = true
        showActiveDialog = true
        isTelegramDispatched = true

        val timeFormatted = java.text.SimpleDateFormat("HH:mm:ss, dd.MM.yyyy", Locale.getDefault()).format(java.util.Date())
        // Dispatch to Admin App & WebSocket fleet controllers
        SosEmergencyManager.recordEmergency(
            context = context,
            record = SosEmergencyRecord(
                touristName = touristName,
                touristPhone = touristPhone,
                latitude = latitude,
                longitude = longitude,
                address = addressEstimate,
                reason = emergencyReason,
                timestamp = timeFormatted
            )
        )

        // Send to Telegram Bot via background HTTP API (no app switching / no exiting app)
        TelegramBotManager.sendSosViaTelegramApi(
            context = context,
            touristName = touristName,
            phone = touristPhone,
            latitude = latitude,
            longitude = longitude,
            nearestMonument = addressEstimate,
            emergencyType = emergencyReason
        )

        // Start 5-minute countdown (300 to 0 seconds)
        countdownJob?.cancel()
        countdownJob = scope.launch {
            while (remainingSeconds > 0 && isActive && isSosActive) {
                delay(1000L)
                remainingSeconds--
            }
        }
    }

    fun endSos() {
        isSosActive = false
        showActiveDialog = false
        countdownJob?.cancel()
        countdownJob = null
        remainingSeconds = TOTAL_COUNTDOWN_SECONDS
    }

    fun minimizeDialog() {
        showActiveDialog = false
    }

    fun reopenDialog() {
        showActiveDialog = true
    }

    fun formattedRemainingTime(): String {
        val minutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60
        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }

    fun progressFraction(): Float {
        return (remainingSeconds.toFloat() / TOTAL_COUNTDOWN_SECONDS.toFloat()).coerceIn(0f, 1f)
    }
}
