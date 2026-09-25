package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object GoogleMapHelper {

    fun openInGoogleMaps(context: Context, latitude: Double, longitude: Double, label: String = "Manzil") {
        try {
            val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(${Uri.encode(label)})")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(mapIntent)
        } catch (e: Exception) {
            // Fallback to Google Maps Web URL
            try {
                val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(webIntent)
            } catch (err: Exception) {
                Toast.makeText(context, "Google Maps ochilmadi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun buildRouteInGoogleMaps(
        context: Context,
        destinationLat: Double,
        destinationLon: Double,
        destinationName: String = "Manzil",
        mode: String = "d" // d: driving, w: walking, r: transit
    ) {
        try {
            // Native Google Navigation Intent
            val gmmIntentUri = Uri.parse("google.navigation:q=$destinationLat,$destinationLon&mode=$mode")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(mapIntent)
        } catch (e: Exception) {
            try {
                val travelMode = when (mode) {
                    "w" -> "walking"
                    "r" -> "transit"
                    else -> "driving"
                }
                val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$destinationLat,$destinationLon&travelmode=$travelMode")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(webIntent)
            } catch (err: Exception) {
                Toast.makeText(context, "Marshrutni ochishda xatolik", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
