package com.example.util

import com.example.model.AppLanguage
import com.example.model.ArMonumentFact
import com.example.model.CulturalEvent
import com.example.model.Destination
import com.example.model.SampleArFacts
import com.example.model.SampleCulturalEvents
import com.example.model.SampleDestinations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

data class LiveMonumentSnapshot(
    val monument: ArMonumentFact,
    val liveWeatherTempC: Int,
    val liveWeatherDesc: String,
    val currentCrowdStatus: String,
    val currentMinuteNote: String,
    val lastUpdatedTime: String
)

data class MinuteDataState(
    val minuteCount: Long = 0,
    val secondsRemaining: Int = 60,
    val currentArFact: ArMonumentFact = SampleArFacts.items.first(),
    val currentArFactIndex: Int = 0,
    val currentDestination: Destination = SampleDestinations.items.first(),
    val currentCulturalEvent: CulturalEvent = SampleCulturalEvents.items.first(),
    val dynamicWeatherTempC: Int = 26,
    val dynamicWeatherText: String = "Quyoshli & Ochiq osmon",
    val activeVisitorsCount: Int = 340,
    val lastUpdatedTimestamp: String = "Hozir yangilandi",
    val liveFactsBanner: String = "Jonli ma'lumotlar zaxirasi har minutda avtomatik yangilanadi"
)

object MinuteDataRotationManager {
    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private var tickerJob: Job? = null

    private val _state = MutableStateFlow(MinuteDataState())
    val state: StateFlow<MinuteDataState> = _state.asStateFlow()

    private val weatherConditions = listOf(
        Pair(24, "Tiniq quyoshli, shabboda"),
        Pair(26, "Ipak yo'li oftobi, havo ochiq"),
        Pair(27, "Iliq moviy osmon"),
        Pair(25, "Mayin shabada, sayohat uchun a'lo"),
        Pair(23, "Salqin shabboda, sof tog' nafasi"),
        Pair(28, "Quyoshli va iliq kun")
    )

    private val silkRoadLiveTips = listOf(
        "Registon maydonida quyosh nurlari Tilya-Kori tillarang gumbaziga tushmoqda ☀️",
        "Minorai Kalon atrofida g'ishtlarning naqshinkor soyalari o'zgarmoqda 🕌",
        "Ichan Qal'a tor ko'chalarida kulolchilik va ipak to'quv ustalari ishlamoqda 🏺",
        "Go'ri Amir moviy qovurg'ali gumbazida moviy feruza koshinlar jilolanmoqda 💎",
        "Buxoro Arki forpostida sharqona qadimiy sokinlik hukm surmoqda 🏰",
        "Chor Minor va Labi Hovuz choyxonalarida za'faron choy va qandlar tortilmoqda 🫖",
        "Charvak va Chimgan tog'larida musaffo shabboda va feruza suv to'lqinlari 🏔️"
    )

    init {
        startMinuteTicker()
    }

    private fun startMinuteTicker() {
        if (tickerJob?.isActive == true) return

        tickerJob = scope.launch {
            // Update immediately on launch
            updateData()

            var secondsLeft = 60
            while (isActive) {
                delay(1000L)
                secondsLeft--
                if (secondsLeft <= 0) {
                    secondsLeft = 60
                    updateData()
                } else {
                    _state.value = _state.value.copy(secondsRemaining = secondsLeft)
                }
            }
        }
    }

    fun rotateNextNow() {
        scope.launch {
            updateData()
        }
    }

    private fun updateData() {
        val current = _state.value
        val newCount = current.minuteCount + 1

        val arFacts = SampleArFacts.items
        val newArIndex = ((current.currentArFactIndex + 1) % arFacts.size)
        val newArFact = arFacts[newArIndex]

        val destinations = SampleDestinations.items
        val newDest = destinations[(newCount.toInt() % destinations.size)]

        val events = SampleCulturalEvents.items
        val newEvent = events[(newCount.toInt() % events.size)]

        val randomWeather = weatherConditions.random()
        val randomVisitors = 180 + Random.nextInt(450)
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val formattedTime = timeFormat.format(Date())

        val dynamicBanner = silkRoadLiveTips[(newCount.toInt() % silkRoadLiveTips.size)]

        _state.value = MinuteDataState(
            minuteCount = newCount,
            secondsRemaining = 60,
            currentArFact = newArFact,
            currentArFactIndex = newArIndex,
            currentDestination = newDest,
            currentCulturalEvent = newEvent,
            dynamicWeatherTempC = randomWeather.first,
            dynamicWeatherText = randomWeather.second,
            activeVisitorsCount = randomVisitors,
            lastUpdatedTimestamp = formattedTime,
            liveFactsBanner = dynamicBanner
        )
    }
}
