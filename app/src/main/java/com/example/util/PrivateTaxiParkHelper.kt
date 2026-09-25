package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object PrivateTaxiParkHelper {

    // Central 24/7 Taxi Park Dispatch Hotline
    const val DISPATCH_PHONE_MAIN = "+998910330460"
    const val DISPATCH_PHONE_SECONDARY = "+998781507777"
    const val TELEGRAM_BOT_USERNAME = "avtomaktab77bot"

    fun callDispatcher(context: Context, phoneNumber: String = DISPATCH_PHONE_MAIN) {
        try {
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(callIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Dispetcher raqami: $phoneNumber", Toast.LENGTH_LONG).show()
        }
    }

    fun openTelegramDispatch(context: Context, bookingDetails: String = "") {
        // 1. Dispatch directly to Telegram Bot API for ID 6089586932
        if (bookingDetails.isNotBlank()) {
            TelegramBotManager.sendTaxiBookingViaTelegramApi(context, bookingDetails)
        }

        // 2. Open Telegram client with prefilled details
        try {
            val message = if (bookingDetails.isNotBlank()) {
                Uri.encode("Salom! Shaxsiy Avtoparkdan taxi buyurtma qilmoqchiman:\n$bookingDetails")
            } else {
                Uri.encode("Salom! Shaxsiy Avtoparkdan taxi buyurtma qilmoqchiman.")
            }
            val tgUri = Uri.parse("https://t.me/$TELEGRAM_BOT_USERNAME?text=$message")
            val intent = Intent(Intent.ACTION_VIEW, tgUri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val webUri = Uri.parse("https://t.me/$TELEGRAM_BOT_USERNAME")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(webIntent)
            } catch (err: Exception) {
                Toast.makeText(context, "Telegram bot: @$TELEGRAM_BOT_USERNAME (ID: ${TelegramBotManager.PRIMARY_DISPATCHER_CHAT_ID})", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun openNavigationMap(context: Context, latitude: Double, longitude: Double, label: String = "Manzil") {
        try {
            // Standard Geo URI (compatible with all Android map apps installed)
            val geoUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(${Uri.encode(label)})")
            val intent = Intent(Intent.ACTION_VIEW, geoUri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val webUri = Uri.parse("https://yandex.uz/maps/?pt=$longitude,$latitude&z=16&l=map&text=${Uri.encode(label)}")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(webIntent)
            } catch (err: Exception) {
                Toast.makeText(context, "Koordinatalar: $latitude, $longitude", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun buildRoute(context: Context, latTo: Double, lonTo: Double, destinationName: String = "Manzil") {
        try {
            val geoUri = Uri.parse("google.navigation:q=$latTo,$lonTo")
            val intent = Intent(Intent.ACTION_VIEW, geoUri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val webUri = Uri.parse("https://yandex.uz/maps/?rtext=~$latTo,$lonTo&rtt=auto")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(webIntent)
            } catch (err: Exception) {
                Toast.makeText(context, "Marshrut: $destinationName", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
