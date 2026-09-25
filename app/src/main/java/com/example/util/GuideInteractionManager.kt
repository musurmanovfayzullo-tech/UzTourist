package com.example.util

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.model.GuideChatMessage
import com.example.model.GuideLocationInfo
import com.example.model.GuideReviewItem
import com.example.model.TourCheckpointItem
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Central state and persistence manager for Guide-Tourist interactive capabilities:
 * 1. QR Code scanner & direct pairing
 * 2. Live Guide Tracking & Meeting Point
 * 3. Direct Chat / Walkie-talkie quick messages
 * 4. Daily Tour Itinerary checklist
 * 5. Guide Review & Rating system
 *
 * All actions synchronize directly with AdminServerScreen and TelegramBotManager.
 */
object GuideInteractionManager {

    private const val PREFS_NAME = "uz_tourist_guide_interactions"
    private const val KEY_CHAT_MESSAGES = "chat_messages_json"
    private const val KEY_CHECKPOINTS = "checkpoints_json"
    private const val KEY_REVIEWS = "reviews_json"

    // Reactive state triggers for Jetpack Compose UI
    var chatUpdateTrigger by mutableStateOf(0)
    var checkpointsUpdateTrigger by mutableStateOf(0)
    var reviewsUpdateTrigger by mutableStateOf(0)

    // Live Guide Position & Meeting Point
    val currentGuideLocation = GuideLocationInfo(
        guideName = "Alisher Navoiy (Sertifikatlangan Gid)",
        guidePhone = "+998 90 777 88 99",
        latitude = 39.6547,
        longitude = 66.9758,
        meetingPointName = "Registon Maydoni • Sherdor Madrasasi oldi",
        meetingTime = "Bugun, 10:00 - 18:00",
        status = "Jonli kuzatuv faol • Yaqin atrofda"
    )

    // Calculate approximate distance between tourist and guide
    fun calculateDistanceMeters(touristLat: Double, touristLon: Double): Int {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(
            touristLat, touristLon,
            currentGuideLocation.latitude, currentGuideLocation.longitude,
            results
        )
        return results[0].toInt().coerceAtLeast(15)
    }

    // ----------------------------------------------------
    // 1. QR Code Parsing
    // ----------------------------------------------------
    fun parseGuideNumberFromQr(qrContent: String): String {
        val trimmed = qrContent.trim()
        if (trimmed.startsWith("UZ-GUIDE-", ignoreCase = true)) {
            return trimmed.substringAfter("UZ-GUIDE-").trim()
        }
        if (trimmed.startsWith("GUIDE:", ignoreCase = true)) {
            return trimmed.substringAfter("GUIDE:").trim()
        }
        val digitsOnly = trimmed.filter { it.isDigit() }
        return if (digitsOnly.length in 3..8) digitsOnly else trimmed.take(8)
    }

    // ----------------------------------------------------
    // 2. Direct Guide-Tourist Chat
    // ----------------------------------------------------
    fun getChatMessages(context: Context): List<GuideChatMessage> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val rawJson = prefs.getString(KEY_CHAT_MESSAGES, null) ?: return defaultInitialMessages()
        val list = mutableListOf<GuideChatMessage>()
        try {
            val jsonArray = JSONArray(rawJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    GuideChatMessage(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        senderName = obj.optString("senderName", "Sayyoh"),
                        senderId = obj.optString("senderId", "UZ-TOUR"),
                        messageText = obj.optString("messageText", ""),
                        timestamp = obj.optString("timestamp", ""),
                        isFromTourist = obj.optBoolean("isFromTourist", true)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return defaultInitialMessages()
        }
        return if (list.isEmpty()) defaultInitialMessages() else list
    }

    private fun defaultInitialMessages(): List<GuideChatMessage> {
        return listOf(
            GuideChatMessage(
                id = "init_1",
                senderName = "Gid Alisher",
                senderId = "GUIDE-77",
                messageText = "Assalomu alaykum! Xush kelibsiz! Men sizning shaxsiy gidingizman. Savollaringiz bormi?",
                timestamp = "09:30",
                isFromTourist = false
            )
        )
    }

    fun sendChatMessage(
        context: Context,
        messageText: String,
        touristName: String,
        touristId: String,
        guideNumber: String,
        onDispatched: ((Boolean) -> Unit)? = null
    ) {
        if (messageText.isBlank()) return
        val currentList = getChatMessages(context).toMutableList()
        val timeFormatted = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        val newMsg = GuideChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = touristName,
            senderId = touristId,
            messageText = messageText.trim(),
            timestamp = timeFormatted,
            isFromTourist = true
        )
        currentList.add(newMsg)
        saveChatMessages(context, currentList)

        // Dispatch to Telegram directly
        TelegramBotManager.sendGuideChatMessageViaTelegramApi(
            context = context,
            touristName = touristName,
            touristId = touristId,
            messageText = messageText,
            guideNumber = guideNumber,
            onResult = { success, _ ->
                onDispatched?.invoke(success)
            }
        )
    }

    private fun saveChatMessages(context: Context, list: List<GuideChatMessage>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonArray = JSONArray()
        for (m in list) {
            val obj = JSONObject().apply {
                put("id", m.id)
                put("senderName", m.senderName)
                put("senderId", m.senderId)
                put("messageText", m.messageText)
                put("timestamp", m.timestamp)
                put("isFromTourist", m.isFromTourist)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_CHAT_MESSAGES, jsonArray.toString()).apply()
        chatUpdateTrigger++
    }

    // ----------------------------------------------------
    // 3. Tour Itinerary Checkpoints
    // ----------------------------------------------------
    fun getTourCheckpoints(context: Context): List<TourCheckpointItem> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val rawJson = prefs.getString(KEY_CHECKPOINTS, null) ?: return defaultCheckpoints()
        val list = mutableListOf<TourCheckpointItem>()
        try {
            val jsonArray = JSONArray(rawJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    TourCheckpointItem(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        cityName = obj.getString("cityName"),
                        estimatedDuration = obj.getString("estimatedDuration"),
                        isCompleted = obj.getBoolean("isCompleted"),
                        completedTime = obj.optString("completedTime", null)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return defaultCheckpoints()
        }
        return if (list.isEmpty()) defaultCheckpoints() else list
    }

    private fun defaultCheckpoints(): List<TourCheckpointItem> {
        return listOf(
            TourCheckpointItem("cp_1", "Registon Maydoni (Sherdor, Ulug'bek, Tillakori)", "Samarqand", "1.5 soat", true, "10:15"),
            TourCheckpointItem("cp_2", "Go'ri Amir Maqbarasi (Amir Temur dahmasi)", "Samarqand", "45 daqiqa", false),
            TourCheckpointItem("cp_3", "Bibixonim Jome Masjidi", "Samarqand", "1 soat", false),
            TourCheckpointItem("cp_4", "Shohi Zinda Me'moriy Majmuasi", "Samarqand", "1.5 soat", false),
            TourCheckpointItem("cp_5", "Ulug'bek Rasadxonasi va Muzeyi", "Samarqand", "50 daqiqa", false),
            TourCheckpointItem("cp_6", "Siyob Milliy Sharq Bozori (Non va holva)", "Samarqand", "1 soat", false)
        )
    }

    fun toggleCheckpoint(
        context: Context,
        checkpointId: String,
        touristName: String
    ) {
        val list = getTourCheckpoints(context).toMutableList()
        val index = list.indexOfFirst { it.id == checkpointId }
        if (index != -1) {
            val item = list[index]
            val newCompleted = !item.isCompleted
            val timeFormatted = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val updated = item.copy(
                isCompleted = newCompleted,
                completedTime = if (newCompleted) timeFormatted else null
            )
            list[index] = updated
            saveCheckpoints(context, list)

            if (newCompleted) {
                val completedCount = list.count { it.isCompleted }
                TelegramBotManager.sendItineraryProgressViaTelegramApi(
                    context = context,
                    touristName = touristName,
                    checkpointTitle = updated.title,
                    completedCount = completedCount,
                    totalCount = list.size,
                    cityName = updated.cityName
                )
            }
        }
    }

    private fun saveCheckpoints(context: Context, list: List<TourCheckpointItem>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonArray = JSONArray()
        for (cp in list) {
            val obj = JSONObject().apply {
                put("id", cp.id)
                put("title", cp.title)
                put("cityName", cp.cityName)
                put("estimatedDuration", cp.estimatedDuration)
                put("isCompleted", cp.isCompleted)
                put("completedTime", cp.completedTime)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_CHECKPOINTS, jsonArray.toString()).apply()
        checkpointsUpdateTrigger++
    }

    // ----------------------------------------------------
    // 4. Guide Reviews & Ratings
    // ----------------------------------------------------
    fun getAllReviews(context: Context): List<GuideReviewItem> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val rawJson = prefs.getString(KEY_REVIEWS, null) ?: return defaultReviews()
        val list = mutableListOf<GuideReviewItem>()
        try {
            val jsonArray = JSONArray(rawJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val tagsList = mutableListOf<String>()
                val tagsArr = obj.optJSONArray("tags")
                if (tagsArr != null) {
                    for (t in 0 until tagsArr.length()) tagsList.add(tagsArr.getString(t))
                }
                list.add(
                    GuideReviewItem(
                        id = obj.getString("id"),
                        touristId = obj.getString("touristId"),
                        touristName = obj.getString("touristName"),
                        guideAssignedNumber = obj.getString("guideAssignedNumber"),
                        rating = obj.getInt("rating"),
                        tags = tagsList,
                        comment = obj.getString("comment"),
                        dateFormatted = obj.getString("dateFormatted")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return defaultReviews()
        }
        return if (list.isEmpty()) defaultReviews() else list
    }

    private fun defaultReviews(): List<GuideReviewItem> {
        return listOf(
            GuideReviewItem(
                id = "rev_1",
                touristId = "UZ-TOUR-9021",
                touristName = "Michael Vance",
                guideAssignedNumber = "7788",
                rating = 5,
                tags = listOf("Bilimli gid 🎓", "Samimiy xizmat 😊", "Vaqtni qadrlaydi ⏱️"),
                comment = "Gid Alisher Registon va Shohi Zinda tarixini juda qiziqarli so'zlab berdi! Tavsiya qilaman.",
                dateFormatted = "Bugun, 11:20"
            )
        )
    }

    fun submitReview(
        context: Context,
        touristId: String,
        touristName: String,
        guideNumber: String,
        rating: Int,
        tags: List<String>,
        comment: String,
        onFinished: ((Boolean) -> Unit)? = null
    ) {
        val currentList = getAllReviews(context).toMutableList()
        val timeFormatted = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())

        val newReview = GuideReviewItem(
            id = UUID.randomUUID().toString(),
            touristId = touristId,
            touristName = touristName,
            guideAssignedNumber = guideNumber,
            rating = rating.coerceIn(1, 5),
            tags = tags,
            comment = comment.trim(),
            dateFormatted = timeFormatted
        )
        currentList.add(0, newReview) // Add at top
        saveReviews(context, currentList)

        // Send to Telegram
        TelegramBotManager.sendGuideReviewViaTelegramApi(
            context = context,
            touristName = touristName,
            touristId = touristId,
            guideNumber = guideNumber,
            ratingStars = rating,
            tags = tags,
            comment = comment,
            onResult = { success, _ ->
                onFinished?.invoke(success)
            }
        )
    }

    private fun saveReviews(context: Context, list: List<GuideReviewItem>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonArray = JSONArray()
        for (r in list) {
            val obj = JSONObject().apply {
                put("id", r.id)
                put("touristId", r.touristId)
                put("touristName", r.touristName)
                put("guideAssignedNumber", r.guideAssignedNumber)
                put("rating", r.rating)
                val tagsArr = JSONArray()
                r.tags.forEach { tagsArr.put(it) }
                put("tags", tagsArr)
                put("comment", r.comment)
                put("dateFormatted", r.dateFormatted)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_REVIEWS, jsonArray.toString()).apply()
        reviewsUpdateTrigger++
    }
}
