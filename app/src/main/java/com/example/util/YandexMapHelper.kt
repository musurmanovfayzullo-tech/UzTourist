package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object YandexMapHelper {

    fun openInYandexMaps(context: Context, latitude: Double, longitude: Double, label: String = "Mo'ljal") {
        try {
            // Try Yandex Maps application URI first
            val yandexAppUri = Uri.parse("yandexmaps://maps.yandex.ru/?pt=$longitude,$latitude&z=16&l=map")
            val intent = Intent(Intent.ACTION_VIEW, yandexAppUri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to Yandex web browser map
            try {
                val webUri = Uri.parse("https://yandex.uz/maps/?pt=$longitude,$latitude&z=16&l=map&text=${Uri.encode(label)}")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(webIntent)
            } catch (err: Exception) {
                Toast.makeText(context, "Xaritani ochishda xatolik yuz berdi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun buildRouteInYandexMaps(context: Context, latTo: Double, lonTo: Double, destinationName: String = "Manzil") {
        try {
            // Direct route in Yandex Maps
            val routeUri = Uri.parse("yandexmaps://build_route_on_map?lat_to=$latTo&lon_to=$lonTo")
            val intent = Intent(Intent.ACTION_VIEW, routeUri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val webUri = Uri.parse("https://yandex.uz/maps/?rtext=~$latTo,$lonTo&rtt=auto")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(webIntent)
            } catch (err: Exception) {
                Toast.makeText(context, "Marshrutni ochishda xatolik", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun orderYandexTaxi(context: Context, destLat: Double, destLon: Double, destName: String) {
        try {
            val yandexGoUri = Uri.parse("yandexgo://route?end_lat=$destLat&end_lon=$destLon&appkey=uz_tourist")
            val intent = Intent(Intent.ACTION_VIEW, yandexGoUri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val webUri = Uri.parse("https://taxi.yandex.uz/")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(webIntent)
            } catch (err: Exception) {
                Toast.makeText(context, "Yandex Go Taxi yuklanmoqda...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun callPhoneNumber(context: Context, phoneNumber: String) {
        try {
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(callIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Telefon raqami: $phoneNumber", Toast.LENGTH_LONG).show()
        }
    }
}
