package com.example.model

data class CurrencyRate(
    val code: String,
    val name: String,
    val flag: String,
    val rateToUzs: Double, // 1 Unit = X UZS
    val symbol: String
)

data class ExpenseCategory(
    val id: String,
    val title: String,
    val iconName: String,
    val allocatedUzs: Double,
    val spentUzs: Double
)

data class ExpenseItem(
    val id: String,
    val title: String,
    val categoryId: String,
    val amountUzs: Double,
    val timeAgo: String,
    val city: String
)

object CurrencyBudgetRepository {
    val supportedCurrencies = listOf(
        CurrencyRate("USD", "AQSH Dollari", "🇺🇸", 12850.0, "$"),
        CurrencyRate("EUR", "Yevro", "🇪🇺", 13950.0, "€"),
        CurrencyRate("RUB", "Rossiya Rubli", "🇷🇺", 138.5, "₽"),
        CurrencyRate("CNY", "Xitoy Yuani", "🇨🇳", 1780.0, "¥"),
        CurrencyRate("GBP", "Angliya Funt Sterlingi", "🇬🇧", 16400.0, "£"),
        CurrencyRate("TRY", "Turk Lirasi", "🇹🇷", 380.0, "₺"),
        CurrencyRate("JPY", "Yaponiya Iyenasi", "🇯🇵", 85.5, "¥"),
        CurrencyRate("KZT", "Qozoq Tengesi", "🇰🇿", 27.5, "₸"),
        CurrencyRate("UZS", "O'zbek So'mi", "🇺🇿", 1.0, "so'm")
    )

    val sampleCategories = listOf(
        ExpenseCategory("cat_transport", "Shaxsiy Taxi & Poezd", "taxi", 1500000.0, 480000.0),
        ExpenseCategory("cat_dining", "Milliy Taomlar & Choy", "restaurant", 2000000.0, 850000.0),
        ExpenseCategory("cat_hotel", "Karvonsaroy & Mehmonxona", "hotel", 4500000.0, 2600000.0),
        ExpenseCategory("cat_tickets", "Muzey & Obidalar", "ticket", 800000.0, 320000.0),
        ExpenseCategory("cat_souvenirs", "Hunarmandchilik & Bozor", "shopping", 1200000.0, 450000.0)
    )

    val sampleRecentExpenses = listOf(
        ExpenseItem("exp_1", "VIP Kia K5 Transfer (Aeroport ➔ Mehmonxona)", "cat_transport", 120000.0, "Bugun, 09:30", "Samarqand"),
        ExpenseItem("exp_2", "Muborak Samarqand Oshi & Qaymoq", "cat_dining", 95000.0, "Bugun, 13:00", "Samarqand"),
        ExpenseItem("exp_3", "Registon Maydoni Kirish Chiptasi & AR", "cat_tickets", 65000.0, "Kecha, 16:20", "Samarqand"),
        ExpenseItem("exp_4", "Rishton Lojuvard Lagani (Esdalik)", "cat_souvenirs", 180000.0, "Kecha, 18:40", "Samarqand"),
        ExpenseItem("exp_5", "Kalyan Heritage Caravanserai Xonasi", "cat_hotel", 550000.0, "2 kun oldin", "Buxoro")
    )

    val tippingGuides = listOf(
        "🍽️ Restoran va Choyxonalar" to "Odatda hisob-kitobga 10-15% xizmat haqi qo'shiladi. Alohida xizmat yoqsa, 10,000 - 30,000 UZS qoldirish samimiy mehmondo'stlik belgisi hisoblanadi.",
        "🚖 Shaxsiy Taxi Haydovchisi" to "Shaharlararo yoki shahar ichi xizmatda yuklarga yordam bersa, 10,000 - 20,000 UZS yoki hisobni yaxlitlash tavsiya etiladi.",
        "🏨 Mehmonxona Xizmatchisi" to "Chamadonlarni xonaga olib kirib bergan porterga 10,000 - 15,000 UZS.",
        "🏺 Mahalliy Tarixchi Gid" to "Katta qiziqish bilan 2-3 soatlik ekskursiya o'tkazib bergan gidga 50,000 - 100,000 UZS minnatdorchilik bildiriladi."
    )
}
