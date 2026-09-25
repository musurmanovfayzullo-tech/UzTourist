package com.example.model

/**
 * Data models for Booking Responsible Tour Guides, Escorts, and VIP Drivers
 * Turistlarni aylantirish uchun mas'ul xodimlarga buyurtma berish modellari
 */

enum class StaffRoleType(
    val titleUz: String,
    val subtitleUz: String,
    val iconName: String
) {
    HISTORIAN_GUIDE("Tarixchi Shaxsiy Gid", "Tarixiy obidalar, me'morchilik va chuqur ekskursiya", "guide"),
    VIP_DRIVER_ESCORT("VIP Haydovchi & Gid", "Premium avtomobil, qulay transfer va shahar sayohati", "driver"),
    FULL_CONCIERGE("To'liq Sayohat Hamrohi", "Aeroportdan uchib ketguncha barcha xizmatlar kuratori", "concierge"),
    PHOTO_GUIDE("Foto-Gid & Media Hamroh", "Tarixiy obidalarda professional fotosessiya va ekskursiya", "photo")
}

data class TourGuideStaff(
    val id: String,
    val fullName: String,
    val roleType: StaffRoleType,
    val roleBadge: String,
    val experienceYears: Int,
    val rating: Float,
    val completedToursCount: Int,
    val languages: List<String>,
    val operatingCities: List<String>,
    val specialties: List<String>,
    val vehicleInfo: String? = null,
    val pricePerHourUzs: Long,
    val pricePerDayUzs: Long,
    val phoneNumber: String,
    val telegramUsername: String,
    val isAvailableToday: Boolean = true,
    val avatarColorHex: Long = 0xFF0047AB,
    val bioUz: String,
    val verifiedBadge: String = "Litsenziyali Mas'ul Xodim"
)

data class TourBookingOrder(
    val orderId: String,
    val staffId: String,
    val staffName: String,
    val staffRole: StaffRoleType,
    val staffPhone: String,
    val touristName: String,
    val touristPhone: String,
    val selectedCity: String,
    val tourDate: String,
    val tourStartTime: String,
    val durationHours: Int,
    val touristCount: Int,
    val preferredLanguage: String,
    val pickupLocation: String,
    val includedExtras: List<String>,
    val specialRequests: String,
    val totalAmountUzs: Long,
    val status: BookingOrderStatus = BookingOrderStatus.CONFIRMED,
    val createdAtFormatted: String
)

enum class BookingOrderStatus(val labelUz: String, val colorHex: Long) {
    PENDING("Kutilmoqda", 0xFFFFA000),
    CONFIRMED("Xodim Biriktirildi & Tasdiqlandi", 0xFF00C853),
    ON_THE_WAY("Xodim Manzilga Yo'lda", 0xFF00B0FF),
    COMPLETED("Muvaffaqiyatli Yakunlandi", 0xFF7C4DFF),
    CANCELLED("Bekor Qilindi", 0xFFFF5252)
}

object SampleTourStaff {

    val staffList: List<TourGuideStaff> = listOf(
        TourGuideStaff(
            id = "staff_bobur_01",
            fullName = "Bobur Mirzayev",
            roleType = StaffRoleType.HISTORIAN_GUIDE,
            roleBadge = "OLIY TOIFALI TARIXCHI GID",
            experienceYears = 9,
            rating = 4.99f,
            completedToursCount = 480,
            languages = listOf("🇬🇧 Ingliz", "🇷🇺 Rus", "🇺🇿 O'zbek"),
            operatingCities = listOf("Samarqand", "Buxoro", "Shahrisabz"),
            specialties = listOf("Registon & Amir Temur davri", "Sharq me'morchiligi", "Afrosiyob arxeologiyasi", "Madaniy an'analar"),
            pricePerHourUzs = 120000,
            pricePerDayUzs = 850000,
            phoneNumber = "+998907771234",
            telegramUsername = "@bobur_guide_uz",
            isAvailableToday = true,
            avatarColorHex = 0xFF0047AB,
            bioUz = "O'zbekiston Turizm qo'mitasi litsenziyasiga ega oliy toifali tarixchi. Samarqand va Buxoro tarixini eng nozik tafsilotlarigacha qiziqarli hikoya qilib beradi."
        ),
        TourGuideStaff(
            id = "staff_sherzod_02",
            fullName = "Sherzod Rahimov",
            roleType = StaffRoleType.VIP_DRIVER_ESCORT,
            roleBadge = "VIP HAYDOVCHI & HAMROH",
            experienceYears = 12,
            rating = 4.97f,
            completedToursCount = 620,
            languages = listOf("🇷🇺 Rus", "🇬🇧 Ingliz", "🇺🇿 O'zbek", "🇹🇷 Turk"),
            operatingCities = listOf("Toshkent", "Samarqand", "Buxoro", "Zomin"),
            specialties = listOf("Aeroportda kutib olish", "Shahar bo'ylab VIP sayr", "Tog'li hududlar safari", "Eng sara restoranlar"),
            vehicleInfo = "Chevrolet Malibu 2 Premier 2024 (Konditsioner, Wi-Fi, Muzdek ichimliklar)",
            pricePerHourUzs = 150000,
            pricePerDayUzs = 1100000,
            phoneNumber = "+998918885678",
            telegramUsername = "@sherzod_vip_drive",
            isAvailableToday = true,
            avatarColorHex = 0xFF00897B,
            bioUz = "12 yillik professional haydash tajribasi. Xorijiy delegatsiyalar va VIP mehmonlar uchun qulay, xavfsiz va maroqli sayohatni ta'minlaydi."
        ),
        TourGuideStaff(
            id = "staff_nilufar_03",
            fullName = "Nilufar Karimova",
            roleType = StaffRoleType.HISTORIAN_GUIDE,
            roleBadge = "SAN'AT VA HUNARMANDCHILIK GIDI",
            experienceYears = 7,
            rating = 4.98f,
            completedToursCount = 390,
            languages = listOf("🇫🇷 Fransuz", "🇬🇧 Ingliz", "🇺🇿 O'zbek"),
            operatingCities = listOf("Buxoro", "Xiva", "Samarqand"),
            specialties = listOf("Ichan Qal'a sirlari", "Ipak gilamchilik", "Zardo'zlik va kulolchilik", "Xonliklar davri tarixi"),
            pricePerHourUzs = 130000,
            pricePerDayUzs = 900000,
            phoneNumber = "+998935559012",
            telegramUsername = "@nilufar_bukhara_guide",
            isAvailableToday = true,
            avatarColorHex = 0xFF7B1FA2,
            bioUz = "Buxoro va Xiva qadimiy obidalari bo'yicha mutaxassis. Fransuz va ingliz sayyohlari bilan ko'p yillik muvaffaqiyatli ish tajribasiga ega."
        ),
        TourGuideStaff(
            id = "staff_dilshod_04",
            fullName = "Dilshodbek Mansurov",
            roleType = StaffRoleType.FULL_CONCIERGE,
            roleBadge = "TO'LIQ SAYOHAT KURATORI",
            experienceYears = 14,
            rating = 5.0f,
            completedToursCount = 750,
            languages = listOf("🇬🇧 Ingliz", "🇩🇪 Nemis", "🇷🇺 Rus", "🇹🇷 Turk", "🇺🇿 O'zbek"),
            operatingCities = listOf("Barcha shaharlar", "Toshkent", "Samarqand", "Buxoro", "Xiva"),
            specialties = listOf("24/7 Shaxsiy Konsyerj", "Navbatsiz VIP chiptalar", "Eng yaxshi milliy choyxonalar", "Xavfsizlik va transfer"),
            vehicleInfo = "Mercedes Sprinter VIP / Minivan (Guruhlar uchun)",
            pricePerHourUzs = 200000,
            pricePerDayUzs = 1500000,
            phoneNumber = "+998901112233",
            telegramUsername = "@dilshod_concierge_uz",
            isAvailableToday = true,
            avatarColorHex = 0xFFC2185B,
            bioUz = "Butun O'zbekiston bo'ylab 1 kundan 10 kungacha bo'lgan turlarni to'liq tashkillashtiruvchi bosh kurator. Mehmonxona, transport, ovqatlanish va ekskursiyalarni 100% o'z zimmasiga oladi."
        ),
        TourGuideStaff(
            id = "staff_aziz_05",
            fullName = "Azizbek Qodirov",
            roleType = StaffRoleType.PHOTO_GUIDE,
            roleBadge = "FOTO-GID & MEDIA HAMROH",
            experienceYears = 6,
            rating = 4.95f,
            completedToursCount = 310,
            languages = listOf("🇬🇧 Ingliz", "🇷🇺 Rus", "🇺🇿 O'zbek"),
            operatingCities = listOf("Samarqand", "Buxoro", "Toshkent"),
            specialties = listOf("Sony Alpha 4K suratga olish", "Eng chiroyli Instagram nuqtalar", "Tarixiy kiyimlar ijarasi", "Kvadrokopter aerotushirish"),
            pricePerHourUzs = 140000,
            pricePerDayUzs = 950000,
            phoneNumber = "+998946663344",
            telegramUsername = "@aziz_photoguide",
            isAvailableToday = true,
            avatarColorHex = 0xFFE65100,
            bioUz = "Tarixiy obidalar oldida unutilmas professional fotosuratlar va roliklar tayyorlovchi g'oyat iqtidorli foto-gid."
        )
    )

    val initialActiveOrders: List<TourBookingOrder> = emptyList()
}
