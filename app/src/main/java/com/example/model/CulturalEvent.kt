package com.example.model

data class CulturalEvent(
    val id: String,
    val title: String,
    val city: String,
    val location: String,
    val dateText: String,
    val timeText: String,
    val category: String,
    val attendeesCount: Int,
    val isLiveNow: Boolean,
    val description: String,
    val badge: String
)

object SampleCulturalEvents {
    val items = listOf(
        CulturalEvent(
            id = "sharq_taronalari",
            title = "Sharq Taronalari Music Gala",
            city = "Samarkand",
            location = "Registan Open Stage",
            dateText = "Tonight",
            timeText = "20:30 - 23:00",
            category = "Traditional Music",
            attendeesCount = 3800,
            isLiveNow = true,
            description = "UNESCO-partnered international ethno-music festival featuring master dutar, doira, and tanbur performers under illuminated Registan arches.",
            badge = "LIVE NOW"
        ),
        CulturalEvent(
            id = "silk_spice_fest",
            title = "Silk & Spice Heritage Fair",
            city = "Bukhara",
            location = "Lyabi-Khauz Caravanserai",
            dateText = "Tomorrow",
            timeText = "10:00 - 19:00",
            category = "Crafts & Food",
            attendeesCount = 1920,
            isLiveNow = false,
            description = "Handmade Margilan Ikat silk textiles, saffron spice bazaars, master coppersmithing, and gold-thread embroidery demonstrations.",
            badge = "FEATURED"
        ),
        CulturalEvent(
            id = "plov_culinary_slam",
            title = "Osh-Plov Master Grand Prix",
            city = "Tashkent",
            location = "Central Asian Plov Center",
            dateText = "Saturday",
            timeText = "12:00 - 15:30",
            category = "Gastronomy",
            attendeesCount = 2450,
            isLiveNow = false,
            description = "Tasting competition among 12 regional Osh masters from Fergana, Samarkand, and Khorezm cooking in 3-meter cast iron kazans.",
            badge = "TASTING"
        )
    )
}
