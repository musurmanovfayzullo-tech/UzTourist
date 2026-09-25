package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GuideChatMessage
import com.example.model.TourCheckpointItem
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.RegistanBlueDark
import com.example.ui.theme.SilkGold
import com.example.ui.theme.TurquoiseTile
import com.example.util.GpsLocationManager
import com.example.util.GuideInteractionManager
import com.example.util.UserSessionManager
import kotlinx.coroutines.launch

/**
 * Unified Modern Hub Sheet for Guide Interactions:
 * Tab 0: 📍 Live Guide Tracking & Meeting Point
 * Tab 1: 💬 Direct Quick Chat & Alerts to Guide/Admin/Telegram
 * Tab 2: 🗺️ Tour Itinerary Checkpoints & Progress
 * Tab 3: ⭐ Guide Rating & Review Submission
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideInteractionHubSheet(
    initialTab: Int = 0,
    onDismiss: () -> Unit,
    onOpenMapScreen: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(initialTab) }

    val guideRecord = remember { UserSessionManager.getGuideVerification(context) }
    val assignedNumber = guideRecord?.guideNumber ?: "7788"
    val touristProfile = remember { UserSessionManager.loadProfile(context) }
    val touristName = if (touristProfile != null && touristProfile.firstName.isNotBlank()) {
        "${touristProfile.firstName} ${touristProfile.lastName}".trim()
    } else {
        "VIP Sayyoh"
    }
    val touristId = remember { UserSessionManager.getUserId(context) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF030D1E),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(top = 16.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(NeonGold, TurquoiseTile))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👑", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "SHAXSIY GID MARKAZI",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = NeonGold
                            )
                        )
                        Text(
                            text = "Gid kodi: $assignedNumber • $touristName",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0x22FFFFFF))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Yopish",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 5 Modern Luxury Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF06152F),
                contentColor = NeonGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = NeonGold,
                        height = 3.dp
                    )
                }
            ) {
                val tabs = listOf(
                    "📍 Joylashuv",
                    "🎙️ Efir",
                    "💬 Aloqa",
                    "🗺️ Marshrut",
                    "⭐ Baho"
                )
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Black else FontWeight.Normal,
                                color = if (selectedTab == index) NeonGold else Color(0xFF94A3B8)
                            )
                        }
                    )
                }
            }

            // Tab Content Panes
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                when (selectedTab) {
                    0 -> GuideLiveLocationTab(
                        assignedNumber = assignedNumber,
                        onOpenMapScreen = onOpenMapScreen
                    )
                    1 -> TourGuideWhisperView(
                        touristName = touristName,
                        assignedNumber = assignedNumber
                    )
                    2 -> GuideDirectChatTab(
                        touristName = touristName,
                        touristId = touristId,
                        assignedNumber = assignedNumber
                    )
                    3 -> TourItineraryTab(
                        touristName = touristName
                    )
                    4 -> GuideReviewTab(
                        touristId = touristId,
                        touristName = touristName,
                        assignedNumber = assignedNumber
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 0: LIVE GUIDE LOCATION & MEETING POINT
// -------------------------------------------------------------------------------------------------
@Composable
private fun GuideLiveLocationTab(
    assignedNumber: String,
    onOpenMapScreen: () -> Unit
) {
    val context = LocalContext.current
    val guideInfo = GuideInteractionManager.currentGuideLocation
    val liveGps = GpsLocationManager.currentLocation.value
    val distance = remember(liveGps) {
        if (liveGps != null) {
            GuideInteractionManager.calculateDistanceMeters(liveGps.latitude, liveGps.longitude)
        } else {
            110
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Guide Identity Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF071E43)),
                border = androidx.compose.foundation.BorderStroke(1.dp, TurquoiseTile.copy(alpha = 0.5f))
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
                            .background(Brush.linearGradient(listOf(Color(0xFF0052D4), Color(0xFF4364F7), Color(0xFF6FB1FC)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = guideInfo.guideName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Verified",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "Biriktirilgan Gid kodi: $assignedNumber",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonGold
                        )
                        Text(
                            text = "🟢 ${guideInfo.status}",
                            fontSize = 11.sp,
                            color = Color(0xFF34D399)
                        )
                    }

                    // Direct Phone Call Button
                    IconButton(
                        onClick = {
                            val callIntent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${guideInfo.guidePhone}")
                            }
                            context.startActivity(callIntent)
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Call,
                            contentDescription = "Qo'ng'iroq",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Radar Distance & Meeting Point Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B2447)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonGold.copy(alpha = 0.6f))
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
                            Icon(
                                imageVector = Icons.Filled.Navigation,
                                contentDescription = null,
                                tint = NeonGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Gidgacha masofa:",
                                fontSize = 12.5.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Text(
                            text = "$distance metr",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonGold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x33000000))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "📍 BELGILANGAN UCHRASHUV NUQTASI:",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TurquoiseTile
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = guideInfo.meetingPointName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "⏰ Rejalashtirilgan vaqt: ${guideInfo.meetingTime}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Open in Map
                        Button(
                            onClick = onOpenMapScreen,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TurquoiseTile)
                        ) {
                            Icon(imageVector = Icons.Filled.Map, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Xaritada Ko'rish", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        // External Google Maps Route
                        Button(
                            onClick = {
                                val gmmIntentUri = Uri.parse("google.navigation:q=${guideInfo.latitude},${guideInfo.longitude}")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                try {
                                    context.startActivity(mapIntent)
                                } catch (e: Exception) {
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${guideInfo.latitude},${guideInfo.longitude}"))
                                    context.startActivity(browserIntent)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGold)
                        ) {
                            Icon(imageVector = Icons.Filled.Directions, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Yo'nalish Olish", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    var showSafeTourRadarSheet by remember { mutableStateOf(false) }

                    // High-Tech SafeTour Radar Button
                    Button(
                        onClick = { showSafeTourRadarSheet = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981),
                            contentColor = Color.Black
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.NearMe,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🦺 SAFETOUR GEORADAR (ADASHMASLIK REJIMI)",
                                fontWeight = FontWeight.Black,
                                fontSize = 11.5.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    if (showSafeTourRadarSheet) {
                        SafeTourRadarSheet(
                            onDismiss = { showSafeTourRadarSheet = false }
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 1: DIRECT QUICK CHAT & ALERTS TO GUIDE & TELEGRAM & ADMIN
// -------------------------------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GuideDirectChatTab(
    touristName: String,
    touristId: String,
    assignedNumber: String
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var messageInput by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }

    val messages = remember(GuideInteractionManager.chatUpdateTrigger) {
        GuideInteractionManager.getChatMessages(context)
    }

    val quickTemplates = listOf(
        "📍 Biz uchrashuv joyidamiz",
        "⏳ 5 daqiqa kuting, kelyapmiz",
        "🙋‍♂️ Registon maydonida kutib turing",
        "❓ Gid, ayni paytda qayerdasiz?",
        "📸 Ajoyib joy, rasmga olyapmiz!",
        "✅ Hammasi a'lo darajada, rahmat!"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Chat Message List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = false,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x2210B981))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚡ Xabarlar bir vaqtning o'zida Gid, Telegram va Admin boshqaruv paneliga yetkaziladi",
                        fontSize = 10.sp,
                        color = Color(0xFF10B981),
                        textAlign = TextAlign.Center
                    )
                }
            }

            items(messages) { msg ->
                ChatMessageBubble(msg)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Quick Message Chips
        Text(
            text = "Tezkor tayyor xabarlar:",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.75f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            quickTemplates.forEach { text ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0C274E))
                        .border(1.dp, TurquoiseTile.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .clickable {
                            messageInput = text
                        }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(text = text, fontSize = 10.5.sp, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageInput,
                onValueChange = { messageInput = it },
                placeholder = { Text("Gidga xabar yozing...", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonGold,
                    unfocusedBorderColor = Color(0x44FFFFFF),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF061730),
                    unfocusedContainerColor = Color(0xFF061730)
                ),
                maxLines = 2
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (messageInput.isNotBlank() && !isSending) {
                        isSending = true
                        val textToSend = messageInput
                        messageInput = ""
                        GuideInteractionManager.sendChatMessage(
                            context = context,
                            messageText = textToSend,
                            touristName = touristName,
                            touristId = touristId,
                            guideNumber = assignedNumber,
                            onDispatched = {
                                isSending = false
                                Toast.makeText(context, "Xabar yetkazildi! ✓", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                },
                enabled = messageInput.isNotBlank() && !isSending,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (messageInput.isNotBlank()) NeonGold else Color(0x33FFFFFF))
            ) {
                if (isSending) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(imageVector = Icons.Filled.Send, contentDescription = "Yuborish", tint = Color.Black, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun ChatMessageBubble(msg: GuideChatMessage) {
    val isTourist = msg.isFromTourist
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isTourist) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isTourist) 16.dp else 2.dp,
                        bottomEnd = if (isTourist) 2.dp else 16.dp
                    )
                )
                .background(
                    if (isTourist) Brush.horizontalGradient(listOf(Color(0xFF0052D4), Color(0xFF4364F7)))
                    else Brush.horizontalGradient(listOf(Color(0xFF1F2937), Color(0xFF374151)))
                )
                .padding(12.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isTourist) "Siz (Sayyoh)" else msg.senderName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (isTourist) NeonGold else TurquoiseTile
                    )
                    Text(
                        text = msg.timestamp,
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = msg.messageText,
                    fontSize = 12.5.sp,
                    color = Color.White
                )
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 2: TOUR ITINERARY CHECKPOINTS
// -------------------------------------------------------------------------------------------------
@Composable
private fun TourItineraryTab(touristName: String) {
    val context = LocalContext.current
    val checkpoints = remember(GuideInteractionManager.checkpointsUpdateTrigger) {
        GuideInteractionManager.getTourCheckpoints(context)
    }
    val completedCount = checkpoints.count { it.isCompleted }
    val progressPercent = if (checkpoints.isNotEmpty()) (completedCount * 100) / checkpoints.size else 0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Progress Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B2447)),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonGold.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "KUNLIK MARSHRUT TARAQQIYOTI:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = NeonGold
                        )
                        Text(
                            text = "$completedCount / ${checkpoints.size} ($progressPercent%)",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = Color(0xFF10B981)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0x33FFFFFF))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressPercent / 100f)
                                .fillMaxHeight()
                                .background(Brush.horizontalGradient(listOf(Color(0xFF10B981), NeonGold)))
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Belgilangan har bir manzil avtomatik ravishda Gid va Admin nazoratiga yetkaziladi.",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.65f)
                    )
                }
            }
        }

        items(checkpoints) { cp ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        GuideInteractionManager.toggleCheckpoint(context, cp.id, touristName)
                    },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (cp.isCompleted) Color(0xFF08261D) else Color(0xFF061835)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (cp.isCompleted) Color(0xFF10B981) else Color(0x33FFFFFF)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = cp.isCompleted,
                        onCheckedChange = {
                            GuideInteractionManager.toggleCheckpoint(context, cp.id, touristName)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF10B981),
                            uncheckedColor = Color.White.copy(alpha = 0.6f)
                        )
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = cp.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (cp.isCompleted) Color(0xFF34D399) else Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🏛️ ${cp.cityName} • ⏱️ ${cp.estimatedDuration}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.65f)
                            )
                            if (cp.completedTime != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "✓ ${cp.completedTime} da tashrif buyurildi",
                                    fontSize = 10.sp,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 3: GUIDE REVIEW & RATING SUBMISSION
// -------------------------------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GuideReviewTab(
    touristId: String,
    touristName: String,
    assignedNumber: String
) {
    val context = LocalContext.current
    var selectedStars by remember { mutableIntStateOf(5) }
    var commentInput by remember { mutableStateOf("") }
    val selectedTags = remember { mutableStateOf(mutableSetOf("Bilimli gid 🎓", "Samimiy xizmat 😊")) }
    var isSubmitting by remember { mutableStateOf(false) }

    val availableTags = listOf(
        "Bilimli gid 🎓",
        "Samimiy xizmat 😊",
        "Vaqtni qadrlaydi ⏱️",
        "Ajoyib hikoyalar 📖",
        "Xavfsizlik a'lo 🛡️",
        "Foto/video yordami 📸"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF071E43)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonGold.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "GID XIZMATIGA BAHO BERING",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = NeonGold
                    )
                    Text(
                        text = "Sizning fikringiz Telegram kanali va Admin hisobotiga yuboriladi",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 5 Golden Interactive Stars
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (star in 1..5) {
                            IconButton(
                                onClick = { selectedStars = star },
                                modifier = Modifier.size(44.dp)
                            ) {
                                Icon(
                                    imageVector = if (star <= selectedStars) Icons.Filled.Star else Icons.Outlined.Star,
                                    contentDescription = "$star Stars",
                                    tint = if (star <= selectedStars) NeonGold else Color(0x44FFFFFF),
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = when (selectedStars) {
                            5 -> "⭐⭐⭐⭐⭐ A'lo darajada! Juda tavsiya qilaman"
                            4 -> "⭐⭐⭐⭐ Yaxshi va mazmunli"
                            3 -> "⭐⭐⭐ O'rtacha qoniqarli"
                            2 -> "⭐⭐ Kamchiliklar bor"
                            else -> "⭐ Qoniqarsiz"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tags Selection
                    Text(
                        text = "Gidning eng ma'qul xususiyatlarini tanlang:",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        availableTags.forEach { tag ->
                            val isSelected = selectedTags.value.contains(tag)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSelected) NeonGold else Color(0xFF0C274E))
                                    .clickable {
                                        val newSet = selectedTags.value.toMutableSet()
                                        if (isSelected) newSet.remove(tag) else newSet.add(tag)
                                        selectedTags.value = newSet
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                    color = if (isSelected) Color.Black else Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Comment Input
                    OutlinedTextField(
                        value = commentInput,
                        onValueChange = { commentInput = it },
                        placeholder = { Text("Gid haqidagi taassurotlaringizni yozing...", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGold,
                            unfocusedBorderColor = Color(0x44FFFFFF),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF04132B),
                            unfocusedContainerColor = Color(0xFF04132B)
                        ),
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (!isSubmitting) {
                                isSubmitting = true
                                GuideInteractionManager.submitReview(
                                    context = context,
                                    touristId = touristId,
                                    touristName = touristName,
                                    guideNumber = assignedNumber,
                                    rating = selectedStars,
                                    tags = selectedTags.value.toList(),
                                    comment = commentInput,
                                    onFinished = { success ->
                                        isSubmitting = false
                                        Toast.makeText(
                                            context,
                                            if (success) "Baho va sharhingiz Telegram va Adminga yuborildi! Katta rahmat! ✓" else "Xatolik yuz berdi",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        commentInput = ""
                                    }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGold)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text(
                                text = "BAHONI YUBORISH (TELEGRAM & ADMIN) ➔",
                                color = RegistanBlueDark,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
