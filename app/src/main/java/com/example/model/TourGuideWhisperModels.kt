package com.example.model

/**
 * Data models for "Tour Guide Audio Whisper" (Gid Guruhi Jonli Ovozli Efir):
 * Allows the tour guide to broadcast crystal-clear voice directly to tourists'
 * earphones with noise suppression, proximity distance radar, chime notifications,
 * and question hand-raising without costly radio hardware.
 */

enum class WhisperUserRole(val titleUz: String, val titleEn: String) {
    GUIDE_BROADCASTER("🎙️ Gid (Efir Uzatuvchi)", "Guide Broadcaster"),
    TOURIST_LISTENER("🎧 Sayyoh (Tinglovchi)", "Tourist Listener")
}

enum class WhisperAudioPreset(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val noiseReductionPercent: Int,
    val latencyMs: Int
) {
    ULTRA_HD(
        id = "ultra_hd",
        title = "Kristall Ovoz (Ultra HD)",
        subtitle = "Ochiq maydon va sokin obidalar uchun eng ravon va tiniq diksiya",
        iconEmoji = "💎",
        noiseReductionPercent = 40,
        latencyMs = 45
    ),
    NOISE_SHIELD(
        id = "noise_shield",
        title = "Bozor & Shovqin Filtri",
        subtitle = "Siypb va Chorsu bozorlari, gavjum ko'chalardagi shovqinni kesadi",
        iconEmoji = "🛡️",
        noiseReductionPercent = 88,
        latencyMs = 60
    ),
    ANCIENT_ACOUSTICS(
        id = "ancient_acoustics",
        title = "Madrasa & Gumbaz Rezonansi",
        subtitle = "Tarixiy koshinli gumbazlarda mayin va yoqimli akustik aks-sado",
        iconEmoji = "🏛️",
        noiseReductionPercent = 55,
        latencyMs = 50
    ),
    BATTERY_SAVER(
        id = "battery_saver",
        title = "Eko & Tejamkor Rejim",
        subtitle = "Kun bo'yi davom etadigan uzoq ekskursiyalar uchun kam batareya",
        iconEmoji = "⚡",
        noiseReductionPercent = 60,
        latencyMs = 75
    )
}

data class TouristGroupMember(
    val id: String,
    val name: String,
    val flagEmoji: String,
    val country: String,
    val distanceMeters: Int,
    val isEarphoneConnected: Boolean = true,
    val signalBars: Int = 5,
    val isHandRaised: Boolean = false,
    val handRaisedTime: String? = null
)

data class WhisperAudioChannel(
    val channelCode: String = "UZ-WHISPER-7788",
    val guideName: String = "Alisher Navoiy (Gid)",
    val tourName: String = "Registon Maydoni • 3 Madrasa Sayohati",
    val isLive: Boolean = true,
    val listenersCount: Int = 12,
    val preset: WhisperAudioPreset = WhisperAudioPreset.NOISE_SHIELD
)
