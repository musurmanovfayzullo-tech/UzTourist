package com.example.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.model.SosEmergencyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.ui.screens.UserRegistrationProfile
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object TelegramBotManager {
    private const val TAG = "TelegramBotManager"
    const val BOT_TOKEN = SosEmergencyRepository.TELEGRAM_BOT_TOKEN
    const val BOT_USERNAME = SosEmergencyRepository.TELEGRAM_SOS_BOT
    const val PRIMARY_DISPATCHER_CHAT_ID = SosEmergencyRepository.TELEGRAM_DISPATCH_CHAT_ID // 6089586932

    private const val PREFS_NAME = "uz_turist_telegram_bot_prefs"
    private const val KEY_SAVED_CHAT_ID = "saved_dispatcher_chat_id"

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(12, TimeUnit.SECONDS)
            .readTimeout(12, TimeUnit.SECONDS)
            .writeTimeout(12, TimeUnit.SECONDS)
            .build()
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getSavedChatId(context: Context): String {
        val id = getPrefs(context).getString(KEY_SAVED_CHAT_ID, null)?.trim()
        return if (!id.isNullOrBlank()) id else PRIMARY_DISPATCHER_CHAT_ID
    }

    fun saveChatId(context: Context, chatId: String) {
        getPrefs(context).edit().putString(KEY_SAVED_CHAT_ID, chatId.trim()).apply()
    }

    /**
     * Queries Telegram getUpdates API to automatically discover the chat ID of anyone
     * who interacted with @avtomaktab77bot (/start or any message).
     */
    suspend fun discoverLatestChatId(context: Context): String? = withContext(Dispatchers.IO) {
        try {
            val url = "https://api.telegram.org/bot$BOT_TOKEN/getUpdates"
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "getUpdates failed with code: ${response.code}")
                    return@withContext null
                }
                val body = response.body?.string() ?: return@withContext null
                val json = JSONObject(body)
                if (!json.optBoolean("ok")) return@withContext null

                val results = json.optJSONArray("result") ?: return@withContext null
                if (results.length() == 0) return@withContext null

                // Look from the most recent update backwards
                for (i in results.length() - 1 downTo 0) {
                    val update = results.optJSONObject(i) ?: continue
                    val message = update.optJSONObject("message")
                        ?: update.optJSONObject("channel_post")
                        ?: update.optJSONObject("my_chat_member")
                        ?: update.optJSONObject("callback_query")?.optJSONObject("message")
                    val chat = message?.optJSONObject("chat")
                    val chatId = chat?.optLong("id")
                    if (chatId != null && chatId != 0L) {
                        val chatIdStr = chatId.toString()
                        saveChatId(context, chatIdStr)
                        Log.d(TAG, "Discovered Chat ID: $chatIdStr")
                        return@withContext chatIdStr
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error discovering Chat ID", e)
        }
        return@withContext null
    }

    /**
     * Sends the SOS Alert message and exact live GPS location pinpoint directly
     * to Telegram Bot API (Target Telegram ID: 6089586932).
     */
    fun sendSosViaTelegramApi(
        context: Context,
        touristName: String,
        phone: String,
        latitude: Double,
        longitude: Double,
        nearestMonument: String,
        emergencyType: String,
        onResult: ((success: Boolean, message: String) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Collect target chat IDs: always include primary 6089586932, plus any saved/discovered
                val targetChatIds = mutableSetOf(PRIMARY_DISPATCHER_CHAT_ID)
                val savedId = getSavedChatId(context)
                if (savedId.isNotBlank()) targetChatIds.add(savedId)
                val discovered = discoverLatestChatId(context)
                if (!discovered.isNullOrBlank()) targetChatIds.add(discovered)

                val timeFormatted = SimpleDateFormat("HH:mm:ss, dd.MM.yyyy", Locale.getDefault()).format(Date())
                val googleLiveMap = "https://maps.google.com/?q=$latitude,$longitude"
                val yandexLiveMap = "https://yandex.uz/maps/?pt=$longitude,$latitude&z=17&l=map"

                val htmlMessage = """
                    🚨 <b>UZTURIST: SHOSHILINCH SOS DISPETCHER CHAQIRUVI!</b>
                    ━━━━━━━━━━━━━━━━━━━━
                    👤 <b>Sayyoh:</b> $touristName
                    📞 <b>Aloqa telefoni:</b> $phone
                    ⚠️ <b>Holat / Sabab:</b> $emergencyType
                    📍 <b>Yaqin manzil / Obida:</b> $nearestMonument
                    🌐 <b>GPS Koordinata:</b> $latitude, $longitude
                    ⏰ <b>Vaqt:</b> $timeFormatted
                    📞 <b>SOS Call Center:</b> ${SosEmergencyRepository.CALL_CENTER_DISPLAY_PHONE}
                    ━━━━━━━━━━━━━━━━━━━━
                    🗺️ <a href="$googleLiveMap">Google Mapsda Jonli Ko'rish</a>
                    🧭 <a href="$yandexLiveMap">Yandex Xaritada Jonli Ko'rish</a>
                """.trimIndent()

                var sentSuccessfully = false

                for (targetChatId in targetChatIds) {
                    try {
                        // 1. Send formatted HTML text message
                        val messageBody = FormBody.Builder()
                            .add("chat_id", targetChatId)
                            .add("text", htmlMessage)
                            .add("parse_mode", "HTML")
                            .build()

                        val messageRequest = Request.Builder()
                            .url("https://api.telegram.org/bot$BOT_TOKEN/sendMessage")
                            .post(messageBody)
                            .build()

                        httpClient.newCall(messageRequest).execute().use { resp ->
                            if (resp.isSuccessful) {
                                sentSuccessfully = true
                                Log.d(TAG, "SOS Alert text sent to Telegram Chat ID: $targetChatId")
                            } else {
                                Log.w(TAG, "Failed sending to $targetChatId: HTTP ${resp.code}")
                            }
                        }

                        // 2. Send interactive native GPS Location pin
                        val locationBody = FormBody.Builder()
                            .add("chat_id", targetChatId)
                            .add("latitude", latitude.toString())
                            .add("longitude", longitude.toString())
                            .build()

                        val locationRequest = Request.Builder()
                            .url("https://api.telegram.org/bot$BOT_TOKEN/sendLocation")
                            .post(locationBody)
                            .build()

                        httpClient.newCall(locationRequest).execute().use { resp ->
                            if (resp.isSuccessful) {
                                Log.d(TAG, "SOS Location pin sent to Telegram Chat ID: $targetChatId")
                            }
                        }
                    } catch (chatError: Exception) {
                        Log.e(TAG, "Error sending to chat ID $targetChatId", chatError)
                    }
                }

                withContext(Dispatchers.Main) {
                    if (sentSuccessfully) {
                        onResult?.invoke(true, "Xabar Telegram ID ($PRIMARY_DISPATCHER_CHAT_ID) ga yuborildi!")
                    } else {
                        onResult?.invoke(false, "Botga ulanish: @$BOT_USERNAME ga /start yuboring (ID: $PRIMARY_DISPATCHER_CHAT_ID)")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error sending SOS to Telegram API", e)
                withContext(Dispatchers.Main) {
                    onResult?.invoke(false, e.localizedMessage ?: "Telegram API xatoligi")
                }
            }
        }
    }

    /**
     * Sends taxi & transfer bookings directly to Telegram ID (6089586932) via bot API.
     */
    fun sendTaxiBookingViaTelegramApi(
        context: Context,
        bookingDetails: String,
        onResult: ((success: Boolean, message: String) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val targetChatIds = mutableSetOf(PRIMARY_DISPATCHER_CHAT_ID)
                val savedId = getSavedChatId(context)
                if (savedId.isNotBlank()) targetChatIds.add(savedId)
                val discovered = discoverLatestChatId(context)
                if (!discovered.isNullOrBlank()) targetChatIds.add(discovered)

                val timeFormatted = SimpleDateFormat("HH:mm:ss, dd.MM.yyyy", Locale.getDefault()).format(Date())

                val htmlMessage = """
                    🚕 <b>UZTURIST: YANGI TAKSI VA TRANSFER BUYURTMASI</b>
                    ━━━━━━━━━━━━━━━━━━━━
                    $bookingDetails
                    ━━━━━━━━━━━━━━━━━━━━
                    ⏰ <b>Buyurtma vaqti:</b> $timeFormatted
                    📞 <b>Dispetcherlik:</b> ${SosEmergencyRepository.CALL_CENTER_DISPLAY_PHONE}
                """.trimIndent()

                var sent = false
                for (chatId in targetChatIds) {
                    try {
                        val body = FormBody.Builder()
                            .add("chat_id", chatId)
                            .add("text", htmlMessage)
                            .add("parse_mode", "HTML")
                            .build()

                        val req = Request.Builder()
                            .url("https://api.telegram.org/bot$BOT_TOKEN/sendMessage")
                            .post(body)
                            .build()

                        httpClient.newCall(req).execute().use { resp ->
                            if (resp.isSuccessful) {
                                sent = true
                                Log.d(TAG, "Taxi booking sent to Telegram Chat ID: $chatId")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error sending booking to $chatId", e)
                    }
                }

                withContext(Dispatchers.Main) {
                    if (sent) {
                        onResult?.invoke(true, "Buyurtma Telegram ID ($PRIMARY_DISPATCHER_CHAT_ID) ga yetkazildi!")
                    } else {
                        onResult?.invoke(false, "Botga ulanish: @$BOT_USERNAME ga /start yuboring")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in sendTaxiBookingViaTelegramApi", e)
                withContext(Dispatchers.Main) {
                    onResult?.invoke(false, e.localizedMessage ?: "Telegram API xatosi")
                }
            }
        }
    }

    /**
     * Sends newly registered tourist profile with all personal and device details
     * to Telegram Dispatch Bot (Primary ID: 6089586932, saved chats, and active sessions).
     */
    fun sendNewUserRegistrationViaTelegramApi(
        context: Context,
        profile: UserRegistrationProfile,
        onResult: ((success: Boolean, message: String) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val targetChatIds = mutableSetOf(PRIMARY_DISPATCHER_CHAT_ID)
                val savedId = getSavedChatId(context)
                if (savedId.isNotBlank()) targetChatIds.add(savedId)
                val discovered = discoverLatestChatId(context)
                if (!discovered.isNullOrBlank()) targetChatIds.add(discovered)

                val timeFormatted = SimpleDateFormat("HH:mm:ss, dd.MM.yyyy", Locale.getDefault()).format(Date())
                val userId = UserSessionManager.getUserId(context)
                val isAiActive = UserSessionManager.isAiActivated(context)
                val deviceModel = "${android.os.Build.MANUFACTURER.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} ${android.os.Build.MODEL} (Android ${android.os.Build.VERSION.RELEASE}, API ${android.os.Build.VERSION.SDK_INT})"

                val fullName = "${profile.firstName} ${profile.lastName}".trim().ifBlank { "Ism kiritilmagan" }
                val phone = profile.phoneNumber.trim().ifBlank { "Kiritilmagan" }
                val email = profile.email.trim().ifBlank { "Kiritilmagan" }
                val lang = "${profile.languageName} [${profile.languageCode.uppercase()}]"
                val touristId = profile.touristId.ifBlank { userId }
                val regDate = profile.registeredDate.ifBlank { timeFormatted }

                val htmlMessage = """
                    🆕 <b>UZTURIST: YANGI FOYDALANUVCHI RO'YXATDAN O'TDI!</b>
                    ━━━━━━━━━━━━━━━━━━━━
                    👤 <b>Sayyoh (Foydalanuvchi):</b> $fullName
                    📞 <b>Telefon raqami:</b> <code>$phone</code>
                    📧 <b>Elektron pochta:</b> <code>$email</code>
                    🌐 <b>Tanlangan til:</b> $lang
                    🎫 <b>Sayyohlik ID (Tourist ID):</b> <code>$touristId</code>
                    🆔 <b>Qurilma / Tizim ID:</b> <code>$userId</code>
                    📱 <b>Qurilma modeli:</b> $deviceModel
                    🤖 <b>AI Aktivatsiya holati:</b> ${if (isAiActive) "🟢 FAOLLASHTIRILGAN" else "🔴 BLOKLANGAN (Admin ruxsati kutilmoqda)"}
                    ⏰ <b>Ro'yxatdan o'tgan vaqt:</b> $regDate
                    ━━━━━━━━━━━━━━━━━━━━
                    ⚙️ <b>Admin amali:</b>
                    Ushbu foydalanuvchiga barcha AI xizmatlarini ochish uchun ilovadagi <b>Admin Panel ➔ AI Aktivatsiya</b> bo'limida ushbu ID ni kiriting:
                    👉 <code>$touristId</code> yoki <code>$userId</code>
                """.trimIndent()

                var sentSuccessfully = false
                for (chatId in targetChatIds) {
                    try {
                        val body = FormBody.Builder()
                            .add("chat_id", chatId)
                            .add("text", htmlMessage)
                            .add("parse_mode", "HTML")
                            .build()

                        val request = Request.Builder()
                            .url("https://api.telegram.org/bot$BOT_TOKEN/sendMessage")
                            .post(body)
                            .build()

                        httpClient.newCall(request).execute().use { resp ->
                            if (resp.isSuccessful) {
                                sentSuccessfully = true
                                Log.d(TAG, "New user profile sent to Telegram Chat ID: $chatId")
                            } else {
                                Log.w(TAG, "Failed sending profile to $chatId: HTTP ${resp.code}")
                            }
                        }
                    } catch (chatError: Exception) {
                        Log.e(TAG, "Error sending registration to chat $chatId", chatError)
                    }
                }

                withContext(Dispatchers.Main) {
                    if (sentSuccessfully) {
                        onResult?.invoke(true, "Foydalanuvchi ma'lumotlari Telegramga ($PRIMARY_DISPATCHER_CHAT_ID) muvaffaqiyatli yuborildi!")
                    } else {
                        onResult?.invoke(false, "Telegramga yuborilmadi: @$BOT_USERNAME ga /start yuboring")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in sendNewUserRegistrationViaTelegramApi", e)
                withContext(Dispatchers.Main) {
                    onResult?.invoke(false, e.localizedMessage ?: "Telegram API xatosi")
                }
            }
        }
    }

    /**
     * Sends guide acceptance verification details including guide-assigned number,
     * connected phone, full name, gmail, and tourist ID to Telegram bot.
     */
    fun sendGuideAcceptedVerificationViaTelegramApi(
        context: Context,
        guideNumber: String,
        userName: String,
        userPhone: String,
        userEmail: String,
        userId: String,
        onResult: ((success: Boolean, message: String) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val targetChatIds = mutableSetOf(PRIMARY_DISPATCHER_CHAT_ID)
                val savedId = getSavedChatId(context)
                if (savedId.isNotBlank()) targetChatIds.add(savedId)
                val discovered = discoverLatestChatId(context)
                if (!discovered.isNullOrBlank()) targetChatIds.add(discovered)

                val timeFormatted = SimpleDateFormat("HH:mm:ss, dd.MM.yyyy", Locale.getDefault()).format(Date())
                val deviceModel = "${android.os.Build.MANUFACTURER.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} ${android.os.Build.MODEL} (Android ${android.os.Build.VERSION.RELEASE})"

                val htmlMessage = """
                    🎯 <b>UZTURIST: GID QABUL QILDI VA RAQAM TASDIQLANDI!</b>
                    ━━━━━━━━━━━━━━━━━━━━
                    🔢 <b>Gid bergan raqam (KOD):</b> <code>${guideNumber.trim()}</code>
                    👤 <b>Foydalanuvchi ismi:</b> ${userName.trim()}
                    📞 <b>Ulangan telefon raqami:</b> <code>${userPhone.trim()}</code>
                    📧 <b>Gmail / Elektron pochta:</b> <code>${userEmail.trim()}</code>
                    🎫 <b>Foydalanuvchi ID (Tourist ID):</b> <code>${userId.trim()}</code>
                    📱 <b>Qurilma:</b> $deviceModel
                    🟢 <b>Holat:</b> Gid ID orqali qabul qildi va ilova aktivlashtirildi ✓
                    ⏰ <b>Tasdiqlangan vaqt:</b> $timeFormatted
                    ━━━━━━━━━━━━━━━━━━━━
                    ⚡ <b>Admin hisoboti:</b> Ushbu barcha ma'lumotlar Admin Ilova (Dashboard) va Markaziy Tizimga ham saqlandi!
                """.trimIndent()

                var sentSuccessfully = false
                for (chatId in targetChatIds) {
                    try {
                        val body = FormBody.Builder()
                            .add("chat_id", chatId)
                            .add("text", htmlMessage)
                            .add("parse_mode", "HTML")
                            .build()

                        val request = Request.Builder()
                            .url("https://api.telegram.org/bot$BOT_TOKEN/sendMessage")
                            .post(body)
                            .build()

                        httpClient.newCall(request).execute().use { resp ->
                            if (resp.isSuccessful) {
                                sentSuccessfully = true
                                Log.d(TAG, "Guide verification sent to Telegram Chat ID: $chatId")
                            } else {
                                Log.w(TAG, "Failed sending guide verification to $chatId: HTTP ${resp.code}")
                            }
                        }
                    } catch (chatError: Exception) {
                        Log.e(TAG, "Error sending guide verification to chat $chatId", chatError)
                    }
                }

                withContext(Dispatchers.Main) {
                    if (sentSuccessfully) {
                        onResult?.invoke(true, "Gid raqami va ma'lumotlar Telegram ($PRIMARY_DISPATCHER_CHAT_ID) va Admin tizimga yetkazildi! ✓")
                    } else {
                        onResult?.invoke(false, "Telegramga yuborishda xatolik yuz berdi (@$BOT_USERNAME ga /start yuboring)")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in sendGuideAcceptedVerificationViaTelegramApi", e)
                withContext(Dispatchers.Main) {
                    onResult?.invoke(false, e.localizedMessage ?: "Telegram API xatosi")
                }
            }
        }
    }

    /**
     * Sends a quick message from Tourist to Guide/Admin via Telegram
     */
    fun sendGuideChatMessageViaTelegramApi(
        context: Context,
        touristName: String,
        touristId: String,
        messageText: String,
        guideNumber: String,
        onResult: ((success: Boolean, message: String) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val targetChatIds = mutableSetOf(PRIMARY_DISPATCHER_CHAT_ID)
                val savedId = getSavedChatId(context)
                if (savedId.isNotBlank()) targetChatIds.add(savedId)
                val discovered = discoverLatestChatId(context)
                if (!discovered.isNullOrBlank()) targetChatIds.add(discovered)

                val timeFormatted = SimpleDateFormat("HH:mm:ss, dd.MM.yyyy", Locale.getDefault()).format(Date())

                val htmlMessage = """
                    💬 <b>UZTURIST: GID VA SAYYOH TEZKOR ALOQASI</b>
                    ━━━━━━━━━━━━━━━━━━━━
                    👤 <b>Sayyoh:</b> ${touristName.trim()} (ID: <code>${touristId.trim()}</code>)
                    🔢 <b>Biriktirilgan Gid kodi:</b> <code>${guideNumber.trim()}</code>
                    ⏰ <b>Vaqt:</b> $timeFormatted
                    ━━━━━━━━━━━━━━━━━━━━
                    📩 <b>XABAR MATNI:</b>
                    «${messageText.trim()}»
                    ━━━━━━━━━━━━━━━━━━━━
                    ⚡ <i>Ushbu xabar Admin Ilovasining Jonli Chat bo'limiga ham yetkazildi.</i>
                """.trimIndent()

                var sentSuccessfully = false
                for (chatId in targetChatIds) {
                    try {
                        val body = FormBody.Builder()
                            .add("chat_id", chatId)
                            .add("text", htmlMessage)
                            .add("parse_mode", "HTML")
                            .build()

                        val request = Request.Builder()
                            .url("https://api.telegram.org/bot$BOT_TOKEN/sendMessage")
                            .post(body)
                            .build()

                        httpClient.newCall(request).execute().use { resp ->
                            if (resp.isSuccessful) sentSuccessfully = true
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error sending chat to $chatId", e)
                    }
                }

                withContext(Dispatchers.Main) {
                    onResult?.invoke(sentSuccessfully, if (sentSuccessfully) "Xabar Gid va Adminga yetkazildi ✓" else "Telegram xatosi")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult?.invoke(false, e.localizedMessage ?: "Aloqa xatosi")
                }
            }
        }
    }

    /**
     * Sends Tour Itinerary progress updates to Telegram
     */
    fun sendItineraryProgressViaTelegramApi(
        context: Context,
        touristName: String,
        checkpointTitle: String,
        completedCount: Int,
        totalCount: Int,
        cityName: String,
        onResult: ((success: Boolean, message: String) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val targetChatIds = mutableSetOf(PRIMARY_DISPATCHER_CHAT_ID)
                val savedId = getSavedChatId(context)
                if (savedId.isNotBlank()) targetChatIds.add(savedId)
                val discovered = discoverLatestChatId(context)
                if (!discovered.isNullOrBlank()) targetChatIds.add(discovered)

                val percentage = (completedCount * 100) / totalCount
                val timeFormatted = SimpleDateFormat("HH:mm:ss, dd.MM.yyyy", Locale.getDefault()).format(Date())

                val htmlMessage = """
                    🗺️ <b>UZTURIST: EKSKURSIYA TARAQQIYOTI BELGILANDI</b>
                    ━━━━━━━━━━━━━━━━━━━━
                    👤 <b>Sayyoh:</b> ${touristName.trim()}
                    🏛️ <b>Obida:</b> $checkpointTitle ($cityName)
                    📊 <b>Marshrut ko'rsatkichi:</b> $completedCount / $totalCount ($percentage%)
                    ⏰ <b>Vaqt:</b> $timeFormatted
                    ━━━━━━━━━━━━━━━━━━━━
                    ✅ <i>Sayyoh ushbu manzilga muvaffaqiyatli tashrif buyurdi va marshrut ro'yxatida belgilandi.</i>
                """.trimIndent()

                var sentSuccessfully = false
                for (chatId in targetChatIds) {
                    try {
                        val body = FormBody.Builder()
                            .add("chat_id", chatId)
                            .add("text", htmlMessage)
                            .add("parse_mode", "HTML")
                            .build()

                        val request = Request.Builder()
                            .url("https://api.telegram.org/bot$BOT_TOKEN/sendMessage")
                            .post(body)
                            .build()

                        httpClient.newCall(request).execute().use { resp ->
                            if (resp.isSuccessful) sentSuccessfully = true
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error sending itinerary to $chatId", e)
                    }
                }

                withContext(Dispatchers.Main) {
                    onResult?.invoke(sentSuccessfully, if (sentSuccessfully) "Ekskursiya ma'lumoti Telegramga uzatildi ✓" else "Xatolik")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult?.invoke(false, e.localizedMessage ?: "Xatolik")
                }
            }
        }
    }

    /**
     * Sends Guide Rating and Review directly to Telegram
     */
    fun sendGuideReviewViaTelegramApi(
        context: Context,
        touristName: String,
        touristId: String,
        guideNumber: String,
        ratingStars: Int,
        tags: List<String>,
        comment: String,
        onResult: ((success: Boolean, message: String) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val targetChatIds = mutableSetOf(PRIMARY_DISPATCHER_CHAT_ID)
                val savedId = getSavedChatId(context)
                if (savedId.isNotBlank()) targetChatIds.add(savedId)
                val discovered = discoverLatestChatId(context)
                if (!discovered.isNullOrBlank()) targetChatIds.add(discovered)

                val starsEmoji = "⭐".repeat(ratingStars.coerceIn(1, 5))
                val timeFormatted = SimpleDateFormat("HH:mm:ss, dd.MM.yyyy", Locale.getDefault()).format(Date())
                val tagsFormatted = if (tags.isNotEmpty()) tags.joinToString(", ") else "Ko'rsatilmagan"

                val htmlMessage = """
                    ⭐ <b>UZTURIST: GIDGA YANGI BAHOR VA SHARH KELDI!</b>
                    ━━━━━━━━━━━━━━━━━━━━
                    🌟 <b>Baholash:</b> $starsEmoji ($ratingStars / 5)
                    👤 <b>Sayyoh:</b> ${touristName.trim()} (ID: <code>${touristId.trim()}</code>)
                    🔢 <b>Gid raqami:</b> <code>${guideNumber.trim()}</code>
                    🏷️ <b>Xususiyatlar:</b> $tagsFormatted
                    ⏰ <b>Sana:</b> $timeFormatted
                    ━━━━━━━━━━━━━━━━━━━━
                    💬 <b>Sayyoh sharhi:</b>
                    «${if (comment.isNotBlank()) comment.trim() else "Izoh qoldirilmadi"}»
                    ━━━━━━━━━━━━━━━━━━━━
                    🏆 <i>Ushbu baho Admin panelidagi Gidlar reytingiga qo'shildi!</i>
                """.trimIndent()

                var sentSuccessfully = false
                for (chatId in targetChatIds) {
                    try {
                        val body = FormBody.Builder()
                            .add("chat_id", chatId)
                            .add("text", htmlMessage)
                            .add("parse_mode", "HTML")
                            .build()

                        val request = Request.Builder()
                            .url("https://api.telegram.org/bot$BOT_TOKEN/sendMessage")
                            .post(body)
                            .build()

                        httpClient.newCall(request).execute().use { resp ->
                            if (resp.isSuccessful) sentSuccessfully = true
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error sending review to $chatId", e)
                    }
                }

                withContext(Dispatchers.Main) {
                    onResult?.invoke(sentSuccessfully, if (sentSuccessfully) "Baho Telegram va Adminga muvaffaqiyatli yuborildi! ✓" else "Xatolik")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult?.invoke(false, e.localizedMessage ?: "Xatolik")
                }
            }
        }
    }

    /**
     * Sends Digital Wallet Event (Top-Up, Payment, Guide Tip, Currency Exchange)
     * directly to Telegram Bot API.
     */
    fun sendTouristWalletEventViaTelegramApi(
        context: Context,
        eventType: String, // "TOP_UP", "PAYMENT", "GUIDE_TIP", "EXCHANGE"
        touristName: String,
        touristId: String,
        walletId: String,
        cardNumber: String,
        amountFormatted: String,
        merchantOrTarget: String,
        newBalanceFormatted: String,
        txnId: String,
        note: String = "",
        onResult: ((success: Boolean, message: String) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val targetChatIds = mutableSetOf(PRIMARY_DISPATCHER_CHAT_ID)
                val savedId = getSavedChatId(context)
                if (savedId.isNotBlank()) targetChatIds.add(savedId)
                val discovered = discoverLatestChatId(context)
                if (!discovered.isNullOrBlank()) targetChatIds.add(discovered)

                val timeFormatted = SimpleDateFormat("HH:mm:ss, dd.MM.yyyy", Locale.getDefault()).format(Date())

                val (headerEmoji, headerTitle) = when (eventType) {
                    "TOP_UP" -> "💳" to "SAYYOH HAMYONIGA MABLAG' TUSHDI (+)"
                    "PAYMENT" -> "💸" to "ELEKTRON HAMYONDAN TO'LOV AMALGA OSHIRILDI (-)"
                    "GUIDE_TIP" -> "🎁" to "GIDGA CHOYCHAQA / MUKOFOT TO'LANDI"
                    "EXCHANGE" -> "🔄" to "VALYUTA AYIRBOSHLASH (KONVERTATSIYA)"
                    else -> "💳" to "ELEKTRON HAMYON AMALIYOTI"
                }

                val htmlMessage = """
                    $headerEmoji <b>UZTURIST: $headerTitle</b>
                    ━━━━━━━━━━━━━━━━━━━━
                    👤 <b>Sayyoh:</b> ${touristName.trim()} (ID: <code>${touristId.trim()}</code>)
                    💼 <b>Hamyon ID:</b> <code>${walletId.trim()}</code>
                    💳 <b>Virtual Karta:</b> <code>${cardNumber.trim()}</code>
                    💰 <b>Summa:</b> <b>$amountFormatted</b>
                    🏢 <b>To'lov maqsadi:</b> $merchantOrTarget
                    ${if (note.isNotBlank()) "📝 <b>Izoh:</b> $note\n" else ""}━━━━━━━━━━━━━━━━━━━━
                    💵 <b>Yangi Balans:</b> <b>$newBalanceFormatted</b>
                    🧾 <b>Tranzaksiya Cheki:</b> <code>$txnId</code>
                    ⏰ <b>Vaqt:</b> $timeFormatted
                    ━━━━━━━━━━━━━━━━━━━━
                    ⚡ <i>Silk Road Pay • Tranzaksiya Admin panelida tasdiqlandi!</i>
                """.trimIndent()

                var sentSuccessfully = false
                for (chatId in targetChatIds) {
                    try {
                        val body = FormBody.Builder()
                            .add("chat_id", chatId)
                            .add("text", htmlMessage)
                            .add("parse_mode", "HTML")
                            .build()

                        val request = Request.Builder()
                            .url("https://api.telegram.org/bot$BOT_TOKEN/sendMessage")
                            .post(body)
                            .build()

                        httpClient.newCall(request).execute().use { resp ->
                            if (resp.isSuccessful) sentSuccessfully = true
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error sending wallet event to $chatId", e)
                    }
                }

                withContext(Dispatchers.Main) {
                    onResult?.invoke(sentSuccessfully, if (sentSuccessfully) "To'lov cheki Telegram va Adminga yuborildi! ✓" else "Xatolik")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult?.invoke(false, e.localizedMessage ?: "Xatolik")
                }
            }
        }
    }

    /**
     * Dispatches SafeTour Georadar separation alert to the Tour Guide & Admin Dispetcher via Telegram
     */
    fun sendSafeTourRadarAlertViaTelegramApi(
        context: Context,
        touristName: String,
        touristPhone: String,
        touristId: String,
        guideNumber: String,
        distanceMeters: Int,
        directionDescription: String,
        latitude: Double,
        longitude: Double,
        onResult: ((Boolean, String) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val savedChatId = getSavedChatId(context)
                val targetChatIds = setOf(PRIMARY_DISPATCHER_CHAT_ID, savedChatId).filter { it.isNotBlank() }
                val timeFormatted = SimpleDateFormat("HH:mm:ss, dd.MM.yyyy", Locale.getDefault()).format(Date())
                val mapsUrl = "https://maps.google.com/?q=$latitude,$longitude"

                val htmlMessage = """
                    🦺 <b>SAFETOUR GEORADAR: SAYYOH GIDDAN UZOQLASHDI!</b>
                    ━━━━━━━━━━━━━━━━━━━━
                    👤 <b>Sayyoh:</b> $touristName
                    📞 <b>Telefon:</b> <code>$touristPhone</code>
                    🆔 <b>Sayyoh ID:</b> <code>$touristId</code>
                    👨‍💼 <b>Biriktirilgan Gid:</b> <b>$guideNumber</b>
                    ━━━━━━━━━━━━━━━━━━━━
                    📏 <b>Masofa:</b> <b>$distanceMeters metr</b> (Chegara buzildi ⚠️)
                    🧭 <b>Gidga qarab yo'nalish:</b> $directionDescription
                    📍 <b>Jonli Geolocation:</b> <code>$latitude, $longitude</code>
                    🗺️ <a href="$mapsUrl">Google Maps'da Sayyoh Joylashuvi</a>
                    ⏰ <b>Vaqt:</b> $timeFormatted
                    ━━━━━━━━━━━━━━━━━━━━
                    ⚡ <i>SafeTour Radar xavfsizlik perimetri bo'yicha ogohlantirish yuborildi! Gid zudlik bilan sayyoh bilan bog'lanishi tavsiya etiladi.</i>
                """.trimIndent()

                var sentSuccessfully = false
                for (chatId in targetChatIds) {
                    try {
                        val body = FormBody.Builder()
                            .add("chat_id", chatId)
                            .add("text", htmlMessage)
                            .add("parse_mode", "HTML")
                            .build()

                        val request = Request.Builder()
                            .url("https://api.telegram.org/bot$BOT_TOKEN/sendMessage")
                            .post(body)
                            .build()

                        httpClient.newCall(request).execute().use { resp ->
                            if (resp.isSuccessful) sentSuccessfully = true
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error sending SafeTour radar alert to $chatId", e)
                    }
                }

                withContext(Dispatchers.Main) {
                    onResult?.invoke(
                        sentSuccessfully,
                        if (sentSuccessfully) "Gidga va dispetcherga ogohlantirish yuborildi! ✓" else "Xatolik yuz berdi"
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult?.invoke(false, e.localizedMessage ?: "Xatolik")
                }
            }
        }
    }

    /**
     * Dispatches Audio Whisper session alerts (Broadcast started, Chime bell, Question raised) to Telegram
     */
    fun sendAudioWhisperEventViaTelegramApi(
        context: Context,
        channelCode: String,
        eventType: String,
        details: String,
        onResult: ((Boolean, String) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val savedChatId = getSavedChatId(context)
                val targetChatIds = setOf(PRIMARY_DISPATCHER_CHAT_ID, savedChatId).filter { it.isNotBlank() }
                val timeFormatted = SimpleDateFormat("HH:mm:ss, dd.MM.yyyy", Locale.getDefault()).format(Date())

                val htmlMessage = """
                    🎙️ <b>GID OVOZLI EFIRI (AUDIO WHISPER)</b>
                    ━━━━━━━━━━━━━━━━━━━━
                    📻 <b>Kanal Kodi:</b> <code>$channelCode</code>
                    ⚡ <b>Hodisa:</b> $eventType
                    ℹ️ <b>Tafsilot:</b> $details
                    🕒 <b>Vaqt:</b> $timeFormatted
                    ━━━━━━━━━━━━━━━━━━━━
                    🎧 <i>Turistlar quloqchin orqali gid ovozini toza va shovqinsiz qabul qilmoqda.</i>
                """.trimIndent()

                var successAny = false
                var lastResp = ""

                for (targetChatId in targetChatIds) {
                    val formBody = FormBody.Builder()
                        .add("chat_id", targetChatId)
                        .add("text", htmlMessage)
                        .add("parse_mode", "HTML")
                        .build()

                    val request = Request.Builder()
                        .url("https://api.telegram.org/bot$BOT_TOKEN/sendMessage")
                        .post(formBody)
                        .build()

                    httpClient.newCall(request).execute().use { response ->
                        val respBody = response.body?.string() ?: ""
                        if (response.isSuccessful) {
                            successAny = true
                            lastResp = respBody
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    onResult?.invoke(successAny, if (successAny) "Telegramga yetkazildi" else lastResp)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult?.invoke(false, e.localizedMessage ?: "Xatolik")
                }
            }
        }
    }
}
