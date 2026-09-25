package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.CurrencyBudgetRepository
import com.example.model.CurrencyRate
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.SilkGold
import com.example.ui.theme.TurquoiseTile

@Composable
fun CurrencyBudgetSheet(
    onDismiss: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Konvertor, 1: Byudjet & Xarajatlar, 2: Choychaqa (Tips)

    var inputAmountStr by remember { mutableStateOf("100") }
    var selectedCurrency by remember { mutableStateOf(CurrencyBudgetRepository.supportedCurrencies.first()) }
    var isReverseConvert by remember { mutableStateOf(false) } // False: Foreign -> UZS, True: UZS -> Foreign

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF09162D) else Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
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
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Brush.horizontalGradient(listOf(NeonGold, SilkGold))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CurrencyExchange,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "VALYUTA & SAYOHAT BYUDJETI",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    ),
                                    color = if (isDark) NeonGold else SilkGold
                                )
                                Text(
                                    text = "Markaziy Bank kursi & Oflayn kalkulyator",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = TurquoiseTile
                                )
                            }
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Filled.Close, contentDescription = "Yopish")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tab Row
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = if (isDark) Color(0x330047AB) else Color(0xFFF0F4FC),
                        contentColor = NeonGold
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Konvertor", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Byudjet & Xarajat", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("Choychaqa (Tips)", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    when (selectedTab) {
                        0 -> {
                            // TAB 0: CURRENCY CONVERTER
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Currency Selector Chips
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(CurrencyBudgetRepository.supportedCurrencies.filter { it.code != "UZS" }) { cur ->
                                        val isSel = cur.code == selectedCurrency.code
                                        val chipBgModifier = if (isSel) {
                                            Modifier.background(Brush.horizontalGradient(listOf(NeonGold, SilkGold)))
                                        } else {
                                            Modifier.background(if (isDark) Color(0x330047AB) else Color(0xFFF0F4FC))
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .then(chipBgModifier)
                                                .clickable { selectedCurrency = cur }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = cur.flag, fontSize = 13.sp)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = cur.code,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isSel) Color.Black else MaterialTheme.colorScheme.onBackground
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Converter Box
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(
                                            if (isDark) Color(0x550047AB) else Color(0xFFF3F7FD)
                                        )
                                        .border(1.dp, TurquoiseTile.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                                        .padding(14.dp)
                                ) {
                                    Column {
                                        val inputVal = inputAmountStr.toDoubleOrNull() ?: 0.0
                                        val convertedResult = if (!isReverseConvert) {
                                            inputVal * selectedCurrency.rateToUzs
                                        } else {
                                            if (selectedCurrency.rateToUzs > 0) inputVal / selectedCurrency.rateToUzs else 0.0
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (!isReverseConvert) "${selectedCurrency.flag} ${selectedCurrency.code}" else "🇺🇿 UZS",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black)
                                            )
                                            IconButton(onClick = { isReverseConvert = !isReverseConvert }) {
                                                Icon(Icons.Filled.SwapHoriz, contentDescription = "Swap", tint = NeonGold)
                                            }
                                            Text(
                                                text = if (!isReverseConvert) "🇺🇿 UZS (So'm)" else "${selectedCurrency.flag} ${selectedCurrency.code}",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black, color = TurquoiseTile)
                                            )
                                        }

                                        OutlinedTextField(
                                            value = inputAmountStr,
                                            onValueChange = { inputAmountStr = it },
                                            label = { Text(if (!isReverseConvert) "Miqdorni kiriting (${selectedCurrency.code})" else "Miqdorni kiriting (UZS)") },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp),
                                            singleLine = true
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Result Display
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (isDark) Color(0xFF0D234C) else Color.White)
                                                .padding(12.dp)
                                        ) {
                                            Column {
                                                Text(
                                                    text = "Natija:",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                                )
                                                Text(
                                                    text = if (!isReverseConvert) {
                                                        "${String.format("%,.0f", convertedResult)} UZS"
                                                    } else {
                                                        "${String.format("%,.2f", convertedResult)} ${selectedCurrency.code}"
                                                    },
                                                    style = MaterialTheme.typography.titleLarge.copy(
                                                        fontWeight = FontWeight.Black,
                                                        color = if (isDark) NeonGold else SilkGold
                                                    )
                                                )
                                                Text(
                                                    text = "1 ${selectedCurrency.code} = ${String.format("%,.0f", selectedCurrency.rateToUzs)} UZS",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                                    color = TurquoiseTile
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        1 -> {
                            // TAB 1: BUDGET & EXPENSES
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(290.dp)
                            ) {
                                // Total Budget Summary
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Brush.horizontalGradient(listOf(RegistanBlue, Color(0xFF003280))))
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = "Umumiy Sayohat Byudjeti", style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.8f)))
                                            Text(text = "Qoldiq: 5,280,000 UZS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = NeonGold))
                                        }
                                        Text(text = "10,000,000 UZS (~$778)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = Color.White))
                                        Spacer(modifier = Modifier.height(6.dp))
                                        LinearProgressIndicator(
                                            progress = { 0.472f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = NeonGold,
                                            trackColor = Color.White.copy(alpha = 0.3f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "So'nggi xarajatlar:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    items(CurrencyBudgetRepository.sampleRecentExpenses) { exp ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isDark) Color(0x330047AB) else Color(0xFFF7FAFD))
                                                .padding(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(text = exp.title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), maxLines = 1)
                                                    Text(text = "${exp.timeAgo} • ${exp.city}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = TurquoiseTile)
                                                }
                                                Text(
                                                    text = "-${String.format("%,.0f", exp.amountUzs)} UZS",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, color = Color(0xFFFF5252))
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        2 -> {
                            // TAB 2: TIPPING GUIDES (CHOYCHAQA)
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(290.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(CurrencyBudgetRepository.tippingGuides) { (title, desc) ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isDark) Color(0x330047AB) else Color(0xFFF3F7FD))
                                            .border(1.dp, Color(0x33D4AF37), RoundedCornerShape(12.dp))
                                            .padding(10.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = title,
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                                                color = if (isDark) NeonGold else SilkGold
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = desc,
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
