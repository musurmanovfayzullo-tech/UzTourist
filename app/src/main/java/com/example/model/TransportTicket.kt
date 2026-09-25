package com.example.model

/**
 * Transport types for ticket booking: Flight (Avia) and Train (Poyezd).
 */
enum class TransportType(val titleUz: String, val iconEmoji: String) {
    FLIGHT("Aviabiletlar", "✈️"),
    TRAIN("Poyezd biletlar", "🚆")
}

/**
 * Model representing a scheduled flight or train service available for booking.
 */
data class TicketOffer(
    val id: String,
    val transportType: TransportType,
    val carrierName: String,
    val serviceCode: String, // e.g. HY-051, AFROSIYOB #762
    val originCity: String,
    val destinationCity: String,
    val originStationOrAirport: String,
    val destinationStationOrAirport: String,
    val departureTime: String,
    val arrivalTime: String,
    val duration: String,
    val travelClass: String,
    val priceUzs: Long,
    val availableSeats: Int,
    val vehicleModel: String,
    val amenities: List<String>,
    val rating: Double = 4.9,
    val isFastest: Boolean = false,
    val isBestPrice: Boolean = false
)

/**
 * Model representing a completed digital ticket booking order.
 */
data class TicketBookingOrder(
    val orderId: String,
    val ticketOffer: TicketOffer,
    val passengerFirstName: String,
    val passengerLastName: String,
    val docType: String,
    val docNumber: String,
    val passengerPhone: String,
    val passengerEmail: String,
    val passengerCount: Int,
    val seatNumber: String,
    val travelDate: String,
    val totalPriceUzs: Double,
    val paymentMethod: String,
    val bookingStatus: String = "Tasdiqlandi",
    val pnrCode: String,
    val qrCodePayload: String,
    val createdTimestamp: Long = System.currentTimeMillis()
)

/**
 * Pre-populated realistic routes for Uzbekistan Airways, Silk Avia, Qanot Sharq,
 * and O'zbekiston Temir Yo'llari (Afrosiyob, Sharq, Nasaf).
 */
object SampleTicketOffers {
    val cities = listOf(
        "Toshkent",
        "Samarqand",
        "Buxoro",
        "Urganch / Xiva",
        "Nukus",
        "Farg'ona",
        "Termiz",
        "Navoiy"
    )

    val flightOffers = listOf(
        TicketOffer(
            id = "avia_tas_skd_01",
            transportType = TransportType.FLIGHT,
            carrierName = "Uzbekistan Airways",
            serviceCode = "HY-051",
            originCity = "Toshkent",
            destinationCity = "Samarqand",
            originStationOrAirport = "Toshkent Janubiy (TAS Terminal 3)",
            destinationStationOrAirport = "Samarqand Xalqaro Aeroporti (SKD)",
            departureTime = "07:30",
            arrivalTime = "08:25",
            duration = "55 daqiqa",
            travelClass = "Ekonom",
            priceUzs = 295_000,
            availableSeats = 18,
            vehicleModel = "Airbus A320neo",
            amenities = listOf("Qo'l yuki 8 kg", "Yuk 23 kg", "Choy & Ichimlik", "Elektron chipta"),
            rating = 4.9,
            isFastest = true
        ),
        TicketOffer(
            id = "avia_tas_skd_02",
            transportType = TransportType.FLIGHT,
            carrierName = "Silk Avia",
            serviceCode = "US-115",
            originCity = "Toshkent",
            destinationCity = "Samarqand",
            originStationOrAirport = "Toshkent Janubiy (TAS)",
            destinationStationOrAirport = "Samarqand Aeroporti (SKD)",
            departureTime = "14:15",
            arrivalTime = "15:15",
            duration = "1 soat",
            travelClass = "Standart",
            priceUzs = 245_000,
            availableSeats = 12,
            vehicleModel = "ATR 72-600",
            amenities = listOf("Qo'l yuki 5 kg", "Tezkor registratsiya", "Konfort o'rindiq"),
            rating = 4.8,
            isBestPrice = true
        ),
        TicketOffer(
            id = "avia_tas_bhk_01",
            transportType = TransportType.FLIGHT,
            carrierName = "Uzbekistan Airways",
            serviceCode = "HY-023",
            originCity = "Toshkent",
            destinationCity = "Buxoro",
            originStationOrAirport = "Islom Karimov nomidagi Toshkent XA (TAS)",
            destinationStationOrAirport = "Buxoro Xalqaro Aeroporti (BHK)",
            departureTime = "09:00",
            arrivalTime = "10:10",
            duration = "1s 10d",
            travelClass = "Ekonom",
            priceUzs = 340_000,
            availableSeats = 24,
            vehicleModel = "Boeing 787-8 Dreamliner",
            amenities = listOf("Qo'l yuki 8 kg", "Yuk 23 kg", "Issiq yegulik", "Media ekran"),
            rating = 5.0,
            isFastest = true
        ),
        TicketOffer(
            id = "avia_tas_ugc_01",
            transportType = TransportType.FLIGHT,
            carrierName = "Qanot Sharq",
            serviceCode = "HH-207",
            originCity = "Toshkent",
            destinationCity = "Urganch / Xiva",
            originStationOrAirport = "Toshkent Xalqaro Aeroporti (TAS)",
            destinationStationOrAirport = "Urganch Xalqaro Aeroporti (UGC)",
            departureTime = "11:40",
            arrivalTime = "13:10",
            duration = "1s 30d",
            travelClass = "Ekonom",
            priceUzs = 420_000,
            availableSeats = 15,
            vehicleModel = "Airbus A321neo",
            amenities = listOf("Qo'l yuki 8 kg", "Yuk 20 kg", "Nonushta", "Konditsioner"),
            rating = 4.8,
            isBestPrice = true
        ),
        TicketOffer(
            id = "avia_tas_nku_01",
            transportType = TransportType.FLIGHT,
            carrierName = "Uzbekistan Airways",
            serviceCode = "HY-011",
            originCity = "Toshkent",
            destinationCity = "Nukus",
            originStationOrAirport = "Toshkent Xalqaro Aeroporti (TAS)",
            destinationStationOrAirport = "Nukus Aeroporti (NCU)",
            departureTime = "16:30",
            arrivalTime = "18:15",
            duration = "1s 45d",
            travelClass = "Ekonom",
            priceUzs = 460_000,
            availableSeats = 9,
            vehicleModel = "Airbus A320",
            amenities = listOf("Qo'l yuki 8 kg", "Yuk 23 kg", "Kofe va sharbat", "USB zaryad"),
            rating = 4.7
        ),
        TicketOffer(
            id = "avia_skd_tas_01",
            transportType = TransportType.FLIGHT,
            carrierName = "Uzbekistan Airways",
            serviceCode = "HY-052",
            originCity = "Samarqand",
            destinationCity = "Toshkent",
            originStationOrAirport = "Samarqand XA (SKD)",
            destinationStationOrAirport = "Toshkent XA (TAS)",
            departureTime = "19:40",
            arrivalTime = "20:35",
            duration = "55 daqiqa",
            travelClass = "Ekonom",
            priceUzs = 295_000,
            availableSeats = 22,
            vehicleModel = "Airbus A320neo",
            amenities = listOf("Qo'l yuki 8 kg", "Yuk 23 kg", "Choy & Kofe"),
            rating = 4.9,
            isFastest = true
        ),
        TicketOffer(
            id = "avia_bhk_tas_01",
            transportType = TransportType.FLIGHT,
            carrierName = "Silk Avia",
            serviceCode = "US-124",
            originCity = "Buxoro",
            destinationCity = "Toshkent",
            originStationOrAirport = "Buxoro XA (BHK)",
            destinationStationOrAirport = "Toshkent XA (TAS)",
            departureTime = "18:00",
            arrivalTime = "19:15",
            duration = "1s 15d",
            travelClass = "Standart",
            priceUzs = 320_000,
            availableSeats = 14,
            vehicleModel = "ATR 72-600",
            amenities = listOf("Qo'l yuki 5 kg", "Tezkor parvoz"),
            rating = 4.8
        )
    )

    val trainOffers = listOf(
        TicketOffer(
            id = "train_tas_skd_afro_01",
            transportType = TransportType.TRAIN,
            carrierName = "Afrosiyob Tezyurar Ekspress",
            serviceCode = "AFROSIYOB #762",
            originCity = "Toshkent",
            destinationCity = "Samarqand",
            originStationOrAirport = "Toshkent Markaziy (Shimoliy Vokzal)",
            destinationStationOrAirport = "Samarqand Vokzali",
            departureTime = "08:00",
            arrivalTime = "10:13",
            duration = "2s 13d (250 km/h)",
            travelClass = "Ekonom Klass",
            priceUzs = 185_000,
            availableSeats = 38,
            vehicleModel = "Talgo 250 (Ispaniya)",
            amenities = listOf("Konditsioner", "Rozetka 220V", "Bistro vagon", "Quloqchin & Audio"),
            rating = 5.0,
            isFastest = true
        ),
        TicketOffer(
            id = "train_tas_skd_afro_vip",
            transportType = TransportType.TRAIN,
            carrierName = "Afrosiyob Tezyurar Ekspress",
            serviceCode = "AFROSIYOB #762-VIP",
            originCity = "Toshkent",
            destinationCity = "Samarqand",
            originStationOrAirport = "Toshkent Markaziy Vokzali",
            destinationStationOrAirport = "Samarqand Vokzali",
            departureTime = "08:00",
            arrivalTime = "10:13",
            duration = "2s 13d (250 km/h)",
            travelClass = "VIP Lyuks",
            priceUzs = 360_000,
            availableSeats = 6,
            vehicleModel = "Talgo 250 VIP Salon",
            amenities = listOf("Charm keng o'rindiqlar", "Issiq nonushta & Kofe", "Shaxsiy xizmatchi", "Wi-Fi"),
            rating = 5.0
        ),
        TicketOffer(
            id = "train_tas_bhk_afro_01",
            transportType = TransportType.TRAIN,
            carrierName = "Afrosiyob Tezyurar Ekspress",
            serviceCode = "AFROSIYOB #764",
            originCity = "Toshkent",
            destinationCity = "Buxoro",
            originStationOrAirport = "Toshkent Markaziy Vokzali",
            destinationStationOrAirport = "Buxoro-1 (Kogon Vokzali)",
            departureTime = "08:30",
            arrivalTime = "12:28",
            duration = "3s 58d (250 km/h)",
            travelClass = "Ekonom Klass",
            priceUzs = 240_000,
            availableSeats = 28,
            vehicleModel = "Talgo 250 (Ispaniya)",
            amenities = listOf("Konditsioner", "Bistro vagon", "Elektron chipta", "Qulay o'rindiq"),
            rating = 4.9,
            isFastest = true
        ),
        TicketOffer(
            id = "train_tas_skd_sharq",
            transportType = TransportType.TRAIN,
            carrierName = "Sharq Tezyurar Poyezdi",
            serviceCode = "SHARQ #010",
            originCity = "Toshkent",
            destinationCity = "Samarqand",
            originStationOrAirport = "Toshkent Janubiy Vokzali",
            destinationStationOrAirport = "Samarqand Vokzali",
            departureTime = "09:15",
            arrivalTime = "12:35",
            duration = "3s 20d",
            travelClass = "1-Klass Kupe",
            priceUzs = 125_000,
            availableSeats = 45,
            vehicleModel = "Tezyurar Lokomotiv",
            amenities = listOf("Choyxona xizmati", "Stol & Rozetka", "Keng bagaj joyi"),
            rating = 4.7,
            isBestPrice = true
        ),
        TicketOffer(
            id = "train_tas_ugc_express",
            transportType = TransportType.TRAIN,
            carrierName = "O'zbekiston Temir Yo'llari",
            serviceCode = "KHIVA EXPRESS #056",
            originCity = "Toshkent",
            destinationCity = "Urganch / Xiva",
            originStationOrAirport = "Toshkent Janubiy Vokzali",
            destinationStationOrAirport = "Xiva Tarixiy Vokzali",
            departureTime = "21:00",
            arrivalTime = "10:45",
            duration = "13s 45d (Tungi reys)",
            travelClass = "Kupe Yotoqli",
            priceUzs = 285_000,
            availableSeats = 19,
            vehicleModel = "Modern Yotoq Vagoni",
            amenities = listOf("Yotoq to'plami (ko'rpa-yostiq)", "Choy & Qandolat", "Konditsioner", "Dush xonasi"),
            rating = 4.8
        ),
        TicketOffer(
            id = "train_skd_tas_afro",
            transportType = TransportType.TRAIN,
            carrierName = "Afrosiyob Tezyurar Ekspress",
            serviceCode = "AFROSIYOB #761",
            originCity = "Samarqand",
            destinationCity = "Toshkent",
            originStationOrAirport = "Samarqand Vokzali",
            destinationStationOrAirport = "Toshkent Markaziy Vokzali",
            departureTime = "17:30",
            arrivalTime = "19:43",
            duration = "2s 13d",
            travelClass = "Ekonom Klass",
            priceUzs = 185_000,
            availableSeats = 32,
            vehicleModel = "Talgo 250",
            amenities = listOf("Konditsioner", "Rozetka", "Bistro vagon"),
            rating = 5.0,
            isFastest = true
        )
    )

    fun getAllOffers(): List<TicketOffer> = flightOffers + trainOffers
}
