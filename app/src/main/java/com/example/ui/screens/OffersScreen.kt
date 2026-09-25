package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AirlineSeatReclineExtra
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.DiningOffer
import com.example.model.LodgingOffer
import com.example.model.SampleOffers
import com.example.model.ServiceType
import com.example.model.TaxiOffer
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.ui.components.AudioTourPlayerSheet
import com.example.ui.components.CurrencyBudgetSheet
import com.example.ui.components.LanguageSelectorDialog
import com.example.ui.components.SosEmergencyDialog
import com.example.util.GpsLocationManager
import com.example.util.SosDispatchHelper
import com.example.util.SosSessionManager
import com.example.util.UserSessionManager
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassPillBadge
import com.example.ui.components.UzbekStarEmblem
import com.example.ui.components.threed.Celestial3DAstrolabe
import com.example.ui.components.threed.LiveOnlineStatusHub
import com.example.ui.components.threed.Parallax3DContainer
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.MidnightCanvas
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.SilkGold
import com.example.ui.theme.SilkGoldLight
import com.example.ui.theme.TurquoiseTile
import com.example.util.PrivateTaxiParkHelper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OffersScreen(
    onNavigateToMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    var selectedServiceType by remember { mutableStateOf(ServiceType.ALL) }
    var selectedCityFilter by remember { mutableStateOf("Barchasi") }

    // Dialog state for Taxi Booking
    var selectedTaxiForBooking by remember { mutableStateOf<TaxiOffer?>(null) }
    var passengerPickupLocation by remember { mutableStateOf("Samarqand Markaziy Vokzal") }
    var passengerDestinationLocation by remember { mutableStateOf("Registon Maydoni") }
    var passengerName by remember { mutableStateOf("") }
    var passengerPhone by remember { mutableStateOf("+998 ") }
    var meetTablichkaName by remember { mutableStateOf("") }

    // Dialog state for Dining & Lodging
    var bookingDialogItemName by remember { mutableStateOf<String?>(null) }
    var bookingDialogType by remember { mutableStateOf("hotel") } // hotel, dining
    var guestName by remember { mutableStateOf("") }
    var guestPhone by remember { mutableStateOf("+998 ") }

    // Companion states (SOS, Audio Guide, Currency, Language)
    var showSosDialog by remember { mutableStateOf(false) }
    var showAudioTourSheet by remember { mutableStateOf(false) }
    var showCurrencySheet by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var currentLanguage by remember { mutableStateOf(AppLanguage.currentLanguage) }
    val scope = rememberCoroutineScope()
    val liveGpsState by GpsLocationManager.currentLocation.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        GpsLocationManager.startLocationUpdates(context)
    }

    val onTriggerSosWithGps: () -> Unit = {
        scope.launch {
            val freshGps = GpsLocationManager.getAccurateCurrentLocation(context)
            val profile = UserSessionManager.loadProfile(context)
            val fullName = profile?.let { "${it.firstName} ${it.lastName}".trim() }
            val name = if (!fullName.isNullOrBlank()) fullName else "Hurmatli Sayyoh"
            val userPhone = profile?.phoneNumber?.trim()
            val phone = if (!userPhone.isNullOrBlank()) userPhone else "+998 91 033 04 60"

            SosSessionManager.startSos(
                context = context,
                name = name,
                phone = phone,
                lat = freshGps.latitude,
                lon = freshGps.longitude,
                address = freshGps.addressEstimate,
                reason = "SOS Favqulodda Yordam Chaqiruvi (Xizmatlar / Taksi)"
            )
            showSosDialog = true
        }
    }

    val cities = listOf("Barchasi", "Samarqand", "Buxoro", "Xiva", "Toshkent")

    val bgBrush = Brush.verticalGradient(
        colors = if (isDark) {
            listOf(MidnightCanvas, Color(0xFF06152E), Color(0xFF020712))
        } else {
            listOf(Color(0xFFF0F5FD), Color(0xFFE5EEFC), Color(0xFFDCE8F8))
        }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(bottom = 90.dp)
        ) {
            // Header Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                UzbekStarEmblem(
                                    size = 20.dp,
                                    primaryColor = NeonGold,
                                    secondaryColor = TurquoiseTile
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "IPAK YO'LI EKSKLYUZIV SERVISLAR",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.5.sp
                                    ),
                                    color = if (isDark) NeonGold else SilkGold
                                )
                            }
                            Text(
                                text = "Takliflarimiz",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 26.sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        // Action Shortcuts Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // SOS Quick trigger
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFF1744))
                                    .clickable { onTriggerSosWithGps() }
                                    .padding(horizontal = 8.dp, vertical = 7.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Warning,
                                        contentDescription = "SOS",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "SOS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 10.sp
                                        ),
                                        color = Color.White
                                    )
                                }
                            }

                            // Currency quick trigger
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isDark) Color(0x3300BFA5) else Color(0xFFE0F2F1))
                                    .border(1.dp, Color(0xFF00BFA5).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .clickable { showCurrencySheet = true }
                                    .padding(horizontal = 8.dp, vertical = 7.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CurrencyExchange,
                                    contentDescription = "Valyuta",
                                    tint = if (isDark) Color(0xFF64FFDA) else Color(0xFF00796B),
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            // Language Selector trigger
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isDark) Color(0x330047AB) else Color(0xFFE8F0FE))
                                    .border(1.dp, TurquoiseTile.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .clickable { showLanguageDialog = true }
                                    .padding(horizontal = 7.dp, vertical = 7.dp)
                            ) {
                                Text(text = currentLanguage.flag, fontSize = 13.sp)
                            }

                            // Map Quick Shortcut
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isDark) Color(0x330047AB) else Color(0x1F0047AB))
                                    .border(1.dp, NeonGold.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                    .clickable { onNavigateToMap() }
                                    .padding(horizontal = 8.dp, vertical = 7.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Map,
                                        contentDescription = "Xarita",
                                        tint = NeonGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Xarita",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        color = if (isDark) Color.White else RegistanBlue
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Main Service Category Chips - Each with distinct accent colors
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(ServiceType.values()) { type ->
                            val isSelected = selectedServiceType == type
                            val (catIcon, catActiveBg, catBorderColor) = when (type) {
                                ServiceType.ALL -> Triple(
                                    Icons.Filled.Star,
                                    Brush.horizontalGradient(listOf(NeonGold, SilkGold)),
                                    NeonGold
                                )
                                ServiceType.TAXI -> Triple(
                                    Icons.Filled.DirectionsCar,
                                    Brush.horizontalGradient(listOf(Color(0xFFFFB300), Color(0xFFFF8F00))),
                                    Color(0xFFFFB300)
                                )
                                ServiceType.DINING -> Triple(
                                    Icons.Filled.Restaurant,
                                    Brush.horizontalGradient(listOf(Color(0xFFE65100), Color(0xFFF57C00))),
                                    Color(0xFFFF9800)
                                )
                                ServiceType.LODGING -> Triple(
                                    Icons.Filled.Hotel,
                                    Brush.horizontalGradient(listOf(RegistanBlue, Color(0xFF0D47A1))),
                                    TurquoiseTile
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (isSelected) catActiveBg else {
                                            if (isDark) Brush.linearGradient(listOf(Color(0x990A1B3B), Color(0x7707152B)))
                                            else Brush.linearGradient(listOf(Color.White, Color(0xFFF8FAFD)))
                                        }
                                    )
                                    .border(
                                        1.2.dp,
                                        if (isSelected) catBorderColor else (if (isDark) Color(0x33D4AF37) else Color(0x220047AB)),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { selectedServiceType = type }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                                    .testTag("service_tab_${type.name.lowercase()}")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = catIcon,
                                        contentDescription = type.title,
                                        tint = if (isSelected) {
                                            if (type == ServiceType.LODGING) Color.White else Color.Black
                                        } else {
                                            if (isDark) SilkGoldLight else RegistanBlue
                                        },
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = type.title,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 12.sp
                                        ),
                                        color = if (isSelected) {
                                            if (type == ServiceType.LODGING) Color.White else Color.Black
                                        } else {
                                            MaterialTheme.colorScheme.onBackground
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // City Filter Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(cities) { city ->
                            val isSelected = selectedCityFilter == city
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (isSelected) TurquoiseTile.copy(alpha = 0.25f) else Color.Transparent
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) TurquoiseTile else Color(0x33888888),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable { selectedCityFilter = city }
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = city,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    ),
                                    color = if (isSelected) TurquoiseTile else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            // ==========================================
            // CATALOG 1: SHAXSIY AVTOPARK & VIP TRANSFER
            // Distinct Design: Carbon-Black & Gold VIP Luxury
            // ==========================================
            if (selectedServiceType == ServiceType.ALL || selectedServiceType == ServiceType.TAXI) {
                item {
                    CatalogSectionHeader(
                        title = "🚖 Shaxsiy Avtopark & VIP Transfer",
                        subtitle = "Shaxsiy litsenziyali avtopark, 24/7 dispetcher, aeroport kutib olish & shaharlararo qatnov",
                        badge = "24/7 Shaxsiy Dispetcherlik",
                        accentColor = Color(0xFFFFB300),
                        isDark = isDark
                    )
                }

                // 24/7 Dispatch Banner Callout
                item {
                    DispatchHotlineBanner(
                        isDark = isDark,
                        onCall = { PrivateTaxiParkHelper.callDispatcher(context) },
                        onTelegram = { PrivateTaxiParkHelper.openTelegramDispatch(context) }
                    )
                }

                val filteredTaxi = SampleOffers.taxiList.filter {
                    selectedCityFilter == "Barchasi" || it.targetCity.contains(selectedCityFilter, ignoreCase = true)
                }

                items(filteredTaxi) { taxi ->
                    PrivateTaxiParkCard(
                        taxi = taxi,
                        isDark = isDark,
                        onBookRide = {
                            selectedTaxiForBooking = taxi
                            passengerPickupLocation = "Samarqand Aeroporti / Vokzal"
                            passengerDestinationLocation = taxi.title
                        },
                        onCallDispatcher = {
                            PrivateTaxiParkHelper.callDispatcher(context, taxi.phoneNumber)
                        },
                        onTelegram = {
                            PrivateTaxiParkHelper.openTelegramDispatch(
                                context,
                                "${taxi.tariff} - ${taxi.carModel} uchun buyurtma"
                            )
                        }
                    )
                }
            }

            // ==========================================
            // CATALOG 2: MILLIY TAOMLAR & CHOYXONALAR
            // Distinct Design: Saffron, Paprika & Terracotta
            // ==========================================
            if (selectedServiceType == ServiceType.ALL || selectedServiceType == ServiceType.DINING) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    CatalogSectionHeader(
                        title = "🍽️ Milliy Ovqatlanish & Choyxonalar",
                        subtitle = "Samarqand oshi, Buxoro tandiri, Xiva shivit oshi va qadimiy karvonsaroy choyxonalari",
                        badge = "100% Halol Kafolatlangan",
                        accentColor = Color(0xFFFF7043),
                        isDark = isDark
                    )
                }

                val filteredDining = SampleOffers.diningList.filter {
                    selectedCityFilter == "Barchasi" || it.city.equals(selectedCityFilter, ignoreCase = true)
                }

                items(filteredDining) { dining ->
                    DiningCatalogCard(
                        dining = dining,
                        isDark = isDark,
                        onReserveTable = {
                            bookingDialogItemName = dining.name
                            bookingDialogType = "dining"
                        },
                        onOpenMap = {
                            PrivateTaxiParkHelper.openNavigationMap(context, dining.latitude, dining.longitude, dining.name)
                        },
                        onCallRestaurant = {
                            PrivateTaxiParkHelper.callDispatcher(context, dining.phoneNumber)
                        },
                        onOrderPrivateTaxiTo = {
                            selectedTaxiForBooking = SampleOffers.taxiList.first()
                            passengerDestinationLocation = "${dining.name} (${dining.city})"
                        }
                    )
                }
            }

            // ==========================================
            // CATALOG 3: MEHMONXONALAR & KARVONSAROYLAR
            // Distinct Design: Registan Deep Sapphire & Turquoise
            // ==========================================
            if (selectedServiceType == ServiceType.ALL || selectedServiceType == ServiceType.LODGING) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    CatalogSectionHeader(
                        title = "🏨 Mehmonxonalar & Karvonsaroylar",
                        subtitle = "Tarixiy madrasalar ichidagi xonalar, shohona butik saroylar va zamonaviy xostellar",
                        badge = "Eng Yaxshi Narx Kafolati",
                        accentColor = TurquoiseTile,
                        isDark = isDark
                    )
                }

                val filteredLodging = SampleOffers.lodgingList.filter {
                    selectedCityFilter == "Barchasi" || it.city.equals(selectedCityFilter, ignoreCase = true)
                }

                items(filteredLodging) { lodging ->
                    LodgingCatalogCard(
                        lodging = lodging,
                        isDark = isDark,
                        onBookRoom = {
                            bookingDialogItemName = lodging.name
                            bookingDialogType = "hotel"
                        },
                        onOpenMap = {
                            PrivateTaxiParkHelper.openNavigationMap(context, lodging.latitude, lodging.longitude, lodging.name)
                        },
                        onCallHotel = {
                            PrivateTaxiParkHelper.callDispatcher(context, lodging.phoneNumber)
                        },
                        onOrderTransferTo = {
                            selectedTaxiForBooking = SampleOffers.taxiList.find { it.id == "fleet_airport_meet_greet" }
                                ?: SampleOffers.taxiList.first()
                            passengerDestinationLocation = "${lodging.name} (${lodging.city})"
                        }
                    )
                }
            }
        }

        // ==========================================
        // PRIVATE TAXI PARK DIRECT BOOKING DIALOG
        // ==========================================
        if (selectedTaxiForBooking != null) {
            val taxi = selectedTaxiForBooking!!
            AlertDialog(
                onDismissRequest = { selectedTaxiForBooking = null },
                shape = RoundedCornerShape(24.dp),
                containerColor = if (isDark) Color(0xFF0C1B3B) else Color.White,
                title = {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.DirectionsCar,
                                    contentDescription = null,
                                    tint = NeonGold,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Shaxsiy Taxi Buyurtmasi",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 17.sp
                                    ),
                                    color = if (isDark) Color.White else Color.Black
                                )
                            }
                            GlassPillBadge(
                                text = taxi.tariff,
                                accentColor = NeonGold,
                                textColor = if (isDark) NeonGold else RegistanBlue
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${taxi.carModel} • ${taxi.dispatcherName}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = TurquoiseTile
                        )
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = passengerPickupLocation,
                            onValueChange = { passengerPickupLocation = it },
                            label = { Text("Qayerdan (Olish manzili)") },
                            leadingIcon = {
                                Icon(Icons.Filled.Place, contentDescription = null, tint = NeonGold, modifier = Modifier.size(16.dp))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = passengerDestinationLocation,
                            onValueChange = { passengerDestinationLocation = it },
                            label = { Text("Qayerga (Borish manzili)") },
                            leadingIcon = {
                                Icon(Icons.Filled.Navigation, contentDescription = null, tint = TurquoiseTile, modifier = Modifier.size(16.dp))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = passengerName,
                                onValueChange = { passengerName = it },
                                label = { Text("Ismingiz") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = passengerPhone,
                                onValueChange = { passengerPhone = it },
                                label = { Text("Telefon") },
                                modifier = Modifier.weight(1.2f),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }

                        if (taxi.tariff == "Meet & Greet" || taxi.isVipFleet) {
                            OutlinedTextField(
                                value = meetTablichkaName,
                                onValueChange = { meetTablichkaName = it },
                                label = { Text("Tablichkaga yoziladigan ism (Kutib olish)") },
                                placeholder = { Text("Masalan: Mr. John Smith") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }

                        // Price summary banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDark) Color(0x330047AB) else Color(0xFFF0F6FF))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Belgilangan narx:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = taxi.priceEstimateUzs,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = if (isDark) NeonGold else SilkGold
                                    )
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val details = """
                                🚖 Yangi Taxi Buyurtmasi:
                                - Avto: ${taxi.carModel} (${taxi.tariff})
                                - Olish: $passengerPickupLocation
                                - Borish: $passengerDestinationLocation
                                - Mijoz: $passengerName ($passengerPhone)
                                ${if (meetTablichkaName.isNotBlank()) "- Tablichka: $meetTablichkaName" else ""}
                            """.trimIndent()

                            PrivateTaxiParkHelper.openTelegramDispatch(context, details)
                            Toast.makeText(
                                context,
                                "Buyurtma dispetcherga yo'naltirildi! Haydovchi ${taxi.etaMinutes} daqiqada yetib keladi.",
                                Toast.LENGTH_LONG
                            ).show()
                            selectedTaxiForBooking = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonGold,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Dispetcherga Yuborish", fontWeight = FontWeight.Black)
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedTaxiForBooking = null }) {
                        Text("Bekor qilish")
                    }
                }
            )
        }

        // ==========================================
        // DINING & HOTEL BOOKING MODAL
        // ==========================================
        if (bookingDialogItemName != null) {
            AlertDialog(
                onDismissRequest = { bookingDialogItemName = null },
                shape = RoundedCornerShape(24.dp),
                containerColor = if (isDark) Color(0xFF0C1B3B) else Color.White,
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (bookingDialogType == "hotel") Icons.Filled.Hotel else Icons.Filled.Restaurant,
                                contentDescription = null,
                                tint = if (bookingDialogType == "hotel") TurquoiseTile else Color(0xFFFF7043),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (bookingDialogType == "hotel") "Xona Band Qilish" else "Stol Band Qilish",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isDark) Color.White else Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = bookingDialogItemName ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = TurquoiseTile
                        )
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Iltimos, ma'lumotlaringizni qoldiring. Administrator 5 daqiqa ichida siz bilan bog'lanadi:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        OutlinedTextField(
                            value = guestName,
                            onValueChange = { guestName = it },
                            label = { Text("Ism familiyangiz") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = guestPhone,
                            onValueChange = { guestPhone = it },
                            label = { Text("Telefon raqamingiz") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            Toast.makeText(
                                context,
                                "Muvaffaqiyatli band qilindi! $bookingDialogItemName menejeri tez orada bog'lanadi.",
                                Toast.LENGTH_LONG
                            ).show()
                            bookingDialogItemName = null
                            guestName = ""
                            guestPhone = "+998 "
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (bookingDialogType == "hotel") RegistanBlue else Color(0xFFFF7043),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Tasdiqlash", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { bookingDialogItemName = null }) {
                        Text("Bekor qilish")
                    }
                }
            )
        }

        // SOS Emergency Dialog (Offline + Online Live GPS Dispatch)
        if (showSosDialog) {
            SosEmergencyDialog(
                currentLat = liveGpsState?.latitude ?: 41.3111,
                currentLon = liveGpsState?.longitude ?: 69.2797,
                nearestLandmark = liveGpsState?.addressEstimate ?: "VIP Xizmatlar / GPS aniqlanmoqda...",
                onDismiss = { showSosDialog = false }
            )
        }

        // National Audio Tour Sheet
        if (showAudioTourSheet) {
            AudioTourPlayerSheet(
                onDismiss = { showAudioTourSheet = false }
            )
        }

        // Currency Converter & Budget Sheet
        if (showCurrencySheet) {
            CurrencyBudgetSheet(
                onDismiss = { showCurrencySheet = false }
            )
        }

        // Language Selector Dialog
        if (showLanguageDialog) {
            LanguageSelectorDialog(
                currentLanguage = currentLanguage,
                onLanguageSelected = { lang ->
                    currentLanguage = lang
                    AppLanguage.currentLanguage = lang
                },
                onDismiss = { showLanguageDialog = false }
            )
        }
    }
}

@Composable
fun CatalogSectionHeader(
    title: String,
    subtitle: String,
    badge: String,
    accentColor: Color,
    isDark: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            GlassPillBadge(
                text = badge,
                accentColor = accentColor,
                textColor = accentColor
            )
        }
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

// 24/7 Dispatcher Banner Callout
@Composable
fun DispatchHotlineBanner(
    isDark: Boolean,
    onCall: () -> Unit,
    onTelegram: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    if (isDark) listOf(Color(0xFF1E1700), Color(0xFF332600), Color(0xFF0F1A30))
                    else listOf(Color(0xFFFFF8E1), Color(0xFFFFECB3), Color(0xFFE3F2FD))
                )
            )
            .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(20.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFB300))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.HeadsetMic,
                        contentDescription = "Dispetcher",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "24/7 SHAXSIY DISPETCHER",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            fontSize = 10.sp,
                            color = if (isDark) NeonGold else Color(0xFFB78103)
                        )
                    )
                    Text(
                        text = "To'g'ridan-to'g'ri avtopark aloqasi",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(
                    onClick = onTelegram,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF29B6F6))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = "Telegram",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFB300))
                        .clickable { onCall() }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Call,
                            contentDescription = "Qo'ng'iroq",
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Aloqa",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// CARD 1: PRIVATE TAXI PARK FLEET CARD
// Design: VIP Carbon/Gold Theme with Vehicle Spec Grid
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrivateTaxiParkCard(
    taxi: TaxiOffer,
    isDark: Boolean,
    onBookRide: () -> Unit,
    onCallDispatcher: () -> Unit,
    onTelegram: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 6.dp)
            .testTag("taxi_card_${taxi.id}"),
        shape = RoundedCornerShape(22.dp),
        backgroundColor = if (isDark) Color(0xEE0B1528) else Color.White,
        borderColor = if (taxi.isVipFleet) Color(0xFFFFD54F) else Color(0x330047AB),
        elevation = 7.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header with VIP Badge & ETA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    if (taxi.isVipFleet) listOf(Color(0xFFFFD54F), Color(0xFFFF8F00))
                                    else listOf(Color(0xFF0047AB), Color(0xFF00E5FF))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DirectionsCar,
                            contentDescription = null,
                            tint = if (taxi.isVipFleet) Color.Black else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = taxi.title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Text(
                            text = taxi.carModel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                color = TurquoiseTile,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                // ETA Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(AccentEmerald.copy(alpha = 0.15f))
                        .border(1.dp, AccentEmerald.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${taxi.etaMinutes} daq",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = AccentEmerald
                            )
                        )
                        Text(
                            text = "Kelish",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                color = AccentEmerald
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = taxi.description,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.82f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Vehicle Specs Grid: Seats, Luggage, Rating, City
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0x330047AB) else Color(0xFFF1F6FF))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Seats
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AirlineSeatReclineExtra, contentDescription = null, tint = NeonGold, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${taxi.passengerSeats} o'rindiq",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Luggage
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Luggage, contentDescription = null, tint = TurquoiseTile, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${taxi.luggageCapacity} chamadon",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = NeonGold, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${taxi.rating}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Feature Badges
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                taxi.features.forEach { feature ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDark) Color(0x33D4AF37) else Color(0xFFFFF8E1))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "✓ $feature",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDark) SilkGoldLight else Color(0xFF8D6E00)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer with Price and Dispatch Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Belgilangan Tarif",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Text(
                        text = taxi.priceEstimateUzs,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = if (isDark) NeonGold else SilkGold
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Call dispatcher
                    IconButton(
                        onClick = onCallDispatcher,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0x330047AB) else Color(0xFFE8F0FE))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Phone,
                            contentDescription = "Dispetcher",
                            tint = NeonGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Direct booking button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFFD54F), Color(0xFFFF8F00))
                                )
                            )
                            .clickable { onBookRide() }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.DirectionsCar, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Buyurtma",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = Color.Black
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// CARD 2: DINING CATALOG CARD
// Design: Warm Amber & Terracotta Theme with Halal & Chef's Dishes
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiningCatalogCard(
    dining: DiningOffer,
    isDark: Boolean,
    onReserveTable: () -> Unit,
    onOpenMap: () -> Unit,
    onCallRestaurant: () -> Unit,
    onOrderPrivateTaxiTo: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 6.dp)
            .testTag("dining_card_${dining.id}"),
        shape = RoundedCornerShape(22.dp),
        backgroundColor = if (isDark) Color(0xEE0D1625) else Color.White,
        borderColor = if (isDark) Color(0xFFFF8A65).copy(alpha = 0.5f) else Color(0xFFFFCCBC),
        elevation = 6.dp
    ) {
        Column {
            // Photo with Halal & Cuisine tags
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
            ) {
                Image(
                    painter = painterResource(id = dining.imageRes),
                    contentDescription = dining.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xDD000918))
                            )
                        )
                )

                // Top badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xCC000000))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${dining.city.uppercase()} • ${dining.cuisineType}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFCC80)
                            )
                        )
                    }

                    if (dining.isHalal) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentEmerald)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "✓ 100% HALOL",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }

                // Bottom Title on Image
                Text(
                    text = dining.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = Color.White
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                )
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = dining.description,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Signature Dishes
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    dining.signatureDishes.forEach { dish ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDark) Color(0x33FF5722) else Color(0xFFFBE9E7))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "🍲 $dish",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = if (isDark) Color(0xFFFFAB91) else Color(0xFFD84315)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Special Perk
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.WorkspacePremium, contentDescription = null, tint = NeonGold, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = dining.specialOffer,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) NeonGold else SilkGold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Shaxsiy taksi chaqirish (Taxi directly to this restaurant)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDark) Color(0x330047AB) else Color(0xFFE8F0FE))
                            .border(1.dp, TurquoiseTile.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable { onOrderPrivateTaxiTo() }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.DirectionsCar, contentDescription = null, tint = TurquoiseTile, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Taksida Borish",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = if (isDark) Color.White else RegistanBlue
                                )
                            )
                        }
                    }

                    // Stol band qilish
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFFFF7043), Color(0xFFFF5722)))
                            )
                            .clickable { onReserveTable() }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Stol Band Qilish",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// CARD 3: LODGING CATALOG CARD
// Design: Registan Silk Sapphire & Royal Palace Gold
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LodgingCatalogCard(
    lodging: LodgingOffer,
    isDark: Boolean,
    onBookRoom: () -> Unit,
    onOpenMap: () -> Unit,
    onCallHotel: () -> Unit,
    onOrderTransferTo: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 6.dp)
            .testTag("lodging_card_${lodging.id}"),
        shape = RoundedCornerShape(22.dp),
        backgroundColor = if (isDark) Color(0xEE09142A) else Color.White,
        borderColor = if (isDark) TurquoiseTile.copy(alpha = 0.5f) else Color(0x330047AB),
        elevation = 6.dp
    ) {
        Column {
            // Photo Header with Stars
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Image(
                    painter = painterResource(id = lodging.imageRes),
                    contentDescription = lodging.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xDD000918))
                            )
                        )
                )

                // Top Star & City Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xCC000000))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${lodging.city.uppercase()} • ${lodging.lodgingType}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TurquoiseTile
                            )
                        )
                    }

                    // Star Rating Badges
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xCC000000))
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(lodging.stars) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = NeonGold,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                // Bottom Title & Room Type
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                ) {
                    Text(
                        text = lodging.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    )
                    Text(
                        text = lodging.roomType,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = SilkGoldLight
                        )
                    )
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = lodging.description,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Amenities Badges
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    lodging.amenities.forEach { amenity ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDark) Color(0x330047AB) else Color(0xFFF1F6FF))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "✓ $amenity",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = if (isDark) SilkGoldLight else RegistanBlue
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bonus Perk
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.BookmarkAdded, contentDescription = null, tint = TurquoiseTile, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = lodging.bonusPerk,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = TurquoiseTile,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Footer with Price and Booking
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Bir kecha uchun",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Text(
                            text = lodging.pricePerNightUzs,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = if (isDark) NeonGold else SilkGold
                            )
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Transfer to Hotel
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDark) Color(0x330047AB) else Color(0xFFE8F0FE))
                                .border(1.dp, TurquoiseTile.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .clickable { onOrderTransferTo() }
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Transfer",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = if (isDark) Color.White else RegistanBlue
                                )
                            )
                        }

                        // Book Room
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.horizontalGradient(listOf(RegistanBlue, Color(0xFF0D47A1)))
                                )
                                .clickable { onBookRoom() }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Xona Band Qilish",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
