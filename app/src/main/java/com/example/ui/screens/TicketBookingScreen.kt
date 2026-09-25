package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AirplaneTicket
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.compose.ui.graphics.vector.ImageVector
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
import androidx.compose.ui.window.DialogProperties
import com.example.model.SampleTicketOffers
import com.example.model.TicketBookingOrder
import com.example.model.TicketOffer
import com.example.model.TransportType
import com.example.ui.components.GlassCard
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.RegistanBlueDark
import com.example.ui.theme.SilkGold
import com.example.ui.theme.SilkGoldDark
import com.example.ui.theme.SilkGoldLight
import com.example.ui.theme.TurquoiseTile
import com.example.util.UserSessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketBookingScreen(
    initialType: TransportType = TransportType.FLIGHT,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val scope = rememberCoroutineScope()

    var selectedTransportType by remember { mutableStateOf(initialType) }
    var selectedOrigin by remember { mutableStateOf("Toshkent") }
    var selectedDestination by remember { mutableStateOf("Samarqand") }
    var selectedDateText by remember { mutableStateOf("Ertaga (08 Sentabr)") }
    var passengerCount by remember { mutableIntStateOf(1) }
    var filterFastestOnly by remember { mutableStateOf(false) }

    // Active booking bottomsheet state
    var selectedOfferForBooking by remember { mutableStateOf<TicketOffer?>(null) }
    var completedOrderTicket by remember { mutableStateOf<TicketBookingOrder?>(null) }

    // Load available tickets based on filter
    val allOffers = remember(selectedTransportType) {
        if (selectedTransportType == TransportType.FLIGHT) {
            SampleTicketOffers.flightOffers
        } else {
            SampleTicketOffers.trainOffers
        }
    }

    val filteredOffers = remember(allOffers, selectedOrigin, selectedDestination, filterFastestOnly) {
        allOffers.filter { offer ->
            val matchOrigin = selectedOrigin == "Barchasi" || offer.originCity.contains(selectedOrigin, ignoreCase = true)
            val matchDest = selectedDestination == "Barchasi" || offer.destinationCity.contains(selectedDestination, ignoreCase = true)
            val matchFastest = !filterFastestOnly || offer.isFastest
            (matchOrigin && matchDest && matchFastest) || (selectedOrigin == "Barchasi" && selectedDestination == "Barchasi")
        }.ifEmpty {
            // If strict filter is empty, fallback to same transport type offers
            allOffers
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                if (isDark) {
                    Brush.verticalGradient(listOf(Color(0xFF070F1E), Color(0xFF030710)))
                } else {
                    Brush.verticalGradient(listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0)))
                }
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Header with Back and Title
            item {
                TicketBookingHeader(
                    isDark = isDark,
                    onNavigateBack = onNavigateBack
                )
            }

            // Transport Switcher Tab (✈️ Avia vs 🚆 Poyezd)
            item {
                TransportTypeSegmentedBar(
                    selectedType = selectedTransportType,
                    onTypeSelected = { selectedTransportType = it },
                    isDark = isDark
                )
            }

            // Interactive Route Search Card
            item {
                TicketRouteSearchCard(
                    origin = selectedOrigin,
                    destination = selectedDestination,
                    dateText = selectedDateText,
                    passengerCount = passengerCount,
                    isDark = isDark,
                    onOriginChange = { selectedOrigin = it },
                    onDestinationChange = { selectedDestination = it },
                    onDateChange = { selectedDateText = it },
                    onPassengerCountChange = { passengerCount = it },
                    onSwap = {
                        val temp = selectedOrigin
                        selectedOrigin = selectedDestination
                        selectedDestination = temp
                    }
                )
            }

            // Quick Filter & Count Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedTransportType == TransportType.FLIGHT) {
                            "✈️ Topilgan reyslar (${filteredOffers.size})"
                        } else {
                            "🚆 Afrosiyob & Poyezdlar (${filteredOffers.size})"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) SilkGoldLight else RegistanBlue
                        )
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (filterFastestOnly) {
                                    if (isDark) NeonGold.copy(alpha = 0.25f) else RegistanBlue.copy(alpha = 0.15f)
                                } else {
                                    Color.Transparent
                                }
                            )
                            .clickable { filterFastestOnly = !filterFastestOnly }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DirectionsTransit,
                            contentDescription = null,
                            tint = if (isDark) NeonGold else RegistanBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Tezyurar",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (filterFastestOnly) FontWeight.Bold else FontWeight.Medium,
                                color = if (isDark) NeonGold else RegistanBlue
                            )
                        )
                    }
                }
            }

            // Tickets List
            items(filteredOffers, key = { it.id }) { offer ->
                TicketOfferCard(
                    offer = offer,
                    isDark = isDark,
                    onBookClick = { selectedOfferForBooking = offer }
                )
            }
        }

        // ====================================================
        // BOOKING MODAL BOTTOM SHEET
        // ====================================================
        selectedOfferForBooking?.let { offer ->
            TicketBookingModalBottomSheet(
                offer = offer,
                passengerCount = passengerCount,
                travelDate = selectedDateText,
                isDark = isDark,
                onDismiss = { selectedOfferForBooking = null },
                onBookingComplete = { completedOrder ->
                    selectedOfferForBooking = null
                    completedOrderTicket = completedOrder

                    // Sync booking to Supabase `bookings` table for Admin & Staff app
                    kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                        val emoji = if (completedOrder.ticketOffer.transportType == TransportType.FLIGHT) "✈️ Avia" else "🚆 Poyezd"
                        val remoteBooking = com.example.network.SupabaseClient.RemoteBooking(
                            id = "",
                            bookingCode = completedOrder.orderId,
                            tourId = "0",
                            tourTitle = "$emoji: ${completedOrder.ticketOffer.carrierName} (${completedOrder.ticketOffer.originCity} → ${completedOrder.ticketOffer.destinationCity})",
                            touristName = "${completedOrder.passengerFirstName} ${completedOrder.passengerLastName}",
                            touristPhone = completedOrder.passengerPhone,
                            touristEmail = completedOrder.passengerEmail.ifBlank { "passenger@silkroad.uz" },
                            touristCountry = "O'zbekiston",
                            startDate = completedOrder.travelDate,
                            peopleCount = completedOrder.passengerCount,
                            totalPrice = completedOrder.totalPriceUzs,
                            bookingStatus = "Tasdiqlandi",
                            paymentStatus = completedOrder.paymentMethod,
                            notes = "Chipta PNR: ${completedOrder.pnrCode} | Joy: ${completedOrder.seatNumber} | Reys: ${completedOrder.ticketOffer.serviceCode}"
                        )
                        val res = com.example.network.SupabaseClient.createBooking(context, remoteBooking)
                        android.util.Log.d("TicketBookingScreen", "Ticket booking created in Supabase: $res")
                        // Instant real-time transmission via WebSockets to Admin
                        com.example.network.SupabaseRealtimeManager.broadcastBooking(context, remoteBooking)
                    }
                }
            )
        }

        // ====================================================
        // DIGITAL E-TICKET SUCCESS DIALOG WITH QR CODE
        // ====================================================
        completedOrderTicket?.let { order ->
            DigitalTicketSuccessDialog(
                order = order,
                isDark = isDark,
                onDismiss = { completedOrderTicket = null }
            )
        }
    }
}

@Composable
private fun TicketBookingHeader(
    isDark: Boolean,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0x33FFFFFF) else Color(0x1A0B2240))
                    .testTag("ticket_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Orqaga",
                    tint = if (isDark) Color.White else RegistanBlueDark
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Chiptalarga Onlayn Zakaz",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = if (isDark) Color.White else RegistanBlueDark
                    )
                )
                Text(
                    text = "Samolyot & Afrosiyob tezyurar poyezdlari",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun TransportTypeSegmentedBar(
    selectedType: TransportType,
    onTypeSelected: (TransportType) -> Unit,
    isDark: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(if (isDark) Color(0x401E293B) else Color(0xFFE2E8F0))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        TransportType.values().forEach { type ->
            val isSelected = selectedType == type
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) {
                    if (isDark) NeonGold else RegistanBlue
                } else {
                    Color.Transparent
                },
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "typeBgColor"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) {
                    if (isDark) Color.Black else Color.White
                } else {
                    if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                },
                label = "typeTextColor"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(bgColor)
                    .clickable { onTypeSelected(type) }
                    .padding(vertical = 12.dp)
                    .testTag("tab_${type.name.lowercase()}"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = type.iconEmoji,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = type.titleUz,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = textColor,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun TicketRouteSearchCard(
    origin: String,
    destination: String,
    dateText: String,
    passengerCount: Int,
    isDark: Boolean,
    onOriginChange: (String) -> Unit,
    onDestinationChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onPassengerCountChange: (Int) -> Unit,
    onSwap: () -> Unit
) {
    var showCityPickerForOrigin by remember { mutableStateOf(false) }
    var showCityPickerForDest by remember { mutableStateOf(false) }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = 8.dp,
        backgroundColor = if (isDark) Color(0xCC0F1C31) else Color(0xF2FFFFFF),
        borderColor = if (isDark) NeonGold.copy(alpha = 0.35f) else SilkGold.copy(alpha = 0.35f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Origin and Destination Row with Swap Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Origin
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color(0x33334155) else Color(0xFFF1F5F9))
                        .clickable { showCityPickerForOrigin = true }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Qayerdan",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = origin,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else RegistanBlueDark,
                            fontSize = 15.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Swap Icon
                IconButton(
                    onClick = onSwap,
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isDark) NeonGold.copy(alpha = 0.2f) else RegistanBlue.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Filled.SwapHoriz,
                        contentDescription = "Almashtirish",
                        tint = if (isDark) NeonGold else RegistanBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Destination
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color(0x33334155) else Color(0xFFF1F5F9))
                        .clickable { showCityPickerForDest = true }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Qayerga",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = destination,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else RegistanBlueDark,
                            fontSize = 15.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick City Chips Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SampleTicketOffers.cities) { city ->
                    val isSelected = city == destination
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) {
                                    if (isDark) NeonGold.copy(alpha = 0.25f) else RegistanBlue.copy(alpha = 0.15f)
                                } else {
                                    if (isDark) Color(0x22FFFFFF) else Color(0x0F000000)
                                }
                            )
                            .clickable { onDestinationChange(city) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = city,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) {
                                    if (isDark) NeonGold else RegistanBlue
                                } else {
                                    if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                                },
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Date & Passengers Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Date picker chip
                Column(
                    modifier = Modifier
                        .weight(1.3f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color(0x33334155) else Color(0xFFF1F5F9))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Sayohat sanasi",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Color.White else RegistanBlueDark,
                            fontSize = 13.sp
                        )
                    )
                }

                // Passenger count stepper
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color(0x33334155) else Color(0xFFF1F5F9))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { if (passengerCount > 1) onPassengerCountChange(passengerCount - 1) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Text(
                            text = "−",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else RegistanBlueDark
                            )
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$passengerCount kishi",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else RegistanBlueDark,
                                fontSize = 12.5.sp
                            )
                        )
                    }

                    IconButton(
                        onClick = { if (passengerCount < 9) onPassengerCountChange(passengerCount + 1) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Text(
                            text = "+",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else RegistanBlueDark
                            )
                        )
                    }
                }
            }
        }
    }

    // City Selection Dialogs
    if (showCityPickerForOrigin) {
        CitySelectionDialog(
            title = "Jo'nash shahrini tanlang",
            cities = SampleTicketOffers.cities,
            currentCity = origin,
            isDark = isDark,
            onSelectCity = {
                onOriginChange(it)
                showCityPickerForOrigin = false
            },
            onDismiss = { showCityPickerForOrigin = false }
        )
    }

    if (showCityPickerForDest) {
        CitySelectionDialog(
            title = "Borish shahrini tanlang",
            cities = SampleTicketOffers.cities,
            currentCity = destination,
            isDark = isDark,
            onSelectCity = {
                onDestinationChange(it)
                showCityPickerForDest = false
            },
            onDismiss = { showCityPickerForDest = false }
        )
    }
}

@Composable
private fun CitySelectionDialog(
    title: String,
    cities: List<String>,
    currentCity: String,
    isDark: Boolean,
    onSelectCity: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) Color(0xFF0F172A) else Color.White
            )
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
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else RegistanBlueDark
                        )
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Yopish",
                            tint = if (isDark) Color.White else Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                cities.forEach { city ->
                    val isSelected = city == currentCity
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) {
                                    if (isDark) NeonGold.copy(alpha = 0.2f) else RegistanBlue.copy(alpha = 0.1f)
                                } else {
                                    Color.Transparent
                                }
                            )
                            .clickable { onSelectCity(city) }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = city,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) {
                                    if (isDark) NeonGold else RegistanBlue
                                } else {
                                    if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B)
                                }
                            )
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = if (isDark) NeonGold else RegistanBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TicketOfferCard(
    offer: TicketOffer,
    isDark: Boolean,
    onBookClick: () -> Unit
) {
    val formattedPrice = remember(offer.priceUzs) {
        NumberFormat.getNumberInstance(Locale.US).format(offer.priceUzs).replace(",", " ")
    }
    val priceUsd = remember(offer.priceUzs) {
        "≈ $" + (offer.priceUzs / 12800)
    }

    val transportColor = if (offer.transportType == TransportType.FLIGHT) {
        if (isDark) Color(0xFF38BDF8) else RegistanBlue
    } else {
        if (isDark) TurquoiseTile else Color(0xFF0D9488)
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 7.dp),
        shape = RoundedCornerShape(22.dp),
        elevation = 6.dp,
        backgroundColor = if (isDark) Color(0xCC0D1829) else Color(0xF5FFFFFF),
        borderColor = if (offer.isFastest) {
            NeonGold.copy(alpha = 0.5f)
        } else {
            if (isDark) Color(0x22FFFFFF) else Color(0x1A000000)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Carrier, Code & Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(transportColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (offer.transportType == TransportType.FLIGHT) {
                                Icons.Filled.Flight
                            } else {
                                Icons.Filled.Train
                            },
                            contentDescription = null,
                            tint = transportColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = offer.carrierName,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else RegistanBlueDark,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = "${offer.serviceCode} • ${offer.vehicleModel}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                if (offer.isFastest) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(NeonGold.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "⚡ Eng tezyurar",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) NeonGold else Color(0xFFB45309),
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Flight/Train Route & Timetable Visual Layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Departure
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = offer.departureTime,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                            color = if (isDark) Color.White else RegistanBlueDark
                        )
                    )
                    Text(
                        text = offer.originCity,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155),
                            fontSize = 13.sp
                        )
                    )
                }

                // Flight path / train line
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = offer.duration,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(transportColor)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            thickness = 1.5.dp,
                            color = transportColor.copy(alpha = 0.5f)
                        )
                        Icon(
                            imageVector = if (offer.transportType == TransportType.FLIGHT) {
                                Icons.Filled.Flight
                            } else {
                                Icons.Filled.DirectionsTransit
                            },
                            contentDescription = null,
                            tint = transportColor,
                            modifier = Modifier.size(16.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            thickness = 1.5.dp,
                            color = transportColor.copy(alpha = 0.5f)
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(transportColor)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = offer.travelClass,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = transportColor,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.5.sp
                        )
                    )
                }

                // Arrival
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = offer.arrivalTime,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                            color = if (isDark) Color.White else RegistanBlueDark
                        )
                    )
                    Text(
                        text = offer.destinationCity,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155),
                            fontSize = 13.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Amenities row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                offer.amenities.take(3).forEach { amenity ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDark) Color(0x22334155) else Color(0xFFF1F5F9))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = amenity,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(
                color = if (isDark) Color(0x1AFFFFFF) else Color(0x0F000000)
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Price and Booking Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$formattedPrice so'm",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isDark) NeonGold else RegistanBlueDark,
                                fontSize = 18.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = priceUsd,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                fontSize = 11.5.sp
                            )
                        )
                    }
                    Text(
                        text = "${offer.availableSeats} ta joy qoldi",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (offer.availableSeats < 10) Color(0xFFEF4444) else Color(0xFF10B981),
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    )
                }

                Button(
                    onClick = onBookClick,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) NeonGold else RegistanBlue
                    ),
                    modifier = Modifier.testTag("book_ticket_${offer.id}")
                ) {
                    Text(
                        text = "Zakaz berish",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.Black else Color.White,
                            fontSize = 13.sp
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TicketBookingModalBottomSheet(
    offer: TicketOffer,
    passengerCount: Int,
    travelDate: String,
    isDark: Boolean,
    onDismiss: () -> Unit,
    onBookingComplete: (TicketBookingOrder) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    // Pre-fill user data if registered
    val userProfile = remember { UserSessionManager.loadProfile(context) }
    var firstName by remember { mutableStateOf(userProfile?.firstName ?: "") }
    var lastName by remember { mutableStateOf(userProfile?.lastName ?: "") }
    var phone by remember { mutableStateOf(userProfile?.phoneNumber ?: "+998 9") }
    var email by remember { mutableStateOf(userProfile?.email ?: "sayyoh@silkroad.uz") }
    var docNumber by remember { mutableStateOf("AA 1234567") }
    var selectedSeatType by remember {
        mutableStateOf(
            if (offer.transportType == TransportType.FLIGHT) "Deraza yonidagi (Window A/F)" else "Deraza yonidagi joy"
        )
    }
    var selectedPaymentMethod by remember { mutableStateOf("Click") }
    var promoCode by remember { mutableStateOf("") }
    var promoApplied by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    val basePrice = offer.priceUzs * passengerCount
    val discount = if (promoApplied) (basePrice * 0.1).toLong() else 0L
    val finalPrice = (basePrice - discount).toDouble()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (isDark) Color(0xFF0B1424) else Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (offer.transportType == TransportType.FLIGHT) {
                            "✈️ Aviabiletni Rasmiylashtirish"
                        } else {
                            "🚆 Poyezd Biletini Rasmiylashtirish"
                        },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDark) Color.White else RegistanBlueDark,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = "${offer.carrierName} • ${offer.originCity} → ${offer.destinationCity}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isDark) SilkGoldLight else RegistanBlue,
                            fontSize = 12.sp
                        )
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Yopish",
                        tint = if (isDark) Color.White else Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Name Inputs
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            label = { Text("Ismingiz") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_ticket_first_name"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isDark) NeonGold else RegistanBlue,
                                unfocusedBorderColor = if (isDark) Color(0x44FFFFFF) else Color(0x33000000)
                            )
                        )

                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            label = { Text("Familiyangiz") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_ticket_last_name"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isDark) NeonGold else RegistanBlue,
                                unfocusedBorderColor = if (isDark) Color(0x44FFFFFF) else Color(0x33000000)
                            )
                        )
                    }
                }

                // Phone & Document Number
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Telefon raqam") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1.1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isDark) NeonGold else RegistanBlue,
                                unfocusedBorderColor = if (isDark) Color(0x44FFFFFF) else Color(0x33000000)
                            )
                        )

                        OutlinedTextField(
                            value = docNumber,
                            onValueChange = { docNumber = it },
                            label = { Text("Pasport / ID") },
                            modifier = Modifier.weight(0.9f),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isDark) NeonGold else RegistanBlue,
                                unfocusedBorderColor = if (isDark) Color(0x44FFFFFF) else Color(0x33000000)
                            )
                        )
                    }
                }

                // Seat Type Selection
                item {
                    Text(
                        text = "O'rindiq joylashuvini tanlang:",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155)
                        )
                    )

                    val seatOptions = if (offer.transportType == TransportType.FLIGHT) {
                        listOf("Deraza yonidagi (Window A/F)", "Yo'lak yonidagi (Aisle C/D)", "Keng oraliq (Extra Legroom)")
                    } else {
                        listOf("Deraza yonidagi joy", "Yo'lak yonidagi joy", "Stol yonidagi guruh joyi")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        seatOptions.forEach { opt ->
                            val isSelected = selectedSeatType == opt
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) {
                                            if (isDark) NeonGold.copy(alpha = 0.25f) else RegistanBlue.copy(alpha = 0.12f)
                                        } else {
                                            if (isDark) Color(0x22334155) else Color(0xFFF1F5F9)
                                        }
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) {
                                            if (isDark) NeonGold else RegistanBlue
                                        } else {
                                            Color.Transparent
                                        },
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedSeatType = opt }
                                    .padding(vertical = 10.dp, horizontal = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = opt,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) {
                                            if (isDark) NeonGold else RegistanBlue
                                        } else {
                                            if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                        },
                                        fontSize = 10.5.sp,
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }
                        }
                    }
                }

                // Payment Method
                item {
                    Text(
                        text = "To'lov tizimi:",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155)
                        )
                    )

                    val paymentMethods = listOf("Click", "Payme", "Uzum Bank", "Visa / Master", "Naqd")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(paymentMethods) { method ->
                            val isSelected = selectedPaymentMethod == method
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) {
                                            if (isDark) NeonGold else RegistanBlue
                                        } else {
                                            if (isDark) Color(0x22334155) else Color(0xFFF1F5F9)
                                        }
                                    )
                                    .clickable { selectedPaymentMethod = method }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = method,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) {
                                            if (isDark) Color.Black else Color.White
                                        } else {
                                            if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                                        },
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Promo code input
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = promoCode,
                            onValueChange = { promoCode = it },
                            placeholder = { Text("Promokod (masalan: SILKROAD)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isDark) NeonGold else RegistanBlue
                            )
                        )

                        Button(
                            onClick = {
                                if (promoCode.trim().uppercase() in listOf("SILKROAD", "UZB2026", "TRAVEL")) {
                                    promoApplied = true
                                    Toast.makeText(context, "10% chegirma faollashtirildi!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Promokod topilmadi", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDark) Color(0x44334155) else Color(0xFFE2E8F0)
                            )
                        ) {
                            Text(
                                text = if (promoApplied) "✓ Berildi" else "Qo'llash",
                                color = if (promoApplied) Color(0xFF10B981) else if (isDark) Color.White else Color.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = if (isDark) Color(0x22FFFFFF) else Color(0x14000000))
            Spacer(modifier = Modifier.height(14.dp))

            // Total and Checkout Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Jami to'lov:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    )
                    Text(
                        text = "${NumberFormat.getNumberInstance(Locale.US).format(finalPrice.toLong()).replace(",", " ")} so'm",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDark) NeonGold else RegistanBlueDark,
                            fontSize = 20.sp
                        )
                    )
                }

                Button(
                    onClick = {
                        val validFirst = firstName.ifBlank { "Sayyoh" }
                        val validLast = lastName.ifBlank { "Saidov" }
                        val randomPnr = "UZB-" + (100000..999999).random()
                        val randomSeat = if (offer.transportType == TransportType.FLIGHT) {
                            "${(10..28).random()}${listOf("A", "B", "C", "D", "E", "F").random()}"
                        } else {
                            "Vagon ${(1..7).random()}, O'rindiq ${(1..36).random()}"
                        }

                        val order = TicketBookingOrder(
                            orderId = randomPnr,
                            ticketOffer = offer,
                            passengerFirstName = validFirst,
                            passengerLastName = validLast,
                            docType = "Biometrik Pasport",
                            docNumber = docNumber.ifBlank { "AB 9876543" },
                            passengerPhone = phone.ifBlank { "+998 90 123 45 67" },
                            passengerEmail = email.ifBlank { "tourist@silkroad.uz" },
                            passengerCount = passengerCount,
                            seatNumber = randomSeat,
                            travelDate = travelDate,
                            totalPriceUzs = finalPrice,
                            paymentMethod = selectedPaymentMethod,
                            pnrCode = randomPnr,
                            qrCodePayload = "UZ-TKT-$randomPnr-${offer.serviceCode}-$validFirst"
                        )

                        onBookingComplete(order)
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) NeonGold else RegistanBlue
                    ),
                    modifier = Modifier.testTag("submit_ticket_booking")
                ) {
                    Text(
                        text = "To'lash va Chiptani olish",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.Black else Color.White,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun DigitalTicketSuccessDialog(
    order: TicketBookingOrder,
    isDark: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val offer = order.ticketOffer
    val formattedPrice = remember(order.totalPriceUzs) {
        NumberFormat.getNumberInstance(Locale.US).format(order.totalPriceUzs.toLong()).replace(",", " ")
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xCC000000))
                .clickable { onDismiss() }
                .padding(horizontal = 20.dp, vertical = 30.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {},
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF0F1A2D) else Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    // Top Success Banner
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
                                    .background(Color(0x2210B981)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = "Chipta Muvaffaqiyatli Olinadi!",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color.White else RegistanBlueDark,
                                        fontSize = 15.sp
                                    )
                                )
                                Text(
                                    text = "PNR Kod: ${order.pnrCode}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isDark) NeonGold else RegistanBlue,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Yopish",
                                tint = if (isDark) Color.White else Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Boarding Pass Ticket Body (Styled Card)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                if (isDark) Color(0x33334155) else Color(0xFFF1F5F9)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isDark) NeonGold.copy(alpha = 0.3f) else SilkGold.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Carrier & Vehicle Code
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${offer.transportType.iconEmoji} ${offer.carrierName}",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color.White else RegistanBlueDark
                                    )
                                )
                                Text(
                                    text = offer.serviceCode,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isDark) NeonGold else RegistanBlue
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Route
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = offer.originCity,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isDark) Color.White else RegistanBlueDark,
                                            fontSize = 16.sp
                                        )
                                    )
                                    Text(
                                        text = offer.departureTime,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                        )
                                    )
                                }

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = if (isDark) NeonGold else RegistanBlue,
                                    modifier = Modifier.size(20.dp)
                                )

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = offer.destinationCity,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isDark) Color.White else RegistanBlueDark,
                                            fontSize = 16.sp
                                        )
                                    )
                                    Text(
                                        text = offer.arrivalTime,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(
                                color = if (isDark) Color(0x22FFFFFF) else Color(0x14000000)
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Passenger, Seat & Date info
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "YO'LOVCHI",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                            fontSize = 10.sp
                                        )
                                    )
                                    Text(
                                        text = "${order.passengerFirstName} ${order.passengerLastName}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color.White else RegistanBlueDark
                                        )
                                    )
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "O'RINDIY / VAGON",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                            fontSize = 10.sp
                                        )
                                    )
                                    Text(
                                        text = order.seatNumber,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) NeonGold else RegistanBlue
                                        )
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "SANA",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                            fontSize = 10.sp
                                        )
                                    )
                                    Text(
                                        text = order.travelDate,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color.White else RegistanBlueDark
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // QR Code Graphic & Verification
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.QrCode,
                                contentDescription = "QR Chipta",
                                tint = Color.Black,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Elektron nazorat uchun QR-kod",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                fontSize = 11.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Action buttons: Share / Save & Done
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val shareText = """
                                    🎫 UZ TOURIST ELEKTRON CHIPTA
                                    ━━━━━━━━━━━━━━━━━━━━━━
                                    Reys: ${offer.serviceCode} (${offer.carrierName})
                                    Yo'nalish: ${offer.originCity} ➔ ${offer.destinationCity}
                                    Jo'nash vaqti: ${offer.departureTime}
                                    Sana: ${order.travelDate}
                                    Yo'lovchi: ${order.passengerFirstName} ${order.passengerLastName}
                                    O'rindiq: ${order.seatNumber}
                                    PNR KODI: ${order.pnrCode}
                                    Holati: Tasdiqlandi (To'langan)
                                    ━━━━━━━━━━━━━━━━━━━━━━
                                    Oq yo'l tilaymiz!
                                """.trimIndent()

                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Chiptani ulashish"))
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDark) Color(0x33FFFFFF) else Color(0xFFE2E8F0)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = null,
                                tint = if (isDark) Color.White else RegistanBlueDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Ulashish",
                                color = if (isDark) Color.White else RegistanBlueDark
                            )
                        }

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1.2f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDark) NeonGold else RegistanBlue
                            )
                        ) {
                            Text(
                                text = "Tushunarli",
                                color = if (isDark) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
