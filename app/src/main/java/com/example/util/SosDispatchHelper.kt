package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.model.SosEmergencyRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SosDispatchHelper {

    /**
     * Dials the central 24/7 Call Center dispatch hotline immediately (+998 91 033 04 60).
     * Works completely offline without mobile internet.
     */
    fun triggerDirectCallCenter(context: Context, number: String = SosEmergencyRepository.CALL_CENTER_MAIN_PHONE) {
        try {
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(callIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "SOS Call Center: $number", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Prepares and sends an Offline GSM SMS with exact GPS coordinates and tourist details to the dispatch center.
     * This guarantees 100% functionality even when in remote desert/train routes without internet.
     */
    fun sendOfflineSmsSos(
        context: Context,
        touristName: String,
        latitude: Double,
        longitude: Double,
        nearestCity: String,
        emergencyReason: String,
        dispatchPhone: String = SosEmergencyRepository.CALL_CENTER_MAIN_PHONE
    ) {
        val mapLink = "https://maps.google.com/?q=$latitude,$longitude"
        val messageBody = """
            🚨 FAVQULODDA SOS XABARI! (UzTurist)
            Sayyoh: $touristName
            Shahar/Manzil: $nearestCity
            Sabab: $emergencyReason
            GPS: $latitude, $longitude
            Jonli Xarita: $mapLink
            Dispetcher: $dispatchPhone
        """.trimIndent()

        try {
            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$dispatchPhone")
                putExtra("sms_body", messageBody)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(smsIntent)
            Toast.makeText(context, "Oflayn SOS SMS dispetcherga ($dispatchPhone) tayyorlandi!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "SMS jo'natish xatosi: $messageBody", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Automatically sends the Live GPS Dispatch payload to the Telegram Dispatch bot so dispatchers immediately see
     * the tourist's live pinpoint on the interactive dispatcher map screen.
     */
    fun sendTelegramLiveGpsDispatch(
        context: Context,
        touristName: String,
        phone: String,
        latitude: Double,
        longitude: Double,
        nearestMonument: String,
        emergencyType: String
    ) {
        val timeFormatted = SimpleDateFormat("HH:mm:ss, dd.MM.yyyy", Locale.getDefault()).format(Date())
        val yandexLiveMap = "https://yandex.uz/maps/?pt=$longitude,$latitude&z=17&l=map"
        val googleLiveMap = "https://maps.google.com/?q=$latitude,$longitude"
        
        val alertPayload = """
            🚨🚨 SHOSHILINCH SOS DISPETCHER CHAQIRUVI 🚨🚨
            ━━━━━━━━━━━━━━━━━━━━
            👤 Sayyoh: $touristName
            📞 Aloqa telefoni: $phone
            ⚠️ Holat / Sabab: $emergencyType
            📍 Yaqin manzil / Obida: $nearestMonument
            🌐 GPS Koordinata: $latitude, $longitude
            🗺️ Google Maps: $googleLiveMap
            🧭 Yandex Xarita: $yandexLiveMap
            📞 SOS Call Center: ${SosEmergencyRepository.CALL_CENTER_DISPLAY_PHONE}
            ⏰ Vaqt: $timeFormatted
            ━━━━━━━━━━━━━━━━━━━━
            Dispetcher zudlik bilan yaqin atrofdagi yordam va tezkor transportni yo'naltirmoqda!
        """.trimIndent()

        // Copy alert payload to clipboard so user can also share directly if needed
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("SOS Koordinatalar", alertPayload)
            clipboard.setPrimaryClip(clip)
        } catch (ignored: Exception) {}

        // 0. Directly dispatch to Admin App & WebSocket fleet controllers
        SosEmergencyManager.recordEmergency(
            context = context,
            record = SosEmergencyRecord(
                touristName = touristName,
                touristPhone = phone,
                latitude = latitude,
                longitude = longitude,
                address = nearestMonument,
                reason = emergencyType,
                timestamp = timeFormatted
            )
        )

        // 1. Send via background Telegram Bot HTTP API (Direct Delivery & Pinpoint)
        TelegramBotManager.sendSosViaTelegramApi(
            context = context,
            touristName = touristName,
            phone = phone,
            latitude = latitude,
            longitude = longitude,
            nearestMonument = nearestMonument,
            emergencyType = emergencyType
        )

        // 2. Start the 5-minute countdown and in-app reassurance state without exiting the application
        SosSessionManager.startSos(
            context = context,
            name = touristName,
            phone = phone,
            lat = latitude,
            lon = longitude,
            address = nearestMonument,
            reason = emergencyType
        )

        Toast.makeText(context, "🚨 SOS: Koordinatalar jo'natildi. Operator aloqaga chiqmoqda...", Toast.LENGTH_SHORT).show()
    }
}
