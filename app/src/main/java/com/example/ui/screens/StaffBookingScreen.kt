package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BookingOrderStatus
import com.example.model.SampleTourStaff
import com.example.model.StaffRoleType
import com.example.model.TourBookingOrder
import com.example.model.TourGuideStaff
import com.example.model.appString
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassPillBadge
import com.example.ui.components.GoldGradientButton
import com.example.ui.components.UzbekStarEmblem
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.MidnightCanvas
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.RegistanBlueDark
import com.example.ui.theme.SilkGold
import com.example.ui.theme.SilkGoldLight
import com.example.ui.theme.TurquoiseTile
import com.example.util.UserSessionManager
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StaffBookingScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isDark = isSystemInDarkTheme()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Xodimlar va Zakaz, 1: Mening Buyurtmalarim
    var selectedRoleFilter by remember { mutableStateOf<StaffRoleType?>(null) }
    var selectedCityFilter by remember { mutableStateOf("Barchasi") }

    // Dialog state for booking
    var selectedStaffForBooking by remember { mutableStateOf<TourGuideStaff?>(null) }
    var activeBookingSuccessOrder by remember { mutableStateOf<TourBookingOrder?>(null) }

    val activeOrders = remember {
        mutableStateListOf<TourBookingOrder>().apply {
            addAll(SampleTourStaff.initialActiveOrders)
        }
    }

    val cities = listOf("Barchasi", "Samarqand", "Buxoro", "Xiva", "Toshkent", "Shahrisabz")

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
                                    text = appString("staff_top_badge"),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.5.sp
                                    ),
                                    color = if (isDark) NeonGold else SilkGold
                                )
                            }
                            Text(
                                text = appString("staff_top_title"),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 24.sp
                                ),
                                color = if (isDark) Color.White else Color(0xFF0F172A)
                            )
                        }

                        // Order Count Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0x2200C853))
                                .border(1.dp, Color(0xFF00C853).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                                .clickable { selectedTab = 1 }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.BookmarkAdded,
                                    contentDescription = null,
                                    tint = Color(0xFF00E676),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${activeOrders.size} ${appString("orders_count_suffix")}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF00E676)
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = appString("staff_top_subtitle"),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        ),
                        color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF475569)
                    )
                }
            }

            // Top Navigation Tabs (Xodimlar Katalogi / Mening Buyurtmalarim)
            item {
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
                    divider = {},
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.SupervisorAccount,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = appString("staff_tab_staff"),
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        },
                        selectedContentColor = NeonGold,
                        unselectedContentColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )

                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.History,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${appString("staff_tab_my_orders")} (${activeOrders.size})",
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        },
                        selectedContentColor = NeonGold,
                        unselectedContentColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // TAB 0: Xodimlar va Zakaz berish
            if (selectedTab == 0) {
                // Hero Banner
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(22.dp),
                        backgroundColor = if (isDark) Color(0x330047AB) else Color.White,
                        borderColor = NeonGold.copy(alpha = 0.4f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Brush.radialGradient(listOf(NeonGold, SilkGold)))
                                    .padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.WorkspacePremium,
                                    contentDescription = null,
                                    tint = MidnightCanvas,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "100% Rasmiy & Litsenziyali Gidlar",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isDark) Color.White else Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Sayyohlarni kutib olish, shahar bo'ylab xavfsiz sayohat va madaniy ekskursiya kafolati.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }

                // City Filter Chips
                item {
                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        Text(
                            text = "SHAHARNI TANLANG",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp
                            ),
                            color = if (isDark) NeonGold else SilkGold,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                        )
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(cities) { city ->
                                val isSelected = selectedCityFilter == city
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isSelected) NeonGold else if (isDark) Color(0x22FFFFFF) else Color.White
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) NeonGold else Color(0x33FFFFFF),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { selectedCityFilter = city }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = city,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                                        ),
                                        color = if (isSelected) MidnightCanvas else if (isDark) Color.White else Color(0xFF1E293B)
                                    )
                                }
                            }
                        }
                    }
                }

                // Role Filter Chips
                item {
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        Text(
                            text = "XODIM VA XIZMAT TOIFASI",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp
                            ),
                            color = if (isDark) NeonGold else SilkGold,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                        )
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                val isSelected = selectedRoleFilter == null
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) Color(0x33F6C845) else if (isDark) Color(0x22FFFFFF) else Color.White)
                                        .border(1.dp, if (isSelected) NeonGold else Color(0x22FFFFFF), RoundedCornerShape(12.dp))
                                        .clickable { selectedRoleFilter = null }
                                        .padding(horizontal = 12.dp, vertical = 7.dp)
                                ) {
                                    Text(
                                        text = "Barchasi",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) NeonGold else if (isDark) Color.White else Color(0xFF1E293B)
                                    )
                                }
                            }

                            items(StaffRoleType.values()) { role ->
                                val isSelected = selectedRoleFilter == role
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) Color(0x33F6C845) else if (isDark) Color(0x22FFFFFF) else Color.White)
                                        .border(1.dp, if (isSelected) NeonGold else Color(0x22FFFFFF), RoundedCornerShape(12.dp))
                                        .clickable { selectedRoleFilter = role }
                                        .padding(horizontal = 12.dp, vertical = 7.dp)
                                ) {
                                    Text(
                                        text = role.titleUz,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) NeonGold else if (isDark) Color.White else Color(0xFF1E293B)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Filtered Staff List
                val filteredStaff = SampleTourStaff.staffList.filter { staff ->
                    val roleMatches = selectedRoleFilter == null || staff.roleType == selectedRoleFilter
                    val cityMatches = selectedCityFilter == "Barchasi" || staff.operatingCities.contains(selectedCityFilter) || staff.operatingCities.contains("Barcha shaharlar")
                    roleMatches && cityMatches
                }

                items(filteredStaff) { staff ->
                    StaffGuideCard(
                        staff = staff,
                        onBookClick = { selectedStaffForBooking = staff },
                        onCallClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${staff.phoneNumber}"))
                            context.startActivity(intent)
                        },
                        isDark = isDark,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            // TAB 1: Mening Buyurtmalarim (Active & Completed Bookings)
            if (selectedTab == 1) {
                if (activeOrders.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 60.dp, bottom = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.BookmarkAdded,
                                contentDescription = null,
                                tint = NeonGold.copy(alpha = 0.5f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Hozircha faol buyurtmalar yo'q",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isDark) Color.White else Color(0xFF0F172A)
                            )
                            Text(
                                text = "Mas'ul xodimlardan birini tanlang va sayyohlik ekskursiyasiga buyurtma bering.",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            GoldGradientButton(
                                text = "Xodimlarni ko'rish  ➔",
                                onClick = { selectedTab = 0 },
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                        }
                    }
                } else {
                    items(activeOrders) { order ->
                        ActiveOrderCard(
                            order = order,
                            onCallStaff = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${order.staffPhone}"))
                                context.startActivity(intent)
                            },
                            onCancelOrder = {
                                activeOrders.remove(order)
                                Toast.makeText(context, "Buyurtma bekor qilindi", Toast.LENGTH_SHORT).show()
                            },
                            isDark = isDark,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // ==========================================
        // BOOKING INTERACTIVE MODAL DIALOG
        // ==========================================
        selectedStaffForBooking?.let { staff ->
            StaffBookingFormDialog(
                staff = staff,
                onDismiss = { selectedStaffForBooking = null },
                onConfirmBooking = { order ->
                    activeOrders.add(0, order)
                    selectedStaffForBooking = null
                    activeBookingSuccessOrder = order
                    selectedTab = 1

                    // Push booking to Supabase `bookings` table for Admin app
                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                        val remoteBooking = com.example.network.SupabaseClient.RemoteBooking(
                            id = "",
                            bookingCode = order.orderId,
                            tourId = "0",
                            tourTitle = "${order.staffRole.titleUz}: ${order.staffName}",
                            touristName = order.touristName,
                            touristPhone = order.touristPhone,
                            touristEmail = "tourist@silkroad.uz",
                            touristCountry = "O'zbekiston",
                            startDate = order.tourDate,
                            peopleCount = order.touristCount,
                            totalPrice = order.totalAmountUzs.toDouble(),
                            bookingStatus = "Kutilmoqda",
                            paymentStatus = "Naqd to'lov",
                            notes = order.specialRequests
                        )
                        val result = com.example.network.SupabaseClient.createBooking(context, remoteBooking)
                        android.util.Log.d("StaffBookingScreen", "Staff booking create result: $result")
                        // Instant real-time transmission via WebSockets to Admin
                        com.example.network.SupabaseRealtimeManager.broadcastBooking(context, remoteBooking)
                    }
                },
                isDark = isDark
            )
        }

        // ==========================================
        // SUCCESS ORDER CONFIRMATION DIALOG
        // ==========================================
        activeBookingSuccessOrder?.let { order ->
            BookingSuccessDialog(
                order = order,
                onDismiss = { activeBookingSuccessOrder = null },
                onCallStaff = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${order.staffPhone}"))
                    context.startActivity(intent)
                },
                isDark = isDark
            )
        }
    }
}

/**
 * Individual Staff Guide Profile Card
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StaffGuideCard(
    staff: TourGuideStaff,
    onBookClick: () -> Unit,
    onCallClick: () -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val formattedPrice = NumberFormat.getNumberInstance(Locale.US).format(staff.pricePerHourUzs)
    val formattedDayPrice = NumberFormat.getNumberInstance(Locale.US).format(staff.pricePerDayUzs)

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        backgroundColor = if (isDark) Color(0x33081A38) else Color.White,
        borderColor = NeonGold.copy(alpha = 0.35f)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Top Row: Avatar & Badges & Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar Badge with Star
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(staff.avatarColorHex), RegistanBlueDark)
                            )
                        )
                        .border(2.dp, NeonGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = staff.fullName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString(""),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = NeonGold
                        )
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = staff.fullName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            ),
                            color = if (isDark) Color.White else Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.Verified,
                            contentDescription = "Litsenziyali",
                            tint = TurquoiseTile,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = staff.roleBadge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp
                        ),
                        color = NeonGold
                    )
                }

                // Rating & Completed Tours
                Column(horizontalAlignment = Alignment.End) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x22F6C845))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = NeonGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${staff.rating}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = NeonGold
                            )
                        )
                    }
                    Text(
                        text = "${staff.completedToursCount} tur",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bio
            Text(
                text = staff.bioUz,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 17.sp),
                color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Vehicle info if applicable
            staff.vehicleInfo?.let { vehicle ->
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x1800E5FF))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.DirectionsCar,
                        contentDescription = null,
                        tint = TurquoiseTile,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = vehicle,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = TurquoiseTile
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Spoken Languages & Cities
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                staff.languages.forEach { lang ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDark) Color(0x22FFFFFF) else Color(0xFFE2E8F0))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = lang,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = if (isDark) Color.White else Color(0xFF1E293B)
                        )
                    }
                }

                staff.operatingCities.take(2).forEach { city ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x1F0047AB))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Place,
                                contentDescription = null,
                                tint = TurquoiseTile,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = city,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = TurquoiseTile
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Price & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$formattedPrice UZS / soat",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        ),
                        color = NeonGold
                    )
                    Text(
                        text = "$formattedDayPrice UZS / kun",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Call Button
                    IconButton(
                        onClick = onCallClick,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0x2600C853))
                            .border(1.dp, Color(0xFF00C853).copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Call,
                            contentDescription = "Qo'ng'iroq",
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Book Action Button
                    GoldGradientButton(
                        text = "Zakaz berish  ➔",
                        onClick = onBookClick,
                        modifier = Modifier
                            .height(42.dp)
                            .testTag("book_staff_${staff.id}")
                    )
                }
            }
        }
    }
}

/**
 * Interactive Booking Form Dialog
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StaffBookingFormDialog(
    staff: TourGuideStaff,
    onDismiss: () -> Unit,
    onConfirmBooking: (TourBookingOrder) -> Unit,
    isDark: Boolean
) {
    var selectedCity by remember { mutableStateOf(staff.operatingCities.firstOrNull() ?: "Samarqand") }
    var selectedDate by remember { mutableStateOf("Bugun") }
    var selectedStartTime by remember { mutableStateOf("10:00") }
    var durationHours by remember { mutableIntStateOf(6) } // 3, 6, 8 (Full day)
    var touristCount by remember { mutableIntStateOf(2) }
    var preferredLanguage by remember { mutableStateOf(staff.languages.firstOrNull() ?: "O'zbek") }
    var pickupAddress by remember { mutableStateOf("Registon Plaza Mehmonxonasi") }
    val savedProfile = UserSessionManager.currentProfileState
    var touristName by remember {
        mutableStateOf(
            if (savedProfile != null && savedProfile.firstName.isNotBlank())
                "${savedProfile.firstName} ${savedProfile.lastName}".trim()
            else ""
        )
    }
    var touristPhone by remember {
        mutableStateOf(
            if (savedProfile != null && savedProfile.phoneNumber.isNotBlank())
                savedProfile.phoneNumber
            else "+998 "
        )
    }
    var specialRequests by remember { mutableStateOf("") }

    // Extras
    val extraOptions = listOf(
        "🎟️ Registon va obidalar chiptalarini oldindan olish (+50,000 UZS)",
        "🍽️ Samarqand Osh Markazida stol band qilish (+30,000 UZS)",
        "🪧 Aeroport / Vokzalda ismli tablichka bilan kutib olish (+40,000 UZS)",
        "📸 Professional 4K fotosessiya (+100,000 UZS)"
    )
    val selectedExtras = remember { mutableStateListOf<String>() }

    // Price calculation
    val basePrice = if (durationHours >= 8) staff.pricePerDayUzs else staff.pricePerHourUzs * durationHours
    val extrasPrice = selectedExtras.size * 50000L
    val totalCalculatedPrice = basePrice + extrasPrice

    val formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(totalCalculatedPrice)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                UzbekStarEmblem(size = 20.dp, primaryColor = NeonGold, secondaryColor = TurquoiseTile)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Mas'ul Xodimga Zakaz Berish",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                    Text(
                        text = "${staff.fullName} (${staff.roleBadge})",
                        style = MaterialTheme.typography.labelSmall.copy(color = NeonGold)
                    )
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(440.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // City Selection
                item {
                    Text(
                        text = "1. Shaharni tanlang:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Samarqand", "Buxoro", "Xiva", "Toshkent").forEach { city ->
                            val isSel = selectedCity == city
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) NeonGold else if (isDark) Color(0x22FFFFFF) else Color(0xFFE2E8F0))
                                    .clickable { selectedCity = city }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = city,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    ),
                                    color = if (isSel) MidnightCanvas else if (isDark) Color.White else Color(0xFF1E293B)
                                )
                            }
                        }
                    }
                }

                // Date & Time
                item {
                    Text(
                        text = "2. Sana va boshlanish vaqti:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Bugun", "Ertaga", "Indinga").forEach { d ->
                            val isSel = selectedDate == d
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) Color(0x33F6C845) else if (isDark) Color(0x22FFFFFF) else Color(0xFFE2E8F0))
                                    .border(1.dp, if (isSel) NeonGold else Color.Transparent, RoundedCornerShape(10.dp))
                                    .clickable { selectedDate = d }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = d,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSel) NeonGold else if (isDark) Color.White else Color(0xFF1E293B)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("09:00", "10:30", "14:00", "16:00", "18:30").forEach { time ->
                            val isSel = selectedStartTime == time
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) NeonGold else if (isDark) Color(0x22FFFFFF) else Color(0xFFE2E8F0))
                                    .clickable { selectedStartTime = time }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = time,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = if (isSel) MidnightCanvas else if (isDark) Color.White else Color(0xFF1E293B)
                                )
                            }
                        }
                    }
                }

                // Duration & Tourist count
                item {
                    Text(
                        text = "3. Davomiylik va Sayyohlar soni:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(3 to "3 soat", 6 to "6 soat (Yarim kun)", 8 to "To'liq 1 kun").forEach { (hrs, label) ->
                            val isSel = durationHours == hrs
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) Color(0x3300E5FF) else if (isDark) Color(0x22FFFFFF) else Color(0xFFE2E8F0))
                                    .border(1.dp, if (isSel) TurquoiseTile else Color.Transparent, RoundedCornerShape(10.dp))
                                    .clickable { durationHours = hrs }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSel) TurquoiseTile else if (isDark) Color.White else Color(0xFF1E293B),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sayyohlar soni: $touristCount kishi",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDark) Color.White else Color(0xFF0F172A)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(1, 2, 4, 8, 15).forEach { count ->
                                val isSel = touristCount == count
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (isSel) NeonGold else if (isDark) Color(0x22FFFFFF) else Color(0xFFE2E8F0))
                                    .clickable { touristCount = count },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$count",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSel) FontWeight.Black else FontWeight.Normal
                                        ),
                                        color = if (isSel) MidnightCanvas else if (isDark) Color.White else Color(0xFF1E293B)
                                    )
                                }
                            }
                        }
                    }
                }

                // Pickup location
                item {
                    OutlinedTextField(
                        value = pickupAddress,
                        onValueChange = { pickupAddress = it },
                        label = { Text("Kutib olish manzili (Mehmonxona / Aeroport)") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Filled.LocationOn, contentDescription = null, tint = NeonGold)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGold,
                            unfocusedBorderColor = Color(0x44FFFFFF)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Tourist Contact Info
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = touristName,
                            onValueChange = { touristName = it },
                            label = { Text("Ismingiz") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = touristPhone,
                            onValueChange = { touristPhone = it },
                            label = { Text("Telefon") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // Included Extras Checklist
                item {
                    Text(
                        text = "Qo'shimcha qulayliklar:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                    extraOptions.forEach { extra ->
                        val isChecked = selectedExtras.contains(extra)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isChecked) selectedExtras.remove(extra) else selectedExtras.add(extra)
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { check ->
                                    if (check) selectedExtras.add(extra) else selectedExtras.remove(extra)
                                },
                                colors = CheckboxDefaults.colors(checkedColor = NeonGold)
                            )
                            Text(
                                text = extra,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155)
                            )
                        }
                    }
                }

                // Price Summary Banner
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        backgroundColor = Color(0x22F6C845),
                        borderColor = NeonGold
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
                                    text = "JAMI ZAKAZ NARXI:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    color = NeonGold
                                )
                                Text(
                                    text = "$durationHours soatlik to'liq xizmat",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B)
                                )
                            }

                            Text(
                                text = "$formattedTotal UZS",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp
                                ),
                                color = NeonGold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            GoldGradientButton(
                text = "Buyurtmani Tasdiqlash  ✓",
                onClick = {
                    val newOrder = TourBookingOrder(
                        orderId = "UZ-TOUR-${(1000..9999).random()}",
                        staffId = staff.id,
                        staffName = staff.fullName,
                        staffRole = staff.roleType,
                        staffPhone = staff.phoneNumber,
                        touristName = touristName.trim().ifEmpty { "Hurmatli Mehmon" },
                        touristPhone = touristPhone.trim(),
                        selectedCity = selectedCity,
                        tourDate = selectedDate,
                        tourStartTime = selectedStartTime,
                        durationHours = durationHours,
                        touristCount = touristCount,
                        preferredLanguage = preferredLanguage,
                        pickupLocation = pickupAddress.trim().ifEmpty { "Registon Plaza" },
                        includedExtras = selectedExtras.toList(),
                        specialRequests = specialRequests.trim(),
                        totalAmountUzs = totalCalculatedPrice,
                        status = BookingOrderStatus.CONFIRMED,
                        createdAtFormatted = "Bugun, hozir"
                    )
                    onConfirmBooking(newOrder)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("confirm_booking_button")
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Bekor qilish", color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B))
            }
        },
        containerColor = if (isDark) MidnightCanvas else Color(0xFFF8FAFC)
    )
}

/**
 * Active Order Card in "Mening Buyurtmalarim"
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActiveOrderCard(
    order: TourBookingOrder,
    onCallStaff: () -> Unit,
    onCancelOrder: () -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(order.totalAmountUzs)

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        backgroundColor = if (isDark) Color(0x330047AB) else Color.White,
        borderColor = Color(order.status.colorHex).copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Top Order ID & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    UzbekStarEmblem(size = 18.dp, primaryColor = NeonGold, secondaryColor = TurquoiseTile)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = order.orderId,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = NeonGold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(order.status.colorHex).copy(alpha = 0.2f))
                        .border(1.dp, Color(order.status.colorHex).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = order.status.labelUz,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = Color(order.status.colorHex)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Staff Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0x33F6C845))
                        .border(1.dp, NeonGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = NeonGold,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.staffName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                    Text(
                        text = order.staffRole.titleUz,
                        style = MaterialTheme.typography.labelSmall.copy(color = TurquoiseTile)
                    )
                }

                // Call Staff Button
                IconButton(
                    onClick = onCallStaff,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0x2600C853))
                        .border(1.dp, Color(0xFF00C853), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Call,
                        contentDescription = "Qo'ng'iroq",
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Order Details Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isDark) Color(0x22020B18) else Color(0xFFF1F5F9))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "📍 Manzil & Shahar:",
                        style = MaterialTheme.typography.bodySmall.copy(color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B))
                    )
                    Text(
                        text = "${order.selectedCity} • ${order.pickupLocation}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "⏰ Vaqt & Davomiylik:",
                        style = MaterialTheme.typography.bodySmall.copy(color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B))
                    )
                    Text(
                        text = "${order.tourDate}, ${order.tourStartTime} (${order.durationHours} soat)",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = NeonGold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "👥 Sayyohlar soni:",
                        style = MaterialTheme.typography.bodySmall.copy(color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B))
                    )
                    Text(
                        text = "${order.touristCount} nafar",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                }

                if (order.includedExtras.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Kiritilgan qulayliklar: ${order.includedExtras.joinToString(", ")}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = TurquoiseTile
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Total & Cancel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "JAMI SUMMA:",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B)
                    )
                    Text(
                        text = "$formattedTotal UZS",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = NeonGold
                    )
                }

                TextButton(onClick = onCancelOrder) {
                    Text(
                        text = "Bekor qilish",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFFF5252))
                    )
                }
            }
        }
    }
}

/**
 * Success Dialog after Booking
 */
@Composable
fun BookingSuccessDialog(
    order: TourBookingOrder,
    onDismiss: () -> Unit,
    onCallStaff: () -> Unit,
    isDark: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0x2600C853)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(38.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Buyurtmangiz Qabul Qilindi!",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = if (isDark) Color.White else Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Zakaz kodi: ${order.orderId}",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = NeonGold)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Mas'ul xodim ${order.staffName} sizning sayohatingizga biriktirildi. Xodim ko'rsatilgan vaqtda (${order.tourStartTime}) sizni ${order.pickupLocation} manzilida kutib oladi.",
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                    textAlign = TextAlign.Center,
                    color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x1800C853))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Verified,
                        contentDescription = null,
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Xodim bilan to'g'ridan-to'g'ri aloqa o'rnatildi",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF00E676)
                    )
                }
            }
        },
        confirmButton = {
            GoldGradientButton(
                text = "Xodimga Qo'ng'iroq Qilish  📞",
                onClick = onCallStaff,
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Tushunarli, yopish",
                    color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B)
                )
            }
        },
        containerColor = if (isDark) MidnightCanvas else Color(0xFFF8FAFC)
    )
}
