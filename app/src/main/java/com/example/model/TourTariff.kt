package com.example.model

import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

enum class PaymentMethod(val title: String, val icon: String, val desc: String) {
    CARD("Karta orqali to'lov", "💳", "Uzcard, Humo, Visa, Mastercard"),
    CASH("Naqd pul to'lovi", "💵", "Xodimga yoki yetib kelganda to'lash")
}

data class TariffPlace(
    val name: String,
    val description: String,
    val icon: String,
    val highlights: List<String>
)

data class ExtraExpense(
    val title: String,
    val category: String,
    val amountUsd: Double,
    val date: String,
    val receiptNumber: String,
    val isIncludedInTariff: Boolean = true
)

data class TourTariff(
    val id: String,
    val tariffNumber: Int,
    val name: String,
    val titleBadge: String,
    val priceUsd: Double,
    val durationDays: Int,
    val description: String,
    val coverEmoji: String,
    val hotelIncludedDesc: String,
    val foodIncludedDesc: String,
    val transportIncludedDesc: String,
    val tourGuideIncludedDesc: String,
    val placesToVisit: List<TariffPlace>,
    val includedFeatures: List<String>,
    val extraExpensesEstimate: List<ExtraExpense>,
    val isPopular: Boolean = false,
    val isVip: Boolean = false
) {
    val priceUzs: Long
        get() = (priceUsd * 12600).toLong()

    fun getFormattedUsd(): String = "$${NumberFormat.getNumberInstance(Locale.US).format(priceUsd.toLong())}"
    fun getFormattedUzs(): String = "${NumberFormat.getNumberInstance(Locale.US).format(priceUzs)} UZS"
}

data class TariffBookingReceipt(
    val orderId: String = "UZ-${UUID.randomUUID().toString().take(8).uppercase()}",
    val tariff: TourTariff,
    val touristName: String,
    val touristPhone: String,
    val startDate: String,
    val guestsCount: Int,
    val paymentMethod: PaymentMethod,
    val cardNumberMasked: String? = null,
    val cardType: String? = null,
    val totalAmountUsd: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val paymentStatus: String = "TO'LANDI / TASDIQLANDI",
    val qrCodePayload: String = "UZ-TOUR-PAY-${UUID.randomUUID().toString().take(6).uppercase()}",
    val extraExpenses: List<ExtraExpense> = emptyList(),
    val note: String = "Samarqand & O'zbekiston bo'ylab to'liq kafolatlangan xizmat"
)

object SampleTourTariffs {
    val items: List<TourTariff> = listOf(
        // 1-TARIF: Samarqand Klassik Tur (2 kun)
        TourTariff(
            id = "tariff_1_samarkand_2days",
            tariffNumber = 1,
            name = "1-Tarif: Samarqand Klassik Tur (2 Kun)",
            titleBadge = "ENG MASHHUR VA SEVIMLI",
            priceUsd = 500.0,
            durationDays = 2,
            description = "Samarqandning eng go'zal 5 ta tarixiy va zamonaviy maskanini 2 kun davomida to'liq ziyorat qilish, 3 mahal milliy oshxona, qulay mehmonxona va shaxsiy taksi xizmati.",
            coverEmoji = "🕌",
            hotelIncludedDesc = "2 kechalik qulay 4-yulduzli mehmonxona (nonushtasi bilan)",
            foodIncludedDesc = "3 mahal milliy taomlar: mashhur Samarqand oshi, tandir kabob, somsa va choyxona mehmondorchiligi",
            transportIncludedDesc = "Shaxsiy qulay Taxi / Haydovchi (barcha yo'nalishlar, vokzal/aeroportdan kutib olish va kuzatish)",
            tourGuideIncludedDesc = "Samarqandlik tajribali sertifikatlangan shaxsiy gid",
            placesToVisit = listOf(
                TariffPlace(
                    name = "1. Registon Maydoni",
                    description = "Sharq durdonasi — Ulug'bek, Tillakori va Sherdor madrasalari ansambli",
                    icon = "🕌",
                    highlights = listOf("Tarixiy madrasalar", "Kechki chiroqlar shousi", "Tillakori oltin gumbazi")
                ),
                TariffPlace(
                    name = "2. Go'ri Amir Maqbarasi",
                    description = "Buyuk Sohibqiron Amir Temur va Temuriylar sulolasi muazzam maqbarasi",
                    icon = "👑",
                    highlights = listOf("Nefrit qabrtosh", "Moviy gumbazlar", "Temuriylar tarixi")
                ),
                TariffPlace(
                    name = "3. Shohi Zinda Majmuasi",
                    description = "Nafis moviy koshinlar, zinalar va Qusam ibn Abbos ziyoratgohi",
                    icon = "🏛️",
                    highlights = listOf("Muqaddas feruza koshinlar", "40 pog'onali zinalar", "Sharqona me'morchilik")
                ),
                TariffPlace(
                    name = "4. Islom Karimov Qabri & Maqbarasi",
                    description = "O'zbekistonning Birinchi Prezidenti Islom Karimov maqbarasi (Hazrati Xizr majmuasi)",
                    icon = "🌸",
                    highlights = listOf("Hazrati Xizr masjidi", "Go'zal shahar panoramasi", "Milliy marmar naqshlar")
                ),
                TariffPlace(
                    name = "5. Mirzo Ulug'bek Rasadxonasi & Silk Road Park",
                    description = "XV asr astronomik mo'jizasi hamda 'Boqiy Shahar' zamonaviy etno-parki",
                    icon = "🔭",
                    highlights = listOf("Sekstant asbobi", "Boqiy Shahar majmuasi", "Favvoralar va kechki sayr")
                )
            ),
            includedFeatures = listOf(
                "🏨 4-yulduzli qulay mehmonxona (2 kecha)",
                "🍲 3 mahal mazali milliy ovqatlanish (Samarqand oshi, kabob)",
                "🚕 Shaxsiy Taxi / Transfer (2 kun to'liq siz bilan)",
                "🎫 5 ta barcha tarixiy obidalarga kirish chiptalari",
                "🧾 Barcha xarajatlar cheklari ilovada ko'rinadi",
                "💳 Karta yoki Naqd to'lov imkoniyati"
            ),
            extraExpensesEstimate = listOf(
                ExtraExpense("Mehmonxona (2 kecha, nonushta bilan)", "Hotel", 160.0, "1-2 kun", "CHK-HTL-8841"),
                ExtraExpense("3 mahal milliy taomlar (Samarqand Oshi, Go'sht)", "Taom", 120.0, "1-2 kun", "CHK-REST-3920"),
                ExtraExpense("Shaxsiy Taxi & Transfer (2 kun cheklovsiz)", "Transport", 110.0, "1-2 kun", "CHK-TAXI-5512"),
                ExtraExpense("5 ta obidaga kirish chiptalari & Gid xizmati", "Chipta/Gid", 80.0, "1-2 kun", "CHK-TKT-1049"),
                ExtraExpense("Milliy suvenirlar va shirinliklar zaxirasi", "Qo'shimcha", 30.0, "2-kun", "CHK-EXTRA-9901")
            ),
            isPopular = true,
            isVip = false
        ),

        // 2-TARIF: VIP Ipak Yo'li Turi (7 kun)
        TourTariff(
            id = "tariff_2_vip_7days",
            tariffNumber = 2,
            name = "2-Tarif: 1500$ VIP Ipak Yo'li Turi (7 Kun)",
            titleBadge = "PREMIUM VIP TANLOV",
            priceUsd = 1500.0,
            durationDays = 7,
            description = "7 kunlik qirolona sayohat: 5-yulduzli VIP mehmonxonalar, eng elita restoranlarda 3 mahal taomlar, VIP shaxsiy avtomobil va Toshkent-Samarqand-Buxoro bo'ylab unutilmas tajriba.",
            coverEmoji = "💎",
            hotelIncludedDesc = "7 kechalik 5-yulduzli lyuks mehmonxonalar (Hilton, Silk Road Samarkand, Sahid Zarafshan)",
            foodIncludedDesc = "3 mahal A-la-carte va premium restoranlarda eng sara taomlar, shirinliklar, salqin ichimliklar",
            transportIncludedDesc = "VIP Shaxsiy Avtomobil (Kia K5 / Malibu 2 Premier 2024), tezyurar Afrosiyob VIP poyezd chiptalari",
            tourGuideIncludedDesc = "Shaxsiy VIP tarixchi va ko'p tilli professional gid",
            placesToVisit = listOf(
                TariffPlace(
                    name = "Toshkent: Tashkent City & Magic City",
                    description = "Zamonaviy poytaxt ko'rki, musiqiy favvoralar, Amir Temur xiyoboni",
                    icon = "🌆",
                    highlights = listOf("Tashkent City Mall", "Magic City", "Chorsu bozori")
                ),
                TariffPlace(
                    name = "Samarqand: Registon, Go'ri Amir, Boqiy Shahar",
                    description = "Temuriylar poytaxtining barcha 5 ta asosiy maskani va Silk Road Samarkand majmuasi",
                    icon = "🕌",
                    highlights = listOf("Registon maydoni", "Shohi Zinda", "Boqiy Shahar VIP")
                ),
                TariffPlace(
                    name = "Buxoro: Minorai Kalon, Ark Qal'asi, Labi Hovuz",
                    description = "Buxoroi Sharif qadimiy ko'chalari, karvonsaroylar va saroylar",
                    icon = "✨",
                    highlights = listOf("Ark qal'asi", "Poi Kalon", "Sitorai Mohi Xosa")
                ),
                TariffPlace(
                    name = "Amirsoy Tog' Kurorti (Qo'shimcha Relaks)",
                    description = "Chorvoq va Amirsoy tog'larida dor yo'li sayri va toza havo",
                    icon = "🏔️",
                    highlights = listOf("Amirsoy dor yo'li", "Tog' manzaralari", "Premium relaks")
                )
            ),
            includedFeatures = listOf(
                "🌟 7 kecha 5-yulduzli lyuks mehmonxonalar",
                "🍽️ 3 mahal premium VIP restoranlarda taomlanish",
                "🚘 Shaxsiy VIP avtopark & shaxsiy haydovchi (7 kun)",
                "🚅 Afrosiyob VIP tezyurar poyezd chiptalari",
                "👑 Shaxsiy VIP Gid va tarjimon",
                "🧾 Ilovada barcha cheklar va xarajatlar aniq hisobi",
                "💳 Karta (Visa/Master/Humo/Uzcard) yoki Naqd to'lov"
            ),
            extraExpensesEstimate = listOf(
                ExtraExpense("5-Yulduzli VIP Hotel (7 kecha, All-Inclusive)", "Hotel", 650.0, "1-7 kun", "CHK-VIP-HTL-771"),
                ExtraExpense("3 mahal VIP Restoran & A-la-carte taomlar", "Taom", 380.0, "1-7 kun", "CHK-VIP-REST-410"),
                ExtraExpense("Shaxsiy VIP Avtomobil + Afrosiyob VIP poyezd", "Transport", 320.0, "1-7 kun", "CHK-VIP-AUTO-992"),
                ExtraExpense("VIP Gid, Teatr va Barcha muzey chiptalari", "Chipta/Gid", 150.0, "1-7 kun", "CHK-VIP-TKT-301")
            ),
            isPopular = false,
            isVip = true
        ),

        // 3-TARIF: Butun O'zbekiston Bo'ylab Grand Tur (14 kun)
        TourTariff(
            id = "tariff_3_uzbekistan_grand_14days",
            tariffNumber = 3,
            name = "3-Tarif: 4500$ Butun O'zbekiston Bo'ylab Grand Tur",
            titleBadge = "ENG TO'LIQ VA MUKAMMAL GRAND TUR",
            priceUsd = 4500.0,
            durationDays = 14,
            description = "14 kun davomida Butun O'zbekiston bo'ylab (Toshkent, Samarqand, Buxoro, Xiva, Shahrisabz, Zomin, Qoraqalpog'iston): barcha samolyot/poyezd chiptalari, eng oliy darajadagi hotellar, xarajatlar cheklari va to'liq xizmat.",
            coverEmoji = "🇺🇿",
            hotelIncludedDesc = "14 kechalik eng nufuzli 5-yulduzli & milliy Butik mehmonxonalar",
            foodIncludedDesc = "3 mahal xalqaro va milliy gourmet taomlanish (har bir viloyatning eng mashhur tansiq taomlari)",
            transportIncludedDesc = "Shaxsiy biznes klass avtomobillar, ichki aviareyslar (Toshkent-Urganch-Nukus) va Afrosiyob VIP poyezdlar",
            tourGuideIncludedDesc = "Professional akademik tarixchi va shaxsiy 24/7 konsyerj xizmati",
            placesToVisit = listOf(
                TariffPlace(
                    name = "Toshkent & Amirsoy",
                    description = "Poytaxtning zamonaviy binolari, Amirsoy kurorti va Chorvoq suvlari",
                    icon = "🏔️",
                    highlights = listOf("Toshkent teleminorasi", "Amirsoy Resort", "Chorvoq")
                ),
                TariffPlace(
                    name = "Samarqand & Shahrisabz",
                    description = "Registon, Go'ri Amir, Shohi Zinda, Islom Karimov qabri, Oqsaroy majmuasi",
                    icon = "🕌",
                    highlights = listOf("Registon 5 ta maskan", "Oqsaroy", "Silk Road Samarkand")
                ),
                TariffPlace(
                    name = "Buxoro & Karvonsaroylar",
                    description = "Minorai Kalon, Ark qal'asi, Sitorai Mohi Xosa saroyi, qadimiy xammomlar",
                    icon = "✨",
                    highlights = listOf("Ark", "Poi Kalon", "Labi Hovuz")
                ),
                TariffPlace(
                    name = "Xiva & Ichan Qal'a",
                    description = "Ochiq osmon ostidagi muzey, Kalta Minor, Tosh Hovli, Juma masjidi",
                    icon = "🏰",
                    highlights = listOf("Ichan Qal'a", "Kalta Minor", "Xorazm lazzatlari")
                ),
                TariffPlace(
                    name = "Zomin & Mo'ynoq (Orol Dengizi)",
                    description = "O'zbekiston Shveytsariyasi tog'lari va Orol dengizi kemalar qabristoni",
                    icon = "🌊",
                    highlights = listOf("Zomin osma ko'prigi", "Orol dengizi", "Savitskiy muzeyi")
                )
            ),
            includedFeatures = listOf(
                "🇺🇿 14 kun davomida butun O'zbekiston bo'ylab to'liq tur",
                "🏨 5-yulduzli elita va milliy butik mehmonxonalar",
                "✈️ Ichki samolyot reyslari va Afrosiyob VIP poyezdlar",
                "🚘 Shaxsiy 24/7 biznes klass mashina va shaxsiy haydovchi",
                "🍲 3 mahal eng sara tansiq milliy va xalqaro taomlar",
                "🎫 Barcha muzeylar, qo'riqxonalar va tarixiy obidalarga kirish",
                "🧾 Har bir xarajat va to'lov ilovada cheklari bilan to'liq saqlanadi",
                "💳 Karta yoki Naqd pul bilan to'lash"
            ),
            extraExpensesEstimate = listOf(
                ExtraExpense("5-Yulduzli va Butik Mehmonxonalar (14 kecha)", "Hotel", 1850.0, "1-14 kun", "CHK-GRAND-HTL-1401"),
                ExtraExpense("3 mahal tansiq milliy va xalqaro taomlanish", "Taom", 1100.0, "1-14 kun", "CHK-GRAND-REST-552"),
                ExtraExpense("Ichki Aviareyslar + Afrosiyob VIP + Shaxsiy Mashina", "Transport", 950.0, "1-14 kun", "CHK-GRAND-TRNS-803"),
                ExtraExpense("Shaxsiy Akademik Gid, Ruxsatnomalar & Barcha chiptalar", "Chipta/Gid", 450.0, "1-14 kun", "CHK-GRAND-TKT-211"),
                ExtraExpense("Suvenirlar, xalq hunarmandchiligi va maxsus sovg'alar", "Qo'shimcha", 150.0, "14-kun", "CHK-GRAND-SUV-901")
            ),
            isPopular = false,
            isVip = true
        )
    )
}
