package com.example.model

/**
 * Data models for comprehensive Tour Guide interactions:
 * - Live Guide GPS & Meeting Point
 * - Direct Quick Messages
 * - Daily Itinerary Checkpoints
 * - Guide Rating & Reviews
 */
data class GuideLocationInfo(
    val guideName: String = "Alisher Navoiy (Gid)",
    val guidePhone: String = "+998 90 777 88 99",
    val guidePhotoUrl: String = "",
    val latitude: Double = 39.6547,
    val longitude: Double = 66.9758,
    val meetingPointName: String = "Registon Maydoni • Sherdor Madrasasi oldida",
    val meetingTime: String = "Bugun, 10:00",
    val status: String = "Jonli kuzatuv faol • 110 metr yaqinlikda"
)

data class GuideChatMessage(
    val id: String,
    val senderName: String,
    val senderId: String,
    val messageText: String,
    val timestamp: String,
    val isFromTourist: Boolean = true
)

data class TourCheckpointItem(
    val id: String,
    val title: String,
    val cityName: String,
    val estimatedDuration: String,
    val isCompleted: Boolean = false,
    val completedTime: String? = null
)

data class GuideReviewItem(
    val id: String,
    val touristId: String,
    val touristName: String,
    val guideAssignedNumber: String,
    val rating: Int, // 1 to 5
    val tags: List<String>,
    val comment: String,
    val dateFormatted: String
)
