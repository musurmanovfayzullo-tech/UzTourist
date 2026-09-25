package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassPillBadge
import com.example.ui.components.GoldGradientButton
import com.example.ui.components.UzbekStarEmblem
import com.example.ui.theme.MidnightCanvas
import com.example.ui.theme.NeonGold
import com.example.util.UserSessionManager
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.RegistanBlueDark
import com.example.ui.theme.SilkGold
import com.example.ui.theme.TurquoiseTile
import java.text.NumberFormat
import java.util.Locale

data class TouristLiveExpense(
    val id: String,
    val title: String,
    val category: ExpenseType,
    val amountUzs: Double,
    val amountUsd: Double,
    val date: String,
    val time: String,
    val location: String,
    val recordedBy: String,
    val staffRole: String,
    val staffId: String,
    val receiptNumber: String,
    val notes: String = "",
    val isVerified: Boolean = true
)

enum class ExpenseType(
    val displayName: String,
    val icon: ImageVector,
    val color: Color
) {
    ALL("Barchasi", Icons.AutoMirrored.Filled.ReceiptLong, NeonGold),
    RESTAURANT("Restoran & Taom", Icons.Filled.Restaurant, Color(0xFFFF9800)),
    TRANSPORT("VIP Transport & Haydovchi", Icons.Filled.DirectionsCar, Color(0xFF2196F3)),
    HOTEL("Mehmonxona & Dam", Icons.Filled.Hotel, Color(0xFF9C27B0)),
    TICKETS("Muzey & Chiptalar", Icons.Filled.ConfirmationNumber, Color(0xFF00BCD4)),
    SHOPPING("Bozor & Suvenir", Icons.Filled.ShoppingBag, Color(0xFFE91E63)),
    GUIDE("Gid & Maxsus Servis", Icons.Filled.SupportAgent, Color(0xFF4CAF50))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TouristExpenseScreen(
    onNavigateBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val isDark = isSystemInDarkTheme()

    // Tourist Profile & Assigned Staff Data
    val userProfile = remember { UserSessionManager.loadProfile(context) }
    val touristName = remember(userProfile) {
        userProfile?.let { "${it.firstName} ${it.lastName}".trim() }.takeIf { !it.isNullOrBlank() } ?: "Hurmatli Sayyoh"
    }
    val touristOrigin = "O'zbekiston Sayyohi"
    val touristBookingCode = remember(userProfile) {
        userProfile?.touristId?.ifEmpty { "UZ-TOUR-1001" } ?: "UZ-TOUR-1001"
    }
    val assignedStaffName = "Shaxsiy VIP Gid"
    val assignedStaffRole = "Tur Gidi & Konsyerj"
    val assignedStaffPhone = "+998 90 712-34-56"
    val assignedStaffId = "GID-UZ-8492"

    // Currency Conversion Rate (1 USD = 12,850 UZS)
    val usdRate = 12850.0

    // Deposit and Financial State
    var totalDepositUzs by remember { mutableDoubleStateOf(0.0) }
    var selectedCategoryFilter by remember { mutableStateOf(ExpenseType.ALL) }
    var searchQuery by remember { mutableStateOf("") }

    // Dialog & Sheet States
    var showStaffAddExpenseSheet by remember { mutableStateOf(false) }
    var showTopUpDepositDialog by remember { mutableStateOf(false) }
    var selectedExpenseForReceipt by remember { mutableStateOf<TouristLiveExpense?>(null) }
    var showStaffContactDialog by remember { mutableStateOf(false) }

    // Interactive Expense List (Starts empty, records added live by user or staff)
    val expenseList = remember {
        mutableStateListOf<TouristLiveExpense>()
    }

    // Calculate Dynamic Financial Totals
    val totalSpentUzs = expenseList.sumOf { it.amountUzs }
    val remainingBalanceUzs = (totalDepositUzs - totalSpentUzs).coerceAtLeast(0.0)
    val totalSpentUsd = totalSpentUzs / usdRate
    val remainingBalanceUsd = remainingBalanceUzs / usdRate
    val totalDepositUsd = totalDepositUzs / usdRate
    val spentPercentage = if (totalDepositUzs > 0) (totalSpentUzs / totalDepositUzs).toFloat().coerceIn(0f, 1f) else 0f

    // Filter list by category and search
    val filteredExpenses = expenseList.filter { item ->
        val matchesCategory = selectedCategoryFilter == ExpenseType.ALL || item.category == selectedCategoryFilter
        val matchesSearch = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.location.contains(searchQuery, ignoreCase = true) ||
                item.receiptNumber.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    val uzsFormatter = remember {
        NumberFormat.getNumberInstance(Locale.forLanguageTag("uz-UZ")).apply {
            maximumFractionDigits = 0
        }
    }
    val usdFormatter = remember {
        NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Registon fon rasmi (Registan Background Image)
        Image(
            painter = painterResource(id = R.drawable.img_registan_1787819168326),
            contentDescription = "Registon Maydoni",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Nafis va yuqori kontrastli gradient qatlami
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isDark) {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xDD040E1E),
                                Color(0xEE00122E),
                                Color(0xF8010611)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xB8010F24),
                                Color(0xD0001A3D),
                                Color(0xE2020B1A)
                            )
                        )
                    }
                )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .testTag("tourist_expense_screen"),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // 1. TOP HEADER & APP BAR
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (onNavigateBack != null) {
                            IconButton(
                                onClick = onNavigateBack,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x330047AB))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Orqaga",
                                    tint = NeonGold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        }

                        UzbekStarEmblem(
                            size = 28.dp,
                            primaryColor = NeonGold,
                            secondaryColor = TurquoiseTile
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "IPAK YO'LI TURIST XARAJATLARI",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    letterSpacing = 1.2.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = NeonGold
                            )
                            Text(
                                text = "Hisob-kitob & Balans",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 22.sp
                                ),
                                color = Color.White
                            )
                        }
                    }

                    // Assigned Staff Profile Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x22F6C845))
                            .border(1.dp, NeonGold.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                            .clickable { showStaffContactDialog = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Badge,
                                contentDescription = "Hodim",
                                tint = NeonGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Biriktirilgan Gid",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = NeonGold
                            )
                        }
                    }
                }
            }

            // 2. TOURIST & LIVE STAFF BANNER
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(22.dp),
                    backgroundColor = if (isDark) Color(0x33002D6B) else Color(0xF2FFFFFF),
                    borderColor = NeonGold.copy(alpha = 0.5f),
                    elevation = 8.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(NeonGold, SilkGold))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = null,
                                        tint = Color(0xFF00122E),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = touristName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        ),
                                        color = if (isDark) Color.White else Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = "$touristOrigin • ID: $touristBookingCode",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 12.sp,
                                            color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B)
                                        )
                                    )
                                }
                            }

                            GlassPillBadge(
                                text = "🟢 Jonli Hisob",
                                accentColor = TurquoiseTile,
                                textColor = TurquoiseTile
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0x22FFFFFF))
                        Spacer(modifier = Modifier.height(10.dp))

                        // Assigned Staff info line
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.VerifiedUser,
                                    contentDescription = null,
                                    tint = TurquoiseTile,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Biriktirilgan hodim: ",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B)
                                    )
                                )
                                Text(
                                    text = assignedStaffName,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = NeonGold
                                    )
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(TurquoiseTile.copy(alpha = 0.15f))
                                    .clickable { showStaffContactDialog = true }
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Phone,
                                    contentDescription = null,
                                    tint = TurquoiseTile,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Aloqa",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TurquoiseTile
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 3. MAIN FINANCIAL BALANCE CARD
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(26.dp),
                    backgroundColor = if (isDark) Color(0xF2071836) else Color.White,
                    borderColor = NeonGold,
                    elevation = 12.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "QOLGAN BALANS / DEPOZIT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    ),
                                    color = NeonGold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${uzsFormatter.format(remainingBalanceUzs)} UZS",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 25.sp,
                                        letterSpacing = (-0.5).sp
                                    ),
                                    color = if (remainingBalanceUzs > 0) Color.White else Color(0xFFFF5252)
                                )
                                Text(
                                    text = "≈ $ ${usdFormatter.format(remainingBalanceUsd)} USD",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    ),
                                    color = TurquoiseTile
                                )
                            }

                            // Top up / Deposit Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Brush.linearGradient(listOf(NeonGold, SilkGold)))
                                    .clickable { showTopUpDepositDialog = true }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "Depozit qo'shish",
                                        tint = Color(0xFF00122E),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "+ Depozit",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp
                                        ),
                                        color = Color(0xFF00122E)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Budget Usage Progress Bar
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Jami sarflangan: ${(spentPercentage * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B)
                                    )
                                )
                                Text(
                                    text = "Depozit: ${uzsFormatter.format(totalDepositUzs)} UZS ($${usdFormatter.format(totalDepositUsd)})",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonGold
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(Color(0x33FFFFFF))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(spentPercentage)
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(TurquoiseTile, NeonGold, Color(0xFFFF9800))
                                            )
                                        )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0x22FFFFFF))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Two Column Summary: Jami Xarajatlar & Operatsiyalar soni
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Jami Xarajatlar:",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B)
                                    )
                                )
                                Text(
                                    text = "-${uzsFormatter.format(totalSpentUzs)} UZS",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                    color = Color(0xFFFF8A80)
                                )
                                Text(
                                    text = "≈ -$${usdFormatter.format(totalSpentUsd)} USD",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp
                                    ),
                                    color = Color(0xFFFF8A80).copy(alpha = 0.8f)
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Jami Cheklar:",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B)
                                    )
                                )
                                Text(
                                    text = "${expenseList.size} ta xarajat",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                    color = TurquoiseTile
                                )
                                Text(
                                    text = "100% cheklar biriktirilgan",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp
                                    ),
                                    color = TurquoiseTile.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            // 4. ACTION BUTTONS: STAFF INPUT CONSOLE & EXPORT STATEMENT
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Staff Add Expense Button ("Hodim yozib boradi uz telfonida")
                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .shadow(8.dp, RoundedCornerShape(18.dp), spotColor = NeonGold.copy(alpha = 0.5f))
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF003882), RegistanBlue, Color(0xFF001A3D))
                                )
                            )
                            .border(1.5.dp, NeonGold, RoundedCornerShape(18.dp))
                            .clickable { showStaffAddExpenseSheet = true }
                            .padding(vertical = 12.dp, horizontal = 10.dp)
                            .testTag("btn_staff_add_expense"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(NeonGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = null,
                                    tint = Color(0xFF00122E),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "+ Xarajat Yozish",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp
                                    ),
                                    color = NeonGold
                                )
                                Text(
                                    text = "Hodim Kabineti",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    // Share / Export Full Ledger Report
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0x330047AB))
                            .border(1.dp, TurquoiseTile.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                            .clickable {
                                val reportText = buildString {
                                    appendLine("📋 UZ TOURIST VIP • SHAFFOF HISOBOT")
                                    appendLine("👤 Turist: $touristName ($touristBookingCode)")
                                    appendLine("👨‍💼 Gid/Hodim: $assignedStaffName (ID: $assignedStaffId)")
                                    appendLine("💰 Jami Depozit: ${uzsFormatter.format(totalDepositUzs)} UZS ($${usdFormatter.format(totalDepositUsd)})")
                                    appendLine("📉 Jami Xarajat: ${uzsFormatter.format(totalSpentUzs)} UZS ($${usdFormatter.format(totalSpentUsd)})")
                                    appendLine("💵 Qolgan Balans: ${uzsFormatter.format(remainingBalanceUzs)} UZS ($${usdFormatter.format(remainingBalanceUsd)})")
                                    appendLine("\n--- XARAJATLAR RO'YXATI ---")
                                    expenseList.forEachIndexed { i, exp ->
                                        appendLine("${i + 1}. ${exp.title}")
                                        appendLine("   Summa: ${uzsFormatter.format(exp.amountUzs)} UZS ($${usdFormatter.format(exp.amountUsd)})")
                                        appendLine("   Vaqt: ${exp.date}, ${exp.time} • Chek: ${exp.receiptNumber}")
                                    }
                                }
                                clipboardManager.setText(AnnotatedString(reportText))
                                Toast.makeText(context, "Hisobot nusxalandi! Turistga yuborishingiz mumkin.", Toast.LENGTH_LONG).show()
                            }
                            .padding(vertical = 12.dp, horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Ulashish",
                                tint = TurquoiseTile,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Hisobotni Olish",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                ),
                                color = TurquoiseTile
                            )
                        }
                    }
                }
            }

            // 5. CATEGORY FILTER CHIPS
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 6.dp)
                ) {
                    Text(
                        text = "KATEGORIYALAR BO'YICHA SARALASH",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = NeonGold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(ExpenseType.values()) { type ->
                            val isSelected = selectedCategoryFilter == type
                            val bgColor by animateColorAsState(
                                targetValue = if (isSelected) NeonGold else if (isDark) Color(0x33002244) else Color(0xFFE2E8F0),
                                label = "FilterBg"
                            )
                            val textColor by animateColorAsState(
                                targetValue = if (isSelected) Color(0xFF00122E) else if (isDark) Color.White else Color(0xFF1E293B),
                                label = "FilterText"
                            )

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(bgColor)
                                    .border(
                                        1.dp,
                                        if (isSelected) NeonGold else Color(0x33FFFFFF),
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable { selectedCategoryFilter = type }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = type.icon,
                                    contentDescription = type.displayName,
                                    tint = if (isSelected) Color(0xFF00122E) else type.color,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = type.displayName,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp
                                    ),
                                    color = textColor
                                )
                            }
                        }
                    }
                }
            }

            // 6. DETAILED EXPENSE LEDGER LIST (Turist ko'rib turadigan har bir xarajat ma'lumoti)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BARCHA XARAJATLAR TAFSILOTI",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = NeonGold
                    )
                    Text(
                        text = "${filteredExpenses.size} ta xarajat",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B)
                        )
                    )
                }
            }

            if (filteredExpenses.isEmpty()) {
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                contentDescription = null,
                                tint = NeonGold.copy(alpha = 0.6f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Ushbu toifada xarajatlar mavjud emas",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color(0xFF0F172A)
                                )
                            )
                            Text(
                                text = "Biriktirilgan hodim xarajat yozganda bu yerda real vaqtda paydo bo'ladi.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B),
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                }
            } else {
                items(filteredExpenses, key = { it.id }) { expense ->
                    TouristExpenseItemCard(
                        expense = expense,
                        uzsFormatter = uzsFormatter,
                        usdFormatter = usdFormatter,
                        isDark = isDark,
                        onViewReceipt = {
                            selectedExpenseForReceipt = expense
                        }
                    )
                }
            }
        }
    }

    // MODAL SHEET 1: STAFF EXPENSE INPUT CONSOLE ("Hodim yozib boradi uz telfonida")
    if (showStaffAddExpenseSheet) {
        StaffAddExpenseBottomSheet(
            touristName = touristName,
            assignedStaffName = assignedStaffName,
            assignedStaffRole = assignedStaffRole,
            assignedStaffId = assignedStaffId,
            usdRate = usdRate,
            onDismiss = { showStaffAddExpenseSheet = false },
            onSaveExpense = { newExpense ->
                expenseList.add(0, newExpense)
                showStaffAddExpenseSheet = false
                Toast.makeText(context, "✅ Xarajat turist hisobiga kiritildi va saqlandi!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // MODAL DIALOG 2: TOP-UP DEPOSIT MODAL
    if (showTopUpDepositDialog) {
        TopUpDepositDialog(
            usdRate = usdRate,
            onDismiss = { showTopUpDepositDialog = false },
            onDepositAdded = { addedAmountUzs ->
                totalDepositUzs += addedAmountUzs
                showTopUpDepositDialog = false
                Toast.makeText(context, "✅ Depozit muvaffaqiyatli to'ldirildi!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // MODAL DIALOG 3: DETAILED RECEIPT / CHEK VIEW
    if (selectedExpenseForReceipt != null) {
        ReceiptDetailsDialog(
            expense = selectedExpenseForReceipt!!,
            uzsFormatter = uzsFormatter,
            usdFormatter = usdFormatter,
            touristName = touristName,
            onDismiss = { selectedExpenseForReceipt = null }
        )
    }

    // MODAL DIALOG 4: ASSIGNED STAFF CONTACT MODAL
    if (showStaffContactDialog) {
        AssignedStaffContactDialog(
            staffName = assignedStaffName,
            staffRole = assignedStaffRole,
            staffPhone = assignedStaffPhone,
            staffId = assignedStaffId,
            onDismiss = { showStaffContactDialog = false }
        )
    }
}

/**
 * Individual Expense Item Card
 */
@Composable
fun TouristExpenseItemCard(
    expense: TouristLiveExpense,
    uzsFormatter: NumberFormat,
    usdFormatter: NumberFormat,
    isDark: Boolean,
    onViewReceipt: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable { onViewReceipt() }
            .testTag("expense_card_${expense.id}"),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = if (isDark) Color(0x33002244) else Color.White,
        borderColor = expense.category.color.copy(alpha = 0.4f),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Category Badge + Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(expense.category.color.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = expense.category.icon,
                            contentDescription = expense.category.displayName,
                            tint = expense.category.color,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = expense.category.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = expense.category.color
                        )
                        Text(
                            text = "${expense.date} • ${expense.time}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B)
                            )
                        )
                    }
                }

                // Amount
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "-${uzsFormatter.format(expense.amountUzs)} UZS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        ),
                        color = Color(0xFFFF6D00)
                    )
                    Text(
                        text = "≈ -$${usdFormatter.format(expense.amountUsd)} USD",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = TurquoiseTile
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & Location
            Text(
                text = expense.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = if (isDark) Color.White else Color(0xFF0F172A)
            )

            if (expense.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = expense.notes,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = if (isDark) Color(0xFFB0C4DE) else Color(0xFF475569)
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0x18FFFFFF))
            Spacer(modifier = Modifier.height(8.dp))

            // Footer: Recorded by Staff + Receipt ID + View Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.VerifiedUser,
                        contentDescription = null,
                        tint = TurquoiseTile,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Gid: ${expense.recordedBy}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B)
                        )
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x22F6C845))
                        .clickable { onViewReceipt() }
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Receipt,
                        contentDescription = null,
                        tint = NeonGold,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Chek #${expense.receiptNumber}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = NeonGold
                        )
                    )
                }
            }
        }
    }
}

/**
 * Bottom Sheet for Assigned Staff / Guide to Add a New Expense from their phone
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffAddExpenseBottomSheet(
    touristName: String,
    assignedStaffName: String,
    assignedStaffRole: String,
    assignedStaffId: String,
    usdRate: Double,
    onDismiss: () -> Unit,
    onSaveExpense: (TouristLiveExpense) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isDark = isSystemInDarkTheme()

    var selectedType by remember { mutableStateOf(ExpenseType.RESTAURANT) }
    var title by remember { mutableStateOf("") }
    var amountUzsText by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Samarqand") }
    var notes by remember { mutableStateOf("") }
    var receiptCode by remember { mutableStateOf("CHK-${(10000..99999).random()}") }

    val amountUzs = amountUzsText.toDoubleOrNull() ?: 0.0
    val amountUsd = amountUzs / usdRate

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (isDark) Color(0xFF071224) else Color.White,
        contentColor = if (isDark) Color.White else Color(0xFF0F172A),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 10.dp)
                .padding(bottom = 30.dp)
        ) {
            // Header
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
                            .background(NeonGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = null,
                            tint = Color(0xFF00122E),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "HODIM / GID KABINETI",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            ),
                            color = NeonGold
                        )
                        Text(
                            text = "Yangi Xarajat Yozish",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        )
                    }
                }

                GlassPillBadge(
                    text = "ID: $assignedStaffId",
                    accentColor = TurquoiseTile,
                    textColor = TurquoiseTile
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tourist Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x220047AB))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = TurquoiseTile,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Turist hisobiga: ",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = Color(0xFFA0B2C6)
                    )
                )
                Text(
                    text = touristName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = NeonGold
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category Selector
            Text(
                text = "Xarajat Turi (Kategoriya):",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF475569)
            )
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(ExpenseType.values().filter { it != ExpenseType.ALL }) { type ->
                    val isSelected = selectedType == type
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) type.color else if (isDark) Color(0x33002244) else Color(0xFFE2E8F0))
                            .clickable { selectedType = type }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = type.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            ),
                            color = if (isSelected) Color.White else if (isDark) Color(0xFFA0B2C6) else Color(0xFF334155)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Muassasa / Xizmat nomi (masalan: Oshxona, VIP Taxi)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonGold,
                    unfocusedBorderColor = Color(0x44FFFFFF)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Amount Input (UZS)
            OutlinedTextField(
                value = amountUzsText,
                onValueChange = { amountUzsText = it.filter { ch -> ch.isDigit() } },
                label = { Text("Summa (So'mda / UZS)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = {
                    if (amountUzs > 0) {
                        Text(
                            text = "≈ $${String.format(Locale.US, "%.2f", amountUsd)}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TurquoiseTile
                            ),
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonGold,
                    unfocusedBorderColor = Color(0x44FFFFFF)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Location & Receipt
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Joylashuv / Shahar") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGold,
                        unfocusedBorderColor = Color(0x44FFFFFF)
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = receiptCode,
                    onValueChange = { receiptCode = it },
                    label = { Text("Chek #") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGold,
                        unfocusedBorderColor = Color(0x44FFFFFF)
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Notes Input
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Izoh / Xarajat tafsilotlari") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonGold,
                    unfocusedBorderColor = Color(0x44FFFFFF)
                ),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Save Button
            GoldGradientButton(
                text = "Xarajatni Turist Balansiga Qo'shish",
                onClick = {
                    if (title.isBlank() || amountUzs <= 0) {
                        return@GoldGradientButton
                    }
                    val newExp = TouristLiveExpense(
                        id = "exp_${System.currentTimeMillis()}",
                        title = title.trim(),
                        category = selectedType,
                        amountUzs = amountUzs,
                        amountUsd = amountUsd,
                        date = "Bugun, 30-Avgust",
                        time = "14:10",
                        location = location.trim(),
                        recordedBy = assignedStaffName,
                        staffRole = assignedStaffRole,
                        staffId = assignedStaffId,
                        receiptNumber = receiptCode.trim(),
                        notes = notes.trim(),
                        isVerified = true
                    )
                    onSaveExpense(newExp)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Top Up Deposit Dialog
 */
@Composable
fun TopUpDepositDialog(
    usdRate: Double,
    onDismiss: () -> Unit,
    onDepositAdded: (Double) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    val amount = amountText.toDoubleOrNull() ?: 0.0
    val isDark = isSystemInDarkTheme()

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            backgroundColor = if (isDark) Color(0xFF0A1832) else Color.White,
            borderColor = NeonGold,
            elevation = 16.dp
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(NeonGold, SilkGold))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountBalanceWallet,
                        contentDescription = null,
                        tint = Color(0xFF00122E),
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Turist Depozitini To'ldirish",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    ),
                    color = if (isDark) Color.White else Color(0xFF0F172A)
                )

                Text(
                    text = "Turist tomonidan taqdim etilgan yangi summani kiriting",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Summa (UZS)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Preset Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(1000000.0 to "+1 mln", 3000000.0 to "+3 mln", 5000000.0 to "+5 mln").forEach { (preset, label) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x330047AB))
                                .clickable { amountText = preset.toLong().toString() }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = NeonGold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Bekor qilish", color = Color(0xFFA0B2C6))
                    }

                    GoldGradientButton(
                        text = "Tasdiqlash",
                        onClick = {
                            if (amount > 0) {
                                onDepositAdded(amount)
                            }
                        },
                        modifier = Modifier.weight(1.2f)
                    )
                }
            }
        }
    }
}

/**
 * Receipt Details Dialog (Chek / Kvitansiya oynasi)
 */
@Composable
fun ReceiptDetailsDialog(
    expense: TouristLiveExpense,
    uzsFormatter: NumberFormat,
    usdFormatter: NumberFormat,
    touristName: String,
    onDismiss: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            backgroundColor = if (isDark) Color(0xFF08152B) else Color.White,
            borderColor = NeonGold,
            elevation = 20.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UzbekStarEmblem(size = 20.dp, primaryColor = NeonGold, secondaryColor = TurquoiseTile)
                    Text(
                        text = "RASMIY CHEK KVITANSIYASI",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            fontSize = 10.sp
                        ),
                        color = NeonGold
                    )
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Tasdiqlangan",
                        tint = TurquoiseTile,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Amount Headline
                Text(
                    text = "${uzsFormatter.format(expense.amountUzs)} UZS",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp
                    ),
                    color = NeonGold
                )
                Text(
                    text = "≈ $${usdFormatter.format(expense.amountUsd)} USD",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TurquoiseTile
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0x22FFFFFF))
                Spacer(modifier = Modifier.height(12.dp))

                // Detail Rows
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReceiptRow(label = "Xizmat / Nomi:", value = expense.title, isDark = isDark)
                    ReceiptRow(label = "Kategoriya:", value = expense.category.displayName, isDark = isDark)
                    ReceiptRow(label = "Sana & Vaqt:", value = "${expense.date}, ${expense.time}", isDark = isDark)
                    ReceiptRow(label = "Joylashuv:", value = expense.location, isDark = isDark)
                    ReceiptRow(label = "Chek Raqami:", value = "#${expense.receiptNumber}", isDark = isDark, isHighlight = true)
                    ReceiptRow(label = "Turist:", value = touristName, isDark = isDark)
                    ReceiptRow(label = "Kiritgan Hodim:", value = "${expense.recordedBy} (${expense.staffId})", isDark = isDark)
                    if (expense.notes.isNotBlank()) {
                        ReceiptRow(label = "Izoh:", value = expense.notes, isDark = isDark)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = {
                            val copyText = "🧾 Chek #${expense.receiptNumber}: ${expense.title} - ${uzsFormatter.format(expense.amountUzs)} UZS ($${usdFormatter.format(expense.amountUsd)}). Gid: ${expense.recordedBy}"
                            clipboardManager.setText(AnnotatedString(copyText))
                            Toast.makeText(context, "Chek ma'lumotlari nusxalandi!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Filled.Share, contentDescription = null, tint = TurquoiseTile, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Nusxalash", color = TurquoiseTile)
                    }

                    GoldGradientButton(
                        text = "Yopish",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun ReceiptRow(
    label: String,
    value: String,
    isDark: Boolean,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B)
            ),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isHighlight) FontWeight.Black else FontWeight.SemiBold,
                fontSize = 12.sp,
                color = if (isHighlight) NeonGold else if (isDark) Color.White else Color(0xFF0F172A)
            ),
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.5f)
        )
    }
}

/**
 * Assigned Staff Contact Dialog
 */
@Composable
fun AssignedStaffContactDialog(
    staffName: String,
    staffRole: String,
    staffPhone: String,
    staffId: String,
    onDismiss: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            backgroundColor = if (isDark) Color(0xFF0A1934) else Color.White,
            borderColor = TurquoiseTile,
            elevation = 16.dp
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(TurquoiseTile, RegistanBlue))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.SupportAgent,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = staffName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    ),
                    color = if (isDark) Color.White else Color(0xFF0F172A)
                )

                Text(
                    text = "$staffRole • ID: $staffId",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = NeonGold
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Turistning barcha safar xarajatlarini shaffof tarzda o'z telefonida yozib boruvchi biriktirilgan mas'ul xodim.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Phone Display & Action
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x330047AB))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.Phone, contentDescription = null, tint = TurquoiseTile, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = staffPhone,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(TurquoiseTile)
                            .clickable {
                                Toast.makeText(context, "Qo'ng'iroq qilinmoqda: $staffPhone", Toast.LENGTH_SHORT).show()
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Qo'ng'iroq",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = Color(0xFF00122E)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                GoldGradientButton(
                    text = "Tushunarli",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
