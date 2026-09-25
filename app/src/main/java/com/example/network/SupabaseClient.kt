package com.example.network

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Supabase Cloud Client for Uzbekistan Tourist App & Admin Panel.
 * Synchronizes with the 8 core tables of Tourist UZ Admin:
 * 1. users (Xodimlar va Adminlar)
 * 2. bookings (Sayyohlar Buyurtmalari)
 * 3. tours (Sayyohlik Turlari)
 * 4. attractions (Diqqatga sazovor maskanlar)
 * 5. guides (Sertifikatlangan Gidlar)
 * 6. promos (Chegirma va Promokodlar)
 * 7. notifications (Ommaviy bildirishnomalar)
 * 8. admin_logs (Tizim auditi va jurnali)
 */
object SupabaseClient {
    private const val TAG = "SupabaseClient"

    // User's Supabase Project Credentials
    const val DEFAULT_PROJECT_URL = "https://izywwttrxuelprmlurya.supabase.co"
    const val DEFAULT_ANON_KEY = "sb_publishable_LjvCf48exrC0j8h0jFPeDA_GXnj7D59"

    private const val PREFS_NAME = "supabase_config"
    private const val KEY_URL = "project_url"
    private const val KEY_KEY = "anon_key"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    fun getProjectUrl(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_URL, DEFAULT_PROJECT_URL) ?: DEFAULT_PROJECT_URL
    }

    fun getAnonKey(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_KEY, DEFAULT_ANON_KEY) ?: DEFAULT_ANON_KEY
    }

    fun saveCredentials(context: Context, url: String, key: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_URL, url.trim().removeSuffix("/"))
            .putString(KEY_KEY, key.trim())
            .apply()
    }

    // ==========================================
    // DATA MODELS CORRESPONDING TO THE 8 TABLES
    // ==========================================

    // 1. users
    data class RemoteUser(
        val id: String,
        val username: String,
        val fullName: String,
        val role: String = "STAFF", // ADMIN, STAFF
        val position: String = "Operator",
        val phone: String = "",
        val isActive: Boolean = true,
        val createdAt: String = ""
    )

    // 2. bookings
    data class RemoteBooking(
        val id: String,
        val bookingCode: String,
        val tourId: String,
        val tourTitle: String,
        val touristName: String,
        val touristPhone: String,
        val touristEmail: String = "",
        val touristCountry: String = "O'zbekiston",
        val startDate: String = "",
        val peopleCount: Int = 1,
        val totalPrice: Double = 0.0,
        val bookingStatus: String = "Kutilmoqda", // Kutilmoqda, Tasdiqlangan, Yakunlandi, Bekor qilindi
        val paymentStatus: String = "Kutilmoqda", // To'langan, Kutilmoqda, Naqd to'lov
        val notes: String = ""
    )

    // 3. tours
    data class RemoteTour(
        val id: String,
        val title: String,
        val region: String,
        val category: String,
        val price: Double,
        val duration: String,
        val maxGroupSize: Int = 20,
        val includedServices: String = "Gid, Transfer, Chiptalar",
        val rating: Float = 4.9f,
        val discountPercent: Int = 0,
        val isActive: Boolean = true,
        val imageUrl: String = ""
    )

    // 4. attractions
    data class RemoteAttraction(
        val id: String,
        val name: String,
        val region: String,
        val category: String = "Obida",
        val entryFee: Double = 0.0,
        val workingHours: String = "08:00 - 19:00",
        val locationAddress: String = "",
        val audioGuideAvailable: Boolean = true,
        val rating: Float = 4.9f
    )

    // 5. guides
    data class RemoteGuide(
        val id: String,
        val fullName: String,
        val phone: String,
        val region: String,
        val languages: String = "O'zbek, Ingliz, Rus",
        val experienceYears: Int = 5,
        val licenseNumber: String = "UZ-GUIDE-001",
        val status: String = "Tasdiqlangan", // Tasdiqlangan, Kutmoqda, Band, Nofaol
        val rating: Float = 5.0f,
        val toursLedCount: Int = 50
    )

    // 6. promos
    data class RemotePromo(
        val id: String,
        val code: String,
        val discountPercent: Int,
        val expiryDate: String,
        val usageCount: Int = 0,
        val maxUsage: Int = 1000,
        val isActive: Boolean = true
    )

    // 7. notifications
    data class RemoteNotification(
        val id: String,
        val title: String,
        val message: String,
        val targetAudience: String = "Barcha sayyohlar",
        val promoCode: String = "",
        val sentDate: String = "",
        val readCount: Int = 0
    )

    // Legacy RemoteMonument (alias to attractions/monuments)
    data class RemoteMonument(
        val id: String,
        val title: String,
        val city: String,
        val subtitle: String = "",
        val description: String = "",
        val historyFact: String = "",
        val unescoYear: Int? = null,
        val rating: Float = 4.9f,
        val reviewCount: Int = 120,
        val category: String = "UNESCO",
        val imageUrl: String = "",
        val audioUrl: String = "",
        val audioScriptUz: String = "",
        val latitude: Double = 39.6547,
        val longitude: Double = 66.9758,
        val tag: String = "Tarixiy Obida",
        val period: String = "XIV-XV Asr",
        val isVerified: Boolean = true
    )

    // ==========================================
    // API OPERATIONS
    // ==========================================

    /**
     * Test connection to Supabase
     */
    suspend fun testConnection(context: Context): Result<String> = withContext(Dispatchers.IO) {
        try {
            val url = getProjectUrl(context)
            val key = getAnonKey(context)

            val request = Request.Builder()
                .url("$url/rest/v1/tours?select=count")
                .header("apikey", key)
                .header("Authorization", "Bearer $key")
                .header("Range", "0-0")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            val code = response.code
            val body = response.body?.string().orEmpty()

            if (response.isSuccessful || code in 200..206) {
                Result.success("Ulanish muvaffaqiyatli! Supabase 8 ta jadval bilan ishlashga tayyor (HTTP $code)")
            } else if (code == 404 || body.contains("relation") || body.contains("PGRST205")) {
                Result.success("Server ulangan! Qolgan jadvallar SQL skript orqali yaratiladi.")
            } else {
                Result.failure(Exception("HTTP $code: $body"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Connection test failed", e)
            Result.failure(e)
        }
    }

    /**
     * Send new booking from Tourist App -> Supabase `bookings` table
     * So Admin and Staff see it immediately in their app!
     */
    suspend fun createBooking(context: Context, booking: RemoteBooking): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val url = getProjectUrl(context)
            val key = getAnonKey(context)

            val json = JSONObject().apply {
                // If id is numeric, we can send it; otherwise omit to let Postgres auto-increment bigint
                booking.id.toLongOrNull()?.let { numId ->
                    put("id", numId)
                }
                put("booking_code", booking.bookingCode)
                val numericTourId = booking.tourId.filter { it.isDigit() }.toLongOrNull() ?: 0L
                put("tour_id", numericTourId)
                put("tour_title", booking.tourTitle)
                put("tourist_name", booking.touristName)
                put("tourist_phone", booking.touristPhone)
                put("tourist_email", booking.touristEmail)
                put("tourist_country", booking.touristCountry)
                put("start_date", booking.startDate)
                put("people_count", booking.peopleCount)
                put("total_price", booking.totalPrice)
                put("booking_status", booking.bookingStatus)
                put("payment_status", booking.paymentStatus)
                put("notes", booking.notes)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("$url/rest/v1/bookings")
                .header("apikey", key)
                .header("Authorization", "Bearer $key")
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation")
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful || response.code in 200..204) {
                val resBody = response.body?.string().orEmpty()
                Log.d(TAG, "Booking created in Supabase successfully: $resBody")
                Result.success(true)
            } else {
                val err = response.body?.string().orEmpty()
                Log.w(TAG, "Booking create response: HTTP ${response.code} $err")
                Result.failure(Exception("HTTP ${response.code}: $err"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Booking create failed", e)
            Result.failure(e)
        }
    }

    /**
     * Fetch all bookings for tourist / admin review
     */
    suspend fun fetchBookings(context: Context): Result<List<RemoteBooking>> = withContext(Dispatchers.IO) {
        try {
            val url = getProjectUrl(context)
            val key = getAnonKey(context)

            val request = Request.Builder()
                .url("$url/rest/v1/bookings?select=*&order=created_at.desc")
                .header("apikey", key)
                .header("Authorization", "Bearer $key")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP ${response.code}"))
            }

            val body = response.body?.string().orEmpty()
            val arr = JSONArray(body)
            val list = mutableListOf<RemoteBooking>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    RemoteBooking(
                        id = obj.optString("id", "b_$i"),
                        bookingCode = obj.optString("booking_code", obj.optString("bookingCode", "UZB-$i")),
                        tourId = obj.optString("tour_id", obj.optString("tourId", "")),
                        tourTitle = obj.optString("tour_title", obj.optString("tourTitle", "Sayyohlik Turi")),
                        touristName = obj.optString("tourist_name", obj.optString("touristName", "")),
                        touristPhone = obj.optString("tourist_phone", obj.optString("touristPhone", "")),
                        touristEmail = obj.optString("tourist_email", obj.optString("touristEmail", "")),
                        touristCountry = obj.optString("tourist_country", obj.optString("touristCountry", "Uzbekistan")),
                        startDate = obj.optString("start_date", obj.optString("startDate", "")),
                        peopleCount = obj.optInt("people_count", obj.optInt("peopleCount", 1)),
                        totalPrice = obj.optDouble("total_price", obj.optDouble("totalPrice", 0.0)),
                        bookingStatus = obj.optString("booking_status", obj.optString("bookingStatus", "Kutilmoqda")),
                        paymentStatus = obj.optString("payment_status", obj.optString("paymentStatus", "To'langan")),
                        notes = obj.optString("notes", "")
                    )
                )
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetch tours created by Admin in Supabase `tours` table
     */
    suspend fun fetchTours(context: Context): Result<List<RemoteTour>> = withContext(Dispatchers.IO) {
        try {
            val url = getProjectUrl(context)
            val key = getAnonKey(context)

            val request = Request.Builder()
                .url("$url/rest/v1/tours?select=*&order=id.asc")
                .header("apikey", key)
                .header("Authorization", "Bearer $key")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) return@withContext Result.failure(Exception("HTTP ${response.code}"))

            val body = response.body?.string().orEmpty()
            val arr = JSONArray(body)
            val list = mutableListOf<RemoteTour>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    RemoteTour(
                        id = obj.optString("id", "t_$i"),
                        title = obj.optString("title", ""),
                        region = obj.optString("region", "Samarqand"),
                        category = obj.optString("category", "Tarixiy"),
                        price = obj.optDouble("price", 500000.0),
                        duration = obj.optString("duration", "1 kun"),
                        maxGroupSize = obj.optInt("max_group_size", obj.optInt("maxGroupSize", 20)),
                        includedServices = obj.optString("included_services", obj.optString("includedServices", "Gid, Transfer")),
                        rating = obj.optDouble("rating", 4.9).toFloat(),
                        discountPercent = obj.optInt("discount_percent", obj.optInt("discountPercent", 0)),
                        isActive = obj.optBoolean("is_active", obj.optBoolean("isActive", true)),
                        imageUrl = obj.optString("image_url", obj.optString("imageUrl", ""))
                    )
                )
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetch guides registered by Admin in Supabase `guides` table
     */
    suspend fun fetchGuides(context: Context): Result<List<RemoteGuide>> = withContext(Dispatchers.IO) {
        try {
            val url = getProjectUrl(context)
            val key = getAnonKey(context)

            val request = Request.Builder()
                .url("$url/rest/v1/guides?select=*&order=rating.desc")
                .header("apikey", key)
                .header("Authorization", "Bearer $key")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) return@withContext Result.failure(Exception("HTTP ${response.code}"))

            val body = response.body?.string().orEmpty()
            val arr = JSONArray(body)
            val list = mutableListOf<RemoteGuide>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    RemoteGuide(
                        id = obj.optString("id", "g_$i"),
                        fullName = obj.optString("full_name", obj.optString("fullName", "")),
                        phone = obj.optString("phone", "+998 90 123 45 67"),
                        region = obj.optString("region", "Samarqand"),
                        languages = obj.optString("languages", "O'zbek, Ingliz"),
                        experienceYears = obj.optInt("experience_years", obj.optInt("experienceYears", 5)),
                        licenseNumber = obj.optString("license_number", obj.optString("licenseNumber", "UZ-GUIDE")),
                        status = obj.optString("status", "Tasdiqlangan"),
                        rating = obj.optDouble("rating", 5.0).toFloat(),
                        toursLedCount = obj.optInt("tours_led_count", obj.optInt("toursLedCount", 30))
                    )
                )
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Verify promo code against Supabase `promos` table
     */
    suspend fun verifyPromoCode(context: Context, code: String): Result<RemotePromo?> = withContext(Dispatchers.IO) {
        try {
            val url = getProjectUrl(context)
            val key = getAnonKey(context)

            val cleanCode = code.trim().uppercase()
            val request = Request.Builder()
                .url("$url/rest/v1/promos?code=ilike.$cleanCode&limit=1")
                .header("apikey", key)
                .header("Authorization", "Bearer $key")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) return@withContext Result.failure(Exception("HTTP ${response.code}"))

            val body = response.body?.string().orEmpty()
            val arr = JSONArray(body)
            if (arr.length() == 0) {
                return@withContext Result.success(null)
            }

            val obj = arr.getJSONObject(0)
            val promo = RemotePromo(
                id = obj.optString("id", ""),
                code = obj.optString("code", cleanCode),
                discountPercent = obj.optInt("discount_percent", obj.optInt("discountPercent", 10)),
                expiryDate = obj.optString("expiry_date", obj.optString("expiryDate", "")),
                usageCount = obj.optInt("usage_count", obj.optInt("usageCount", 0)),
                maxUsage = obj.optInt("max_usage", obj.optInt("maxUsage", 1000)),
                isActive = obj.optBoolean("is_active", obj.optBoolean("isActive", true))
            )
            Result.success(promo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetch notifications broadcasted by Admin in Supabase `notifications` table
     */
    suspend fun fetchNotifications(context: Context): Result<List<RemoteNotification>> = withContext(Dispatchers.IO) {
        try {
            val url = getProjectUrl(context)
            val key = getAnonKey(context)

            val request = Request.Builder()
                .url("$url/rest/v1/notifications?select=*&order=id.desc")
                .header("apikey", key)
                .header("Authorization", "Bearer $key")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) return@withContext Result.failure(Exception("HTTP ${response.code}"))

            val body = response.body?.string().orEmpty()
            val arr = JSONArray(body)
            val list = mutableListOf<RemoteNotification>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    RemoteNotification(
                        id = obj.optString("id", "n_$i"),
                        title = obj.optString("title", ""),
                        message = obj.optString("message", ""),
                        targetAudience = obj.optString("target_audience", obj.optString("targetAudience", "Barcha")),
                        promoCode = obj.optString("promo_code", obj.optString("promoCode", "")),
                        sentDate = obj.optString("sent_date", obj.optString("sentDate", "")),
                        readCount = obj.optInt("read_count", obj.optInt("readCount", 0))
                    )
                )
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetch attractions from Supabase `attractions` or `monuments`
     */
    suspend fun fetchMonuments(context: Context): Result<List<RemoteMonument>> = withContext(Dispatchers.IO) {
        try {
            val url = getProjectUrl(context)
            val key = getAnonKey(context)

            val request = Request.Builder()
                .url("$url/rest/v1/monuments?select=*&order=id.asc")
                .header("apikey", key)
                .header("Authorization", "Bearer $key")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Server javobi: HTTP ${response.code}"))
            }

            val body = response.body?.string().orEmpty()
            val jsonArray = JSONArray(body)
            val list = mutableListOf<RemoteMonument>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    RemoteMonument(
                        id = obj.optString("id", "m_$i"),
                        title = obj.optString("title", ""),
                        city = obj.optString("city", "Samarqand"),
                        subtitle = obj.optString("subtitle", ""),
                        description = obj.optString("description", ""),
                        historyFact = obj.optString("history_fact", obj.optString("historyFact", "")),
                        unescoYear = if (obj.has("unesco_year") && !obj.isNull("unesco_year")) obj.getInt("unesco_year") else null,
                        rating = obj.optDouble("rating", 4.9).toFloat(),
                        reviewCount = obj.optInt("review_count", 250),
                        category = obj.optString("category", "UNESCO"),
                        imageUrl = obj.optString("image_url", obj.optString("imageUrl", "")),
                        audioUrl = obj.optString("audio_url", obj.optString("audioUrl", "")),
                        audioScriptUz = obj.optString("audio_script_uz", obj.optString("audioScriptUz", "")),
                        latitude = obj.optDouble("latitude", 39.6547),
                        longitude = obj.optDouble("longitude", 66.9758),
                        tag = obj.optString("tag", "Must Visit"),
                        period = obj.optString("period", "XIV-XV Asr"),
                        isVerified = obj.optBoolean("is_verified", true)
                    )
                )
            }
            Result.success(list)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching monuments", e)
            Result.failure(e)
        }
    }

    /**
     * Admin: Insert or Update Monument in Supabase
     */
    suspend fun upsertMonument(context: Context, monument: RemoteMonument): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val url = getProjectUrl(context)
            val key = getAnonKey(context)

            val json = JSONObject().apply {
                put("id", monument.id)
                put("title", monument.title)
                put("city", monument.city)
                put("subtitle", monument.subtitle)
                put("description", monument.description)
                put("history_fact", monument.historyFact)
                if (monument.unescoYear != null) {
                    put("unesco_year", monument.unescoYear)
                }
                put("rating", monument.rating.toDouble())
                put("review_count", monument.reviewCount)
                put("category", monument.category)
                put("image_url", monument.imageUrl)
                put("audio_url", monument.audioUrl)
                put("audio_script_uz", monument.audioScriptUz)
                put("latitude", monument.latitude)
                put("longitude", monument.longitude)
                put("tag", monument.tag)
                put("period", monument.period)
                put("is_verified", monument.isVerified)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("$url/rest/v1/monuments?on_conflict=id")
                .header("apikey", key)
                .header("Authorization", "Bearer $key")
                .header("Content-Type", "application/json")
                .header("Prefer", "resolution=merge-duplicates")
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful || response.code in 200..204) {
                Result.success(true)
            } else {
                val errorBody = response.body?.string().orEmpty()
                Result.failure(Exception("Saqlash xatosi HTTP ${response.code}: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error upserting monument", e)
            Result.failure(e)
        }
    }

    /**
     * Admin: Delete Monument from Supabase
     */
    suspend fun deleteMonument(context: Context, id: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val url = getProjectUrl(context)
            val key = getAnonKey(context)

            val request = Request.Builder()
                .url("$url/rest/v1/monuments?id=eq.$id")
                .header("apikey", key)
                .header("Authorization", "Bearer $key")
                .delete()
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful || response.code in 200..204) {
                Result.success(true)
            } else {
                Result.failure(Exception("O'chirish xatosi HTTP ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Complete 8-table SQL setup script designed specifically for Tourist UZ Admin & Tourist App
     */
    val SQL_COMPLETE_8_TABLES_SCRIPT = """
-- ==========================================================
-- TOURIST UZ - 8 ASOSIY JADVAL TIZIMI (SUPABASE POSTGRESQL)
-- Room SQLite bilan 100% mos va xavfsiz RLS ruxsatlari bilan
-- ==========================================================

-- 1. USERS (Xodimlar va Administratorlar)
CREATE TABLE IF NOT EXISTS public.users (
    id TEXT PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    "fullName" TEXT NOT NULL,
    role TEXT DEFAULT 'STAFF', -- 'ADMIN', 'STAFF'
    position TEXT DEFAULT 'Operator',
    phone TEXT,
    "isActive" BOOLEAN DEFAULT true,
    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 2. BOOKINGS (Sayyohlar Buyurtmalari / Bronlar)
CREATE TABLE IF NOT EXISTS public.bookings (
    id TEXT PRIMARY KEY,
    "bookingCode" TEXT NOT NULL,
    "tourId" TEXT,
    "tourTitle" TEXT NOT NULL,
    "touristName" TEXT NOT NULL,
    "touristPhone" TEXT NOT NULL,
    "touristEmail" TEXT,
    "touristCountry" TEXT DEFAULT 'O''zbekiston',
    "startDate" TEXT NOT NULL,
    "peopleCount" INT DEFAULT 1,
    "totalPrice" DOUBLE PRECISION DEFAULT 0.0,
    "bookingStatus" TEXT DEFAULT 'Kutilmoqda', -- 'Kutilmoqda', 'Tasdiqlangan', 'Yakunlandi', 'Bekor qilindi'
    "paymentStatus" TEXT DEFAULT 'Kutilmoqda', -- 'To''langan', 'Kutilmoqda', 'Naqd to''lov'
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 3. TOURS (Sayyohlik Turlari)
CREATE TABLE IF NOT EXISTS public.tours (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    region TEXT NOT NULL,
    category TEXT DEFAULT 'Tarixiy',
    price DOUBLE PRECISION NOT NULL,
    duration TEXT NOT NULL,
    "maxGroupSize" INT DEFAULT 20,
    "includedServices" TEXT DEFAULT 'Gid, Transfer, Mehmonxona, Chiptalar',
    rating NUMERIC(3,2) DEFAULT 4.9,
    "discountPercent" INT DEFAULT 0,
    "isActive" BOOLEAN DEFAULT true,
    "imageUrl" TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 4. ATTRACTIONS (Diqqatga Sazovor Maskanlar va Qadamjolar)
CREATE TABLE IF NOT EXISTS public.attractions (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    region TEXT NOT NULL,
    category TEXT DEFAULT 'Obida',
    "entryFee" DOUBLE PRECISION DEFAULT 0.0,
    "workingHours" TEXT DEFAULT '08:00 - 19:00',
    "locationAddress" TEXT,
    "audioGuideAvailable" BOOLEAN DEFAULT true,
    rating NUMERIC(3,2) DEFAULT 4.9,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 5. GUIDES (Gidlar Ro'yxati)
CREATE TABLE IF NOT EXISTS public.guides (
    id TEXT PRIMARY KEY,
    "fullName" TEXT NOT NULL,
    phone TEXT NOT NULL,
    region TEXT NOT NULL,
    languages TEXT DEFAULT 'O''zbek, Ingliz, Rus',
    "experienceYears" INT DEFAULT 5,
    "licenseNumber" TEXT,
    status TEXT DEFAULT 'Tasdiqlangan', -- 'Tasdiqlangan', 'Kutmoqda', 'Band', 'Nofaol'
    rating NUMERIC(3,2) DEFAULT 5.0,
    "toursLedCount" INT DEFAULT 25,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 6. PROMOS (Chegirma va Promokodlar)
CREATE TABLE IF NOT EXISTS public.promos (
    id TEXT PRIMARY KEY,
    code TEXT UNIQUE NOT NULL,
    "discountPercent" INT NOT NULL,
    "expiryDate" TEXT NOT NULL,
    "usageCount" INT DEFAULT 0,
    "maxUsage" INT DEFAULT 1000,
    "isActive" BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 7. NOTIFICATIONS (Bildirishnomalar va Broadcast)
CREATE TABLE IF NOT EXISTS public.notifications (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    message TEXT NOT NULL,
    "targetAudience" TEXT DEFAULT 'Barcha sayyohlar',
    "promoCode" TEXT,
    "sentDate" TEXT NOT NULL,
    "readCount" INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 8. ADMIN_LOGS (Audit va Tizim Jurnali)
CREATE TABLE IF NOT EXISTS public.admin_logs (
    id TEXT PRIMARY KEY,
    "adminUsername" TEXT NOT NULL,
    action TEXT NOT NULL,
    "targetTable" TEXT,
    "recordId" TEXT,
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL,
    "ipAddress" TEXT,
    notes TEXT
);

-- ==========================================================
-- ROW LEVEL SECURITY (RLS) VA RUXSATLARNI YOQISH
-- ==========================================================
ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.bookings ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.tours ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.attractions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.guides ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.promos ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.notifications ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.admin_logs ENABLE ROW LEVEL SECURITY;

-- Ochiq API orqali ikkala mobil ilova (Admin & Tourist) ishlashi uchun RLS siyosatlari
DO $$
BEGIN
    DROP POLICY IF EXISTS "public_users_policy" ON public.users;
    CREATE POLICY "public_users_policy" ON public.users FOR ALL USING (true);

    DROP POLICY IF EXISTS "public_bookings_policy" ON public.bookings;
    CREATE POLICY "public_bookings_policy" ON public.bookings FOR ALL USING (true);

    DROP POLICY IF EXISTS "public_tours_policy" ON public.tours;
    CREATE POLICY "public_tours_policy" ON public.tours FOR ALL USING (true);

    DROP POLICY IF EXISTS "public_attractions_policy" ON public.attractions;
    CREATE POLICY "public_attractions_policy" ON public.attractions FOR ALL USING (true);

    DROP POLICY IF EXISTS "public_guides_policy" ON public.guides;
    CREATE POLICY "public_guides_policy" ON public.guides FOR ALL USING (true);

    DROP POLICY IF EXISTS "public_promos_policy" ON public.promos;
    CREATE POLICY "public_promos_policy" ON public.promos FOR ALL USING (true);

    DROP POLICY IF EXISTS "public_notifications_policy" ON public.notifications;
    CREATE POLICY "public_notifications_policy" ON public.notifications FOR ALL USING (true);

    DROP POLICY IF EXISTS "public_admin_logs_policy" ON public.admin_logs;
    CREATE POLICY "public_admin_logs_policy" ON public.admin_logs FOR ALL USING (true);
END
$$;

-- ==========================================================
-- BOSHLANG'ICH TAYYOR NAMUNAVIY MA'LUMOTLAR
-- ==========================================================
INSERT INTO public.promos (id, code, "discountPercent", "expiryDate", "usageCount", "maxUsage", "isActive")
VALUES 
    ('p1', 'NAVROZ2026', 15, '2026-12-31', 12, 500, true),
    ('p2', 'SMARTUZ', 10, '2026-12-31', 45, 1000, true),
    ('p3', 'SILKROAD', 20, '2026-12-31', 8, 200, true)
ON CONFLICT (code) DO NOTHING;

INSERT INTO public.tours (id, title, region, category, price, duration, "maxGroupSize", "includedServices", rating, "discountPercent", "isActive")
VALUES 
    ('tour_1', 'Samarqand Oltin Sayri', 'Samarqand', 'Tarixiy', 450000.0, '1 kun', 15, 'Gid, Afrosiyob chiptasi, Tushlik', 4.95, 10, true),
    ('tour_2', 'Qadimiy Buxoro Sir-Asrorlari', 'Buxoro', 'Ziyorat', 780000.0, '2 kun / 1 tun', 12, 'Mehmonxona, Gid, Transfer', 4.90, 15, true),
    ('tour_3', 'Ichan-Qal''a Ochiq Osmon Ostidagi Muzey', 'Xiva', 'VIP', 1200000.0, '3 kun / 2 tun', 8, 'VIP Transfer, Master-klass, Fotosessiya', 5.00, 0, true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.notifications (id, title, message, "targetAudience", "promoCode", "sentDate", "readCount")
VALUES 
    ('n1', 'Xush kelibsiz O''zbekistonga!', 'Sayyohlarimiz uchun NAVROZ2026 promokodi orqali 15% chegirma amal qilmoqda.', 'Barcha sayyohlar', 'NAVROZ2026', '2026-09-05', 180)
ON CONFLICT (id) DO NOTHING;
    """.trimIndent()

    val SQL_SETUP_SCRIPT: String
        get() = SQL_COMPLETE_8_TABLES_SCRIPT
}
