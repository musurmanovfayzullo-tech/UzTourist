package com.example.model

data class TripPlan(
    val title: String,
    val durationDays: Int,
    val pace: String,
    val estimatedBudgetUsd: Int,
    val estimatedBudgetUzs: String,
    val themeTag: String,
    val summary: String,
    val days: List<DayItinerary>,
    val conciergeTips: List<String>
)

data class DayItinerary(
    val dayNumber: Int,
    val city: String,
    val title: String,
    val stops: List<ItineraryStop>,
    val transportHighlight: String,
    val culinaryHighlight: String
)

data class ItineraryStop(
    val time: String,
    val title: String,
    val location: String,
    val duration: String,
    val type: StopType,
    val note: String,
    val isKeyHighlight: Boolean = false
)

enum class StopType(val iconName: String) {
    MONUMENT("AccountBalance"),
    GASTRONOMY("Restaurant"),
    TRAIN("DirectionsTransit"),
    CRAFT("Brush"),
    REST("Hotel"),
    SUNSET("WbTwilight")
}

object PredefinedTripPlans {
    val defaultLuxury3Day = TripPlan(
        title = "Royal Silk Road Odyssey",
        durationDays = 3,
        pace = "Refined & Leisurely",
        estimatedBudgetUsd = 480,
        estimatedBudgetUzs = "6,100,000 UZS",
        themeTag = "Timurid Heritage & High-Speed Rail",
        summary = "An elite curated journey across Samarkand and Bukhara, seamlessly linked by the VIP Afrasiyob bullet train with private tea tastings, architectural historian access, and sunset photography spots.",
        days = listOf(
            DayItinerary(
                dayNumber = 1,
                city = "Samarkand",
                title = "Timurid Capital & Astronomical Marvels",
                transportHighlight = "Afrasiyob Express (VIP Carriage) • Tashkent to Samarkand (2h 13m)",
                culinaryHighlight = "Royal Samarkand Plov with devzira rice, tender beef, and yellow carrots at Caravan Oasis",
                stops = listOf(
                    ItineraryStop("08:00", "Afrasiyob High-Speed Departure", "Tashkent Central Station", "2h 13m", StopType.TRAIN, "Panoramic window seats with morning Uzbek green tea service", true),
                    ItineraryStop("10:45", "Registan Square VIP Walk", "Registan", "2h", StopType.MONUMENT, "Private courtyard tour of Tilya-Kori and gilded dome roof inspection", true),
                    ItineraryStop("13:00", "Samarkand Osh Tasting", "National Chaikhana", "1h 30m", StopType.GASTRONOMY, "Traditional layered plov paired with fresh pomegranate salad"),
                    ItineraryStop("15:00", "Shah-i-Zinda Turquoise Avenue", "Shah-i-Zinda", "2h", StopType.MONUMENT, "Majolica and mosaic corridor of royal mausoleums under afternoon sun"),
                    ItineraryStop("18:30", "Sunset at Ulugh Beg Observatory", "Chupan-Ata Hill", "1h 30m", StopType.SUNSET, "Golden hour photography looking over the turquoise domes of Samarkand", true)
                )
            ),
            DayItinerary(
                dayNumber = 2,
                city = "Bukhara",
                title = "Sacred Domes & Spiced Caravans",
                transportHighlight = "Afrasiyob Express • Samarkand to Bukhara (1h 30m)",
                culinaryHighlight = "Bukhara Barbecue Kebabs & Spiced Saffron Tea at Lyabi-Khauz pool pavilion",
                stops = listOf(
                    ItineraryStop("09:00", "Poi Kalyan & Tower of Eternity", "Bukhara Old Town", "2h", StopType.MONUMENT, "Marvel at the 1127 AD brickwork and 100-dome Mir-i-Arab madrasah", true),
                    ItineraryStop("11:30", "Ancient Trading Domes (Toqi)", "Toqi Sarrofon", "1h 30m", StopType.CRAFT, "Meet 5th generation copper engravers and Suzani needlework masters"),
                    ItineraryStop("13:30", "Lyabi-Khauz Oasis Lunch", "Lyabi-Khauz", "1h 30m", StopType.GASTRONOMY, "Dine beside ancient mulberry trees planted in 1477"),
                    ItineraryStop("16:00", "Ark of Bukhara Fortress", "Registan Bukhara", "2h", StopType.MONUMENT, "Massive earthen citadel with Throne Hall and royal coronation chambers"),
                    ItineraryStop("19:00", "Silk Road Hammam & Rooftop Tea", "Bozori Kord (16th c.)", "2h", StopType.REST, "Herbal steam bath and eucalyptus infusion under domed clay vaults", true)
                )
            ),
            DayItinerary(
                dayNumber = 3,
                city = "Khiva / Oasis",
                title = "Desert Citadel of 1,001 Nights",
                transportHighlight = "Express Rail • Bukhara to Khiva across Kyzylkum Desert",
                culinaryHighlight = "Khorezm Green Noodles (Shivit Oshi) & Fried Sazan Fish from Amu Darya",
                stops = listOf(
                    ItineraryStop("09:30", "Ichan Kala West Gate Entry", "Ichan Kala Citadel", "2h", StopType.MONUMENT, "Walk through massive double mudbrick gates into living Silk Road museum", true),
                    ItineraryStop("12:00", "Kalta Minor Minaret Inspection", "Kalta Minor", "1h", StopType.MONUMENT, "Analyze the turquoise ceramic tiles that wrap the entire massive circumference"),
                    ItineraryStop("14:00", "Juma Mosque Pillar Forest", "Juma Mosque", "1h 30m", StopType.CRAFT, "218 unique hand-carved elm-wood columns dating from the 10th to 18th century"),
                    ItineraryStop("17:30", "Watchtower Sunset Panorama", "Kunya-Ark Bastion", "2h", StopType.SUNSET, "360-degree desert sunset over mudbrick parapets and turquoise towers", true)
                )
            )
        ),
        conciergeTips = listOf(
            "Booking Afrasiyob high-speed train 14 days ahead guarantees VIP carriage seating with complimentary refreshments.",
            "Bargaining in Bukhara trading domes is a polite cultural artform—always start with a warm 'Assalomu Alaykum'.",
            "Keep small denomination UZS cash for artisan tea houses in Lyabi-Khauz.",
            "Golden Hour light in Samarkand illuminates Registan's azure tilework at an optimal 45-degree angle from 18:15."
        )
    )
}
