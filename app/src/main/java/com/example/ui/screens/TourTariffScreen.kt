package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.PaymentMethod
import com.example.model.SampleTourTariffs
import com.example.model.TariffBookingReceipt
import com.example.model.TourTariff
import com.example.model.appString
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassPillBadge
import com.example.ui.components.GoldGradientButton
import com.example.ui.components.UzbekStarEmblem
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.RegistanBlueDark
import com.example.ui.theme.SilkGold
import com.example.ui.theme.TurquoiseTile
import com.example.util.TariffStorageManager
import com.example.util.UserSessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourTariffScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(Unit) {
        TariffStorageManager.initialize(context)
    }

    val bookedReceipts by TariffStorageManager.receipts.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Tariflar (3 xil), 1: Cheklar va Kvitansiyalar (Receipts)
    var selectedTariffForBooking by remember { mutableStateOf<TourTariff?>(null) }
    var viewingReceiptDetails by remember { mutableStateOf<TariffBookingReceipt?>(null) }

    val userProfile = remember { UserSessionManager.loadProfile(context) }
    val profileName = remember(userProfile) { userProfile?.let { "${it.firstName} ${it.lastName}".trim() }.orEmpty() }
    val profilePhone = remember(userProfile) { userProfile?.phoneNumber.orEmpty() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF030B1A) else Color(0xFFF2F6FC))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0x33FFFFFF) else Color(0x15000000))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = appString("back_button"),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = appString("tariffs_screen_title"),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            UzbekStarEmblem(size = 18.dp)
                        }
                        Text(
                            text = appString("tariffs_screen_sub"),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = TurquoiseTile,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Receipts Counter Badge (Compact & Responsive)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(NeonGold.copy(alpha = 0.15f))
                        .border(1.dp, NeonGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable { selectedTab = 1 }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Receipt,
                            contentDescription = null,
                            tint = NeonGold,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${bookedReceipts.size}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = if (isDark) NeonGold else SilkGold
                            )
                        )
                    }
                }
            }

            // Tabs: 3 Xil Tarif / Xarajatlar & Cheklar
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = NeonGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = NeonGold,
                        height = 3.dp
                    )
                },
                divider = {
                    HorizontalDivider(
                        color = if (isDark) Color(0x22FFFFFF) else Color(0x15000000)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.WorkspacePremium,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (selectedTab == 0) NeonGold else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = appString("tariffs_tab_packages"),
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp,
                                color = if (selectedTab == 0) NeonGold else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                )

                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Receipt,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (selectedTab == 1) NeonGold else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = appString("tariffs_tab_receipts"),
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp,
                                color = if (selectedTab == 1) NeonGold else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tab Contents
            if (selectedTab == 0) {
                // Tab 0: Tariffs List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        // Trust & Info Banner
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = if (isDark) Color(0xCC091936) else Color(0xEEFFFFFF),
                            borderColor = TurquoiseTile.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(TurquoiseTile.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Verified,
                                        contentDescription = null,
                                        tint = TurquoiseTile,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "100% Rasmiy va Kafolatlangan Xizmat",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = "To'lovni Uzcard, Humo, Visa, Mastercard yoki yetib kelganda Naqd qilishingiz mumkin. Barcha xarajatlar cheklari ilovangizda to'liq saqlanadi.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
                                    )
                                }
                            }
                        }
                    }

                    // 3 Tour Tariffs
                    items(SampleTourTariffs.items) { tariff ->
                        TourTariffCard(
                            tariff = tariff,
                            onBookClicked = {
                                selectedTariffForBooking = tariff
                            },
                            onViewReceiptSample = {
                                selectedTab = 1
                            }
                        )
                    }
                }
            } else {
                // Tab 1: Booked Receipts & Invoices
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text(
                            text = "Mening Buyurtmalarim va To'lov Cheklarim",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Har bir xaridingiz, qo'shimcha xarajatlar va kvitansiyalar ushbu bo'limda saqlanadi.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }

                    if (bookedReceipts.isEmpty()) {
                        item {
                            GlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                backgroundColor = if (isDark) Color(0xAA08162E) else Color(0xF0FFFFFF)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = "🧾", fontSize = 48.sp)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Hozircha faol cheklar mavjud emas",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "3 xil tarifdan birini tanlab buyurtma bering va chekingizni oling.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    GoldGradientButton(
                                        text = "Tariflarni Ko'rish",
                                        onClick = { selectedTab = 0 }
                                    )
                                }
                            }
                        }
                    } else {
                        items(bookedReceipts) { receipt ->
                            BookedReceiptItemCard(
                                receipt = receipt,
                                onViewDetails = { viewingReceiptDetails = receipt }
                            )
                        }
                    }
                }
            }
        }

        // Payment & Booking Bottom Sheet Dialog
        selectedTariffForBooking?.let { tariff ->
            TariffPaymentBottomSheet(
                tariff = tariff,
                userProfileName = profileName,
                userProfilePhone = profilePhone,
                onDismiss = { selectedTariffForBooking = null },
                onPaymentCompleted = { receipt ->
                    TariffStorageManager.addReceipt(context, receipt)
                    selectedTariffForBooking = null
                    viewingReceiptDetails = receipt
                    Toast.makeText(
                        context,
                        "🎉 Tabriklaymiz! ${tariff.name} buyurtmangiz muvaffaqiyatli to'landi!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }

        // Detailed Receipt Modal Dialog
        viewingReceiptDetails?.let { receipt ->
            TariffDetailedReceiptDialog(
                receipt = receipt,
                onClose = { viewingReceiptDetails = null }
            )
        }
    }
}

@Composable
fun TourTariffCard(
    tariff: TourTariff,
    onBookClicked: () -> Unit,
    onViewReceiptSample: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    var isExpanded by remember { mutableStateOf(tariff.tariffNumber == 1) }

    val borderColor = when (tariff.tariffNumber) {
        1 -> NeonGold
        2 -> TurquoiseTile
        else -> SilkGold
    }

    val headerGradient = when (tariff.tariffNumber) {
        1 -> listOf(Color(0xFF0F2B5C), Color(0xFF1E3A8A))
        2 -> listOf(Color(0xFF0D324D), Color(0xFF1A535C))
        else -> listOf(Color(0xFF331D2C), Color(0xFF3F2E56))
    }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp))
            .testTag("tariff_card_${tariff.tariffNumber}"),
        shape = RoundedCornerShape(24.dp),
        backgroundColor = if (isDark) Color(0xF808162E) else Color(0xFAFFFFFF),
        borderColor = borderColor,
        elevation = 10.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(headerGradient))
                    .padding(14.dp)
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
                        Text(
                            text = tariff.coverEmoji,
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f, fill = false)) {
                            Text(
                                text = tariff.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = Color.White
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            GlassPillBadge(
                                text = tariff.titleBadge,
                                textColor = NeonGold,
                                accentColor = NeonGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Price Tag (Responsive: Never collapses or breaks into single vertical characters)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.Black.copy(alpha = 0.35f))
                            .border(1.dp, NeonGold.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = tariff.getFormattedUsd(),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 19.sp,
                                    color = NeonGold
                                ),
                                maxLines = 1,
                                softWrap = false
                            )
                            Text(
                                text = "${tariff.durationDays} kunlik tur",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.5.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                ),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }

            // Body Content
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = tariff.description,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Key Inclusions Grid (Hotel, Food, Taxi)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color(0x3300C2FF) else Color(0x150047AB))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Hotel
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Filled.Hotel,
                            contentDescription = null,
                            tint = NeonGold,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 1.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Mehmonxona (Hotel)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = TurquoiseTile
                                )
                            )
                            Text(
                                text = tariff.hotelIncludedDesc,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    // Food
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Filled.Restaurant,
                            contentDescription = null,
                            tint = NeonGold,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 1.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Milliy Ovqatlanish",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = TurquoiseTile
                                )
                            )
                            Text(
                                text = tariff.foodIncludedDesc,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    // Taxi / Transport
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Filled.DirectionsCar,
                            contentDescription = null,
                            tint = NeonGold,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 1.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Shaxsiy Taxi & Transfer",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = TurquoiseTile
                                )
                            )
                            Text(
                                text = tariff.transportIncludedDesc,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }

                // 1-Tarif Samarqand 5 ta aylanadigan joyi maxsus bloki
                if (tariff.placesToVisit.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExpanded = !isExpanded }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = NeonGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (tariff.tariffNumber == 1) "Samarqandda 5 ta Aylanadigan Joy:" else "Aylanadigan Asosiy Maskonlar (${tariff.placesToVisit.size}):",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                ),
                                color = if (isDark) NeonGold else RegistanBlue
                            )
                        }

                        Text(
                            text = if (isExpanded) "Yopish 🔼" else "Ko'rish 🔽",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = TurquoiseTile
                            )
                        )
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            tariff.placesToVisit.forEach { place ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isDark) Color(0x33061226) else Color(0x100047AB))
                                        .border(1.dp, if (isDark) Color(0x22D4AF37) else Color(0x150047AB), RoundedCornerShape(12.dp))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = place.icon, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = place.name,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Text(
                                            text = place.description,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Extra Expenses & Cheklar Preview
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0x2200E676) else Color(0x1500C853))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
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
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Barcha qo'shimcha xarajatlar cheklari ilovada",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "To'liq Kafolat ✓",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = if (isDark) NeonGold else SilkGold
                        ),
                        modifier = Modifier.clickable { onViewReceiptSample() }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons: Karta / Naqd Buyurtma Berish
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Book Now Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(NeonGold, SilkGold)
                                )
                            )
                            .clickable { onBookClicked() }
                            .padding(vertical = 12.dp)
                            .testTag("book_tariff_${tariff.tariffNumber}_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.CreditCard,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Buyurtma & To'lov (${tariff.getFormattedUsd()})",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TariffPaymentBottomSheet(
    tariff: TourTariff,
    userProfileName: String,
    userProfilePhone: String,
    onDismiss: () -> Unit,
    onPaymentCompleted: (TariffBookingReceipt) -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.CARD) }
    var touristName by remember { mutableStateOf(userProfileName) }
    var touristPhone by remember { mutableStateOf(userProfilePhone) }
    var guestsCount by remember { mutableIntStateOf(1) }
    var startDateText by remember { mutableStateOf("") }

    // Card Fields
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (isDark) Color(0xFF07142A) else Color(0xFFFFFFFF),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Drag Header
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "To'lov va Buyurtma Rasmiylashtirish",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${tariff.name} • ${tariff.getFormattedUsd()}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = NeonGold
                        )
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Yopish",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Payment Method Selector (Card vs Cash)
            Text(
                text = "To'lov Usulini Tanlang:",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Karta To'lovi
                val isCard = selectedPaymentMethod == PaymentMethod.CARD
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isCard) {
                                if (isDark) NeonGold.copy(alpha = 0.2f) else Color(0xFFFFF3CD)
                            } else {
                                if (isDark) Color(0x330B1C38) else Color(0x150047AB)
                            }
                        )
                        .border(
                            1.5.dp,
                            if (isCard) NeonGold else Color.Transparent,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { selectedPaymentMethod = PaymentMethod.CARD }
                        .padding(12.dp)
                        .testTag("select_payment_card")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "💳", fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Kartadan to'lov",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Uzcard/Humo/Visa",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = TurquoiseTile
                        )
                    }
                }

                // 2. Naqd To'lov
                val isCash = selectedPaymentMethod == PaymentMethod.CASH
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isCash) {
                                if (isDark) Color(0xFF00E676).copy(alpha = 0.2f) else Color(0xFFE8F5E9)
                            } else {
                                if (isDark) Color(0x330B1C38) else Color(0x150047AB)
                            }
                        )
                        .border(
                            1.5.dp,
                            if (isCash) Color(0xFF00E676) else Color.Transparent,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { selectedPaymentMethod = PaymentMethod.CASH }
                        .padding(12.dp)
                        .testTag("select_payment_cash")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "💵", fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Naqd to'lov",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Yetib kelganda to'lash",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color(0xFF00E676)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tourist Info Inputs
            OutlinedTextField(
                value = touristName,
                onValueChange = { touristName = it },
                label = { Text("Ism-familiyangiz") },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = NeonGold) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonGold,
                    unfocusedBorderColor = if (isDark) Color(0x44D4AF37) else Color(0x330047AB)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = touristPhone,
                    onValueChange = { touristPhone = it },
                    label = { Text("Telefon") },
                    leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null, tint = TurquoiseTile) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGold,
                        unfocusedBorderColor = if (isDark) Color(0x44D4AF37) else Color(0x330047AB)
                    ),
                    modifier = Modifier.weight(1.3f)
                )

                OutlinedTextField(
                    value = guestsCount.toString(),
                    onValueChange = { guestsCount = it.toIntOrNull() ?: 1 },
                    label = { Text("Kishilar") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGold,
                        unfocusedBorderColor = if (isDark) Color(0x44D4AF37) else Color(0x330047AB)
                    ),
                    modifier = Modifier.weight(0.7f)
                )
            }

            // Card Specific Fields if Card selected
            if (selectedPaymentMethod == PaymentMethod.CARD) {
                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isDark) Color(0x4408162E) else Color(0x100047AB))
                        .border(1.dp, NeonGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Karta ma'lumotlari (Xavfsiz 256-bit)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeonGold
                            )
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "💳 Uzcard", fontSize = 10.sp, color = TurquoiseTile)
                            Text(text = "💳 Humo", fontSize = 10.sp, color = NeonGold)
                            Text(text = "💳 Visa", fontSize = 10.sp, color = Color(0xFF00E676))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { cardNumber = it },
                        label = { Text("Karta raqami (16 xonali)") },
                        leadingIcon = { Icon(Icons.Filled.CreditCard, contentDescription = null, tint = NeonGold) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = cardExpiry,
                            onValueChange = { cardExpiry = it },
                            label = { Text("Muddati (MM/YY)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = cardCvv,
                            onValueChange = { cardCvv = it },
                            label = { Text("CVV/CVC") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))
                // Cash Note
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF00E676).copy(alpha = 0.15f))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Naqd pul bilan to'lov: Shaxsiy haydovchi yoki dispetcherga yetib kelganda to'laysiz. Rasmiy kvitansiya ilovada shakllanadi.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pay & Confirm Button
            if (isProcessing) {
                CircularProgressIndicator(
                    color = NeonGold,
                    modifier = Modifier.size(36.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                if (selectedPaymentMethod == PaymentMethod.CARD) {
                                    listOf(NeonGold, SilkGold)
                                } else {
                                    listOf(Color(0xFF00E676), Color(0xFF00C853))
                                }
                            )
                        )
                        .clickable {
                            isProcessing = true
                            scope.launch {
                                delay(1200) // realistic payment simulation
                                isProcessing = false

                                val maskedCard = if (selectedPaymentMethod == PaymentMethod.CARD) {
                                    if (cardNumber.length >= 4) "•••• •••• •••• " + cardNumber.takeLast(4) else "8600 •••• •••• 4590"
                                } else null

                                val newReceipt = TariffBookingReceipt(
                                    tariff = tariff,
                                    touristName = touristName,
                                    touristPhone = touristPhone,
                                    startDate = startDateText,
                                    guestsCount = guestsCount,
                                    paymentMethod = selectedPaymentMethod,
                                    cardNumberMasked = maskedCard,
                                    cardType = if (selectedPaymentMethod == PaymentMethod.CARD) "Uzcard / Humo / Visa" else "Naqd Pul",
                                    totalAmountUsd = tariff.priceUsd * guestsCount,
                                    paymentStatus = if (selectedPaymentMethod == PaymentMethod.CARD) "TO'LANGAN & KAFOLATLANGAN" else "QABUL QILINDI (NAQD TO'LOV)",
                                    extraExpenses = tariff.extraExpensesEstimate,
                                    note = "${tariff.name} muvaffaqiyatli band qilindi. Barcha 5 ta maskan, hotel, 3 mahal ovqat va taxi ta'minlanadi."
                                )

                                // Sync booking to Supabase `bookings` table for Admin & Staff app
                                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                                    val remoteBooking = com.example.network.SupabaseClient.RemoteBooking(
                                        id = "",
                                        bookingCode = "UZB-${(1000..9999).random()}",
                                        tourId = "0",
                                        tourTitle = tariff.name,
                                        touristName = touristName.ifBlank { "Sayyoh" },
                                        touristPhone = touristPhone.ifBlank { "+998 90 123 45 67" },
                                        touristEmail = "tourist@silkroad.uz",
                                        touristCountry = "O'zbekiston",
                                        startDate = startDateText.ifBlank { "2026-09-06" },
                                        peopleCount = guestsCount,
                                        totalPrice = tariff.priceUsd * guestsCount * 12800.0,
                                        bookingStatus = "Kutilmoqda",
                                        paymentStatus = if (selectedPaymentMethod == PaymentMethod.CARD) "To'langan" else "Kutilmoqda",
                                        notes = "${tariff.name} buyurtmasi. Kishi soni: $guestsCount"
                                    )
                                    val createResult = com.example.network.SupabaseClient.createBooking(context, remoteBooking)
                                    android.util.Log.d("TourTariffScreen", "Create booking result: $createResult")
                                    // Instant real-time transmission via WebSockets to Admin
                                    com.example.network.SupabaseRealtimeManager.broadcastBooking(context, remoteBooking)
                                }

                                onPaymentCompleted(newReceipt)
                            }
                        }
                        .padding(vertical = 14.dp)
                        .testTag("confirm_tariff_payment_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (selectedPaymentMethod == PaymentMethod.CARD) Icons.Filled.Lock else Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (selectedPaymentMethod == PaymentMethod.CARD) {
                                "To'lovni Tasdiqlash ($${(tariff.priceUsd * guestsCount).toInt()})"
                            } else {
                                "Naqd Buyurtmani Tasdiqlash ($${(tariff.priceUsd * guestsCount).toInt()})"
                            },
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun BookedReceiptItemCard(
    receipt: TariffBookingReceipt,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    val dateFormatted = sdf.format(Date(receipt.timestamp))

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onViewDetails() }
            .testTag("receipt_card_${receipt.orderId}"),
        shape = RoundedCornerShape(18.dp),
        backgroundColor = if (isDark) Color(0xF808162E) else Color(0xFAFFFFFF),
        borderColor = NeonGold.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = receipt.tariff.coverEmoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = receipt.tariff.name,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Chek raqami: ${receipt.orderId} • $dateFormatted",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = TurquoiseTile
                            )
                        )
                    }
                }

                // Price
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${receipt.totalAmountUsd.toInt()}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = NeonGold
                        )
                    )
                    Text(
                        text = receipt.paymentMethod.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = if (receipt.paymentMethod == PaymentMethod.CARD) TurquoiseTile else Color(0xFF00E676)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = if (isDark) Color(0x22FFFFFF) else Color(0x15000000))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = receipt.paymentStatus,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = Color(0xFF00E676)
                        )
                    )
                }

                Text(
                    text = "Batafsil Kvitansiya & Cheklar ➔",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = if (isDark) NeonGold else SilkGold
                    )
                )
            }
        }
    }
}

@Composable
fun TariffDetailedReceiptDialog(
    receipt: TariffBookingReceipt,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val isDark = isSystemInDarkTheme()
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }
    val dateFormatted = sdf.format(Date(receipt.timestamp))

    androidx.compose.ui.window.Dialog(onDismissRequest = onClose) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
                .shadow(24.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            backgroundColor = if (isDark) Color(0xFF08152B) else Color(0xFFFFFFFF),
            borderColor = NeonGold
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    // Receipt Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            UzbekStarEmblem(size = 24.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "RASMIY TO'LOV CHEKI",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp
                                    ),
                                    color = NeonGold
                                )
                                Text(
                                    text = "O'zbekiston Turizm va Servis Kvitansiyasi",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                )
                            }
                        }

                        IconButton(onClick = onClose, modifier = Modifier.size(30.dp)) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Yopish",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    HorizontalDivider(color = NeonGold.copy(alpha = 0.4f))
                }

                item {
                    // Invoice / Order Info Table
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDark) Color(0x3300C2FF) else Color(0x100047AB))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Chek raqami:", style = MaterialTheme.typography.labelSmall.copy(color = TurquoiseTile))
                            Text(text = receipt.orderId, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = NeonGold))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Sana & Vaqt:", style = MaterialTheme.typography.labelSmall)
                            Text(text = dateFormatted, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Mijoz (Sayyoh):", style = MaterialTheme.typography.labelSmall)
                            Text(text = receipt.touristName, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Telefon:", style = MaterialTheme.typography.labelSmall)
                            Text(text = receipt.touristPhone, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "To'lov turi:", style = MaterialTheme.typography.labelSmall)
                            Text(
                                text = "${receipt.paymentMethod.title} ${receipt.cardNumberMasked ?: ""}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (receipt.paymentMethod == PaymentMethod.CARD) TurquoiseTile else Color(0xFF00E676)
                                )
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Holati:", style = MaterialTheme.typography.labelSmall)
                            Text(
                                text = receipt.paymentStatus,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF00E676)
                                )
                            )
                        }
                    }
                }

                item {
                    // Breakdown of items included in Tariff (Samarqand 5 ta maskan, Hotel, Food, Taxi)
                    Text(
                        text = "Tarifga Kiritilgan Xizmatlar & Joylar:",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isDark) NeonGold else RegistanBlue
                        )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        receipt.tariff.placesToVisit.forEach { place ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "• ${place.icon} ${place.name}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
                            }
                        }
                        Text(text = "• 🏨 ${receipt.tariff.hotelIncludedDesc}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground)
                        Text(text = "• 🍲 ${receipt.tariff.foodIncludedDesc}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground)
                        Text(text = "• 🚕 ${receipt.tariff.transportIncludedDesc}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground)
                    }
                }

                item {
                    // Breakdown of Extra Expenses and Receipts
                    Text(
                        text = "Barcha Xarajatlar Tafsiloti va Chek Kvitansiyalari:",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TurquoiseTile
                        )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDark) Color(0x33000000) else Color(0x08000000))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        receipt.extraExpenses.forEach { exp ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = exp.title,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = "Kvitansiya: ${exp.receiptNumber} • ${exp.date}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                    )
                                }
                                Text(
                                    text = "$${exp.amountUsd.toInt()}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = NeonGold
                                    )
                                )
                            }
                        }
                    }
                }

                item {
                    // Total Price Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.horizontalGradient(listOf(NeonGold, SilkGold)))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "JAMI TO'LOV SUMMASI:",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = Color.Black
                            )
                        )
                        Text(
                            text = "$${receipt.totalAmountUsd.toInt()} (${(receipt.totalAmountUsd * 12600).toLong()} UZS)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = Color.Black
                            )
                        )
                    }
                }

                item {
                    // QR Code Simulation & Security Stamp
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDark) Color(0x3300E676) else Color(0x1000E676))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.QrCode,
                            contentDescription = "QR Kod",
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "QR-Kod Kvitansiya: ${receipt.qrCodePayload}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = Color(0xFF00E676)
                            )
                            Text(
                                text = "Tekshirish va nazorat uchun rasmiy elektron muhr",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Share & Copy Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDark) Color(0x33FFFFFF) else Color(0x15000000))
                                .clickable {
                                    val receiptText = """
                                        UZBEKISTAN TOUR RECEIPT
                                        Chek: ${receipt.orderId}
                                        Tarif: ${receipt.tariff.name}
                                        Sana: $dateFormatted
                                        Mijoz: ${receipt.touristName}
                                        Summa: $${receipt.totalAmountUsd.toInt()}
                                        To'lov: ${receipt.paymentMethod.title}
                                        Holat: ${receipt.paymentStatus}
                                    """.trimIndent()
                                    clipboardManager.setText(AnnotatedString(receiptText))
                                    Toast.makeText(context, "Chek ma'lumotlari nusxalandi!", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nusxa Olish 📋",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(TurquoiseTile)
                                .clickable {
                                    Toast.makeText(context, "Chek PDF kvitansiyasi saqlandi!", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "PDF Saqlash 📥",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
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
