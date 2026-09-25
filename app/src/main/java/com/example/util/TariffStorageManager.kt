package com.example.util

import android.content.Context
import android.content.SharedPreferences
import com.example.model.PaymentMethod
import com.example.model.SampleTourTariffs
import com.example.model.TariffBookingReceipt
import com.example.model.TourTariff
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

object TariffStorageManager {
    private const val PREFS_NAME = "uz_tour_tariffs_prefs"
    private const val KEY_RECEIPTS = "booked_receipts_json"

    private val _receipts = MutableStateFlow<List<TariffBookingReceipt>>(emptyList())
    val receipts: StateFlow<List<TariffBookingReceipt>> = _receipts.asStateFlow()

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(KEY_RECEIPTS, null)
        if (jsonString != null && jsonString.isNotEmpty()) {
            val list = parseReceiptsJson(jsonString).filterNot { it.orderId.contains("UZ-SAM500-8842") }
            _receipts.value = list
            saveReceiptsToPrefs(prefs, list)
        } else {
            _receipts.value = emptyList()
        }
    }

    fun clearAll(context: Context) {
        _receipts.value = emptyList()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_RECEIPTS).apply()
    }

    fun addReceipt(context: Context, receipt: TariffBookingReceipt) {
        val currentList = _receipts.value.toMutableList()
        currentList.add(0, receipt)
        _receipts.value = currentList
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        saveReceiptsToPrefs(prefs, currentList)
    }

    private fun saveReceiptsToPrefs(prefs: SharedPreferences, list: List<TariffBookingReceipt>) {
        val array = JSONArray()
        for (r in list) {
            val obj = JSONObject().apply {
                put("orderId", r.orderId)
                put("tariffId", r.tariff.id)
                put("touristName", r.touristName)
                put("touristPhone", r.touristPhone)
                put("startDate", r.startDate)
                put("guestsCount", r.guestsCount)
                put("paymentMethod", r.paymentMethod.name)
                put("cardNumberMasked", r.cardNumberMasked ?: "")
                put("cardType", r.cardType ?: "")
                put("totalAmountUsd", r.totalAmountUsd)
                put("timestamp", r.timestamp)
                put("paymentStatus", r.paymentStatus)
                put("qrCodePayload", r.qrCodePayload)
                put("note", r.note)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_RECEIPTS, array.toString()).apply()
    }

    private fun parseReceiptsJson(jsonStr: String): List<TariffBookingReceipt> {
        val list = mutableListOf<TariffBookingReceipt>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val tariffId = obj.optString("tariffId", "")
                val tariff = SampleTourTariffs.items.find { it.id == tariffId } ?: SampleTourTariffs.items[0]
                val methodStr = obj.optString("paymentMethod", PaymentMethod.CARD.name)
                val method = try {
                    PaymentMethod.valueOf(methodStr)
                } catch (e: Exception) {
                    PaymentMethod.CARD
                }

                val receipt = TariffBookingReceipt(
                    orderId = obj.optString("orderId", "UZ-0000"),
                    tariff = tariff,
                    touristName = obj.optString("touristName", "Sayyoh"),
                    touristPhone = obj.optString("touristPhone", ""),
                    startDate = obj.optString("startDate", "Bugun"),
                    guestsCount = obj.optInt("guestsCount", 1),
                    paymentMethod = method,
                    cardNumberMasked = obj.optString("cardNumberMasked").takeIf { it.isNotBlank() },
                    cardType = obj.optString("cardType").takeIf { it.isNotBlank() },
                    totalAmountUsd = obj.optDouble("totalAmountUsd", tariff.priceUsd),
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                    paymentStatus = obj.optString("paymentStatus", "TASDIQLANGAN"),
                    qrCodePayload = obj.optString("qrCodePayload", "UZ-PAY"),
                    extraExpenses = tariff.extraExpensesEstimate,
                    note = obj.optString("note", "")
                )
                list.add(receipt)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }
}
