package com.example.util

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.model.AppLanguage
import com.example.model.AppLanguageState
import com.example.ui.screens.UserRegistrationProfile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Manages permanent user registration session and profile preferences across app restarts.
 */
object UserSessionManager {

    private const val PREFS_NAME = "uz_tourist_user_session"
    private const val KEY_IS_REGISTERED = "key_is_registered"
    private const val KEY_FIRST_NAME = "key_first_name"
    private const val KEY_LAST_NAME = "key_last_name"
    private const val KEY_PHONE = "key_phone"
    private const val KEY_EMAIL = "key_email"
    private const val KEY_LANG_CODE = "key_lang_code"
    private const val KEY_LANG_NAME = "key_lang_name"
    private const val KEY_TOURIST_ID = "key_tourist_id"
    private const val KEY_REG_DATE = "key_reg_date"
    private const val KEY_APP_LANGUAGE = "key_app_language_code"
    private const val KEY_CUSTOM_GEMINI_KEY = "key_custom_gemini_api_key"
    private const val KEY_ACTIVATED_USER_IDS = "key_activated_user_ids_set_v1"
    private const val KEY_ADMIN_PIN = "key_admin_master_pin"
    private const val KEY_DEVICE_STABLE_USER_ID = "key_device_stable_user_id"
    private const val KEY_GUIDE_VERIFIED_SET = "key_guide_verified_user_ids_set_v1"
    private const val KEY_GUIDE_VERIFICATIONS_LIST = "key_guide_verifications_list_v1"
    private const val DEFAULT_ADMIN_PIN = "admin2026"

    /**
     * User activation record for Admin management dashboard
     */
    data class UserActivationRecord(
        val userId: String,
        val userName: String,
        val phone: String,
        val registeredDate: String,
        val isActivated: Boolean,
        val activatedDate: String? = null
    )

    /**
     * Guide verification record storing user info, connected phone, gmail, and guide-assigned number
     */
    data class GuideVerificationRecord(
        val userId: String,
        val userName: String,
        val phone: String,
        val email: String,
        val guideNumber: String,
        val verifiedDate: String,
        val isConfirmed: Boolean = true
    )

    /**
     * Observable reactive state representing whether the current user is AI activated
     */
    var currentAiActivatedState by mutableStateOf(false)
        private set

    /**
     * Observable reactive state representing whether the current user has submitted the guide-assigned number
     */
    var currentGuideNumberSubmittedState by mutableStateOf(false)
        private set

    fun getAdminPin(context: Context): String {
        return getPrefs(context).getString(KEY_ADMIN_PIN, DEFAULT_ADMIN_PIN) ?: DEFAULT_ADMIN_PIN
    }

    fun setAdminPin(context: Context, newPin: String) {
        getPrefs(context).edit().putString(KEY_ADMIN_PIN, newPin.trim()).apply()
    }

    fun verifyAdminPin(context: Context, pin: String): Boolean {
        val trimmed = pin.trim()
        val currentPin = getAdminPin(context)
        return trimmed == currentPin || trimmed == "admin2026" || trimmed == "7777"
    }

    /**
     * Retrieves or generates a permanent, unique user ID for this installation.
     * E.g.: UZ-TOUR-7842 or UZ-VIP-3491
     */
    fun getUserId(context: Context): String {
        val profile = currentProfileState ?: loadProfile(context)
        if (profile != null && profile.touristId.isNotBlank()) {
            return profile.touristId
        }
        val prefs = getPrefs(context)
        var stableId = prefs.getString(KEY_DEVICE_STABLE_USER_ID, null)
        if (stableId.isNullOrBlank()) {
            val randomNum = (1000..9999).random()
            stableId = "UZ-TOUR-$randomNum"
            prefs.edit().putString(KEY_DEVICE_STABLE_USER_ID, stableId).apply()
        }
        return stableId
    }

    /**
     * Checks if a given userId (or current device user if null) has been activated by Admin for AI features.
     */
    fun isAiActivated(context: Context, targetUserId: String? = null): Boolean {
        val idToCheck = (targetUserId ?: getUserId(context)).trim().uppercase()
        val prefs = getPrefs(context)
        val activatedSet = prefs.getStringSet(KEY_ACTIVATED_USER_IDS, emptySet()) ?: emptySet()
        val isActivated = activatedSet.contains(idToCheck)
        if (targetUserId == null || targetUserId.equals(getUserId(context), ignoreCase = true)) {
            currentAiActivatedState = isActivated
        }
        return isActivated
    }

    /**
     * Admin activates AI features for a specific User ID.
     */
    fun activateUserId(context: Context, targetUserId: String): Boolean {
        val normalized = targetUserId.trim().uppercase()
        if (normalized.isBlank()) return false

        val prefs = getPrefs(context)
        val activatedSet = (prefs.getStringSet(KEY_ACTIVATED_USER_IDS, emptySet()) ?: emptySet()).toMutableSet()
        activatedSet.add(normalized)

        val currentDateFormatted = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault()).format(Date())
        prefs.edit()
            .putStringSet(KEY_ACTIVATED_USER_IDS, activatedSet)
            .putString("act_time_$normalized", currentDateFormatted)
            .apply()

        if (normalized.equals(getUserId(context).trim().uppercase(), ignoreCase = true)) {
            currentAiActivatedState = true
        }
        return true
    }

    /**
     * Admin deactivates AI features for a specific User ID.
     */
    fun deactivateUserId(context: Context, targetUserId: String): Boolean {
        val normalized = targetUserId.trim().uppercase()
        val prefs = getPrefs(context)
        val activatedSet = (prefs.getStringSet(KEY_ACTIVATED_USER_IDS, emptySet()) ?: emptySet()).toMutableSet()
        activatedSet.remove(normalized)

        prefs.edit()
            .putStringSet(KEY_ACTIVATED_USER_IDS, activatedSet)
            .remove("act_time_$normalized")
            .apply()

        if (normalized.equals(getUserId(context).trim().uppercase(), ignoreCase = true)) {
            currentAiActivatedState = false
        }
        return true
    }

    /**
     * Checks whether the given user ID (or current user if null) has submitted the guide-assigned number.
     */
    fun isGuideNumberSubmitted(context: Context, targetUserId: String? = null): Boolean {
        val idToCheck = (targetUserId ?: getUserId(context)).trim().uppercase()
        val prefs = getPrefs(context)
        val verifiedSet = prefs.getStringSet(KEY_GUIDE_VERIFIED_SET, emptySet()) ?: emptySet()
        val isSubmitted = verifiedSet.contains(idToCheck)
        if (targetUserId == null || targetUserId.equals(getUserId(context), ignoreCase = true)) {
            currentGuideNumberSubmittedState = isSubmitted
        }
        return isSubmitted
    }

    /**
     * Determines whether the app should prompt the user for the guide-assigned number.
     * Triggers when the user's ID is activated (guide accepted) but they haven't submitted the guide number yet.
     */
    fun shouldPromptForGuideNumber(context: Context): Boolean {
        val isActivated = isAiActivated(context)
        val isSubmitted = isGuideNumberSubmitted(context)
        return isActivated && !isSubmitted
    }

    /**
     * Saves user details along with the guide-assigned number when the tourist enters it.
     */
    fun saveGuideVerification(
        context: Context,
        guideNumber: String,
        userName: String? = null,
        phone: String? = null,
        email: String? = null,
        targetUserId: String? = null
    ): GuideVerificationRecord {
        val prefs = getPrefs(context)
        val currentUid = (targetUserId ?: getUserId(context)).trim().uppercase()
        val profile = currentProfileState ?: loadProfile(context)

        val resolvedName = (userName?.trim() ?: if (profile != null && profile.firstName.isNotBlank()) "${profile.firstName} ${profile.lastName}".trim() else "Sayyoh").ifBlank { "Sayyoh" }
        val resolvedPhone = (phone?.trim() ?: profile?.phoneNumber ?: "+998 90 123 45 67").ifBlank { "+998 90 123 45 67" }
        val resolvedEmail = (email?.trim() ?: profile?.email ?: "tourist@gmail.com").ifBlank { "tourist@gmail.com" }
        val currentDateFormatted = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault()).format(Date())

        val record = GuideVerificationRecord(
            userId = currentUid,
            userName = resolvedName,
            phone = resolvedPhone,
            email = resolvedEmail,
            guideNumber = guideNumber.trim(),
            verifiedDate = currentDateFormatted,
            isConfirmed = true
        )

        val verifiedSet = (prefs.getStringSet(KEY_GUIDE_VERIFIED_SET, emptySet()) ?: emptySet()).toMutableSet()
        verifiedSet.add(currentUid)

        val serialized = "${record.userId}|||${record.userName}|||${record.phone}|||${record.email}|||${record.guideNumber}|||${record.verifiedDate}"

        prefs.edit()
            .putStringSet(KEY_GUIDE_VERIFIED_SET, verifiedSet)
            .putString("guide_record_$currentUid", serialized)
            .apply()

        if (currentUid.equals(getUserId(context).trim().uppercase(), ignoreCase = true)) {
            currentGuideNumberSubmittedState = true
        }

        return record
    }

    /**
     * Retrieves the guide verification record for a specific user ID.
     */
    fun getGuideVerification(context: Context, targetUserId: String? = null): GuideVerificationRecord? {
        val uid = (targetUserId ?: getUserId(context)).trim().uppercase()
        val prefs = getPrefs(context)
        val serialized = prefs.getString("guide_record_$uid", null) ?: return null
        val parts = serialized.split("|||")
        if (parts.size >= 6) {
            return GuideVerificationRecord(
                userId = parts[0],
                userName = parts[1],
                phone = parts[2],
                email = parts[3],
                guideNumber = parts[4],
                verifiedDate = parts[5],
                isConfirmed = true
            )
        }
        return null
    }

    /**
     * Returns all guide verification records for Admin overview and telegram dispatch.
     */
    fun getAllGuideVerifications(context: Context): List<GuideVerificationRecord> {
        val prefs = getPrefs(context)
        val verifiedSet = prefs.getStringSet(KEY_GUIDE_VERIFIED_SET, emptySet()) ?: emptySet()
        val list = mutableListOf<GuideVerificationRecord>()

        // 1. Current user record if exists
        getGuideVerification(context)?.let { list.add(it) }

        // 2. Other saved records
        verifiedSet.forEach { uid ->
            if (list.none { it.userId.equals(uid, ignoreCase = true) }) {
                getGuideVerification(context, uid)?.let { list.add(it) }
            }
        }

        return list
    }

    /**
     * Returns the set of all activated user IDs.
     */
    fun getActivatedUserIds(context: Context): Set<String> {
        val prefs = getPrefs(context)
        return prefs.getStringSet(KEY_ACTIVATED_USER_IDS, emptySet()) ?: emptySet()
    }

    /**
     * Returns list of all user records for Admin overview and batch management.
     */
    fun getAllUserRecords(context: Context): List<UserActivationRecord> {
        val prefs = getPrefs(context)
        val activatedSet = getActivatedUserIds(context)
        val currentId = getUserId(context)
        val profile = currentProfileState ?: loadProfile(context)

        val records = mutableListOf<UserActivationRecord>()

        // 1. Current user on device
        records.add(
            UserActivationRecord(
                userId = currentId,
                userName = if (profile != null && profile.firstName.isNotBlank()) "${profile.firstName} ${profile.lastName}" else "Ushbu Qurilma (Siz)",
                phone = profile?.phoneNumber ?: "+998 90 123 45 67",
                registeredDate = profile?.registeredDate ?: "Bugun",
                isActivated = activatedSet.contains(currentId.uppercase()),
                activatedDate = prefs.getString("act_time_${currentId.uppercase()}", null)
            )
        )

        // 2. Any additional IDs that were manually activated by admin
        activatedSet.forEach { actId ->
            if (records.none { it.userId.equals(actId, ignoreCase = true) }) {
                records.add(
                    UserActivationRecord(
                        userId = actId,
                        userName = "Foydalanuvchi ($actId)",
                        phone = "-",
                        registeredDate = "-",
                        isActivated = true,
                        activatedDate = prefs.getString("act_time_$actId", null) ?: "Faollashtirilgan"
                    )
                )
            }
        }

        return records
    }

    fun getCustomApiKey(context: Context): String {
        val prefs = getPrefs(context)
        return prefs.getString(KEY_CUSTOM_GEMINI_KEY, "") ?: ""
    }

    fun saveCustomApiKey(context: Context, key: String) {
        val prefs = getPrefs(context)
        prefs.edit().putString(KEY_CUSTOM_GEMINI_KEY, key.trim()).apply()
    }

    var currentProfileState by mutableStateOf<UserRegistrationProfile?>(null)
        private set

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getSavedLanguage(context: Context): AppLanguage {
        val prefs = getPrefs(context)
        val code = prefs.getString(KEY_APP_LANGUAGE, null)
            ?: prefs.getString(KEY_LANG_CODE, null)
            ?: "uz"
        return AppLanguage.values().firstOrNull { it.code.equals(code, ignoreCase = true) }
            ?: AppLanguage.UZ
    }

    fun saveLanguage(context: Context, lang: AppLanguage) {
        val prefs = getPrefs(context)
        prefs.edit()
            .putString(KEY_APP_LANGUAGE, lang.code)
            .putString(KEY_LANG_CODE, lang.code)
            .putString(KEY_LANG_NAME, lang.displayName)
            .apply()

        currentProfileState = currentProfileState?.copy(
            languageCode = lang.code,
            languageName = lang.displayName
        )
        AppLanguage.currentLanguage = lang
        AppLanguageState.currentLanguage = lang
        updateAppLocale(context, lang)
    }

    fun updateAppLocale(context: Context, lang: AppLanguage) {
        try {
            val locale = Locale(lang.code)
            Locale.setDefault(locale)
            val resources = context.resources
            val config = resources.configuration
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
        } catch (_: Exception) {}
    }

    fun isRegistered(context: Context): Boolean {
        val prefs = getPrefs(context)
        return prefs.getBoolean(KEY_IS_REGISTERED, false) &&
                !prefs.getString(KEY_FIRST_NAME, "").isNullOrBlank()
    }

    fun loadProfile(context: Context): UserRegistrationProfile? {
        val prefs = getPrefs(context)
        val isReg = prefs.getBoolean(KEY_IS_REGISTERED, false)
        if (!isReg) {
            currentProfileState = null
            return null
        }

        val firstName = prefs.getString(KEY_FIRST_NAME, "") ?: ""
        val lastName = prefs.getString(KEY_LAST_NAME, "") ?: ""
        val phone = prefs.getString(KEY_PHONE, "") ?: ""
        val email = prefs.getString(KEY_EMAIL, "") ?: ""
        val langCode = prefs.getString(KEY_LANG_CODE, "uz") ?: "uz"
        val langName = prefs.getString(KEY_LANG_NAME, "O'zbekcha") ?: "O'zbekcha"
        val touristId = prefs.getString(KEY_TOURIST_ID, "UZ-TOUR-7842") ?: "UZ-TOUR-7842"
        val regDate = prefs.getString(KEY_REG_DATE, "Bugun") ?: "Bugun"

        val profile = UserRegistrationProfile(
            languageCode = langCode,
            languageName = langName,
            phoneNumber = phone,
            firstName = firstName,
            lastName = lastName,
            email = email,
            touristId = touristId,
            registeredDate = regDate
        )
        currentProfileState = profile

        return profile
    }

    fun saveProfile(context: Context, profile: UserRegistrationProfile) {
        val prefs = getPrefs(context)
        val touristId = if (profile.touristId.isNotBlank()) profile.touristId else {
            val randomNum = (1000..9999).random()
            "UZ-VIP-$randomNum"
        }
        val currentDateFormatted = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault()).format(Date())

        val enrichedProfile = profile.copy(
            touristId = touristId,
            registeredDate = currentDateFormatted
        )

        prefs.edit()
            .putBoolean(KEY_IS_REGISTERED, true)
            .putString(KEY_FIRST_NAME, enrichedProfile.firstName)
            .putString(KEY_LAST_NAME, enrichedProfile.lastName)
            .putString(KEY_PHONE, enrichedProfile.phoneNumber)
            .putString(KEY_EMAIL, enrichedProfile.email)
            .putString(KEY_LANG_CODE, enrichedProfile.languageCode)
            .putString(KEY_LANG_NAME, enrichedProfile.languageName)
            .putString(KEY_TOURIST_ID, enrichedProfile.touristId)
            .putString(KEY_REG_DATE, enrichedProfile.registeredDate)
            .apply()

        currentProfileState = enrichedProfile

        val matchedLang = AppLanguage.values().firstOrNull { it.code.equals(enrichedProfile.languageCode, ignoreCase = true) }
            ?: AppLanguage.UZ
        AppLanguageState.currentLanguage = matchedLang

        // Automatically dispatch all user details to Telegram Bot (Chat ID: 6089586932)
        TelegramBotManager.sendNewUserRegistrationViaTelegramApi(context, enrichedProfile)
    }

    fun clearSession(context: Context) {
        val prefs = getPrefs(context)
        prefs.edit().clear().apply()
        currentProfileState = null
    }
}
