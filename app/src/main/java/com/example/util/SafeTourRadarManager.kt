package com.example.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

enum class GeofenceStatus(val label: String, val colorHex: Long) {
    SAFE("XAVFSIZ HUDUD", 0xFF10B981),
    CAUTION("OGOHLANTIRISH", 0xFFF59E0B),
    BREACHED("GIDDAN UZOQLASHDI!", 0xFFEF4444)
}

data class SafeTourAlertLog(
    val id: String,
    val touristName: String,
    val distanceMeters: Int,
    val status: String,
    val timeFormatted: String,
    val locationDesc: String
)

object SafeTourRadarManager : TextToSpeech.OnInitListener {
    private const val TAG = "SafeTourRadarManager"
    private const val PREFS_NAME = "uz_safe_tour_radar_prefs"
    private const val KEY_ALERT_LOGS = "safe_tour_alert_logs_json"

    // Configurable Safe Geofence Radius in meters (default 80m)
    var safeRadiusMeters by mutableIntStateOf(80)

    // Reactive trigger for Compose
    var radarUpdateTrigger by mutableIntStateOf(0)

    // TextToSpeech for voice alarms
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    // Anti-spam cooldown for sound/vibration
    private var lastAlertTimestamp: Long = 0L
    private const val ALERT_COOLDOWN_MS = 20_000L // 20 soniya

    fun init(context: Context) {
        if (tts == null) {
            try {
                tts = TextToSpeech(context.applicationContext, this)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize TTS", e)
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("uz"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.ENGLISH)
            }
            isTtsReady = true
        }
    }

    /**
     * Calculates distance (in meters) and bearing (in degrees) to Tour Guide.
     */
    fun getCurrentRadarData(context: Context): Triple<Int, Float, GeofenceStatus> {
        val guideLoc = GuideInteractionManager.currentGuideLocation
        val liveGps = GpsLocationManager.currentLocation.value

        val (distance, bearing) = if (liveGps != null) {
            val dist = GuideInteractionManager.calculateDistanceMeters(liveGps.latitude, liveGps.longitude)
            val brng = calculateBearing(liveGps.latitude, liveGps.longitude, guideLoc.latitude, guideLoc.longitude)
            Pair(dist, brng)
        } else {
            Pair(0, 0f)
        }

        val status = when {
            distance == 0 -> GeofenceStatus.SAFE
            distance < (safeRadiusMeters * 0.7).toInt() -> GeofenceStatus.SAFE
            distance <= safeRadiusMeters -> GeofenceStatus.CAUTION
            else -> GeofenceStatus.BREACHED
        }

        return Triple(distance, bearing, status)
    }

    /**
     * Bearing calculation from tourist to guide in degrees (0..360)
     */
    fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val phi1 = Math.toRadians(lat1)
        val phi2 = Math.toRadians(lat2)
        val deltaLambda = Math.toRadians(lon2 - lon1)

        val y = sin(deltaLambda) * cos(phi2)
        val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(deltaLambda)
        val theta = atan2(y, x)
        val bearing = (Math.toDegrees(theta) + 360.0) % 360.0
        return bearing.toFloat()
    }

    /**
     * Cardinal direction name in Uzbek
     */
    fun getDirectionDescriptionUz(bearing: Float): String {
        return when (bearing) {
            in 337.5..360.0, in 0.0..22.5 -> "Shimol tomon ⬆️"
            in 22.5..67.5 -> "Shimoli-Sharq tomon ↗️"
            in 67.5..112.5 -> "Sharq tomon ➡️"
            in 112.5..157.5 -> "Janubi-Sharq tomon ↘️"
            in 157.5..202.5 -> "Janub tomon ⬇️"
            in 202.5..247.5 -> "Janubi-G'arb tomon ↙️"
            in 247.5..292.5 -> "G'arb tomon ⬅️"
            in 292.5..337.5 -> "Shimoli-G'arb tomon ↖️"
            else -> "Gid tomon ⬆️"
        }
    }

    /**
     * Checks if geofence is breached and fires haptic, audio and TTS alert with cooldown
     */
    fun checkAndTriggerGeofenceAlert(
        context: Context,
        touristName: String,
        distanceMeters: Int,
        forceAlert: Boolean = false
    ) {
        val now = System.currentTimeMillis()
        if (!forceAlert && (now - lastAlertTimestamp < ALERT_COOLDOWN_MS)) {
            return
        }

        if (distanceMeters > safeRadiusMeters || forceAlert) {
            lastAlertTimestamp = now

            // 1. Vibrate device with recognizable pattern
            triggerVibration(context)

            // 2. Play acoustic tone alarm
            playToneChime()

            // 3. Speak out clear voice warning
            val warningText = "Diqqat! Giddan uzoqlashdingiz. Masofa $distanceMeters metr. Iltimos gid tomon yaqinlashing!"
            speakVoiceWarning(warningText)

            // 4. Save to Admin Audit Log
            logAlertBreach(context, touristName, distanceMeters)
        }
    }

    private fun triggerVibration(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, 300, 150, 300, 150, 400), -1)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 300, 150, 300, 150, 400), -1)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Vibration failed", e)
        }
    }

    private fun playToneChime() {
        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_ALARM, 90)
            toneGen.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 350)
            CoroutineScope(Dispatchers.IO).launch {
                kotlinx.coroutines.delay(400)
                toneGen.release()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Tone alarm failed", e)
        }
    }

    private fun speakVoiceWarning(text: String) {
        try {
            if (isTtsReady && tts != null) {
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "SafeTourAlert")
            }
        } catch (e: Exception) {
            Log.e(TAG, "TTS speak failed", e)
        }
    }

    /**
     * Dispatch "Men adashdim!" alert directly to the guide and telegram bot
     */
    fun dispatchLostSignalToGuide(
        context: Context,
        touristName: String,
        touristPhone: String,
        touristId: String,
        guideNumber: String,
        distanceMeters: Int,
        directionDesc: String,
        onDispatched: (Boolean, String) -> Unit
    ) {
        val liveGps = GpsLocationManager.currentLocation.value
        val lat = liveGps?.latitude ?: GuideInteractionManager.currentGuideLocation.latitude
        val lon = liveGps?.longitude ?: GuideInteractionManager.currentGuideLocation.longitude

        // Trigger local vibration
        triggerVibration(context)

        // Send via Telegram
        TelegramBotManager.sendSafeTourRadarAlertViaTelegramApi(
            context = context,
            touristName = touristName,
            touristPhone = touristPhone,
            touristId = touristId,
            guideNumber = guideNumber,
            distanceMeters = distanceMeters,
            directionDescription = directionDesc,
            latitude = lat,
            longitude = lon,
            onResult = { success, msg ->
                logAlertBreach(context, touristName, distanceMeters, "Adashish signali yuborildi")
                onDispatched(success, msg)
            }
        )
    }

    fun logAlertBreach(
        context: Context,
        touristName: String,
        distanceMeters: Int,
        customStatus: String? = null
    ) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentLogs = getAlertLogs(context).toMutableList()
        val timeFormatted = SimpleDateFormat("HH:mm:ss, dd.MM", Locale.getDefault()).format(Date())

        val newLog = SafeTourAlertLog(
            id = "RADAR-${System.currentTimeMillis() % 100000}",
            touristName = touristName,
            distanceMeters = distanceMeters,
            status = customStatus ?: if (distanceMeters > safeRadiusMeters) "Xavfli uzoqlashish" else "Ogohlantirish",
            timeFormatted = timeFormatted,
            locationDesc = GuideInteractionManager.currentGuideLocation.meetingPointName
        )
        currentLogs.add(0, newLog)
        val trimmed = currentLogs.take(30)

        val jsonArray = JSONArray()
        for (item in trimmed) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("touristName", item.touristName)
                put("distanceMeters", item.distanceMeters)
                put("status", item.status)
                put("timeFormatted", item.timeFormatted)
                put("locationDesc", item.locationDesc)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_ALERT_LOGS, jsonArray.toString()).apply()
        radarUpdateTrigger++
    }

    fun getAlertLogs(context: Context): List<SafeTourAlertLog> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_ALERT_LOGS, null) ?: return defaultLogs()
        val list = mutableListOf<SafeTourAlertLog>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    SafeTourAlertLog(
                        id = obj.getString("id"),
                        touristName = obj.getString("touristName"),
                        distanceMeters = obj.getInt("distanceMeters"),
                        status = obj.getString("status"),
                        timeFormatted = obj.getString("timeFormatted"),
                        locationDesc = obj.getString("locationDesc")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return defaultLogs()
        }
        return if (list.isEmpty()) defaultLogs() else list
    }

    private fun defaultLogs(): List<SafeTourAlertLog> {
        return emptyList()
    }
}
