package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import com.example.R
import com.example.model.CulturalEvent
import com.example.model.Destination
import com.example.model.DestinationCategory
import com.example.model.SampleCulturalEvents
import com.example.model.SampleDestinations
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassPillBadge
import com.example.ui.components.GoldFloatingActionButton
import com.example.ui.components.GoldGradientButton
import com.example.ui.components.UzbekStarEmblem
import com.example.ui.theme.AccentRuby
import com.example.ui.theme.GlassBackgroundDark
import com.example.ui.theme.GlassBackgroundLight
import com.example.ui.theme.GlassBorderDark
import com.example.ui.theme.GlassBorderLight
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.RegistanBlueDark
import com.example.ui.theme.RegistanBlueLight
import com.example.ui.theme.SilkGold
import com.example.ui.theme.SilkGoldDark
import com.example.ui.theme.SilkGoldLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TurquoiseTile
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import com.example.model.AppLanguage
import com.example.model.AppLanguageState
import com.example.model.LocalAppLanguage
import com.example.model.AppStrings
import com.example.model.appString
import com.example.util.GpsLocationManager
import com.example.util.MinuteDataRotationManager
import com.example.util.SosDispatchHelper
import com.example.util.SosSessionManager
import com.example.util.UserSessionManager
import com.example.ui.components.AudioTourPlayerSheet
import com.example.ui.components.CurrencyBudgetSheet
import com.example.ui.components.LanguageSelectorDialog
import com.example.ui.components.SosEmergencyDialog
import com.example.ui.components.threed.Celestial3DAstrolabe
import com.example.ui.components.threed.Interactive3DMonumentViewer
import com.example.ui.components.threed.LiveOnlineStatusHub
import com.example.ui.components.threed.Parallax3DContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAr: (Destination) -> Unit,
    onNavigateToPlanner: (Destination?) -> Unit,
    onNavigateTo3DMap: (Destination?) -> Unit,
    onNavigateToBooking: () -> Unit = {},
    onNavigateToTariffs: () -> Unit = {},
    onNavigateToAdmin: () -> Unit = {},
    onNavigateToTickets: (com.example.model.TransportType) -> Unit = {},
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme() || isDarkMode
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf(DestinationCategory.ALL) }
    var selectedDestinationForDetail by remember { mutableStateOf<Destination?>(null) }
    var showSurpriseMeDialog by remember { mutableStateOf(false) }
    var showSosDialog by remember { mutableStateOf(false) }
    var showAudioTourSheet by remember { mutableStateOf(false) }
    var showCurrencySheet by remember { mutableStateOf(false) }
    var showSafeTourRadarSheet by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showGuideNumberDialog by remember { mutableStateOf(false) }
    var showGuideHubSheet by remember { mutableStateOf(false) }
    var guideHubInitialTab by remember { androidx.compose.runtime.mutableIntStateOf(0) }
    val currentLanguage = LocalAppLanguage.current
    var bookmarkedIds by remember { mutableStateOf(setOf("registan", "bukhara_kalyan")) }
    var surprisePick by remember { mutableStateOf(SampleDestinations.items.first()) }
    val scope = rememberCoroutineScope()
    val liveGpsState by GpsLocationManager.currentLocation.collectAsStateWithLifecycle()
    val minuteState by MinuteDataRotationManager.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        GpsLocationManager.startLocationUpdates(context)
        scope.launch {
            val res = com.example.network.SupabaseClient.fetchMonuments(context)
            if (res.isSuccess) {
                res.getOrNull()?.let { SampleDestinations.updateWithRemoteMonuments(it) }
            }
        }
    }

    // SOS Emergency Trigger with Live GPS coordinates automatically dispatched to Telegram bot
    val onTriggerSosWithGps: () -> Unit = {
        scope.launch {
            val freshGps = GpsLocationManager.getAccurateCurrentLocation(context)
            val userProfile = UserSessionManager.loadProfile(context)
            val fullName = userProfile?.let { "${it.firstName} ${it.lastName}".trim() }
            val name = if (!fullName.isNullOrBlank()) fullName else "Hurmatli Sayyoh"
            val userPhone = userProfile?.phoneNumber?.trim()
            val phone = if (!userPhone.isNullOrBlank()) userPhone else "+998 91 033 04 60"

            SosSessionManager.startSos(
                context = context,
                name = name,
                phone = phone,
                lat = freshGps.latitude,
                lon = freshGps.longitude,
                address = freshGps.addressEstimate,
                reason = "SOS Favqulodda Yordam Chaqiruvi (Bosh Ekran)"
            )
            showSosDialog = true
        }
    }

    val allItems = SampleDestinations.dynamicList
    val filteredDestinations = remember(selectedCategory, allItems.size) {
        if (selectedCategory == DestinationCategory.ALL) {
            allItems.toList()
        } else {
            allItems.filter { it.category == selectedCategory }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Header Section with Clean Luxury Identity
            item {
                HomeHeaderSection(
                    isDark = isDark,
                    currentLanguage = currentLanguage,
                    onToggleDarkMode = onToggleDarkMode,
                    onOpenLanguage = { showLanguageDialog = true },
                    onOpenGuideNumberDialog = { showGuideNumberDialog = true },
                    onOpenGuideHub = { showGuideHubSheet = true }
                )
            }

            // VIP Travel Companion Quick Hub (3 Clean Tools: SOS, Audio Guide, Currency)
            item {
                TravelCompanionQuickHub(
                    isDark = isDark,
                    onOpenSos = onTriggerSosWithGps,
                    onOpenAudioGuide = { showAudioTourSheet = true },
                    onOpenCurrency = { showCurrencySheet = true }
                )
            }

            // VIP Tour Guide Audio Whisper Hero Banner (Live Group Radio Guide)
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable {
                            guideHubInitialTab = 1
                            showGuideHubSheet = true
                        },
                    shape = RoundedCornerShape(22.dp),
                    backgroundColor = if (isDark) Color(0x33003B6F) else Color(0xFFF0F9FF),
                    borderColor = TurquoiseTile.copy(alpha = 0.55f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF0077B6), Color(0xFF00B4D8), Color(0xFF03045E))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Headphones,
                                    contentDescription = null,
                                    tint = NeonGold,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "GID OVOZLI EFIRI",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.5.sp,
                                        letterSpacing = 0.8.sp,
                                        color = if (isDark) NeonGold else RegistanBlue
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0x3300E5FF))
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "JONLI RADIO",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 8.sp,
                                            color = TurquoiseTile
                                        )
                                    }
                                }
                                Text(
                                    text = "Quloqchinda shovqinsiz toza ovoz • Masofa radari • Diqqat signali",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.horizontalGradient(listOf(NeonGold, SilkGold)))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "ULANISH 🎙️",
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                color = RegistanBlueDark
                            )
                        }
                    }
                }
            }

            // SAFETOUR GEORADAR: GIDDAN ADASHMASLIK VA XAVFSIZLIK NAZORATI
            item {
                val (distMeters, bearingDeg, geoStatus) = com.example.util.SafeTourRadarManager.getCurrentRadarData(context)
                val dirText = com.example.util.SafeTourRadarManager.getDirectionDescriptionUz(bearingDeg)

                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { showSafeTourRadarSheet = true }
                        .testTag("home_safetour_radar_banner"),
                    shape = RoundedCornerShape(22.dp),
                    backgroundColor = if (isDark) Color(0xF506152F) else Color(0xFFF1F8E9),
                    borderColor = Color(geoStatus.colorHex).copy(alpha = 0.6f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(geoStatus.colorHex).copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.NearMe,
                                        contentDescription = "Radar",
                                        tint = Color(geoStatus.colorHex),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "SAFETOUR GEORADAR",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.5.sp,
                                            letterSpacing = 1.sp,
                                            color = Color(geoStatus.colorHex)
                                        )
                                    )
                                    Text(
                                        text = "Giddan adashib qolmaslik nazorati",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
                                        )
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(geoStatus.colorHex).copy(alpha = 0.15f))
                                    .border(1.dp, Color(geoStatus.colorHex).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "$distMeters metr",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(geoStatus.colorHex)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Yo'nalish: $dirText",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = when (geoStatus) {
                                        com.example.util.GeofenceStatus.SAFE -> "🟢 Gid yaqinida xavfsiz harakatlanmoqdasiz"
                                        com.example.util.GeofenceStatus.CAUTION -> "🟡 Diqqat: Giddan orqada qolyapsiz"
                                        com.example.util.GeofenceStatus.BREACHED -> "🔴 XAVF: Giddan uzoqlashdingiz! Yaqinlashing"
                                    },
                                    fontSize = 10.sp,
                                    color = Color(geoStatus.colorHex)
                                )
                            }

                            Button(
                                onClick = { showSafeTourRadarSheet = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(geoStatus.colorHex),
                                    contentColor = if (geoStatus == com.example.util.GeofenceStatus.CAUTION) Color.Black else Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("Radarni Ochish", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 3 XIL MAXSUS SAYOHAT TARIFLARI ($500 • $1500 • $4500)
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { onNavigateToTariffs() }
                        .testTag("home_3_tariffs_banner"),
                    shape = RoundedCornerShape(22.dp),
                    backgroundColor = if (isDark) Color(0xF5081734) else Color(0xFFFFFFFF),
                    borderColor = NeonGold.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                UzbekStarEmblem(
                                    size = 18.dp,
                                    primaryColor = NeonGold,
                                    secondaryColor = TurquoiseTile
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = appString("home_3_tariffs_title"),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        letterSpacing = 1.sp,
                                        color = NeonGold
                                    )
                                )
                            }

                            Text(
                                text = "Barchasi →",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isDark) NeonGold else SilkGold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 3 Clean Modern Tariff Cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 1-Tarif ($500)
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isDark) Color(0x330F2B5C) else Color(0x100047AB))
                                    .border(1.dp, NeonGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "🕌", fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = appString("tariff_1_name"),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "500$",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = NeonGold,
                                        fontSize = 13.sp
                                    )
                                )
                            }

                            // 2-Tarif ($1500)
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isDark) Color(0x330D324D) else Color(0x101A535C))
                                    .border(1.dp, TurquoiseTile.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "💎", fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = appString("tariff_2_name"),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "1500$",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = TurquoiseTile,
                                        fontSize = 13.sp
                                    )
                                )
                            }

                            // 3-Tarif ($4500)
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isDark) Color(0x333F2E56) else Color(0x10331D2C))
                                    .border(1.dp, SilkGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "👑", fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = appString("tariff_3_name"),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "4500$",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = SilkGold,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // AVIA VA POYEZD CHIPTALARI ONLAYN ZAKAZ BANNERI
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("home_ticket_booking_banner"),
                    shape = RoundedCornerShape(22.dp),
                    backgroundColor = if (isDark) Color(0xF5061426) else Color(0xFFFFFFFF),
                    borderColor = if (isDark) Color(0xFF38BDF8) else RegistanBlue
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (isDark) Color(0x3338BDF8) else Color(0x1A0047AB)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "🎫", fontSize = 20.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Avia & Poyezd Chiptalari",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp
                                        ),
                                        color = if (isDark) Color.White else RegistanBlueDark
                                    )
                                    Text(
                                        text = "Onlayn zakaz & E-Chipta QR-kod",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.5.sp,
                                            color = if (isDark) NeonGold else RegistanBlue
                                        )
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0x2210B981))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "⚡ 2 daqiqada",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981),
                                        fontSize = 10.5.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Two action cards: Avia vs Poyezd
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Avia biletlar card
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isDark) Color(0x331E293B) else Color(0xFFF1F5F9))
                                    .border(1.dp, if (isDark) Color(0x3338BDF8) else Color(0x220047AB), RoundedCornerShape(16.dp))
                                    .clickable { onNavigateToTickets(com.example.model.TransportType.FLIGHT) }
                                    .padding(12.dp)
                                    .testTag("btn_avia_tickets_home"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "✈️", fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Aviabilet",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isDark) Color.White else RegistanBlueDark
                                        )
                                    )
                                    Text(
                                        text = "Uzbekistan Airways",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 9.5.sp,
                                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                        )
                                    )
                                }
                            }

                            // Poyezd biletlar card
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isDark) Color(0x331E293B) else Color(0xFFF1F5F9))
                                    .border(1.dp, if (isDark) TurquoiseTile.copy(alpha = 0.4f) else Color(0x220D9488), RoundedCornerShape(16.dp))
                                    .clickable { onNavigateToTickets(com.example.model.TransportType.TRAIN) }
                                    .padding(12.dp)
                                    .testTag("btn_train_tickets_home"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🚆", fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Poyezd bilet",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isDark) Color.White else RegistanBlueDark
                                        )
                                    )
                                    Text(
                                        text = "Afrosiyob 250 km/h",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 9.5.sp,
                                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Category Filter Pills
            item {
                CategoryPillsRow(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
            }

            // Bento Grid Section (Creative Highlight)
            item {
                Text(
                    text = appString("curated_bento_title"),
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isDark) NeonGold else RegistanBlue,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 10.dp)
                )

                BentoGridSection(
                    featuredDestination = minuteState.currentDestination,
                    liveEvent = minuteState.currentCulturalEvent,
                    onDestinationClick = { selectedDestinationForDetail = it },
                    onArGuideClick = { onNavigateToAr(minuteState.currentDestination) },
                    onSurpriseMeClick = {
                        surprisePick = SampleDestinations.items.random()
                        showSurpriseMeDialog = true
                    },
                    onItineraryClick = { onNavigateToPlanner(null) },
                    onEventClick = { /* View live event */ },
                    onAfrasiyobClick = { onNavigateToTickets(com.example.model.TransportType.TRAIN) }
                )
            }

            // Trending Destinations Horizontal Carousel
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ICONIC WONDERS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isDark) NeonGold else RegistanBlue
                    )

                    Text(
                        text = "Explore 3D Map →",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = SilkGold,
                        modifier = Modifier.clickable { onNavigateTo3DMap(null) }
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredDestinations) { destination ->
                        DestinationTrendCard(
                            destination = destination,
                            isBookmarked = bookmarkedIds.contains(destination.id),
                            onBookmarkToggle = {
                                bookmarkedIds = if (bookmarkedIds.contains(destination.id)) {
                                    bookmarkedIds - destination.id
                                } else {
                                    bookmarkedIds + destination.id
                                }
                            },
                            onClick = { selectedDestinationForDetail = destination }
                        )
                    }
                }
            }
        }

        // AI Surprise Me Modal
        if (showSurpriseMeDialog) {
            ModalBottomSheet(
                onDismissRequest = { showSurpriseMeDialog = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = if (isDark) GlassBackgroundDark else GlassBackgroundLight
            ) {
                SurpriseMeModalContent(
                    destination = surprisePick,
                    onOpenDetail = {
                        showSurpriseMeDialog = false
                        selectedDestinationForDetail = surprisePick
                    },
                    onPlanTrip = {
                        showSurpriseMeDialog = false
                        onNavigateToPlanner(surprisePick)
                    },
                    onClose = { showSurpriseMeDialog = false }
                )
            }
        }

        // Destination Detail Bottom Sheet
        if (selectedDestinationForDetail != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedDestinationForDetail = null },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = if (isDark) GlassBackgroundDark else GlassBackgroundLight
            ) {
                DestinationDetailContent(
                    destination = selectedDestinationForDetail!!,
                    onNavigateToAr = {
                        val d = selectedDestinationForDetail!!
                        selectedDestinationForDetail = null
                        onNavigateToAr(d)
                    },
                    onNavigateToPlanner = {
                        val d = selectedDestinationForDetail!!
                        selectedDestinationForDetail = null
                        onNavigateToPlanner(d)
                    },
                    onNavigateTo3DMap = {
                        val d = selectedDestinationForDetail!!
                        selectedDestinationForDetail = null
                        onNavigateTo3DMap(d)
                    },
                    onClose = { selectedDestinationForDetail = null }
                )
            }
        }

        // SOS Emergency Dialog (Works Offline + Online Live GPS Dispatch)
        if (showSosDialog) {
            SosEmergencyDialog(
                currentLat = liveGpsState?.latitude ?: 41.3111,
                currentLon = liveGpsState?.longitude ?: 69.2797,
                nearestLandmark = liveGpsState?.addressEstimate ?: "GPS joylashuv aniqlanmoqda...",
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
                    AppLanguage.currentLanguage = lang
                    AppLanguageState.currentLanguage = lang
                },
                onDismiss = { showLanguageDialog = false }
            )
        }

        // Guide Accepted Number Input Dialog
        if (showGuideNumberDialog) {
            com.example.ui.components.GuideAcceptedNumberDialog(
                onDismiss = { showGuideNumberDialog = false },
                onSubmittedSuccess = { showGuideNumberDialog = false }
            )
        }

        // VIP Personal Guide Hub (Live Location, Audio Whisper, Quick Chat, Itinerary, Reviews)
        if (showGuideHubSheet) {
            com.example.ui.components.GuideInteractionHubSheet(
                initialTab = guideHubInitialTab,
                onDismiss = { showGuideHubSheet = false },
                onOpenMapScreen = {
                    showGuideHubSheet = false
                    onNavigateTo3DMap(null)
                }
            )
        }

        // SafeTour Georadar Sheet (Anti-lost live tracking)
        if (showSafeTourRadarSheet) {
            com.example.ui.components.SafeTourRadarSheet(
                onDismiss = { showSafeTourRadarSheet = false },
                onOpenChat = {
                    showSafeTourRadarSheet = false
                    showGuideHubSheet = true
                }
            )
        }
    }
}

@Composable
fun TravelCompanionQuickHub(
    isDark: Boolean,
    onOpenSos: () -> Unit,
    onOpenAudioGuide: () -> Unit,
    onOpenCurrency: () -> Unit,
    onOpenLanguage: () -> Unit = {},
    onOpenAdmin: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // SOS Emergency Card (High Visibility Radiant Red with Golden Border)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFFF1744), Color(0xFFB71C1C))
                        )
                    )
                    .border(1.dp, Color(0xFFFF8A80).copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                    .clickable { onOpenSos() }
                    .padding(11.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = "SOS",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.3f))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "24/7",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.sp
                                ),
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = appString("sos_emergency").uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.White
                    )
                    Text(
                        text = appString("sos_quick_sub"),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 9.sp
                        ),
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Audio Guide Card (Royal Registan Blue with Gold Trim)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (isDark) Brush.verticalGradient(listOf(Color(0xFF003893), Color(0xFF001F52)))
                        else Brush.verticalGradient(listOf(Color(0xFFEBF3FF), Color(0xFFD3E4FC)))
                    )
                    .border(1.dp, NeonGold.copy(alpha = 0.55f), RoundedCornerShape(18.dp))
                    .clickable { onOpenAudioGuide() }
                    .padding(11.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(NeonGold.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Headphones,
                                contentDescription = "Audio",
                                tint = if (isDark) NeonGold else SilkGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(TurquoiseTile.copy(alpha = 0.15f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Nay/Dutor",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TurquoiseTile
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = appString("audio_guide").uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        ),
                        color = if (isDark) NeonGold else RegistanBlue
                    )
                    Text(
                        text = appString("audio_quick_sub"),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 9.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }

            // Currency & Budget Card (Emerald Glaze with Teal Border)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (isDark) Brush.verticalGradient(listOf(Color(0xFF004D40), Color(0xFF00241E)))
                        else Brush.verticalGradient(listOf(Color(0xFFE0F2F1), Color(0xFFB2DFDB)))
                    )
                    .border(1.dp, Color(0xFF00BFA5).copy(alpha = 0.55f), RoundedCornerShape(18.dp))
                    .clickable { onOpenCurrency() }
                    .padding(11.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00BFA5).copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CurrencyExchange,
                                contentDescription = "Currency",
                                tint = if (isDark) Color(0xFF64FFDA) else Color(0xFF00796B),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF00BFA5).copy(alpha = 0.15f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "USD/UZS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (isDark) Color(0xFF64FFDA) else Color(0xFF00796B)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = appString("currency_calc").uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        ),
                        color = if (isDark) Color(0xFF64FFDA) else Color(0xFF00796B)
                    )
                    Text(
                        text = appString("currency_quick_sub"),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 9.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHeaderSection(
    isDark: Boolean,
    currentLanguage: AppLanguage,
    onToggleDarkMode: () -> Unit,
    onOpenLanguage: () -> Unit,
    isOfflineMode: Boolean = false,
    onOpenSos: () -> Unit = {},
    onOpenAudioGuide: () -> Unit = {},
    onOpenCurrency: () -> Unit = {},
    onToggleOffline: () -> Unit = {},
    onOpenAdmin: () -> Unit = {},
    onOpenGuideNumberDialog: () -> Unit = {},
    onOpenGuideHub: () -> Unit = {}
) {
    val context = LocalContext.current
    val isActivated = UserSessionManager.isAiActivated(context)
    val isGuideSubmitted = UserSessionManager.isGuideNumberSubmitted(context)
    val guideRecord = remember(UserSessionManager.currentGuideNumberSubmittedState) {
        UserSessionManager.getGuideVerification(context)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        val savedProfile = UserSessionManager.currentProfileState
        val touristGreetingName = if (savedProfile != null && savedProfile.firstName.isNotBlank()) {
            savedProfile.firstName
        } else {
            "VIP Sayyoh"
        }
        val touristFullName = if (savedProfile != null && savedProfile.firstName.isNotBlank()) {
            "${savedProfile.firstName} ${savedProfile.lastName}".trim()
        } else {
            "Uz Tourist VIP"
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_app_brand_logo),
                    contentDescription = "UzTurist Logo",
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, if (isDark) NeonGold else SilkGold, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "👋 ${currentLanguage.greeting}, $touristGreetingName!".uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isDark) NeonGold else SilkGold
                    )
                    Text(
                        text = touristFullName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Language Switcher Badge (Clean modern capsule)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isDark) Color(0x330047AB) else Color(0xFFF0F4FC))
                        .border(1.dp, if (isDark) TurquoiseTile.copy(alpha = 0.5f) else Color(0x330047AB), RoundedCornerShape(14.dp))
                        .clickable { onOpenLanguage() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = currentLanguage.flag, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentLanguage.code.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                // Dark mode switcher
                IconButton(
                    onClick = onToggleDarkMode,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0x33F6C845) else Color(0x1A0047AB))
                ) {
                    Icon(
                        imageVector = if (isDark) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                        contentDescription = "Toggle Theme",
                        tint = if (isDark) NeonGold else RegistanBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // GUIDE ACCEPTED ALERT BANNER (Prompts for guide-assigned number when guide accepted via ID)
        if (isActivated && !isGuideSubmitted) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF047857), Color(0xFF065F46))))
                    .border(1.5.dp, NeonGold, RoundedCornerShape(14.dp))
                    .clickable { onOpenGuideNumberDialog() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = NeonGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "🎯 GID SIZNI QABUL QILDI!",
                                fontWeight = FontWeight.Black,
                                fontSize = 11.5.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Gid bergan raqamni kiriting ➔",
                                fontSize = 10.sp,
                                color = NeonGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonGold)
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "KIRITISH",
                            fontWeight = FontWeight.Black,
                            fontSize = 10.5.sp,
                            color = Color.Black
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        } else if (guideRecord != null) {
            // Display already submitted guide verification
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x2210B981))
                    .border(1.dp, Color(0xFF10B981).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clickable { onOpenGuideNumberDialog() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔢 Gid kodi: ${guideRecord.guideNumber} • Tasdiqlangan ✓",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp,
                        color = Color(0xFF10B981)
                    )
                    Text(
                        text = "Kodni ko'rish",
                        fontSize = 10.sp,
                        color = NeonGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))

            // Dedicated Guide Interactive Hub Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF003893), Color(0xFF001F52))))
                    .border(1.2.dp, NeonGold, RoundedCornerShape(14.dp))
                    .clickable { onOpenGuideHub() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "👑", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "SHAXSIY GID MARKAZI",
                                fontWeight = FontWeight.Black,
                                fontSize = 11.5.sp,
                                color = NeonGold
                            )
                            Text(
                                text = "📍 Joylashuv • 💬 Aloqa • 🗺️ Marshrut • ⭐ Baho",
                                fontSize = 9.5.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonGold)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "OCHISH ➔",
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            color = Color.Black
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        // VIP Traveler Status Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (isDark) Brush.horizontalGradient(
                        listOf(Color(0x33D4AF37), Color(0x150047AB), Color(0x3300A896))
                    ) else Brush.horizontalGradient(
                        listOf(Color(0xFFFFF8E7), Color(0xFFF0F7FF), Color(0xFFE6FAF6))
                    )
                )
                .border(
                    0.8.dp,
                    if (isDark) NeonGold.copy(alpha = 0.35f) else SilkGold.copy(alpha = 0.35f),
                    RoundedCornerShape(14.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "👑", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "VIP Silk Pass • Eksklyuziv Gid & Xizmatlar",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        ),
                        color = if (isDark) NeonGold else RegistanBlue
                    )
                }
                Text(
                    text = "Aktiv",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = Color(0xFF10B981)
                )
            }
        }
    }
}

@Composable
private fun CategoryPillsRow(
    selectedCategory: DestinationCategory,
    onCategorySelected: (DestinationCategory) -> Unit
) {
    val isDark = isSystemInDarkTheme()

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(DestinationCategory.entries) { category ->
            val isSelected = selectedCategory == category

            val bgCol = if (isSelected) {
                if (isDark) NeonGold else RegistanBlue
            } else {
                if (isDark) GlassBackgroundDark else Color.White
            }

            val textCol = if (isSelected) {
                if (isDark) TextPrimaryLight else Color.White
            } else {
                if (isDark) Color(0xFFC0CCDC) else Color(0xFF475569)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(bgCol)
                    .border(
                        1.dp,
                        if (isSelected) SilkGold else (if (isDark) GlassBorderDark else Color(0x33CBD5E1)),
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .testTag("category_pill_${category.name.lowercase()}"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.getLocalizedLabel(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = textCol
                )
            }
        }
    }
}

@Composable
private fun BentoGridSection(
    featuredDestination: Destination,
    liveEvent: CulturalEvent,
    onDestinationClick: (Destination) -> Unit,
    onArGuideClick: () -> Unit,
    onSurpriseMeClick: () -> Unit,
    onItineraryClick: () -> Unit,
    onEventClick: () -> Unit,
    onAfrasiyobClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Bento Tile 1: Large Featured Experience Card (Registan Square with 3D Parallax Tilt)
        Parallax3DContainer(
            modifier = Modifier
                .fillMaxWidth()
                .height(245.dp),
            onClick = { onDestinationClick(featuredDestination) }
        ) {
            GlassCard(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("bento_featured_registan"),
                shape = RoundedCornerShape(24.dp),
                elevation = 10.dp,
                borderColor = if (isDark) NeonGold.copy(alpha = 0.5f) else Color(0x440047AB)
            ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = featuredDestination.imageRes),
                    contentDescription = featuredDestination.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark gradient overlay for pristine readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0x70001026),
                                    Color(0xF0020914)
                                )
                            )
                        )
                )

                // Top badges: Category & Live Weather Badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlassPillBadge(
                        text = appString("unesco_crown_jewel"),
                        accentColor = NeonGold,
                        textColor = NeonGold
                    )

                    // LIVE 24°C Weather Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xAA000000))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(14.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF22C55E))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LIVE 24°C",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                // Bottom description
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "${featuredDestination.getLocalizedCity().uppercase()}, UZBEKISTAN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = SilkGoldLight
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = featuredDestination.getLocalizedTitle(),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = featuredDestination.getLocalizedSubtitle(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.85f)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        }

        // Bento Row 2 (2 Columns): AR Guide & AI Surprise Me Tiles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tile 2A: AI AR Kamera & Qadimgi Ko'rinish
            GlassCard(
                modifier = Modifier
                    .weight(1f)
                    .height(135.dp)
                    .clickable { onArGuideClick() }
                    .testTag("bento_ar_guide_tile"),
                shape = RoundedCornerShape(24.dp),
                backgroundColor = if (isDark) Color(0xF00A162B) else Color.White,
                borderColor = if (isDark) NeonGold.copy(alpha = 0.4f) else Color(0xFFE2E8F0),
                elevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0x33F6C845) else Color(0xFFEFF6FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Explore,
                                contentDescription = "AR Guide",
                                tint = if (isDark) NeonGold else RegistanBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonGold.copy(alpha = 0.2f))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "1400 AD 3D",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = NeonGold
                                )
                            )
                        }
                    }

                    Column {
                        Text(
                            text = appString("bento_ai_camera_title"),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = appString("bento_ai_camera_sub"),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B),
                            maxLines = 1
                        )
                    }
                }
            }

            // Tile 2B: AI Surprise Me (Deep Registan Blue with Gold Glow)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(135.dp)
                    .shadow(8.dp, RoundedCornerShape(24.dp), ambientColor = NeonGold.copy(alpha = 0.3f))
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF0D54BF),
                                RegistanBlue,
                                Color(0xFF002B66)
                            )
                        )
                    )
                    .border(1.dp, NeonGold.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                    .clickable { onSurpriseMeClick() }
                    .padding(14.dp)
                    .testTag("bento_ai_surprise_tile")
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0x33D4AF37)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = "AI Surprise Me",
                            tint = NeonGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = appString("bento_surprise_title"),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        )
                        Text(
                            text = appString("bento_surprise_sub"),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = SilkGoldLight
                            )
                        )
                    }
                }
            }
        }

        // Bento Tile 3: Current Itinerary Capsule Card (Spanning Full Width)
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItineraryClick() }
                .testTag("bento_itinerary_capsule"),
            shape = RoundedCornerShape(24.dp),
            backgroundColor = if (isDark) Color(0xF00A162B) else Color.White,
            borderColor = if (isDark) Color(0x330047AB) else Color(0xFFE2E8F0),
            elevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Dashed Gold Border Calendar Badge
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0x22D4AF37) else Color(0xFFFFFBEB))
                            .border(1.5.dp, SilkGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = "Calendar",
                            tint = if (isDark) NeonGold else SilkGoldDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = appString("bento_itinerary_title"),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = appString("bento_itinerary_sub"),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0x220047AB) else Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Open Itinerary",
                        tint = if (isDark) NeonGold else RegistanBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Bento Row 4: Live Event & Afrasiyob Train Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Live Cultural Event
            GlassCard(
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp)
                    .clickable { onEventClick() }
                    .testTag("bento_live_event"),
                shape = RoundedCornerShape(22.dp),
                backgroundColor = if (isDark) Color(0x330047AB) else Color(0xFFF0F6FF),
                borderColor = if (isDark) NeonGold.copy(alpha = 0.4f) else RegistanBlue.copy(alpha = 0.2f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentRuby)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "● LIVE NOW",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = SilkGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Column {
                        Text(
                            text = liveEvent.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${liveEvent.city} • ${liveEvent.attendeesCount} Joined",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                            color = SilkGoldDark
                        )
                    }
                }
            }

            // Afrasiyob High-Speed Express
            GlassCard(
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp)
                .clickable { onAfrasiyobClick() }
                .testTag("bento_afrasiyob_train"),
                shape = RoundedCornerShape(22.dp),
                backgroundColor = if (isDark) Color(0x3300A896) else Color(0xFFE6FAF6),
                borderColor = TurquoiseTile.copy(alpha = 0.4f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DirectionsTransit,
                            contentDescription = null,
                            tint = TurquoiseTile,
                            modifier = Modifier.size(20.dp)
                        )

                        Text(
                            text = "250 km/h",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TurquoiseTile
                            )
                        )
                    }

                    Column {
                        Text(
                            text = "Afrasiyob Express",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Tashkent → Samarkand\n2h 13m VIP Cabin",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                            color = if (isDark) Color(0xFFA2B6CC) else Color(0xFF5A6E82)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DestinationTrendCard(
    destination: Destination,
    isBookmarked: Boolean,
    onBookmarkToggle: () -> Unit,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()

    Parallax3DContainer(
        modifier = Modifier
            .width(225.dp)
            .height(265.dp),
        onClick = onClick
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxSize()
                .testTag("destination_card_${destination.id}"),
            shape = RoundedCornerShape(22.dp),
            elevation = 6.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    if (!destination.remoteImageUrl.isNullOrBlank()) {
                        coil.compose.AsyncImage(
                            model = destination.remoteImageUrl,
                            contentDescription = destination.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = destination.imageRes),
                            placeholder = painterResource(id = destination.imageRes)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = destination.imageRes),
                            contentDescription = destination.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Bookmark button
                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x80000000))
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) NeonGold else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Tag badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(RegistanBlue.copy(alpha = 0.85f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = destination.tag,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = destination.getLocalizedCity().uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SilkGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = destination.getLocalizedTitle(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = NeonGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${destination.rating}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        Text(
                            text = destination.afrasiyobDuration,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                                color = if (isDark) Color(0xFFA0B0C4) else Color(0xFF64748B)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CulturalEventCard(event: CulturalEvent) {
    val isDark = isSystemInDarkTheme()

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("event_card_${event.id}"),
        shape = RoundedCornerShape(18.dp),
        elevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                if (event.isLiveNow) listOf(AccentRuby, Color(0xFFFF6B6B)) else listOf(RegistanBlue, TurquoiseTile)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (event.category.contains("Music")) Icons.AutoMirrored.Filled.VolumeUp else Icons.Filled.Restaurant,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${event.location} • ${event.dateText} ${event.timeText}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = if (isDark) Color(0xFFA0B0C4) else Color(0xFF64748B)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (event.isLiveNow) AccentRuby.copy(alpha = 0.15f) else SilkGold.copy(alpha = 0.15f))
                    .border(
                        0.8.dp,
                        if (event.isLiveNow) AccentRuby.copy(alpha = 0.4f) else SilkGold.copy(alpha = 0.4f),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = event.badge,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (event.isLiveNow) AccentRuby else SilkGold
                    )
                )
            }
        }
    }
}

@Composable
private fun SurpriseMeModalContent(
    destination: Destination,
    onOpenDetail: () -> Unit,
    onPlanTrip: () -> Unit,
    onClose: () -> Unit
) {
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = NeonGold,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI SERENDIPITY PICK",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = NeonGold
                    )
                )
            }

            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Large Image Card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(22.dp)
        ) {
            Image(
                painter = painterResource(id = destination.imageRes),
                contentDescription = destination.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = destination.title,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "${destination.city} • ${destination.subtitle}",
            style = MaterialTheme.typography.bodyMedium,
            color = SilkGold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = destination.historyFact,
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
            color = if (isDark) Color(0xFFCAD6E2) else Color(0xFF4A5568)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            GoldGradientButton(
                text = "Add to AI Plan",
                onClick = onPlanTrip,
                modifier = Modifier.weight(1f),
                testTag = "surprise_plan_button"
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isDark) Color(0x330047AB) else Color(0xFFE8F0FE))
                    .clickable { onOpenDetail() }
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Full Details",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) NeonGold else RegistanBlue
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DestinationDetailContent(
    destination: Destination,
    onNavigateToAr: () -> Unit,
    onNavigateToPlanner: () -> Unit,
    onNavigateTo3DMap: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentPadding = PaddingValues(bottom = 30.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassPillBadge(
                    text = destination.city.uppercase(),
                    accentColor = SilkGold,
                    textColor = SilkGold
                )

                IconButton(onClick = onClose) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main 4K Image Hero
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                shape = RoundedCornerShape(22.dp)
            ) {
                Image(
                    painter = painterResource(id = destination.imageRes),
                    contentDescription = destination.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = destination.title,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = destination.subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = SilkGold
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DetailStatChip(label = "Rating", value = "★ ${destination.rating}")
                DetailStatChip(label = "Afrasiyob", value = destination.afrasiyobDuration)
                DetailStatChip(label = "Crowd", value = destination.crowdLevel.label.split(" ").first())
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "HISTORICAL ARCHITECTURE",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) NeonGold else RegistanBlue
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = destination.description,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(14.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = if (isDark) Color(0x33D4AF37) else Color(0xFFFFF9E6),
                borderColor = SilkGold.copy(alpha = 0.5f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Silk Road Secret Fact",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = SilkGoldDark)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = destination.historyFact,
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "KEY HIGHLIGHTS",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) NeonGold else RegistanBlue
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            destination.highlights.forEach { highlight ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(TurquoiseTile)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = highlight,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Shaxsiy VIP Avtopark & Transfer Integration Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = if (isDark) Color(0xEE0B162C) else Color(0xFFF0F6FF),
                borderColor = if (isDark) NeonGold.copy(alpha = 0.5f) else Color(0x330047AB)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SHAXSIY VIP AVTOPARK & TRANSFER",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = NeonGold,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "24/7 Dispetcher: +998 91 033 04 60",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isDark) Color(0x440047AB) else Color.White)
                                .border(1.dp, TurquoiseTile.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .clickable {
                                    com.example.util.PrivateTaxiParkHelper.openNavigationMap(
                                        context,
                                        destination.coordinates.first,
                                        destination.coordinates.second,
                                        destination.title
                                    )
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Xarita",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = if (isDark) Color.White else RegistanBlue
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.horizontalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF8F00)))
                                )
                                .clickable {
                                    com.example.util.PrivateTaxiParkHelper.openTelegramDispatch(
                                        context,
                                        "${destination.title} (${destination.city}) manziliga VIP Shaxsiy Taxi buyurtmasi"
                                    )
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Taxi Chaqirish",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp,
                                    color = Color.Black
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GoldGradientButton(
                    text = "Launch AR Spatial Guide",
                    icon = Icons.Filled.CameraAlt,
                    onClick = onNavigateToAr,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "detail_launch_ar_button"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isDark) Color(0x330047AB) else Color(0xFFE8F0FE))
                            .clickable { onNavigateToPlanner() }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Add to AI Plan",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) NeonGold else RegistanBlue
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isDark) Color(0x3300A896) else Color(0xFFE6FAF6))
                            .clickable { onNavigateTo3DMap() }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "View on 3D Map",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TurquoiseTile
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailStatChip(label: String, value: String) {
    val isDark = isSystemInDarkTheme()

    GlassCard(
        shape = RoundedCornerShape(14.dp),
        elevation = 2.dp,
        modifier = Modifier.height(58.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = if (isDark) Color(0xFF8E9BB0) else Color(0xFF64748B)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
