package com.example.util

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Data Model for Tourist Digital Wallet (Silk Road Pay / Sayyoh Elektron Hamyoni)
 */
data class TouristWallet(
    val walletId: String,
    val touristId: String,
    val touristName: String,
    val touristEmail: String,
    val touristPhone: String,
    val cardNumber: String, // e.g. "8600 7842 9012 3456"
    val cardExpiry: String, // "12/28"
    val cardCvv: String,    // "382"
    val balanceUzs: Double,
    val balanceUsd: Double,
    val balanceEur: Double,
    val dailyLimitUzs: Double = 5_000_000.0,
    val todaySpentUzs: Double = 0.0,
    val isNfcActive: Boolean = true,
    val isCardFrozen: Boolean = false,
    val createdDate: String = "2026-09-01"
)

/**
 * Data Model for Wallet Transactions (Kirim, Chiqim, Chipta, Restoran, Gid, Transfer)
 */
data class WalletTransaction(
    val id: String,
    val title: String,
    val category: String, // "Chipta", "Ovqatlanish", "Gid", "Transport", "Kirim", "Xarid", "Konvertatsiya"
    val amountUzs: Double, // Musbat = Kirim, Manfiy = Chiqim
    val amountUsd: Double,
    val dateFormatted: String,
    val merchant: String,
    val status: String = "Muvaffaqiyatli",
    val referenceId: String,
    val note: String = "",
    val guideNumber: String? = null
)

/**
 * Global Singleton for Tourist Electronic Wallet management, local persistence,
 * instant conversions, and dispatcher synchronization.
 */
object TouristWalletManager {
    private const val PREFS_NAME = "uz_tourist_digital_wallet_prefs_v1"
    private const val KEY_WALLETS_MAP = "key_wallets_json_map_v1"
    private const val KEY_TRANSACTIONS_PREFIX = "key_txns_prefix_v1_"

    const val RATE_USD_TO_UZS = 12850.0
    const val RATE_EUR_TO_UZS = 13950.0

    var walletUpdateTrigger by mutableStateOf(0L)
        private set

    var transactionsUpdateTrigger by mutableStateOf(0L)
        private set

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun formatUzs(amount: Double): String {
        val formatter = DecimalFormat("#,###")
        return "${formatter.format(amount.toLong()).replace(",", " ")} so'm"
    }

    fun formatUsd(amount: Double): String {
        return "$%.2f".format(Locale.US, amount)
    }

    fun formatEur(amount: Double): String {
        return "€%.2f".format(Locale.US, amount)
    }

    /**
     * Retrieves or creates an electronic wallet for the given tourist.
     */
    fun getOrCreateWallet(context: Context, userId: String, touristName: String = "", email: String = "", phone: String = ""): TouristWallet {
        val prefs = getPrefs(context)
        val allWalletsJson = prefs.getString(KEY_WALLETS_MAP, "{}") ?: "{}"
        val walletsObj = JSONObject(allWalletsJson)

        if (walletsObj.has(userId)) {
            val obj = walletsObj.getJSONObject(userId)
            return parseWalletFromJson(obj)
        }

        // Generate brand new Silk Road VIP electronic wallet for this tourist
        val cleanName = if (touristName.isNotBlank()) touristName else "Sayyoh (VIP Mehmon)"
        val randomSuffix = (1000..9999).random()
        val walletId = "UZW-${userId.takeLast(4)}-$randomSuffix"
        val cardNumber = "8600 " + (1000..9999).random() + " " + (1000..9999).random() + " " + (1000..9999).random()
        val initialUzs = 2_500_000.0 // 2.5 million UZS initial balance
        val initialUsd = (initialUzs / RATE_USD_TO_UZS * 100).toLong() / 100.0
        val initialEur = (initialUzs / RATE_EUR_TO_UZS * 100).toLong() / 100.0
        val todayStr = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())

        val newWallet = TouristWallet(
            walletId = walletId,
            touristId = userId,
            touristName = cleanName,
            touristEmail = email,
            touristPhone = phone,
            cardNumber = cardNumber,
            cardExpiry = "12/28",
            cardCvv = (100..999).random().toString(),
            balanceUzs = initialUzs,
            balanceUsd = initialUsd,
            balanceEur = initialEur,
            dailyLimitUzs = 5_000_000.0,
            todaySpentUzs = 450_000.0,
            isNfcActive = true,
            isCardFrozen = false,
            createdDate = todayStr
        )

        saveWallet(context, newWallet)
        initializeDefaultTransactions(context, userId)

        return newWallet
    }

    fun saveWallet(context: Context, wallet: TouristWallet) {
        val prefs = getPrefs(context)
        val allWalletsJson = prefs.getString(KEY_WALLETS_MAP, "{}") ?: "{}"
        val walletsObj = JSONObject(allWalletsJson)

        val obj = JSONObject().apply {
            put("walletId", wallet.walletId)
            put("touristId", wallet.touristId)
            put("touristName", wallet.touristName)
            put("touristEmail", wallet.touristEmail)
            put("touristPhone", wallet.touristPhone)
            put("cardNumber", wallet.cardNumber)
            put("cardExpiry", wallet.cardExpiry)
            put("cardCvv", wallet.cardCvv)
            put("balanceUzs", wallet.balanceUzs)
            put("balanceUsd", wallet.balanceUsd)
            put("balanceEur", wallet.balanceEur)
            put("dailyLimitUzs", wallet.dailyLimitUzs)
            put("todaySpentUzs", wallet.todaySpentUzs)
            put("isNfcActive", wallet.isNfcActive)
            put("isCardFrozen", wallet.isCardFrozen)
            put("createdDate", wallet.createdDate)
        }

        walletsObj.put(wallet.touristId, obj)
        prefs.edit().putString(KEY_WALLETS_MAP, walletsObj.toString()).apply()
        walletUpdateTrigger = System.currentTimeMillis()
    }

    private fun parseWalletFromJson(obj: JSONObject): TouristWallet {
        return TouristWallet(
            walletId = obj.optString("walletId", "UZW-7842-8821"),
            touristId = obj.optString("touristId", "UZ-TOUR-7842"),
            touristName = obj.optString("touristName", "Sayyoh"),
            touristEmail = obj.optString("touristEmail", ""),
            touristPhone = obj.optString("touristPhone", ""),
            cardNumber = obj.optString("cardNumber", "8600 5204 8819 4591"),
            cardExpiry = obj.optString("cardExpiry", "12/28"),
            cardCvv = obj.optString("cardCvv", "382"),
            balanceUzs = obj.optDouble("balanceUzs", 2_500_000.0),
            balanceUsd = obj.optDouble("balanceUsd", 194.55),
            balanceEur = obj.optDouble("balanceEur", 179.21),
            dailyLimitUzs = obj.optDouble("dailyLimitUzs", 5_000_000.0),
            todaySpentUzs = obj.optDouble("todaySpentUzs", 0.0),
            isNfcActive = obj.optBoolean("isNfcActive", true),
            isCardFrozen = obj.optBoolean("isCardFrozen", false),
            createdDate = obj.optString("createdDate", "2026-09-01")
        )
    }

    /**
     * Returns list of transactions for this tourist.
     */
    fun getTransactions(context: Context, userId: String): List<WalletTransaction> {
        val prefs = getPrefs(context)
        val jsonArrayStr = prefs.getString(KEY_TRANSACTIONS_PREFIX + userId, null) ?: return emptyList()
        val list = mutableListOf<WalletTransaction>()
        try {
            val array = JSONArray(jsonArrayStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    WalletTransaction(
                        id = obj.optString("id", "TXN-${i}"),
                        title = obj.optString("title", ""),
                        category = obj.optString("category", "Xarid"),
                        amountUzs = obj.optDouble("amountUzs", 0.0),
                        amountUsd = obj.optDouble("amountUsd", 0.0),
                        dateFormatted = obj.optString("dateFormatted", ""),
                        merchant = obj.optString("merchant", ""),
                        status = obj.optString("status", "Muvaffaqiyatli"),
                        referenceId = obj.optString("referenceId", ""),
                        note = obj.optString("note", ""),
                        guideNumber = if (obj.has("guideNumber")) obj.optString("guideNumber") else null
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun saveTransactions(context: Context, userId: String, transactions: List<WalletTransaction>) {
        val array = JSONArray()
        for (txn in transactions) {
            val obj = JSONObject().apply {
                put("id", txn.id)
                put("title", txn.title)
                put("category", txn.category)
                put("amountUzs", txn.amountUzs)
                put("amountUsd", txn.amountUsd)
                put("dateFormatted", txn.dateFormatted)
                put("merchant", txn.merchant)
                put("status", txn.status)
                put("referenceId", txn.referenceId)
                put("note", txn.note)
                if (txn.guideNumber != null) put("guideNumber", txn.guideNumber)
            }
            array.put(obj)
        }
        getPrefs(context).edit().putString(KEY_TRANSACTIONS_PREFIX + userId, array.toString()).apply()
        transactionsUpdateTrigger = System.currentTimeMillis()
    }

    private fun initializeDefaultTransactions(context: Context, userId: String) {
        val defaults = listOf(
            WalletTransaction(
                id = "txn_welcome_01",
                title = "🎉 Xush kelibsiz! Silk Road Pay Sayyohlik Bonusi",
                category = "Kirim",
                amountUzs = 500_000.0,
                amountUsd = 38.91,
                dateFormatted = "Bugun, 09:00",
                merchant = "O'zbekiston Turizm Qo'mitasi & Silk Road Pay",
                status = "Muvaffaqiyatli",
                referenceId = "SR-BONUS-771"
            ),
            WalletTransaction(
                id = "txn_topup_02",
                title = "💳 Hisob to'ldirildi (Visa Card •••• 4921)",
                category = "Kirim",
                amountUzs = 2_000_000.0,
                amountUsd = 155.64,
                dateFormatted = "Bugun, 10:15",
                merchant = "Universal Bank Terminali",
                status = "Muvaffaqiyatli",
                referenceId = "TXN-TOPUP-948"
            ),
            WalletTransaction(
                id = "txn_registan_03",
                title = "🏛️ Registon Maydoni Kirish Chiptasi & AR Audio",
                category = "Chipta",
                amountUzs = -65_000.0,
                amountUsd = -5.05,
                dateFormatted = "Bugun, 11:30",
                merchant = "Registon Davlat Muzeyi",
                status = "Muvaffaqiyatli",
                referenceId = "TKT-REG-8821"
            ),
            WalletTransaction(
                id = "txn_osh_04",
                title = "🍲 Samarqand Milliy Osh Markazi (Osh & Qaymoq)",
                category = "Ovqatlanish",
                amountUzs = -95_000.0,
                amountUsd = -7.39,
                dateFormatted = "Bugun, 13:20",
                merchant = "Osh Markazi (Registon)",
                status = "Muvaffaqiyatli",
                referenceId = "REST-OSH-451"
            ),
            WalletTransaction(
                id = "txn_guide_tip_05",
                title = "🎁 Gidga minnatdorchilik choychaqasi & bonus",
                category = "Gid",
                amountUzs = -100_000.0,
                amountUsd = -7.78,
                dateFormatted = "Bugun, 15:45",
                merchant = "Sertifikatlangan Gid: Alisher Rustamov (G-8821)",
                status = "Muvaffaqiyatli",
                referenceId = "TIP-GUIDE-8821",
                note = "Ajoyib tarixiy hikoyalar uchun rahmat!",
                guideNumber = "G-8821"
            )
        )
        saveTransactions(context, userId, defaults)
    }

    /**
     * Tops up tourist's wallet.
     */
    fun topUpWallet(
        context: Context,
        userId: String,
        amountUzs: Double,
        method: String, // "Visa / Mastercard", "UzCard / Humo", "Google Pay", "Terminal"
        onResult: (success: Boolean, message: String) -> Unit
    ) {
        val currentWallet = getOrCreateWallet(context, userId)
        if (currentWallet.isCardFrozen) {
            onResult(false, "Karta vaqtincha muzlatilgan! Avval muzlatishni bekor qiling.")
            return
        }

        val newBalanceUzs = currentWallet.balanceUzs + amountUzs
        val newBalanceUsd = (newBalanceUzs / RATE_USD_TO_UZS * 100).toLong() / 100.0
        val newBalanceEur = (newBalanceUzs / RATE_EUR_TO_UZS * 100).toLong() / 100.0

        val updatedWallet = currentWallet.copy(
            balanceUzs = newBalanceUzs,
            balanceUsd = newBalanceUsd,
            balanceEur = newBalanceEur
        )
        saveWallet(context, updatedWallet)

        val timeFormatted = SimpleDateFormat("Bugun, HH:mm", Locale.getDefault()).format(Date())
        val txnId = "TXN-TOP-" + UUID.randomUUID().toString().take(8).uppercase()

        val newTxn = WalletTransaction(
            id = txnId,
            title = "💳 Hisob to'ldirildi ($method)",
            category = "Kirim",
            amountUzs = amountUzs,
            amountUsd = (amountUzs / RATE_USD_TO_UZS * 100).toLong() / 100.0,
            dateFormatted = timeFormatted,
            merchant = "Silk Road Pay ($method)",
            status = "Muvaffaqiyatli",
            referenceId = txnId
        )

        val currentTxns = getTransactions(context, userId).toMutableList()
        currentTxns.add(0, newTxn)
        saveTransactions(context, userId, currentTxns)

        // Send Telegram Notification
        TelegramBotManager.sendTouristWalletEventViaTelegramApi(
            context = context,
            eventType = "TOP_UP",
            touristName = currentWallet.touristName,
            touristId = currentWallet.touristId,
            walletId = currentWallet.walletId,
            cardNumber = currentWallet.cardNumber,
            amountFormatted = "+${formatUzs(amountUzs)} (~${formatUsd(amountUzs / RATE_USD_TO_UZS)})",
            merchantOrTarget = "To'ldirish usuli: $method",
            newBalanceFormatted = "${formatUzs(newBalanceUzs)} (${formatUsd(newBalanceUsd)})",
            txnId = txnId
        )

        onResult(true, "Hamyon muvaffaqiyatli ${formatUzs(amountUzs)} ga to'ldirildi! ✓")
    }

    /**
     * Deducts payment from wallet for monument ticket, dining, souvenir, taxi, etc.
     */
    fun makePayment(
        context: Context,
        userId: String,
        amountUzs: Double,
        title: String,
        category: String,
        merchant: String,
        note: String = "",
        onResult: (success: Boolean, message: String) -> Unit
    ) {
        val currentWallet = getOrCreateWallet(context, userId)
        if (currentWallet.isCardFrozen) {
            onResult(false, "Karta muzlatilgan! Xavfsizlik tufayli to'lov rad etildi.")
            return
        }

        if (currentWallet.balanceUzs < amountUzs) {
            onResult(false, "Mablag' yetarli emas! Hamyon balansi: ${formatUzs(currentWallet.balanceUzs)}")
            return
        }

        if (currentWallet.todaySpentUzs + amountUzs > currentWallet.dailyLimitUzs) {
            onResult(false, "Kunlik xarajat limiti (${formatUzs(currentWallet.dailyLimitUzs)}) oshib ketadi!")
            return
        }

        val newBalanceUzs = currentWallet.balanceUzs - amountUzs
        val newBalanceUsd = (newBalanceUzs / RATE_USD_TO_UZS * 100).toLong() / 100.0
        val newBalanceEur = (newBalanceUzs / RATE_EUR_TO_UZS * 100).toLong() / 100.0

        val updatedWallet = currentWallet.copy(
            balanceUzs = newBalanceUzs,
            balanceUsd = newBalanceUsd,
            balanceEur = newBalanceEur,
            todaySpentUzs = currentWallet.todaySpentUzs + amountUzs
        )
        saveWallet(context, updatedWallet)

        val timeFormatted = SimpleDateFormat("Bugun, HH:mm", Locale.getDefault()).format(Date())
        val txnId = "TXN-PAY-" + UUID.randomUUID().toString().take(8).uppercase()

        val newTxn = WalletTransaction(
            id = txnId,
            title = title,
            category = category,
            amountUzs = -amountUzs,
            amountUsd = -(amountUzs / RATE_USD_TO_UZS * 100).toLong() / 100.0,
            dateFormatted = timeFormatted,
            merchant = merchant,
            status = "Muvaffaqiyatli",
            referenceId = txnId,
            note = note
        )

        val currentTxns = getTransactions(context, userId).toMutableList()
        currentTxns.add(0, newTxn)
        saveTransactions(context, userId, currentTxns)

        // Send Telegram Notification
        TelegramBotManager.sendTouristWalletEventViaTelegramApi(
            context = context,
            eventType = "PAYMENT",
            touristName = currentWallet.touristName,
            touristId = currentWallet.touristId,
            walletId = currentWallet.walletId,
            cardNumber = currentWallet.cardNumber,
            amountFormatted = "-${formatUzs(amountUzs)} (~${formatUsd(amountUzs / RATE_USD_TO_UZS)})",
            merchantOrTarget = "$title • $merchant ($category)",
            newBalanceFormatted = "${formatUzs(newBalanceUzs)} (${formatUsd(newBalanceUsd)})",
            txnId = txnId,
            note = note
        )

        onResult(true, "To'lov muvaffaqiyatli amalga oshirildi! Chek raqami: $txnId")
    }

    /**
     * Direct tip / bonus transfer to designated Tour Guide.
     */
    fun payGuideTip(
        context: Context,
        userId: String,
        guideNumber: String,
        guideName: String,
        amountUzs: Double,
        note: String,
        onResult: (success: Boolean, message: String) -> Unit
    ) {
        val currentWallet = getOrCreateWallet(context, userId)
        if (currentWallet.isCardFrozen) {
            onResult(false, "Karta muzlatilgan! Avval aktivlashtiring.")
            return
        }

        if (currentWallet.balanceUzs < amountUzs) {
            onResult(false, "Mablag' yetarli emas! Hamyon balansi: ${formatUzs(currentWallet.balanceUzs)}")
            return
        }

        val newBalanceUzs = currentWallet.balanceUzs - amountUzs
        val newBalanceUsd = (newBalanceUzs / RATE_USD_TO_UZS * 100).toLong() / 100.0
        val newBalanceEur = (newBalanceUzs / RATE_EUR_TO_UZS * 100).toLong() / 100.0

        val updatedWallet = currentWallet.copy(
            balanceUzs = newBalanceUzs,
            balanceUsd = newBalanceUsd,
            balanceEur = newBalanceEur,
            todaySpentUzs = currentWallet.todaySpentUzs + amountUzs
        )
        saveWallet(context, updatedWallet)

        val timeFormatted = SimpleDateFormat("Bugun, HH:mm", Locale.getDefault()).format(Date())
        val txnId = "TIP-G-" + UUID.randomUUID().toString().take(8).uppercase()

        val newTxn = WalletTransaction(
            id = txnId,
            title = "🎁 Gidga minnatdorchilik choychaqasi ($guideName)",
            category = "Gid",
            amountUzs = -amountUzs,
            amountUsd = -(amountUzs / RATE_USD_TO_UZS * 100).toLong() / 100.0,
            dateFormatted = timeFormatted,
            merchant = "Gid: $guideName (Kodi: $guideNumber)",
            status = "Muvaffaqiyatli",
            referenceId = txnId,
            note = note,
            guideNumber = guideNumber
        )

        val currentTxns = getTransactions(context, userId).toMutableList()
        currentTxns.add(0, newTxn)
        saveTransactions(context, userId, currentTxns)

        // Telegram notification
        TelegramBotManager.sendTouristWalletEventViaTelegramApi(
            context = context,
            eventType = "GUIDE_TIP",
            touristName = currentWallet.touristName,
            touristId = currentWallet.touristId,
            walletId = currentWallet.walletId,
            cardNumber = currentWallet.cardNumber,
            amountFormatted = "${formatUzs(amountUzs)} (~${formatUsd(amountUzs / RATE_USD_TO_UZS)})",
            merchantOrTarget = "Gid: $guideName (Kodi: $guideNumber)",
            newBalanceFormatted = "${formatUzs(newBalanceUzs)} (${formatUsd(newBalanceUsd)})",
            txnId = txnId,
            note = if (note.isNotBlank()) note else "Samimiy xizmat uchun choychaqa"
        )

        onResult(true, "Gidga ${formatUzs(amountUzs)} muvaffaqiyatli o'tkazildi! Telegramga yetkazildi ✓")
    }

    /**
     * Currency exchange between USD, EUR, and UZS at 0% commission.
     */
    fun exchangeCurrency(
        context: Context,
        userId: String,
        fromCurrency: String, // "USD", "EUR"
        toCurrency: String,   // "UZS"
        amount: Double,
        onResult: (success: Boolean, message: String) -> Unit
    ) {
        val currentWallet = getOrCreateWallet(context, userId)
        val rate = if (fromCurrency == "USD") RATE_USD_TO_UZS else RATE_EUR_TO_UZS
        val receivedUzs = amount * rate

        val newBalanceUzs = currentWallet.balanceUzs + receivedUzs
        val newBalanceUsd = (newBalanceUzs / RATE_USD_TO_UZS * 100).toLong() / 100.0
        val newBalanceEur = (newBalanceUzs / RATE_EUR_TO_UZS * 100).toLong() / 100.0

        val updatedWallet = currentWallet.copy(
            balanceUzs = newBalanceUzs,
            balanceUsd = newBalanceUsd,
            balanceEur = newBalanceEur
        )
        saveWallet(context, updatedWallet)

        val timeFormatted = SimpleDateFormat("Bugun, HH:mm", Locale.getDefault()).format(Date())
        val txnId = "EXCH-" + UUID.randomUUID().toString().take(8).uppercase()

        val newTxn = WalletTransaction(
            id = txnId,
            title = "🔄 Valyuta ayirboshlandi ($amount $fromCurrency ➔ ${formatUzs(receivedUzs)})",
            category = "Konvertatsiya",
            amountUzs = receivedUzs,
            amountUsd = amount,
            dateFormatted = timeFormatted,
            merchant = "Silk Road Instant FX (0% Komissiya)",
            status = "Muvaffaqiyatli",
            referenceId = txnId,
            note = "Kurs: 1 $fromCurrency = ${rate.toLong()} UZS"
        )

        val currentTxns = getTransactions(context, userId).toMutableList()
        currentTxns.add(0, newTxn)
        saveTransactions(context, userId, currentTxns)

        TelegramBotManager.sendTouristWalletEventViaTelegramApi(
            context = context,
            eventType = "EXCHANGE",
            touristName = currentWallet.touristName,
            touristId = currentWallet.touristId,
            walletId = currentWallet.walletId,
            cardNumber = currentWallet.cardNumber,
            amountFormatted = "$amount $fromCurrency ➔ ${formatUzs(receivedUzs)}",
            merchantOrTarget = "Markaziy Bank kursi: 1 $fromCurrency = ${rate.toLong()} UZS",
            newBalanceFormatted = "${formatUzs(newBalanceUzs)} (${formatUsd(newBalanceUsd)})",
            txnId = txnId
        )

        onResult(true, "Valyuta muvaffaqiyatli ayirboshlandi! Hamyoningizga ${formatUzs(receivedUzs)} qo'shildi.")
    }

    fun toggleNfc(context: Context, userId: String): Boolean {
        val currentWallet = getOrCreateWallet(context, userId)
        val newState = !currentWallet.isNfcActive
        val updated = currentWallet.copy(isNfcActive = newState)
        saveWallet(context, updated)
        return newState
    }

    fun toggleCardFreeze(context: Context, userId: String): Boolean {
        val currentWallet = getOrCreateWallet(context, userId)
        val newState = !currentWallet.isCardFrozen
        val updated = currentWallet.copy(isCardFrozen = newState)
        saveWallet(context, updated)
        return newState
    }

    fun updateDailyLimit(context: Context, userId: String, newLimitUzs: Double) {
        val currentWallet = getOrCreateWallet(context, userId)
        val updated = currentWallet.copy(dailyLimitUzs = newLimitUzs)
        saveWallet(context, updated)
    }

    /**
     * For Admin Dashboard: Returns list of all tourist wallets.
     */
    fun getAllWalletsForAdmin(context: Context): List<TouristWallet> {
        val prefs = getPrefs(context)
        val allWalletsJson = prefs.getString(KEY_WALLETS_MAP, "{}") ?: "{}"
        val list = mutableListOf<TouristWallet>()
        try {
            val walletsObj = JSONObject(allWalletsJson)
            val keys = walletsObj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                val obj = walletsObj.getJSONObject(k)
                list.add(parseWalletFromJson(obj))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // If empty, ensure at least the primary user's wallet is present
        if (list.isEmpty()) {
            val defaultUserWallet = getOrCreateWallet(context, "UZ-TOUR-7842", "John Doe (VIP)")
            list.add(defaultUserWallet)
        }
        return list
    }
}
