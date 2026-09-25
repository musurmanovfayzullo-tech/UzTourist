package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.SilkGold
import com.example.ui.theme.TurquoiseTile
import com.example.util.TouristWallet
import com.example.util.TouristWalletManager
import com.example.util.UserSessionManager
import com.example.util.WalletTransaction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TouristWalletSheet(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val profile = UserSessionManager.currentProfileState ?: UserSessionManager.loadProfile(context)
    val userId = UserSessionManager.getUserId(context)
    val touristName = profile?.let {
        "${it.firstName} ${it.lastName}".trim().ifEmpty { "VIP Sayyoh" }
    } ?: "VIP Sayyoh"

    val wallet = remember(TouristWalletManager.walletUpdateTrigger) {
        TouristWalletManager.getOrCreateWallet(
            context = context,
            userId = userId,
            touristName = touristName,
            email = profile?.email ?: "",
            phone = profile?.phoneNumber ?: ""
        )
    }

    val transactions = remember(TouristWalletManager.transactionsUpdateTrigger) {
        TouristWalletManager.getTransactions(context, userId)
    }

    var isBalanceHidden by remember { mutableStateOf(false) }
    var showTopUpDialog by remember { mutableStateOf(false) }
    var showQuickPayDialog by remember { mutableStateOf(false) }
    var showTipGuideDialog by remember { mutableStateOf(false) }
    var showExchangeDialog by remember { mutableStateOf(false) }
    var showQrCodeDialog by remember { mutableStateOf(false) }
    var selectedReceiptTxn by remember { mutableStateOf<WalletTransaction?>(null) }
    var selectedCategoryFilter by remember { mutableStateOf("Barchasi") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (isDark) Color(0xFF070E1A) else Color(0xFFF6F8FC),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("tourist_wallet_sheet"),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Header: Title & Close Button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        UzbekStarEmblem(
                            size = 28.dp,
                            primaryColor = NeonGold,
                            secondaryColor = TurquoiseTile
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "SILK ROAD PAY",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                ),
                                color = NeonGold
                            )
                            Text(
                                text = "Sayyoh Elektron Hamyoni • ${wallet.walletId}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0x33FFFFFF) else Color(0x15000000))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Yopish",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            // VIRTUAL TOURIST CARD (High-End Silk Road Gold & Cyan Glass Pattern)
            item {
                SilkRoadVirtualCard(
                    wallet = wallet,
                    isBalanceHidden = isBalanceHidden,
                    onToggleBalanceVisibility = { isBalanceHidden = !isBalanceHidden },
                    onCopyCardNumber = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Silk Road Card", wallet.cardNumber))
                        Toast.makeText(context, "Karta raqami nusxalandi: ${wallet.cardNumber}", Toast.LENGTH_SHORT).show()
                    },
                    onOpenQrCode = { showQrCodeDialog = true }
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // QUICK ACTIONS (To'ldirish, To'lov, Gidga Choychaqa, Valyuta Ayirboshlash)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WalletActionButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.Add,
                        label = "To'ldirish",
                        sublabel = "Visa/Uzcard",
                        containerColor = Color(0xFF10B981),
                        onClick = { showTopUpDialog = true }
                    )
                    WalletActionButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.Receipt,
                        label = "To'lov",
                        sublabel = "Chipta & Taom",
                        containerColor = TurquoiseTile,
                        onClick = { showQuickPayDialog = true }
                    )
                    WalletActionButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.Redeem,
                        label = "Gidga To'lash",
                        sublabel = "Choychaqa",
                        containerColor = NeonGold,
                        contentColor = Color.Black,
                        onClick = { showTipGuideDialog = true }
                    )
                    WalletActionButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.CurrencyExchange,
                        label = "Valyuta",
                        sublabel = "USD ➔ UZS",
                        containerColor = Color(0xFF6366F1),
                        onClick = { showExchangeDialog = true }
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // DAILY BUDGET & SECURITY STATUS (NFC + CARD FREEZE)
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    backgroundColor = if (isDark) Color(0x33003366) else Color(0xFFEEF4FF),
                    borderColor = NeonGold.copy(alpha = 0.35f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📊 KUNLIK XARAJAT SARFI",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                ),
                                color = NeonGold
                            )
                            val percent = ((wallet.todaySpentUzs / wallet.dailyLimitUzs) * 100).toInt().coerceIn(0, 100)
                            Text(
                                text = "$percent%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = if (percent > 80) Color(0xFFFF4757) else Color(0xFF10B981)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        val progress = (wallet.todaySpentUzs / wallet.dailyLimitUzs).toFloat().coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (progress > 0.8f) Color(0xFFFF4757) else TurquoiseTile,
                            trackColor = Color.Black.copy(alpha = 0.15f)
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Sarflangan: ${TouristWalletManager.formatUzs(wallet.todaySpentUzs)}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Limit: ${TouristWalletManager.formatUzs(wallet.dailyLimitUzs)}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color.White.copy(alpha = 0.1f))

                        // Security Toggles
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // NFC Toggle
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Nfc,
                                    contentDescription = null,
                                    tint = if (wallet.isNfcActive) Color(0xFF10B981) else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "NFC Kontaktsiz To'lov",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = if (wallet.isNfcActive) "Faol (Terminallarda ishlaydi)" else "O'chirilgan",
                                        fontSize = 9.sp,
                                        color = if (wallet.isNfcActive) Color(0xFF10B981) else Color.Gray
                                    )
                                }
                            }

                            Switch(
                                checked = wallet.isNfcActive,
                                onCheckedChange = {
                                    val active = TouristWalletManager.toggleNfc(context, userId)
                                    Toast.makeText(context, if (active) "NFC to'lov faollashtirildi" else "NFC to'lov o'chirildi", Toast.LENGTH_SHORT).show()
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF10B981)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Freeze Card Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (wallet.isCardFrozen) Icons.Filled.Lock else Icons.Filled.LockOpen,
                                    contentDescription = null,
                                    tint = if (wallet.isCardFrozen) Color(0xFFFF4757) else NeonGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "Kartani Muzlatish (Freeze)",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = if (wallet.isCardFrozen) "Muzlatilgan (To'lovlar to'xtatilgan)" else "Himoyalangan & Faol",
                                        fontSize = 9.sp,
                                        color = if (wallet.isCardFrozen) Color(0xFFFF4757) else NeonGold
                                    )
                                }
                            }

                            Switch(
                                checked = wallet.isCardFrozen,
                                onCheckedChange = {
                                    val frozen = TouristWalletManager.toggleCardFreeze(context, userId)
                                    Toast.makeText(context, if (frozen) "Karta muzlatildi ❄️" else "Karta ochildi ✓", Toast.LENGTH_SHORT).show()
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFFFF4757)
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // TRANSACTIONS HISTORY HEADER & CATEGORY FILTERS
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TRANZAKSIYALAR TARIXI (${transactions.size})",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Real vaqt • Chek bilan",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = TurquoiseTile
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Filter Pills
                val filterList = listOf("Barchasi", "Chipta", "Ovqatlanish", "Gid", "Kirim", "Xarid")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    items(filterList) { cat ->
                        val isSelected = selectedCategoryFilter == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) NeonGold else if (isDark) Color(0x33FFFFFF) else Color(0xFFE2E8F0)
                                )
                                .clickable { selectedCategoryFilter = cat }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // TRANSACTIONS LIST
            val filteredTxns = transactions.filter {
                selectedCategoryFilter == "Barchasi" || it.category.equals(selectedCategoryFilter, ignoreCase = true)
            }

            if (filteredTxns.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Bu toifada tranzaksiyalar yo'q",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                items(filteredTxns) { txn ->
                    TransactionItemRow(
                        txn = txn,
                        isDark = isDark,
                        onClick = { selectedReceiptTxn = txn }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }

    // ==========================================
    // MODALS & DIALOGS FOR WALLET ACTIONS
    // ==========================================

    // 1. TOP-UP DIALOG (Hisob to'ldirish)
    if (showTopUpDialog) {
        TopUpWalletDialog(
            onDismiss = { showTopUpDialog = false },
            onConfirm = { amountUzs, method ->
                TouristWalletManager.topUpWallet(context, userId, amountUzs, method) { success, msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    if (success) showTopUpDialog = false
                }
            }
        )
    }

    // 2. QUICK PAY DIALOG (Obidalar va xizmatlarga to'lov)
    if (showQuickPayDialog) {
        QuickPayDialog(
            wallet = wallet,
            onDismiss = { showQuickPayDialog = false },
            onConfirm = { amountUzs, title, category, merchant, note ->
                TouristWalletManager.makePayment(context, userId, amountUzs, title, category, merchant, note) { success, msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    if (success) showQuickPayDialog = false
                }
            }
        )
    }

    // 3. TIP GUIDE DIALOG (Gidga minnatdorchilik to'lovi)
    if (showTipGuideDialog) {
        TipGuideDialog(
            wallet = wallet,
            onDismiss = { showTipGuideDialog = false },
            onConfirm = { guideNumber, guideName, amountUzs, note ->
                TouristWalletManager.payGuideTip(context, userId, guideNumber, guideName, amountUzs, note) { success, msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    if (success) showTipGuideDialog = false
                }
            }
        )
    }

    // 4. INSTANT CURRENCY EXCHANGE DIALOG
    if (showExchangeDialog) {
        CurrencyExchangeDialog(
            wallet = wallet,
            onDismiss = { showExchangeDialog = false },
            onConfirm = { fromCurr, toCurr, amountFrom ->
                TouristWalletManager.exchangeCurrency(context, userId, fromCurr, toCurr, amountFrom) { success, msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    if (success) showExchangeDialog = false
                }
            }
        )
    }

    // 5. TOURIST PERSONAL QR CODE DIALOG
    if (showQrCodeDialog) {
        TouristQrCodeDialog(
            wallet = wallet,
            onDismiss = { showQrCodeDialog = false }
        )
    }

    // 6. DIGITAL RECEIPT MODAL
    selectedReceiptTxn?.let { txn ->
        DigitalReceiptDialog(
            txn = txn,
            wallet = wallet,
            onDismiss = { selectedReceiptTxn = null }
        )
    }
}

/**
 * High-End Silk Road VIP Virtual Card Composable
 */
@Composable
fun SilkRoadVirtualCard(
    wallet: TouristWallet,
    isBalanceHidden: Boolean,
    onToggleBalanceVisibility: () -> Unit,
    onCopyCardNumber: () -> Unit,
    onOpenQrCode: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    colors = if (wallet.isCardFrozen) {
                        listOf(Color(0xFF374151), Color(0xFF1F2937), Color(0xFF111827))
                    } else {
                        listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
                    }
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.horizontalGradient(
                    listOf(NeonGold, TurquoiseTile, SilkGold)
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(18.dp)
    ) {
        Column {
            // Card Top Row: Brand & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    UzbekStarEmblem(
                        size = 22.dp,
                        primaryColor = NeonGold,
                        secondaryColor = TurquoiseTile
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SILK ROAD PAY",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 1.2.sp,
                        color = NeonGold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x3300E5FF))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "VIP TOURIST",
                            fontWeight = FontWeight.Black,
                            fontSize = 8.sp,
                            color = Color(0xFF00E5FF)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenQrCode,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.QrCode,
                            contentDescription = "QR Kod",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Filled.Nfc,
                        contentDescription = "NFC",
                        tint = if (wallet.isNfcActive) Color(0xFF10B981) else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Chip Visual & Currency Balance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gold EMV Chip visual
                Box(
                    modifier = Modifier
                        .size(width = 36.dp, height = 26.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFFFD700), Color(0xFFDAA520), Color(0xFFB8860B))
                            )
                        )
                        .border(0.5.dp, Color(0xFF8B6508), RoundedCornerShape(5.dp))
                )

                // Balance with eye toggle
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isBalanceHidden) "•••••••• so'm" else TouristWalletManager.formatUzs(wallet.balanceUzs),
                        fontWeight = FontWeight.Black,
                        fontSize = 19.sp,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    IconButton(
                        onClick = onToggleBalanceVisibility,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isBalanceHidden) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = "Balansni yashirish",
                            tint = Color.White.copy(alpha = 0.75f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Sub-balance in USD and EUR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = if (isBalanceHidden) "~$••• USD • €••• EUR" else "~${TouristWalletManager.formatUsd(wallet.balanceUsd)} USD • ${TouristWalletManager.formatEur(wallet.balanceEur)} EUR",
                    fontSize = 11.sp,
                    color = Color(0xFF64FFDA),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Card Number Row with Copy
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCopyCardNumber() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = wallet.cardNumber,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.5.sp,
                    letterSpacing = 2.sp,
                    color = Color.White
                )
                Icon(
                    imageVector = Icons.Filled.ContentCopy,
                    contentDescription = "Nusxalash",
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Card Bottom: Cardholder, Expiry, CVV & Brand
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "KARTA EGASI",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = wallet.touristName.uppercase(),
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "MUDDATI",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = wallet.cardExpiry,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "UZCARD • HUMO",
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        letterSpacing = 0.8.sp,
                        color = NeonGold
                    )
                    Text(
                        text = "SILK ROAD PAY",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 9.sp,
                        color = TurquoiseTile
                    )
                }
            }

            if (wallet.isCardFrozen) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x55FF1744))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "❄️ KARTA VAQTINCHA MUZLATILGAN (TO'LOVLAR BLOKLANDI)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun WalletActionButton(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    sublabel: String,
    containerColor: Color,
    contentColor: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = contentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontWeight = FontWeight.Black,
                fontSize = 10.5.sp,
                color = contentColor,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = sublabel,
                fontWeight = FontWeight.Normal,
                fontSize = 8.5.sp,
                color = contentColor.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
fun TransactionItemRow(
    txn: WalletTransaction,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val isIncome = txn.amountUzs > 0

    val icon = when (txn.category) {
        "Chipta" -> Icons.Filled.Receipt
        "Ovqatlanish" -> Icons.Filled.Fastfood
        "Gid" -> Icons.Filled.Redeem
        "Transport" -> Icons.Filled.DirectionsCar
        "Konvertatsiya" -> Icons.Filled.CurrencyExchange
        else -> Icons.Filled.Add
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isDark) Color(0x33101E33) else Color.White)
            .border(
                width = 0.8.dp,
                color = if (isIncome) Color(0xFF10B981).copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.08f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            if (isIncome) Color(0x2210B981)
                            else if (txn.category == "Gid") Color(0x22FFD700)
                            else Color(0x153B82F6)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isIncome) Color(0xFF10B981) else if (txn.category == "Gid") NeonGold else TurquoiseTile,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = txn.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${txn.merchant} • ${txn.dateFormatted}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (isIncome) "+${TouristWalletManager.formatUzs(txn.amountUzs)}" else TouristWalletManager.formatUzs(txn.amountUzs),
                    fontWeight = FontWeight.Black,
                    fontSize = 12.5.sp,
                    color = if (isIncome) Color(0xFF10B981) else Color(0xFFFF4757)
                )
                Text(
                    text = if (isIncome) "+${TouristWalletManager.formatUsd(txn.amountUsd)}" else TouristWalletManager.formatUsd(txn.amountUsd),
                    fontSize = 9.5.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        }
    }
}

/**
 * 1. TOP-UP DIALOG
 */
@Composable
fun TopUpWalletDialog(
    onDismiss: () -> Unit,
    onConfirm: (amountUzs: Double, method: String) -> Unit
) {
    var selectedAmount by remember { mutableDoubleStateOf(500_000.0) }
    var selectedMethod by remember { mutableStateOf("Visa / Mastercard") }
    val isDark = isSystemInDarkTheme()

    val presets = listOf(
        200_000.0 to "~$15.50",
        500_000.0 to "~$38.90",
        1_000_000.0 to "~$77.80",
        2_000_000.0 to "~$155.60"
    )

    val methods = listOf("Visa / Mastercard", "UzCard / Humo", "Google Pay", "Bank Terminali")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF0C192C) else Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "💳 HAMYONNI TO'LDIRISH",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = Color(0xFF10B981)
                )
                Text(
                    text = "Tezkor va 0% komissiya bilan hisobni to'ldiring",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Summani tanlang:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    presets.forEach { (amt, usd) ->
                        val isSel = selectedAmount == amt
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) Color(0x2210B981) else if (isDark) Color(0x15FFFFFF) else Color(0xFFF1F5F9))
                                .border(
                                    width = if (isSel) 1.5.dp else 0.5.dp,
                                    color = if (isSel) Color(0xFF10B981) else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedAmount = amt }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = TouristWalletManager.formatUzs(amt),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = if (isSel) Color(0xFF10B981) else MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = usd,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "To'lov usuli:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(methods) { meth ->
                        val isSel = selectedMethod == meth
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) TurquoiseTile else if (isDark) Color(0x22FFFFFF) else Color(0xFFE2E8F0))
                                .clickable { selectedMethod = meth }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = meth,
                                fontSize = 10.5.sp,
                                fontWeight = if (isSel) FontWeight.Black else FontWeight.Normal,
                                color = if (isSel) Color.Black else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Bekor qilish")
                    }

                    Button(
                        onClick = { onConfirm(selectedAmount, selectedMethod) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Text("To'ldirish ✓", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * 2. QUICK PAY DIALOG
 */
@Composable
fun QuickPayDialog(
    wallet: TouristWallet,
    onDismiss: () -> Unit,
    onConfirm: (amountUzs: Double, title: String, category: String, merchant: String, note: String) -> Unit
) {
    val isDark = isSystemInDarkTheme()

    val quickItems = listOf(
        Triple("🏛️ Registon Maydoni Kirish Chiptasi", 65_000.0, "Chipta" to "Registon Davlat Muzeyi"),
        Triple("🕌 Gur-Amir Maqbarasi Kirish", 40_000.0, "Chipta" to "Gur-Amir Muzeyi"),
        Triple("🍲 Samarqand Milliy Oshi & Choyxona", 85_000.0, "Ovqatlanish" to "Osh Markazi"),
        Triple("🚕 VIP Transfer / Shahar Taxi", 45_000.0, "Transport" to "Samarqand City Taxi"),
        Triple("🏺 Rishton Kulolchilik Esdalik", 150_000.0, "Xarid" to "Hunarmandlar Rastasi")
    )

    var selectedIndex by remember { mutableStateOf(0) }
    var customNote by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF0C192C) else Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "📲 TEZKOR SAYYOHLIK TO'LOVI",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = TurquoiseTile
                )
                Text(
                    text = "Hamyon balansi: ${TouristWalletManager.formatUzs(wallet.balanceUzs)}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    quickItems.forEachIndexed { index, item ->
                        val isSel = selectedIndex == index
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) Color(0x2200E5FF) else if (isDark) Color(0x15FFFFFF) else Color(0xFFF1F5F9))
                                .border(
                                    width = if (isSel) 1.5.dp else 0.5.dp,
                                    color = if (isSel) TurquoiseTile else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedIndex = index }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.first,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = item.third.second,
                                        fontSize = 9.5.sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                    )
                                }
                                Text(
                                    text = TouristWalletManager.formatUzs(item.second),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = if (isSel) TurquoiseTile else MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = customNote,
                    onValueChange = { customNote = it },
                    label = { Text("Qo'shimcha izoh (ixtiyoriy)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Bekor")
                    }

                    Button(
                        onClick = {
                            val chosen = quickItems[selectedIndex]
                            onConfirm(chosen.second, chosen.first, chosen.third.first, chosen.third.second, customNote)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TurquoiseTile, contentColor = Color.Black)
                    ) {
                        Text("To'lash ✓", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

/**
 * 3. TIP GUIDE DIALOG
 */
@Composable
fun TipGuideDialog(
    wallet: TouristWallet,
    onDismiss: () -> Unit,
    onConfirm: (guideNumber: String, guideName: String, amountUzs: Double, note: String) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    var guideNumber by remember { mutableStateOf("G-8821") }
    var guideName by remember { mutableStateOf("Alisher Rustamov") }
    var selectedAmount by remember { mutableDoubleStateOf(50_000.0) }
    var thankNote by remember { mutableStateOf("Katta rahmat! Qiziqarli va samimiy ekskursiya bo'ldi.") }

    val tipPresets = listOf(30_000.0, 50_000.0, 100_000.0, 200_000.0)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF0C192C) else Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Redeem, contentDescription = null, tint = NeonGold, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GIDGA CHOYCHAQA VA MUKOFOT",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.5.sp,
                        color = NeonGold
                    )
                }
                Text(
                    text = "Gidning mehnatini qadrlab, to'g'ridan-to'g'ri uning hisobiga o'tkazing",
                    fontSize = 10.5.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = guideNumber,
                    onValueChange = { guideNumber = it },
                    label = { Text("Gid Maxsus Raqami / Kodi") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Choychaqa miqdori:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    tipPresets.forEach { amt ->
                        val isSel = selectedAmount == amt
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) NeonGold else if (isDark) Color(0x22FFFFFF) else Color(0xFFF1F5F9))
                                .clickable { selectedAmount = amt }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${(amt / 1000).toInt()}k",
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = if (isSel) Color.Black else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = thankNote,
                    onValueChange = { thankNote = it },
                    label = { Text("Minnatdorchilik so'zlari") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Bekor")
                    }

                    Button(
                        onClick = { onConfirm(guideNumber, guideName, selectedAmount, thankNote) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGold, contentColor = Color.Black)
                    ) {
                        Text("O'tkazish ✓", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

/**
 * 4. CURRENCY EXCHANGE DIALOG
 */
@Composable
fun CurrencyExchangeDialog(
    wallet: TouristWallet,
    onDismiss: () -> Unit,
    onConfirm: (fromCurr: String, toCurr: String, amountFrom: Double) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    var selectedFromCurr by remember { mutableStateOf("USD") }
    var amountInput by remember { mutableStateOf("50") }

    val rate = if (selectedFromCurr == "USD") TouristWalletManager.RATE_USD_TO_UZS else TouristWalletManager.RATE_EUR_TO_UZS
    val numAmount = amountInput.toDoubleOrNull() ?: 0.0
    val calculatedUzs = numAmount * rate

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF0C192C) else Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "🔄 VALYUTA AYIRBOSHLASH (0% KOMISSIYA)",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.5.sp,
                    color = Color(0xFF6366F1)
                )
                Text(
                    text = "Markaziy Bank rasmiy kursi: 1 $selectedFromCurr = ${rate.toLong()} UZS",
                    fontSize = 10.5.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("USD", "EUR").forEach { curr ->
                        val isSel = selectedFromCurr == curr
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) Color(0xFF6366F1) else if (isDark) Color(0x22FFFFFF) else Color(0xFFE2E8F0))
                                .clickable { selectedFromCurr = curr }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = curr,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.5.sp,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("$selectedFromCurr miqdori") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x2210B981))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "Hamyoningizga tushadigan so'm:",
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                        Text(
                            text = TouristWalletManager.formatUzs(calculatedUzs),
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = Color(0xFF10B981)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Bekor")
                    }

                    Button(
                        onClick = {
                            if (numAmount > 0) onConfirm(selectedFromCurr, "UZS", numAmount)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                    ) {
                        Text("Konvertatsiya ✓", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * 5. TOURIST PERSONAL QR CODE DIALOG
 */
@Composable
fun TouristQrCodeDialog(
    wallet: TouristWallet,
    onDismiss: () -> Unit
) {
    val isDark = isSystemInDarkTheme()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF0C192C) else Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UzbekStarEmblem(
                    size = 32.dp,
                    primaryColor = NeonGold,
                    secondaryColor = TurquoiseTile
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "SHAXSIY SAYYOHLIK QR KODI",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.5.sp,
                    color = NeonGold
                )
                Text(
                    text = "Muzeylar, Gidlar va Bozorlarda kontaktsiz to'lov uchun ko'rsating",
                    fontSize = 10.5.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // High-Contrast QR Code Container
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(2.dp, NeonGold, RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.QrCode,
                            contentDescription = "QR",
                            tint = Color.Black,
                            modifier = Modifier.size(130.dp)
                        )
                        Text(
                            text = wallet.walletId,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Sayyoh: ${wallet.touristName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "ID: ${wallet.touristId} • ${wallet.cardNumber}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TurquoiseTile, contentColor = Color.Black)
                ) {
                    Text("Tushunarli (Yopish)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * 6. DIGITAL RECEIPT DIALOG
 */
@Composable
fun DigitalReceiptDialog(
    txn: WalletTransaction,
    wallet: TouristWallet,
    onDismiss: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val isIncome = txn.amountUzs > 0

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF0C192C) else Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        UzbekStarEmblem(size = 20.dp, primaryColor = NeonGold, secondaryColor = TurquoiseTile)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ELEKTRON CHEK",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = NeonGold
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Yopish")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isIncome) Color(0x1810B981) else Color(0x18FF4757))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isIncome) "+${TouristWalletManager.formatUzs(txn.amountUzs)}" else TouristWalletManager.formatUzs(txn.amountUzs),
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = if (isIncome) Color(0xFF10B981) else Color(0xFFFF4757)
                        )
                        Text(
                            text = if (isIncome) "+${TouristWalletManager.formatUsd(txn.amountUsd)}" else TouristWalletManager.formatUsd(txn.amountUsd),
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Status: ${txn.status} ✓",
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp,
                            color = Color(0xFF10B981)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                ReceiptDetailRow("Xizmat / Mahsulot:", txn.title)
                ReceiptDetailRow("To'lov qabul qiluvchi:", txn.merchant)
                ReceiptDetailRow("Chek / RRN raqami:", txn.referenceId)
                ReceiptDetailRow("Sana va vaqt:", txn.dateFormatted)
                ReceiptDetailRow("Karta raqami:", wallet.cardNumber)
                if (txn.note.isNotBlank()) {
                    ReceiptDetailRow("Izoh:", txn.note)
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TurquoiseTile, contentColor = Color.Black)
                ) {
                    Text("Chekni Yopish", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ReceiptDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 10.5.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
