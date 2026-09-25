package com.example.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.network.SupabaseRealtimeManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SosEmergencyRecord(
    val id: String = "SOS-" + (100000..999999).random(),
    val touristName: String,
    val touristPhone: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val reason: String,
    val timestamp: String = SimpleDateFormat("HH:mm:ss, dd.MM.yyyy", Locale.getDefault()).format(Date()),
    val isResolved: Boolean = false,
    val resolvedTime: String? = null
)

object SosEmergencyManager {
    private const val TAG = "SosEmergencyManager"
    private const val PREFS_NAME = "uz_turist_sos_admin_prefs"
    private const val KEY_EMERGENCIES = "saved_sos_emergency_records"

    // Live alert trigger for in-app alert banner
    private val _latestAlert = MutableStateFlow<SosEmergencyRecord?>(null)
    val latestAlert: StateFlow<SosEmergencyRecord?> = _latestAlert.asStateFlow()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getAllEmergencies(context: Context): List<SosEmergencyRecord> {
        val prefs = getPrefs(context)
        val jsonStr = prefs.getString(KEY_EMERGENCIES, null) ?: return emptyList()
        return try {
            val jsonArray = JSONArray(jsonStr)
            val list = mutableListOf<SosEmergencyRecord>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    SosEmergencyRecord(
                        id = obj.optString("id"),
                        touristName = obj.optString("touristName"),
                        touristPhone = obj.optString("touristPhone"),
                        latitude = obj.optDouble("latitude", 41.3111),
                        longitude = obj.optDouble("longitude", 69.2797),
                        address = obj.optString("address"),
                        reason = obj.optString("reason"),
                        timestamp = obj.optString("timestamp"),
                        isResolved = obj.optBoolean("isResolved", false),
                        resolvedTime = if (obj.has("resolvedTime") && !obj.isNull("resolvedTime")) obj.getString("resolvedTime") else null
                    )
                )
            }
            list
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing saved emergencies", e)
            emptyList()
        }
    }

    fun recordEmergency(
        context: Context,
        record: SosEmergencyRecord
    ) {
        val currentList = getAllEmergencies(context).toMutableList()
        // Remove duplicate if same id
        currentList.removeAll { it.id == record.id }
        // Add to the top of list
        currentList.add(0, record)
        saveList(context, currentList)

        // Trigger in-app live alert banner
        _latestAlert.value = record

        // Broadcast to all connected Admin apps / devices via Supabase Realtime WebSocket
        try {
            SupabaseRealtimeManager.broadcastSosEmergency(context, record)
        } catch (e: Exception) {
            Log.e(TAG, "Error broadcasting SOS to Supabase", e)
        }
    }

    fun resolveEmergency(context: Context, id: String) {
        val currentList = getAllEmergencies(context).toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            val old = currentList[index]
            val resolvedTime = SimpleDateFormat("HH:mm:ss, dd.MM.yyyy", Locale.getDefault()).format(Date())
            currentList[index] = old.copy(isResolved = true, resolvedTime = resolvedTime)
            saveList(context, currentList)
        }
    }

    fun deleteEmergency(context: Context, id: String) {
        val currentList = getAllEmergencies(context).toMutableList()
        currentList.removeAll { it.id == id }
        saveList(context, currentList)
    }

    fun dismissLatestAlert() {
        _latestAlert.value = null
    }

    private fun saveList(context: Context, list: List<SosEmergencyRecord>) {
        try {
            val jsonArray = JSONArray()
            for (rec in list) {
                val obj = JSONObject().apply {
                    put("id", rec.id)
                    put("touristName", rec.touristName)
                    put("touristPhone", rec.touristPhone)
                    put("latitude", rec.latitude)
                    put("longitude", rec.longitude)
                    put("address", rec.address)
                    put("reason", rec.reason)
                    put("timestamp", rec.timestamp)
                    put("isResolved", rec.isResolved)
                    put("resolvedTime", rec.resolvedTime)
                }
                jsonArray.put(obj)
            }
            getPrefs(context).edit().putString(KEY_EMERGENCIES, jsonArray.toString()).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error saving emergencies list", e)
        }
    }
}
