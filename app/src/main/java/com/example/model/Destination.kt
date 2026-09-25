package com.example.model

import com.example.R

data class Destination(
    val id: String,
    val title: String,
    val city: String,
    val subtitle: String,
    val description: String,
    val historyFact: String,
    val unescoYear: Int? = null,
    val rating: Float = 4.9f,
    val reviewCount: Int = 1240,
    val category: DestinationCategory,
    val imageRes: Int,
    val tag: String,
    val coordinates: Pair<Double, Double>, // Lat, Lon
    val distanceKmFromTashkent: Int,
    val bestTimeToVisit: String,
    val highlights: List<String>,
    val architecturalPeriod: String,
    val crowdLevel: CrowdLevel = CrowdLevel.MODERATE,
    val weatherTempC: Int = 26,
    val weatherCondition: String = "Sunny & Clear",
    val afrasiyobDuration: String = "2h 15m",
    val remoteImageUrl: String? = null,
    val remoteAudioUrl: String? = null,
    val remoteAudioScript: String? = null,
    val isFromSupabase: Boolean = false
) {
    fun getLocalizedTitle(lang: AppLanguage = AppLanguage.currentLanguage): String {
        return when (id) {
            "registan" -> when (lang) {
                AppLanguage.UZ -> "Registon Maydoni"
                AppLanguage.RU -> "Площадь Регистан"
                AppLanguage.TR -> "Registan Meydanı"
                AppLanguage.ZH -> "雷吉斯坦广场"
                AppLanguage.JA -> "レギスタン広場"
                AppLanguage.DE -> "Registan-Platz"
                AppLanguage.FR -> "Place du Régistan"
                AppLanguage.ES -> "Plaza de Registán"
                else -> title
            }
            "bukhara_kalyan" -> when (lang) {
                AppLanguage.UZ -> "Poyi Kalon Majmuasi"
                AppLanguage.RU -> "Комплекс Пои Калян"
                AppLanguage.TR -> "Poi Kalyan Külliyesi"
                AppLanguage.ZH -> "卡扬建筑群"
                AppLanguage.JA -> "ポイ・カリャン複合体"
                AppLanguage.DE -> "Poi-Kalyan-Komplex"
                AppLanguage.FR -> "Complexe Po-i-Kalyan"
                AppLanguage.ES -> "Complejo Poi Kalyan"
                else -> title
            }
            "khiva_ichan_kala" -> when (lang) {
                AppLanguage.UZ -> "Ichan Qal'a Qasri"
                AppLanguage.RU -> "Крепость Ичан-Кала"
                AppLanguage.TR -> "İçan Kale"
                AppLanguage.ZH -> "伊钦卡拉古城"
                AppLanguage.JA -> "イチャン・カラ城塞"
                AppLanguage.DE -> "Ichan-Kala-Festung"
                AppLanguage.FR -> "Citadelle d'Itchan Kala"
                AppLanguage.ES -> "Fortaleza de Ichan Kala"
                else -> title
            }
            "chimgan_mountains" -> when (lang) {
                AppLanguage.UZ -> "Chimyon va Zomin Tog'lari"
                AppLanguage.RU -> "Чимган и Зааминские Горы"
                AppLanguage.TR -> "Çimgan ve Zaamin Dağları"
                AppLanguage.ZH -> "齐姆甘与扎明山脉"
                AppLanguage.JA -> "チムガン＆ザーミン山脈"
                AppLanguage.DE -> "Tschimgan- & Zaamin-Gipfel"
                AppLanguage.FR -> "Pics de Tchimgan et Zaamin"
                AppLanguage.ES -> "Picos de Chimgan y Zaamin"
                else -> title
            }
            else -> title
        }
    }

    fun getLocalizedCity(lang: AppLanguage = AppLanguage.currentLanguage): String {
        return when (city.lowercase()) {
            "samarkand", "samarqand" -> when (lang) {
                AppLanguage.UZ -> "Samarqand"
                AppLanguage.RU -> "Самарканд"
                AppLanguage.TR -> "Semerkant"
                AppLanguage.ZH -> "撒马尔罕"
                AppLanguage.JA -> "サマルカンド"
                else -> "Samarkand"
            }
            "bukhara", "buxoro" -> when (lang) {
                AppLanguage.UZ -> "Buxoro"
                AppLanguage.RU -> "Бухара"
                AppLanguage.TR -> "Buhara"
                AppLanguage.ZH -> "布哈拉"
                AppLanguage.JA -> "ブハラ"
                else -> "Bukhara"
            }
            "khiva", "xiva" -> when (lang) {
                AppLanguage.UZ -> "Xiva"
                AppLanguage.RU -> "Хива"
                AppLanguage.TR -> "Hive"
                AppLanguage.ZH -> "希瓦"
                AppLanguage.JA -> "ヒヴァ"
                else -> "Khiva"
            }
            "tashkent", "toshkent", "tashkent region" -> when (lang) {
                AppLanguage.UZ -> "Toshkent"
                AppLanguage.RU -> "Ташкент"
                AppLanguage.TR -> "Taşkent"
                AppLanguage.ZH -> "塔什干"
                AppLanguage.JA -> "タシュケント"
                else -> "Tashkent"
            }
            else -> city
        }
    }

    fun getLocalizedSubtitle(lang: AppLanguage = AppLanguage.currentLanguage): String {
        return when (id) {
            "registan" -> when (lang) {
                AppLanguage.UZ -> "Temuriylar Uyg'onish Davrining Markazi"
                AppLanguage.RU -> "Сердце Тимуридского Ренессанса"
                AppLanguage.TR -> "Timur Rönesansı'nın Kalbi"
                AppLanguage.ZH -> "帖木儿文艺复兴的中心"
                AppLanguage.JA -> "ティムール朝ルネサンスの中心"
                AppLanguage.DE -> "Herz der Timuriden-Renaissance"
                AppLanguage.FR -> "Cœur de la Renaissance timouride"
                AppLanguage.ES -> "Corazón del Renacimiento Timúrida"
                else -> subtitle
            }
            "bukhara_kalyan" -> when (lang) {
                AppLanguage.UZ -> "Abadiyat Minorasi va Muqaddas Madrasa"
                AppLanguage.RU -> "Башня Вечности и Священное Медресе"
                AppLanguage.TR -> "Ebediyet Kulesi ve Kutsal Medrese"
                AppLanguage.ZH -> "永恒之塔与神圣伊斯兰学院"
                AppLanguage.JA -> "永遠の塔と神聖なマドラサ"
                else -> subtitle
            }
            "khiva_ichan_kala" -> when (lang) {
                AppLanguage.UZ -> "Ipak Yo'lining Ochiq Osmon Ostidagi Tirik Muzeyi"
                AppLanguage.RU -> "Живой музей под открытым небом Шелкового пути"
                AppLanguage.TR -> "İpek Yolu'nun Açık Hava Müzesi"
                AppLanguage.ZH -> "丝绸之路露天鲜活博物馆"
                AppLanguage.JA -> "シルクロードの野外生きた博物館"
                else -> subtitle
            }
            "chimgan_mountains" -> when (lang) {
                AppLanguage.UZ -> "O'zbekistonning Alp Tog'lari Go'zalligi"
                AppLanguage.RU -> "Альпийский рай Узбекистана"
                AppLanguage.TR -> "Özbekistan'ın Alp Cenneti"
                AppLanguage.ZH -> "乌兹别克斯坦的高山仙境"
                AppLanguage.JA -> "ウズベキスタンの高山天国"
                else -> subtitle
            }
            else -> subtitle
        }
    }
}

enum class DestinationCategory(val label: String) {
    ALL("All Gems"),
    UNESCO("UNESCO Heritage"),
    SACRED("Sacred Mosques"),
    SILK_ROAD("Silk Road Fortresses"),
    NATURE("Alpine & Lakes"),
    GASTRONOMY("Bazaars & Dining");

    fun getLocalizedLabel(lang: AppLanguage = AppLanguage.currentLanguage): String {
        return when (this) {
            ALL -> AppStrings.get("cat_all", lang)
            UNESCO -> AppStrings.get("cat_unesco", lang)
            SACRED -> AppStrings.get("cat_sacred", lang)
            SILK_ROAD -> AppStrings.get("cat_fortress", lang)
            NATURE -> AppStrings.get("cat_nature", lang)
            GASTRONOMY -> AppStrings.get("cat_dining", lang)
        }
    }
}

enum class CrowdLevel(val label: String, val colorHex: Long) {
    LOW("Quiet (Best Time)", 0xFF10B981),
    MODERATE("Moderate Breeze", 0xFFF59E0B),
    HIGH("Vibrant & Bustling", 0xFFEF4444);

    fun getLocalizedLabel(lang: AppLanguage = AppLanguage.currentLanguage): String {
        return when (this) {
            LOW -> AppStrings.get("crowd_low", lang)
            MODERATE -> AppStrings.get("crowd_moderate", lang)
            HIGH -> AppStrings.get("crowd_high", lang)
        }
    }
}

object SampleDestinations {
    val items = listOf(
        Destination(
            id = "registan",
            title = "Registan Square",
            city = "Samarkand",
            subtitle = "Heart of the Timurid Renaissance",
            description = "Registan is the legendary crown jewel of Samarkand, framed by three monumental madrasahs (Ulugh Beg, Sher-Dor, and Tilya-Kori) dazzling with azure lapis lazuli mosaics and golden domes.",
            historyFact = "Commissioned by Ulugh Beg and later reconstructed in the 17th century, the acoustic courtyard was designed so a whisper in one archway echoes 40 meters across the tiled piazza.",
            unescoYear = 2001,
            rating = 4.98f,
            reviewCount = 4820,
            category = DestinationCategory.UNESCO,
            imageRes = R.drawable.img_registan_1787819168326,
            tag = "Must Visit #1",
            coordinates = Pair(39.6547, 66.9758),
            distanceKmFromTashkent = 310,
            bestTimeToVisit = "Golden Hour (18:00 - 21:00)",
            highlights = listOf(
                "Tilya-Kori Gilded Ceiling",
                "Sher-Dor Tiger Mosaics",
                "Ulugh Beg Astronomy Courtyard",
                "Light & Sound Holographic Show"
            ),
            architecturalPeriod = "Timurid & Janid Eras (1417–1660)",
            crowdLevel = CrowdLevel.MODERATE,
            weatherTempC = 25,
            weatherCondition = "Clear Azure Sky",
            afrasiyobDuration = "2h 13m"
        ),
        Destination(
            id = "bukhara_kalyan",
            title = "Poi Kalyan Complex",
            city = "Bukhara",
            subtitle = "The Tower of Eternity & Sacred Madrasah",
            description = "Rising 45 meters above Bukhara's desert skyline, the Kalyan Minaret survived Genghis Khan's conquest due to its breathtaking ornamental brickwork and harmonious Islamic proportions.",
            historyFact = "Built in 1127 AD by Karakhanid ruler Arslan Khan, its 14 distinct geometric brick bands use no blue tiles, relying purely on natural shadows cast by raw terracotta.",
            unescoYear = 1993,
            rating = 4.95f,
            reviewCount = 3410,
            category = DestinationCategory.UNESCO,
            imageRes = R.drawable.img_bukhara_1787819199064,
            tag = "Historic Wonder",
            coordinates = Pair(39.7758, 64.4158),
            distanceKmFromTashkent = 570,
            bestTimeToVisit = "Sunrise (06:30 - 08:30)",
            highlights = listOf(
                "45m Kalyan Minaret",
                "Mir-i-Arab 100-Dome Madrasah",
                "Ancient Brick Arabesque Arches",
                "Spiced Tea Caravan Teahouses"
            ),
            architecturalPeriod = "Karakhanid & Shaybanid (1127–1536)",
            crowdLevel = CrowdLevel.LOW,
            weatherTempC = 27,
            weatherCondition = "Golden Sunshine",
            afrasiyobDuration = "3h 45m"
        ),
        Destination(
            id = "khiva_ichan_kala",
            title = "Ichan Kala Fortress",
            city = "Khiva",
            subtitle = "Living Open-Air Museum of the Silk Road",
            description = "Step through the fortress gates into an untouched 18th-century clay citadel with turquoise-tiled minarets, marble pillars, and labyrinthine Silk Road alleyways.",
            historyFact = "The Kalta Minor minaret was originally designed to be tall enough to see all the way to Bukhara (400km away), but construction stopped when the Khan died in battle in 1855.",
            unescoYear = 1990,
            rating = 4.92f,
            reviewCount = 2890,
            category = DestinationCategory.SILK_ROAD,
            imageRes = R.drawable.img_khiva_1787819215477,
            tag = "Living Legend",
            coordinates = Pair(41.3783, 60.3639),
            distanceKmFromTashkent = 990,
            bestTimeToVisit = "Sunset & Night Illumination",
            highlights = listOf(
                "Kalta Minor Turquoise Minaret",
                "Juma Mosque 218 Carved Pillars",
                "Tosh Hovli Stone Palace",
                "Sunset View from Watchtower"
            ),
            architecturalPeriod = "Khiva Khanate (17th–19th Century)",
            crowdLevel = CrowdLevel.MODERATE,
            weatherTempC = 28,
            weatherCondition = "Warm Desert Breeze",
            afrasiyobDuration = "Overnight Express (6h)"
        ),
        Destination(
            id = "chimgan_mountains",
            title = "Chimgan & Zaamin Peaks",
            city = "Tashkent Region",
            subtitle = "The Alpine Heaven of Uzbekistan",
            description = "Crisp mountain air, turquoise alpine lakes like Charvak, and dramatic snowy mountain ridges of the Western Tian Shan range offering paragliding, hiking, and pristine tranquility.",
            historyFact = "Part of the UNESCO Western Tian Shan biosphere, these mountain passes served as northern summer trade routes where caravans escaped the scorching desert heat.",
            unescoYear = 2016,
            rating = 4.88f,
            reviewCount = 1950,
            category = DestinationCategory.NATURE,
            imageRes = R.drawable.img_chimgan_mountains_1787819241162,
            tag = "Alpine Adventure",
            coordinates = Pair(41.5200, 70.0100),
            distanceKmFromTashkent = 85,
            bestTimeToVisit = "Spring & Summer Morning",
            highlights = listOf(
                "Charvak Turquoise Reservoir",
                "Amirsoy Scenic Cable Cars",
                "Gulkam Water Canyons",
                "Fresh Mountain Shashlik & Ayran"
            ),
            architecturalPeriod = "Western Tian Shan Biosphere",
            crowdLevel = CrowdLevel.LOW,
            weatherTempC = 20,
            weatherCondition = "Fresh Alpine Breeze",
            afrasiyobDuration = "1h 15m scenic drive"
        )
    )

    // Dynamic reactive destinations list merging local built-in monuments and Supabase cloud monuments
    var dynamicList = androidx.compose.runtime.mutableStateListOf<Destination>().apply {
        addAll(items)
    }

    fun updateWithRemoteMonuments(remoteList: List<com.example.network.SupabaseClient.RemoteMonument>) {
        val merged = mutableListOf<Destination>()
        // First keep built-ins
        merged.addAll(items)

        // Then add or override with Supabase items
        for (remote in remoteList) {
            val existingIndex = merged.indexOfFirst { it.id == remote.id }
            val cat = when (remote.category.uppercase()) {
                "UNESCO" -> DestinationCategory.UNESCO
                "SACRED" -> DestinationCategory.SACRED
                "SILK_ROAD", "FORTRESS" -> DestinationCategory.SILK_ROAD
                "NATURE" -> DestinationCategory.NATURE
                "GASTRONOMY" -> DestinationCategory.GASTRONOMY
                else -> DestinationCategory.UNESCO
            }
            val mappedDest = Destination(
                id = remote.id,
                title = remote.title,
                city = remote.city,
                subtitle = remote.subtitle.ifBlank { "Tarixiy Durdona (${remote.city})" },
                description = remote.description.ifBlank { "O'zbekistonning boy madaniy va me'moriy merosi." },
                historyFact = remote.historyFact.ifBlank { "${remote.title} ko'p asrlik boy tarixga ega." },
                unescoYear = remote.unescoYear,
                rating = remote.rating,
                reviewCount = remote.reviewCount,
                category = cat,
                imageRes = R.drawable.img_registan_1787819168326,
                tag = remote.tag,
                coordinates = Pair(remote.latitude, remote.longitude),
                distanceKmFromTashkent = 300,
                bestTimeToVisit = "Ertalabki va kechki soatlar",
                highlights = listOf("Tarixiy Me'morchilik", "Milliy Bezaklar", "Sayyohlik Marshruti"),
                architecturalPeriod = remote.period,
                remoteImageUrl = remote.imageUrl.ifBlank { null },
                remoteAudioUrl = remote.audioUrl.ifBlank { null },
                remoteAudioScript = remote.audioScriptUz.ifBlank { null },
                isFromSupabase = true
            )

            if (existingIndex >= 0) {
                merged[existingIndex] = mappedDest
            } else {
                merged.add(mappedDest)
            }
        }

        dynamicList.clear()
        dynamicList.addAll(merged)
    }
}

