package com.example.model

data class EmergencyContact(
    val title: String,
    val number: String,
    val description: String,
    val iconType: String,
    val isPrimaryCallCenter: Boolean = false
)

data class TouristEmbassy(
    val country: String,
    val city: String,
    val flag: String,
    val phone: String,
    val address: String
)

data class SosLivePayload(
    val touristName: String,
    val phoneNumber: String,
    val latitude: Double,
    val longitude: Double,
    val nearestLandmark: String,
    val city: String,
    val emergencyType: String, // Medical, Lost, Police, Urgent Dispatch
    val batteryLevel: String = "88%",
    val timestamp: String
)

object SosEmergencyRepository {
    // 24/7 Central Dispatch Call Center (Connected directly to user's fleet dispatch)
    const val CALL_CENTER_MAIN_PHONE = "+998910330460"
    const val CALL_CENTER_DISPLAY_PHONE = "+998 91 033 04 60"
    const val CALL_CENTER_SECONDARY_PHONE = "+998781507777"
    const val TELEGRAM_SOS_BOT = "avtomaktab77bot"
    const val TELEGRAM_BOT_TOKEN = "8641253570:AAFL_xdWOd7LFC2BuWBGwxMOjpGR6oYZmbs"
    const val TELEGRAM_DISPATCH_CHAT_ID = "6089586932"
    const val TOURIST_POLICE_HOTLINE = "1173"
    const val AMBULANCE_HOTLINE = "103"
    const val FIRE_EMERGENCY_HOTLINE = "101"

    val emergencyContacts = listOf(
        EmergencyContact(
            title = "24/7 Call Center & Shaxsiy Dispetcherlik",
            number = CALL_CENTER_MAIN_PHONE,
            description = "Shaxsiy avtopark dispetcherlari, tezkor transfer va sayyohlar bilan jonli aloqa",
            iconType = "dispatcher",
            isPrimaryCallCenter = true
        ),
        EmergencyContact(
            title = "Sayyohlik Politsiyasi (Tourist Police 24/7)",
            number = TOURIST_POLICE_HOTLINE,
            description = "Ingliz va rus tillarida xorijiy sayyohlar xavfsizligini ta'minlovchi davlat xizmati",
            iconType = "police",
            isPrimaryCallCenter = false
        ),
        EmergencyContact(
            title = "Tez Tibbiy Yordam (Ambulance)",
            number = AMBULANCE_HOTLINE,
            description = "Respublika shoshilinch tez tibbiy yordam ko'rsatish markazi",
            iconType = "medical",
            isPrimaryCallCenter = false
        ),
        EmergencyContact(
            title = "Favqulodda Vaziyatlar (FVV Rescue)",
            number = FIRE_EMERGENCY_HOTLINE,
            description = "Qutqaruv xizmati va yong'in xavfsizligi",
            iconType = "rescue",
            isPrimaryCallCenter = false
        )
    )

    val emergencyPhrasesMultiLang = mapOf(
        "UZ" to listOf(
            "Menga yordam bering! (Help me!)",
            "Menga tez yordam shifokori kerak! (I need a doctor!)",
            "Men adashib qoldim, mehmonxonamga borishim kerak (I am lost)",
            "Dispetcherga qo'ng'iroq qiling (+998 91 033 04 60)"
        ),
        "EN" to listOf(
            "I need urgent medical help!",
            "I am lost, please call my dispatcher: +998 91 033 04 60",
            "Where is the nearest tourist police station?",
            "Please show me the way to Registan / City Center"
        ),
        "RU" to listOf(
            "Мне нужна срочная помощь!",
            "Я турист, я потерялся. Свяжитесь с моим диспетчером: +998 91 033 04 60",
            "Где находится туристическая полиция?",
            "Помогите добраться до гостиницы"
        ),
        "ZH" to listOf(
            "我需要紧急帮助！(Help me!)",
            "我是游客，我迷路了。请联系调度中心：+998 91 033 04 60",
            "请帮我叫救护车 (Call ambulance)"
        )
    )

    val embassies = listOf(
        TouristEmbassy("AQSH (USA)", "Toshkent", "🇺🇸", "+998781205450", "Moyqo'rg'on ko'chasi 3, Yunusobod"),
        TouristEmbassy("Germaniya (Germany)", "Toshkent", "🇩🇪", "+998781208440", "Sharaf Rashidov ko'chasi 15"),
        TouristEmbassy("Fransiya (France)", "Toshkent", "🇫🇷", "+998712335382", "Istiqlol ko'chasi 25"),
        TouristEmbassy("Buyuk Britaniya (UK)", "Toshkent", "🇬🇧", "+998781201500", "Gulyamov ko'chasi 67"),
        TouristEmbassy("Rossiya (Russia)", "Toshkent", "🇷🇺", "+998781203504", "Nukus ko'chasi 83"),
        TouristEmbassy("Turkiya (Turkey)", "Toshkent", "🇹🇷", "+998711130300", "G'ulomov ko'chasi 87")
    )
}
