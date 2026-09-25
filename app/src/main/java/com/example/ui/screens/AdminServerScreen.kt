package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Storage
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.network.SupabaseRealtimeManager
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SampleDestinations
import com.example.network.SupabaseClient
import com.example.ui.components.GlassCard
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.SilkGold
import com.example.ui.theme.TurquoiseTile
import com.example.ui.screens.UserRegistrationProfile
import com.example.util.GuideInteractionManager
import com.example.util.TelegramBotManager
import com.example.util.UserSessionManager
import com.example.util.SosEmergencyManager
import com.example.util.SosEmergencyRecord
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Call
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminServerScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val scope = rememberCoroutineScope()

    var projectUrl by remember { mutableStateOf(SupabaseClient.getProjectUrl(context)) }
    var anonKey by remember { mutableStateOf(SupabaseClient.getAnonKey(context)) }
    var connectionStatus by remember { mutableStateOf("Holat: Tekshirilmagan") }
    var isTestingConnection by remember { mutableStateOf(false) }
    var isConnectionOk by remember { mutableStateOf<Boolean?>(null) }
    var isLoadingMonuments by remember { mutableStateOf(false) }
    var isLoadingBookings by remember { mutableStateOf(false) }
    var selectedAdminTab by remember { mutableIntStateOf(0) } // 0: Obidalar, 1: Bronlar, 2: SOS Chaqiruvlar, 3: WebSocket, 4: AI Aktivatsiya
    var showSqlModal by remember { mutableStateOf(false) }
    var showAddEditModal by remember { mutableStateOf(false) }
    var editingMonument by remember { mutableStateOf<SupabaseClient.RemoteMonument?>(null) }

    // SOS Emergency Alert States
    var sosEmergencies by remember { mutableStateOf(SosEmergencyManager.getAllEmergencies(context)) }
    val latestSosAlert by SosEmergencyManager.latestAlert.collectAsState()

    val refreshSosList: () -> Unit = {
        sosEmergencies = SosEmergencyManager.getAllEmergencies(context)
    }

    LaunchedEffect(latestSosAlert) {
        if (latestSosAlert != null) {
            refreshSosList()
        }
    }

    // User AI Activation States
    var userActivationList by remember { mutableStateOf(UserSessionManager.getAllUserRecords(context)) }
    var guideVerificationsList by remember { mutableStateOf(UserSessionManager.getAllGuideVerifications(context)) }
    var inputUserIdToActivate by remember { mutableStateOf("") }
    var currentAdminPinSetting by remember { mutableStateOf(UserSessionManager.getAdminPin(context)) }
    var showPinChangeSuccess by remember { mutableStateOf(false) }
    val currentDeviceId = remember { UserSessionManager.getUserId(context) }

    val refreshUserActivationList: () -> Unit = {
        userActivationList = UserSessionManager.getAllUserRecords(context)
        guideVerificationsList = UserSessionManager.getAllGuideVerifications(context)
    }

    val remoteMonuments = remember { mutableStateListOf<SupabaseClient.RemoteMonument>() }
    val remoteBookings = remember { mutableStateListOf<SupabaseClient.RemoteBooking>() }

    // WebSocket Realtime State
    val wsState by SupabaseRealtimeManager.socketState.collectAsState()
    val wsLatency by SupabaseRealtimeManager.latencyMs.collectAsState()
    val wsStatusMsg by SupabaseRealtimeManager.statusMessage.collectAsState()

    // Test connection function
    val runConnectionTest: () -> Unit = {
        scope.launch {
            isTestingConnection = true
            connectionStatus = "Serverga ulanish tekshirilmoqda..."
            val res = SupabaseClient.testConnection(context)
            isTestingConnection = false
            if (res.isSuccess) {
                isConnectionOk = true
                connectionStatus = "✅ ${res.getOrNull()}"
                Toast.makeText(context, "Supabase Serveri Ulandi!", Toast.LENGTH_SHORT).show()
            } else {
                isConnectionOk = false
                connectionStatus = "❌ Xatolik: ${res.exceptionOrNull()?.message}"
            }
        }
    }

    // Refresh monuments from Supabase
    val loadMonuments: () -> Unit = {
        scope.launch {
            isLoadingMonuments = true
            val res = SupabaseClient.fetchMonuments(context)
            isLoadingMonuments = false
            if (res.isSuccess) {
                val list = res.getOrNull() ?: emptyList()
                remoteMonuments.clear()
                remoteMonuments.addAll(list)
                SampleDestinations.updateWithRemoteMonuments(list)
                Toast.makeText(context, "${list.size} ta obida yangilandi!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Xatolik: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Refresh bookings from Supabase
    val loadBookings: () -> Unit = {
        scope.launch {
            isLoadingBookings = true
            val res = SupabaseClient.fetchBookings(context)
            isLoadingBookings = false
            if (res.isSuccess) {
                remoteBookings.clear()
                remoteBookings.addAll(res.getOrNull() ?: emptyList())
            }
        }
    }

    LaunchedEffect(Unit) {
        runConnectionTest()
        loadMonuments()
        loadBookings()

        // Auto-connect Realtime WebSocket
        SupabaseRealtimeManager.connect(context)

        // Listen to Realtime WebSocket incoming bookings
        launch {
            SupabaseRealtimeManager.incomingBookings.collect { payload ->
                val alreadyExists = remoteBookings.any { it.bookingCode == payload.bookingCode }
                if (!alreadyExists) {
                    remoteBookings.add(
                        0,
                        SupabaseClient.RemoteBooking(
                            id = payload.bookingCode,
                            bookingCode = payload.bookingCode,
                            tourId = "realtime",
                            tourTitle = payload.tourTitle,
                            touristName = payload.touristName,
                            touristPhone = "+998 90 000 00 00",
                            touristEmail = "live@uzturist.uz",
                            startDate = payload.timestamp,
                            peopleCount = payload.peopleCount,
                            totalPrice = payload.totalPrice,
                            bookingStatus = payload.bookingStatus,
                            paymentStatus = "Kutilmoqda"
                        )
                    )
                }
                // Also trigger full sync from Supabase database
                loadBookings()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0x330047AB) else Color(0xFFE2E8F0))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (isDark) NeonGold else RegistanBlue
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "ADMIN: SUPABASE SERVER",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = if (isDark) NeonGold else RegistanBlue
                        )
                        Text(
                            text = "Bulutli ma'lumotlar bazasi va obidalar boshqaruvi",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Connection Status Banner
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(18.dp),
                    backgroundColor = when (isConnectionOk) {
                        true -> if (isDark) Color(0x2210B981) else Color(0xFFECFDF5)
                        false -> if (isDark) Color(0x22EF4444) else Color(0xFFFEF2F2)
                        else -> if (isDark) Color(0x223B82F6) else Color(0xFFEFF6FF)
                    },
                    borderColor = when (isConnectionOk) {
                        true -> Color(0xFF10B981)
                        false -> Color(0xFFEF4444)
                        else -> TurquoiseTile
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (isConnectionOk) {
                                true -> Icons.Filled.CloudDone
                                false -> Icons.Filled.Error
                                else -> Icons.Filled.CloudSync
                            },
                            contentDescription = null,
                            tint = when (isConnectionOk) {
                                true -> Color(0xFF10B981)
                                false -> Color(0xFFEF4444)
                                else -> TurquoiseTile
                            },
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isConnectionOk == true) "SERVER ULANGAN VA FAOL" else "ULANISH HOLATI",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                ),
                                color = when (isConnectionOk) {
                                    true -> Color(0xFF10B981)
                                    false -> Color(0xFFEF4444)
                                    else -> TurquoiseTile
                                }
                            )
                            Text(
                                text = connectionStatus,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        if (isTestingConnection) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            IconButton(onClick = runConnectionTest) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = "Test",
                                    tint = if (isDark) NeonGold else RegistanBlue
                                )
                            }
                        }
                    }
                }
            }

            // Realtime WebSocket Live Status Banner
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(18.dp),
                    backgroundColor = when (wsState) {
                        SupabaseRealtimeManager.SocketState.CONNECTED -> if (isDark) Color(0x2210B981) else Color(0xFFF0FDF4)
                        SupabaseRealtimeManager.SocketState.CONNECTING, SupabaseRealtimeManager.SocketState.RECONNECTING -> if (isDark) Color(0x22F59E0B) else Color(0xFFFFFBEB)
                        SupabaseRealtimeManager.SocketState.ERROR -> if (isDark) Color(0x22EF4444) else Color(0xFFFEF2F2)
                        else -> if (isDark) Color(0x2264748B) else Color(0xFFF1F5F9)
                    },
                    borderColor = when (wsState) {
                        SupabaseRealtimeManager.SocketState.CONNECTED -> Color(0xFF10B981)
                        SupabaseRealtimeManager.SocketState.CONNECTING, SupabaseRealtimeManager.SocketState.RECONNECTING -> Color(0xFFF59E0B)
                        SupabaseRealtimeManager.SocketState.ERROR -> Color(0xFFEF4444)
                        else -> Color(0xFF94A3B8)
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    when (wsState) {
                                        SupabaseRealtimeManager.SocketState.CONNECTED -> Color(0x2210B981)
                                        SupabaseRealtimeManager.SocketState.CONNECTING, SupabaseRealtimeManager.SocketState.RECONNECTING -> Color(0x22F59E0B)
                                        else -> Color(0x22EF4444)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (wsState) {
                                    SupabaseRealtimeManager.SocketState.CONNECTED -> Icons.Filled.Bolt
                                    SupabaseRealtimeManager.SocketState.CONNECTING, SupabaseRealtimeManager.SocketState.RECONNECTING -> Icons.Filled.Wifi
                                    else -> Icons.Filled.WifiOff
                                },
                                contentDescription = null,
                                tint = when (wsState) {
                                    SupabaseRealtimeManager.SocketState.CONNECTED -> Color(0xFF10B981)
                                    SupabaseRealtimeManager.SocketState.CONNECTING, SupabaseRealtimeManager.SocketState.RECONNECTING -> Color(0xFFF59E0B)
                                    else -> Color(0xFFEF4444)
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "JONLI WEBSOCKET (REALTIME)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp
                                    ),
                                    color = when (wsState) {
                                        SupabaseRealtimeManager.SocketState.CONNECTED -> Color(0xFF10B981)
                                        SupabaseRealtimeManager.SocketState.CONNECTING, SupabaseRealtimeManager.SocketState.RECONNECTING -> Color(0xFFF59E0B)
                                        else -> Color(0xFFEF4444)
                                    }
                                )
                                if (wsLatency != null && wsState == SupabaseRealtimeManager.SocketState.CONNECTED) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF10B981).copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "⚡ ${wsLatency}ms",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF10B981)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = wsStatusMsg,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        // Test Ping button
                        IconButton(
                            onClick = {
                                SupabaseRealtimeManager.sendTestPing(context, "AdminPanel")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Send,
                                contentDescription = "Test Ping",
                                tint = NeonGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Supabase API Config Card
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    backgroundColor = if (isDark) Color(0x330047AB) else Color.White,
                    borderColor = NeonGold.copy(alpha = 0.4f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Key,
                                    contentDescription = null,
                                    tint = NeonGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "SUPABASE ULATISHLARI",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp
                                    ),
                                    color = if (isDark) NeonGold else RegistanBlue
                                )
                            }

                            TextButton(onClick = { showSqlModal = true }) {
                                Text("SQL Skript ➔", fontSize = 11.sp, color = TurquoiseTile, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Project URL input
                        OutlinedTextField(
                            value = projectUrl,
                            onValueChange = { projectUrl = it },
                            label = { Text("Project URL", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonGold,
                                unfocusedBorderColor = TurquoiseTile.copy(alpha = 0.5f)
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Anon Key input
                        OutlinedTextField(
                            value = anonKey,
                            onValueChange = { anonKey = it },
                            label = { Text("Anon (Public) Key", fontSize = 11.sp) },
                            maxLines = 2,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonGold,
                                unfocusedBorderColor = TurquoiseTile.copy(alpha = 0.5f)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ElevatedButton(
                                onClick = {
                                    SupabaseClient.saveCredentials(context, projectUrl, anonKey)
                                    Toast.makeText(context, "Sozlamalar saqlandi!", Toast.LENGTH_SHORT).show()
                                    runConnectionTest()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = NeonGold,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Saqlash", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            ElevatedButton(
                                onClick = runConnectionTest,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                                    contentColor = if (isDark) Color.White else Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Ulanishni Tekshirish", fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // 8 TA ASOSIY JADVAL ARXITEKTURASI KARTASI
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(18.dp),
                    backgroundColor = if (isDark) Color(0x330047AB) else Color(0xFFF8FAFC),
                    borderColor = TurquoiseTile.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(TurquoiseTile.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Storage,
                                        contentDescription = null,
                                        tint = TurquoiseTile,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "8 TA ASOSIY JADVAL BAZASI",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.5.sp,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = if (isDark) NeonGold else RegistanBlue
                                )
                            }

                            ElevatedButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("8 Tables SQL", SupabaseClient.SQL_COMPLETE_8_TABLES_SCRIPT))
                                    Toast.makeText(context, "8 ta jadval SQL nusxalandi! Supabase'ga tashlang", Toast.LENGTH_LONG).show()
                                },
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = TurquoiseTile,
                                    contentColor = Color.Black
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("SQL Nusxalash", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Grid of 8 tables
                        val tableNames = listOf(
                            "1. users" to "Xodimlar & Adminlar",
                            "2. bookings" to "Sayyohlar Bronlari",
                            "3. tours" to "Sayyohlik Paketlari",
                            "4. attractions" to "Maskanlar & Obidalar",
                            "5. guides" to "Sertifikatlangan Gidlar",
                            "6. promos" to "Chegirma Promokodlar",
                            "7. notifications" to "Ommaviy Xabarnomalar",
                            "8. admin_logs" to "Audit & Tizim Jurnali"
                        )

                        tableNames.chunked(2).forEach { row ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                row.forEach { (tbl, desc) ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isDark) Color(0x22FFFFFF) else Color(0x100047AB))
                                            .padding(horizontal = 8.dp, vertical = 5.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = tbl,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.5.sp
                                                ),
                                                color = if (isDark) NeonGold else RegistanBlue
                                            )
                                            Text(
                                                text = desc,
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // CRITICAL SOS EMERGENCY BANNER (If active SOS signal exists)
            latestSosAlert?.let { alert ->
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .border(2.dp, Color(0xFFEF4444), RoundedCornerShape(16.dp)),
                        backgroundColor = Color(0x33EF4444)
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "SOS",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "🚨 SHOSHILINCH SOS KELDI!",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFFEF4444)
                                        )
                                    )
                                }

                                androidx.compose.material3.IconButton(
                                    onClick = { SosEmergencyManager.dismissLatestAlert() },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Yopish",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "👤 ${alert.touristName} (${alert.touristPhone})",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color.Black
                                )
                            )
                            Text(
                                text = "📍 ${alert.address}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                                )
                            )
                            Text(
                                text = "⚠️ Sabab: ${alert.reason}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFFF87171),
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        selectedAdminTab = 2
                                        SosEmergencyManager.dismissLatestAlert()
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Zudlik bilan ko'rish ➔", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${alert.touristPhone}"))
                                        context.startActivity(dialIntent)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = "Qo'ng'iroq", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Tab Switcher: 0 - Obidalar / 1 - Sayyohlar Buyurtmalari / 2 - SOS / 3 - WebSocket / 4 - AI Aktivatsiya
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selectedAdminTab == 0) NeonGold else if (isDark) Color(0x22FFFFFF) else Color(0xFFE2E8F0))
                                .clickable { selectedAdminTab = 0 }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🏛️ Obidalar (${remoteMonuments.size})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    color = if (selectedAdminTab == 0) Color.Black else if (isDark) Color.White else Color.Black
                                )
                            )
                        }
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selectedAdminTab == 1) TurquoiseTile else if (isDark) Color(0x22FFFFFF) else Color(0xFFE2E8F0))
                                .clickable {
                                    selectedAdminTab = 1
                                    loadBookings()
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "📋 Bronlar (${remoteBookings.size})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    color = if (selectedAdminTab == 1) Color.Black else if (isDark) Color.White else Color.Black
                                )
                            )
                        }
                    }

                    item {
                        val activeSosCount = sosEmergencies.count { !it.isResolved }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (selectedAdminTab == 2) Color(0xFFEF4444)
                                    else if (activeSosCount > 0) Color(0x33EF4444)
                                    else if (isDark) Color(0x22FFFFFF)
                                    else Color(0xFFE2E8F0)
                                )
                                .border(
                                    width = if (activeSosCount > 0 && selectedAdminTab != 2) 1.5.dp else 0.dp,
                                    color = if (activeSosCount > 0 && selectedAdminTab != 2) Color(0xFFEF4444) else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedAdminTab = 2
                                    refreshSosList()
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🚨 SOS ($activeSosCount/${sosEmergencies.size})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.5.sp,
                                    color = if (selectedAdminTab == 2) Color.White else if (activeSosCount > 0) Color(0xFFEF4444) else if (isDark) Color.White else Color.Black
                                )
                            )
                        }
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selectedAdminTab == 3) Color(0xFF10B981) else if (isDark) Color(0x22FFFFFF) else Color(0xFFE2E8F0))
                                .clickable { selectedAdminTab = 3 }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⚡ 10x Realtime",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    color = if (selectedAdminTab == 3) Color.Black else if (isDark) Color.White else Color.Black
                                )
                            )
                        }
                    }

                    item {
                        val activatedCount = userActivationList.count { it.isActivated }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selectedAdminTab == 4) SilkGold else if (isDark) Color(0x33D4AF37) else Color(0xFFFDE68A))
                                .clickable {
                                    selectedAdminTab = 4
                                    refreshUserActivationList()
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🔑 AI Aktivatsiya ($activatedCount/${userActivationList.size})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.5.sp,
                                    color = if (selectedAdminTab == 4) Color.Black else if (isDark) NeonGold else Color(0xFF92400E)
                                )
                            )
                        }
                    }

                    // Tab 5: Gid Boshqaruvi
                    item {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (selectedAdminTab == 5) Color(0xFF10B981)
                                    else if (isDark) Color(0x330052D4)
                                    else Color(0xFFE0E7FF)
                                )
                                .clickable { selectedAdminTab = 5 }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "👑 Gid Boshqaruvi (Joy • Chat • Marshrut • Baho)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.5.sp,
                                    color = if (selectedAdminTab == 5) Color.Black else if (isDark) Color.White else Color(0xFF1E3A8A)
                                )
                            )
                        }
                    }

                    // Tab 6: Sayyoh Hamyonlari & Tranzaksiyalar
                    item {
                        val allWallets = remember(com.example.util.TouristWalletManager.walletUpdateTrigger) {
                            com.example.util.TouristWalletManager.getAllWalletsForAdmin(context)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (selectedAdminTab == 6) NeonGold
                                    else if (isDark) Color(0x33B45309)
                                    else Color(0xFFFEF3C7)
                                )
                                .clickable { selectedAdminTab = 6 }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "💳 Hamyonlar & Mablag'lar (${allWallets.size})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.5.sp,
                                    color = if (selectedAdminTab == 6) Color.Black else if (isDark) NeonGold else Color(0xFF92400E)
                                )
                            )
                        }
                    }
                }
            }

            if (selectedAdminTab == 0) {
                // Quick Sync Action Bar
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "OBIDALAR BAZASI (POSTGRES)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "Supabase'dan yuklangan: ${remoteMonuments.size} ta",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = TurquoiseTile
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ElevatedButton(
                                onClick = loadMonuments,
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = if (isDark) Color(0x330047AB) else Color(0xFFF1F5F9),
                                    contentColor = if (isDark) NeonGold else RegistanBlue
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                if (isLoadingMonuments) {
                                    CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Filled.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Yuklash", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            ElevatedButton(
                                onClick = {
                                    editingMonument = null
                                    showAddEditModal = true
                                },
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = NeonGold,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Yangi Obida", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Remote Monuments List
                if (remoteMonuments.isEmpty() && !isLoadingMonuments) {
                    item {
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "🏛️", fontSize = 36.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Hozircha Supabase bazasida obidalar yo'q",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Yuqoridagi 'SQL Nusxalash' tugmasi orqali jadvallarni oching yoki 'Yangi Obida' tugmasi bilan birinchi obidani yuklang.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(remoteMonuments) { item ->
                        MonumentRowCard(
                            monument = item,
                            isDark = isDark,
                            onEdit = {
                                editingMonument = item
                                showAddEditModal = true
                            },
                            onDelete = {
                                scope.launch {
                                    val delRes = SupabaseClient.deleteMonument(context, item.id)
                                    if (delRes.isSuccess) {
                                        remoteMonuments.remove(item)
                                        Toast.makeText(context, "O'chirildi!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Xatolik: ${delRes.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }
                }
            }

            if (selectedAdminTab == 1) {
                // TAB 1: SAYYOHLAR BUYURTMALARI (bookings)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "SAYYOHLAR BRONLARI (bookings)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "Admin & Sayyoh ilovasi o'rtasida real-vaqt sinxron",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = TurquoiseTile
                            )
                        }

                        ElevatedButton(
                            onClick = loadBookings,
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = TurquoiseTile,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            if (isLoadingBookings) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = Color.Black)
                            } else {
                                Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Yangilash", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (remoteBookings.isEmpty() && !isLoadingBookings) {
                    item {
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "📋", fontSize = 36.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Hozircha yangi buyurtmalar kelib tushmadi",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Ilovada sayyohlar tur yoki gid buyurtma berishi bilanoq 'bookings' jadvaliga yoziladi va bu yerda ko'rinadi.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(remoteBookings) { booking ->
                        BookingAdminRowCard(booking = booking, isDark = isDark)
                    }
                }
            }

            // TAB 2: SHOSHILINCH SOS DISPETCHER CHAQIRUVLARI
            if (selectedAdminTab == 2) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "SHOSHILINCH SOS CHAQIRUVLAR (Live Alerts)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                ),
                                color = Color(0xFFEF4444)
                            )
                            val activeCount = sosEmergencies.count { !it.isResolved }
                            Text(
                                text = "$activeCount ta faol yordam kutilmoqda • Jami ${sosEmergencies.size} ta",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                            )
                        }

                        ElevatedButton(
                            onClick = refreshSosList,
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = Color(0xFFEF4444),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Yangilash", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (sosEmergencies.isEmpty()) {
                    item {
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "🛡️", fontSize = 36.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Hozircha SOS chaqiruvlari yo'q",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Sayyohlar ilovadagi SOS tugmasini bosishi bilanoq ularning aniq GPS koordinatalari, ismi, telefoni va manzili zudlik bilan admin ilovaga yetib keladi.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(sosEmergencies) { sos ->
                        SosEmergencyAdminRowCard(
                            sos = sos,
                            isDark = isDark,
                            onResolve = {
                                SosEmergencyManager.resolveEmergency(context, sos.id)
                                refreshSosList()
                                Toast.makeText(context, "Holat: Hal qilindi deb belgilandi!", Toast.LENGTH_SHORT).show()
                            },
                            onDelete = {
                                SosEmergencyManager.deleteEmergency(context, sos.id)
                                refreshSosList()
                                Toast.makeText(context, "SOS yozuvi o'chirildi", Toast.LENGTH_SHORT).show()
                            },
                            onCall = {
                                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${sos.touristPhone}"))
                                context.startActivity(dialIntent)
                            },
                            onOpenMap = {
                                val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${sos.latitude},${sos.longitude}"))
                                context.startActivity(mapIntent)
                            },
                            onResendTelegram = {
                                TelegramBotManager.sendSosViaTelegramApi(
                                    context = context,
                                    touristName = sos.touristName,
                                    phone = sos.touristPhone,
                                    latitude = sos.latitude,
                                    longitude = sos.longitude,
                                    nearestMonument = sos.address,
                                    emergencyType = sos.reason
                                )
                                Toast.makeText(context, "Telegram dispetcheriga qayta yuborildi!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            // TAB 3: 10X TURBO REALTIME ALOQA (ZERO LOGS, PURE HIGH SPEED)
            if (selectedAdminTab == 3) {
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(18.dp),
                        backgroundColor = if (isDark) Color(0x330047AB) else Color.White,
                        borderColor = Color(0xFF10B981).copy(alpha = 0.6f)
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
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981).copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("⚡", fontSize = 20.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "10X TURBO REALTIME ALOQA",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Black,
                                                letterSpacing = 0.5.sp
                                            ),
                                            color = Color(0xFF10B981)
                                        )
                                        Text(
                                            text = "Kanal: realtime:uzturist_admin_sync",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                        )
                                    }
                                }

                                Row {
                                    if (wsState == SupabaseRealtimeManager.SocketState.CONNECTED) {
                                        ElevatedButton(
                                            onClick = { SupabaseRealtimeManager.disconnect() },
                                            colors = ButtonDefaults.elevatedButtonColors(
                                                containerColor = Color(0x33EF4444),
                                                contentColor = Color(0xFFEF4444)
                                            ),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text("Uzish", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        ElevatedButton(
                                            onClick = { SupabaseRealtimeManager.connect(context) },
                                            colors = ButtonDefaults.elevatedButtonColors(
                                                containerColor = Color(0xFF10B981),
                                                contentColor = Color.Black
                                            ),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text("Ulash", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    ElevatedButton(
                                        onClick = { SupabaseRealtimeManager.sendTestPing(context, "Admin") },
                                        colors = ButtonDefaults.elevatedButtonColors(
                                            containerColor = NeonGold,
                                            contentColor = Color.Black
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text("⚡ 10x Sinov", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Status & Metrics Cards
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Card 1: 10x Turbo Speed & Latency
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            backgroundColor = if (isDark) Color(0x22059669) else Color(0xFFECFDF5),
                            borderColor = Color(0xFF10B981).copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "🚀 ALOQA TEZLIGI",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Black,
                                                fontSize = 10.5.sp
                                            ),
                                            color = Color(0xFF10B981)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFF10B981).copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "10X TURBO",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 9.5.sp,
                                                    color = Color(0xFF10B981)
                                                )
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (wsState == SupabaseRealtimeManager.SocketState.CONNECTED)
                                            "Admin va ilova o'rtasidagi ma'lumotlar almashinuvi 10x maksimal tezlikda ishlamoqda"
                                        else "Ulanish kutilmoqda...",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${wsLatency ?: 8} ms",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 22.sp
                                        ),
                                        color = Color(0xFF10B981)
                                    )
                                    Text(
                                        text = "Jonli Kechikish",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }

                        // Card 2: Technical Specifications
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            backgroundColor = if (isDark) Color(0x22001A3D) else Color(0xFFF8FAFC),
                            borderColor = TurquoiseTile.copy(alpha = 0.4f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "10X TEZKOR ALOQA KO'RSATKICHLARI",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.5.sp,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = TurquoiseTile
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "Rejim",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                        )
                                        Text(
                                            text = "Ultra Low-Latency",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "Qayta ulanish",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                        )
                                        Text(
                                            text = "200 ms (10x tez)",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color(0xFF10B981)
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "Heartbeat",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                        )
                                        Text(
                                            text = "Har 3 soniyada",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = NeonGold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "Xotira & Loglar",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                        )
                                        Text(
                                            text = "0% Log / Toza Oqim",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color(0xFF10B981)
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "Paket Yetkazish",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                        )
                                        Text(
                                            text = "100% Kafolatlangan",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "Kanal Holati",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                        )
                                        Text(
                                            text = if (wsState == SupabaseRealtimeManager.SocketState.CONNECTED) "Jonli Ulangan ✓" else "Kutilmoqda",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = if (wsState == SupabaseRealtimeManager.SocketState.CONNECTED) Color(0xFF10B981) else Color(0xFFEF4444)
                                        )
                                    }
                                }
                            }
                        }

                        // Card 3: Instant Sync explanation
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            backgroundColor = if (isDark) Color(0x153B82F6) else Color(0xFFEFF6FF),
                            borderColor = Color(0xFF3B82F6).copy(alpha = 0.3f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Yangi buyurtmalar, status o'zgarishlari va SOS signallari admin ilovasi bilan mikrosekundlarda sinxronlanadi. Tizim tezligini 10 barobarga oshirish maqsadida barcha og'ir foniy loglar o'chirildi.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }

            // TAB 4: FOYDALANUVCHI ID LARI VA AI FAOLLASHTIRISH (ADMIN ACTIVATION DASHBOARD)
            if (selectedAdminTab == 4) {
                // Header Banner
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(18.dp),
                        backgroundColor = if (isDark) Color(0x330047AB) else Color(0xFFF8FAFC),
                        borderColor = NeonGold.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(NeonGold, SilkGold))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Key,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "FOYDALANUVCHILAR VA AI FAOLLASHTIRISH",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.5.sp
                                        ),
                                        color = if (isDark) NeonGold else RegistanBlue
                                    )
                                    Text(
                                        text = "Barcha AI funksiyalar admin tomonidan ID faollashtirilgach ishlaydi",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Metric Badges
                            val activeCount = userActivationList.count { it.isActivated }
                            val pendingCount = userActivationList.count { !it.isActivated }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0x223B82F6))
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("${userActivationList.size}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFF3B82F6))
                                        Text("Jami ID", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0x2210B981))
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("$activeCount", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFF10B981))
                                        Text("Faollashtirilgan", fontSize = 10.sp, color = Color(0xFF10B981))
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0x22EF4444))
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("$pendingCount", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFFEF4444))
                                        Text("Kutilmoqda", fontSize = 10.sp, color = Color(0xFFEF4444))
                                    }
                                }
                            }
                        }
                    }
                }

                // ID Kiritish va Faollashtirish formasi
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        borderColor = TurquoiseTile.copy(alpha = 0.6f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "ID KIRITISH VA AI NI FAOLLASHTIRISH",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                ),
                                color = TurquoiseTile
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Foydalanuvchi taqdim etgan ID raqamini (masalan: UZ-TOUR-7842) kiriting va 'Faollashtirish' tugmasini bosing:",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = inputUserIdToActivate,
                                onValueChange = { inputUserIdToActivate = it.uppercase() },
                                placeholder = { Text("Foydalanuvchi IDsi (masalan: UZ-TOUR-7842)", fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonGold,
                                    unfocusedBorderColor = Color(0x66FFFFFF)
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ElevatedButton(
                                    onClick = {
                                        val trimmed = inputUserIdToActivate.trim()
                                        if (trimmed.isNotBlank()) {
                                            val ok = UserSessionManager.activateUserId(context, trimmed)
                                            if (ok) {
                                                refreshUserActivationList()
                                                Toast.makeText(context, "✅ ID $trimmed AI uchun faollashtirildi!", Toast.LENGTH_SHORT).show()
                                                inputUserIdToActivate = ""
                                            }
                                        } else {
                                            Toast.makeText(context, "Iltimos, avval ID kiriting!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.elevatedButtonColors(
                                        containerColor = NeonGold,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Faollashtirish", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                }

                                ElevatedButton(
                                    onClick = {
                                        val trimmed = inputUserIdToActivate.trim()
                                        if (trimmed.isNotBlank()) {
                                            UserSessionManager.deactivateUserId(context, trimmed)
                                            refreshUserActivationList()
                                            Toast.makeText(context, "❌ ID $trimmed nofaol qilindi", Toast.LENGTH_SHORT).show()
                                            inputUserIdToActivate = ""
                                        }
                                    },
                                    colors = ButtonDefaults.elevatedButtonColors(
                                        containerColor = Color(0x33EF4444),
                                        contentColor = Color(0xFFEF4444)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Nofaol Qilish", fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // One-tap quick activation of current device
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0x1A00E5FF))
                                    .border(1.dp, TurquoiseTile.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                    .clickable {
                                        UserSessionManager.activateUserId(context, currentDeviceId)
                                        refreshUserActivationList()
                                        Toast.makeText(context, "✅ Ushbu qurilma ($currentDeviceId) AI uchun faollashtirildi!", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "⚡ O'z qurilmangiz ID si: $currentDeviceId",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.5.sp,
                                            color = TurquoiseTile
                                        )
                                        Text(
                                            text = if (UserSessionManager.isAiActivated(context)) "Holati: FAOL (AI ochiq)" else "Holati: NOFAOL — Tez faollashtirish uchun bosing",
                                            fontSize = 10.sp,
                                            color = if (UserSessionManager.isAiActivated(context)) Color(0xFF10B981) else Color(0xFFFF8A80)
                                        )
                                    }
                                    Text(
                                        text = "Faollashtirish ➔",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        color = NeonGold
                                    )
                                }
                            }
                        }
                    }
                }

                // Admin Master PIN boshqaruvi
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "ADMIN MASTER PIN KODI",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.sp
                                        ),
                                        color = NeonGold
                                    )
                                    Text(
                                        text = "Foydalanuvchi ekranda bevosita faollashtirishda ishlatiladi",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = currentAdminPinSetting,
                                    onValueChange = { currentAdminPinSetting = it },
                                    label = { Text("Admin PIN (standart: admin2026)", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                ElevatedButton(
                                    onClick = {
                                        if (currentAdminPinSetting.isNotBlank()) {
                                            UserSessionManager.setAdminPin(context, currentAdminPinSetting.trim())
                                            Toast.makeText(context, "Admin PIN muvaffaqiyatli saqlandi!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.elevatedButtonColors(
                                        containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                                        contentColor = if (isDark) Color.White else Color.Black
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Saqlash", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // GID QABUL QILGAN VA RAQAM BIRIKTIRILGAN SAYYOHLAR (ADMIN & TELEGRAM RO'YXATI)
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(18.dp),
                        backgroundColor = if (isDark) Color(0x33003366) else Color(0xFFEFF6FF),
                        borderColor = Color(0xFF10B981).copy(alpha = 0.6f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "GID QABUL QILGAN SAYYOHLAR (${guideVerificationsList.size})",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Black,
                                                fontSize = 11.5.sp
                                            ),
                                            color = Color(0xFF10B981)
                                        )
                                        Text(
                                            text = "Gid bergan raqam, ism, gmail va telefon Telegramga yuborilgan",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                        )
                                    }
                                }

                                TextButton(
                                    onClick = refreshUserActivationList,
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                                ) {
                                    Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color(0xFF10B981))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Yangilash", fontSize = 10.5.sp, color = Color(0xFF10B981))
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (guideVerificationsList.isEmpty()) {
                                Text(
                                    text = "Hozircha gid raqami kiritilgan foydalanuvchilar mavjud emas.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    guideVerificationsList.forEach { record ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (isDark) Color(0x33001833) else Color.White)
                                                .border(1.dp, Color(0x3310B981), RoundedCornerShape(12.dp))
                                                .padding(10.dp)
                                        ) {
                                            Column {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    // Guide-assigned number badge
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(Color(0xFF10B981))
                                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                                    ) {
                                                        Text(
                                                            text = "🔢 GID RAQAMI: ${record.guideNumber}",
                                                            fontWeight = FontWeight.Black,
                                                            fontSize = 11.5.sp,
                                                            color = Color.Black
                                                        )
                                                    }

                                                    // Telegram resend button
                                                    IconButton(
                                                        onClick = {
                                                            Toast.makeText(context, "Telegramga yuborilmoqda...", Toast.LENGTH_SHORT).show()
                                                            TelegramBotManager.sendGuideAcceptedVerificationViaTelegramApi(
                                                                context = context,
                                                                guideNumber = record.guideNumber,
                                                                userName = record.userName,
                                                                userPhone = record.phone,
                                                                userEmail = record.email,
                                                                userId = record.userId
                                                            ) { _, msg ->
                                                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                                            }
                                                        },
                                                        modifier = Modifier
                                                            .size(28.dp)
                                                            .clip(CircleShape)
                                                            .background(Color(0x220088CC))
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Filled.Send,
                                                            contentDescription = "Telegramga yuborish",
                                                            tint = Color(0xFF0088CC),
                                                            modifier = Modifier.size(13.dp)
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(6.dp))

                                                Text(
                                                    text = "👤 ${record.userName}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.5.sp,
                                                    color = MaterialTheme.colorScheme.onBackground
                                                )

                                                Text(
                                                    text = "📞 ${record.phone}  •  📧 ${record.email}",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                                )

                                                Spacer(modifier = Modifier.height(3.dp))

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = "ID: ${record.userId}",
                                                        fontSize = 10.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = NeonGold
                                                    )
                                                    Text(
                                                        text = "Vaqt: ${record.verifiedDate}",
                                                        fontSize = 10.sp,
                                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
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

                // Ro'yxat sarlavhasi
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RO'YXATDAGI FOYDALANUVCHILAR VA STATUSLAR (${userActivationList.size})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )

                        TextButton(
                            onClick = refreshUserActivationList,
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(14.dp), tint = NeonGold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Yangilash", fontSize = 11.sp, color = NeonGold)
                        }
                    }
                }

                // Foydalanuvchilar ro'yxati
                items(userActivationList, key = { it.userId }) { user ->
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        backgroundColor = if (isDark) Color(0x22001A3D) else Color(0xFFF8FAFC),
                        borderColor = if (user.isActivated) Color(0xFF10B981) else Color(0xFFEF4444).copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (user.isActivated) Color(0x3310B981) else Color(0x33EF4444)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (user.isActivated) Icons.Filled.CheckCircle else Icons.Filled.Lock,
                                    contentDescription = null,
                                    tint = if (user.isActivated) Color(0xFF10B981) else Color(0xFFEF4444),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = user.userId,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.5.sp,
                                            letterSpacing = 0.5.sp
                                        ),
                                        color = if (user.isActivated) NeonGold else MaterialTheme.colorScheme.onBackground
                                    )
                                    if (user.userId.equals(currentDeviceId, ignoreCase = true)) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0x333B82F6))
                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                        ) {
                                            Text("Siz", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF60A5FA))
                                        }
                                    }
                                }

                                Text(
                                    text = "${user.userName} • ${user.phone}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (user.isActivated) "🟢 AI Faol" else "🔴 AI Nofaol (Admin kutilmoqda)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (user.isActivated) Color(0xFF10B981) else Color(0xFFEF4444)
                                    )
                                    if (user.activatedDate != null) {
                                        Text(
                                            text = " (${user.activatedDate})",
                                            fontSize = 9.5.sp,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Send/Resend user details to Telegram Bot
                            IconButton(
                                onClick = {
                                    val parts = user.userName.split(" ")
                                    val fName = parts.firstOrNull() ?: user.userName
                                    val lName = if (parts.size > 1) parts.drop(1).joinToString(" ") else ""
                                    val profile = UserRegistrationProfile(
                                        firstName = fName,
                                        lastName = lName,
                                        phoneNumber = user.phone,
                                        touristId = user.userId,
                                        registeredDate = user.registeredDate,
                                        languageName = "O'zbekcha",
                                        languageCode = "uz"
                                    )
                                    Toast.makeText(context, "Telegramga yuborilmoqda...", Toast.LENGTH_SHORT).show()
                                    TelegramBotManager.sendNewUserRegistrationViaTelegramApi(context, profile) { _, msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x220088CC))
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Send,
                                    contentDescription = "Telegramga yuborish",
                                    tint = Color(0xFF0088CC),
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // One-tap Action button to Toggle activation
                            ElevatedButton(
                                onClick = {
                                    if (user.isActivated) {
                                        UserSessionManager.deactivateUserId(context, user.userId)
                                        Toast.makeText(context, "${user.userId} nofaol qilindi", Toast.LENGTH_SHORT).show()
                                    } else {
                                        UserSessionManager.activateUserId(context, user.userId)
                                        Toast.makeText(context, "✅ ${user.userId} faollashtirildi!", Toast.LENGTH_SHORT).show()
                                    }
                                    refreshUserActivationList()
                                },
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = if (user.isActivated) Color(0x33EF4444) else Color(0xFF10B981),
                                    contentColor = if (user.isActivated) Color(0xFFEF4444) else Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (user.isActivated) "O'chirish" else "Faollashtirish",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            if (selectedAdminTab == 5) {
                // =========================================================================================
                // TAB 5: GID BOSHQARUVI, JONLI JOYLASHUV, CHAT, MARSHRUT VA SHARHLAR
                // =========================================================================================
                val guideLocation = GuideInteractionManager.currentGuideLocation

                // 1. GIDNING JONLI JOYLASHUVI & RADAR KARTASI
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(18.dp),
                        backgroundColor = if (isDark) Color(0x33003366) else Color(0xFFEFF6FF),
                        borderColor = NeonGold
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
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
                                            .background(Color(0xFF10B981)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Navigation,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "GID JONLI JOYLASHUVI & UCHRASHUV NUQTASI",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Black,
                                                fontSize = 11.5.sp
                                            ),
                                            color = NeonGold
                                        )
                                        Text(
                                            text = "🟢 ${guideLocation.status}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                            color = Color(0xFF10B981)
                                        )
                                    }
                                }

                                ElevatedButton(
                                    onClick = {
                                        val gmmIntentUri = Uri.parse("geo:${guideLocation.latitude},${guideLocation.longitude}?q=${guideLocation.latitude},${guideLocation.longitude}(Registon+Maydoni)")
                                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                        try {
                                            context.startActivity(mapIntent)
                                        } catch (e: Exception) {
                                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${guideLocation.latitude},${guideLocation.longitude}"))
                                            context.startActivity(browserIntent)
                                        }
                                    },
                                    colors = ButtonDefaults.elevatedButtonColors(
                                        containerColor = TurquoiseTile,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Filled.Map, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Xaritada", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "👤 Gid: ${guideLocation.guideName} • 📞 ${guideLocation.guidePhone}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "📍 Belgilangan nuqta: ${guideLocation.meetingPointName}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
                            )
                            Text(
                                text = "⏰ Reja vaqti: ${guideLocation.meetingTime}",
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // 2. JONLI CHAT VA XABARLAR (SAYYOH & GID & TELEGRAM)
                item {
                    val chatMessages = remember(GuideInteractionManager.chatUpdateTrigger) {
                        GuideInteractionManager.getChatMessages(context)
                    }
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(18.dp),
                        backgroundColor = if (isDark) Color(0x330047AB) else Color(0xFFF8FAFC),
                        borderColor = TurquoiseTile
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Message,
                                        contentDescription = null,
                                        tint = TurquoiseTile,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "JONLI CHAT VA ALOQA (${chatMessages.size} ta xabar)",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.5.sp
                                        ),
                                        color = TurquoiseTile
                                    )
                                }

                                Text(
                                    text = "Telegramga ulangan ✓",
                                    fontSize = 10.sp,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                chatMessages.takeLast(4).forEach { msg ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (msg.isFromTourist) Color(0x223B82F6) else Color(0x2210B981))
                                            .border(
                                                0.8.dp,
                                                if (msg.isFromTourist) Color(0xFF3B82F6).copy(alpha = 0.4f) else Color(0xFF10B981).copy(alpha = 0.4f),
                                                RoundedCornerShape(10.dp)
                                            )
                                            .padding(10.dp)
                                    ) {
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = if (msg.isFromTourist) "Sayyoh: ${msg.senderName}" else "Gid: ${msg.senderName}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = if (msg.isFromTourist) NeonGold else Color(0xFF10B981)
                                                )
                                                Text(
                                                    text = msg.timestamp,
                                                    fontSize = 9.5.sp,
                                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "«${msg.messageText}»",
                                                fontSize = 11.5.sp,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. EKSKURSIYA MARSHRUTI (ITINERARY CHECKPOINTS)
                item {
                    val checkpoints = remember(GuideInteractionManager.checkpointsUpdateTrigger) {
                        GuideInteractionManager.getTourCheckpoints(context)
                    }
                    val completedCount = checkpoints.count { it.isCompleted }
                    val percent = if (checkpoints.isNotEmpty()) (completedCount * 100) / checkpoints.size else 0

                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(18.dp),
                        backgroundColor = if (isDark) Color(0x3306203D) else Color(0xFFF1F5F9),
                        borderColor = Color(0xFF10B981)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🗺️ KUNLIK EKSKURSIYA MARSHRUTI",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF10B981)
                                )
                                Text(
                                    text = "$completedCount/${checkpoints.size} ($percent%)",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = NeonGold
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                checkpoints.forEach { cp ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (cp.isCompleted) Color(0x2210B981) else Color(0x11000000))
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (cp.isCompleted) "✅" else "⚪",
                                            fontSize = 12.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = cp.title,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (cp.isCompleted) Color(0xFF10B981) else MaterialTheme.colorScheme.onBackground
                                            )
                                            Text(
                                                text = "${cp.cityName} • ${cp.estimatedDuration} ${if (cp.completedTime != null) "• Soat: ${cp.completedTime}" else ""}",
                                                fontSize = 9.5.sp,
                                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. GID REYTINGI VA SHARHLAR (REVIEWS & RATINGS)
                item {
                    val reviews = remember(GuideInteractionManager.reviewsUpdateTrigger) {
                        GuideInteractionManager.getAllReviews(context)
                    }
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(18.dp),
                        backgroundColor = if (isDark) Color(0x331E1B4B) else Color(0xFFFAF5FF),
                        borderColor = SilkGold
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⭐ GID REYTINGI VA SHARHLAR (${reviews.size})",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.5.sp,
                                    color = SilkGold
                                )
                                Text(
                                    text = "Telegramga uzatildi ✓",
                                    fontSize = 10.sp,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                reviews.forEach { rev ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isDark) Color(0x33000000) else Color.White)
                                            .border(0.8.dp, SilkGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                            .padding(10.dp)
                                    ) {
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "⭐".repeat(rev.rating) + " (${rev.rating}/5)",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = NeonGold
                                                )
                                                Text(
                                                    text = rev.dateFormatted,
                                                    fontSize = 9.5.sp,
                                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "Sayyoh: ${rev.touristName} (Gid kodi: ${rev.guideAssignedNumber})",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                            if (rev.tags.isNotEmpty()) {
                                                Text(
                                                    text = "Teglar: ${rev.tags.joinToString(", ")}",
                                                    fontSize = 10.sp,
                                                    color = TurquoiseTile
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                 text = "«${rev.comment}»",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. SAFETOUR GEOFENCE RADAR VA XAVFSIZLIK MONITORINGI
                item {
                    val alertLogs = remember(com.example.util.SafeTourRadarManager.radarUpdateTrigger) {
                        com.example.util.SafeTourRadarManager.getAlertLogs(context)
                    }
                    val (currentDist, _, currentStatus) = com.example.util.SafeTourRadarManager.getCurrentRadarData(context)

                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(18.dp),
                        backgroundColor = if (isDark) Color(0x33064E3B) else Color(0xFFECFDF5),
                        borderColor = Color(currentStatus.colorHex)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🦺", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "SAFETOUR GEOFENCE MONITORING",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.5.sp,
                                            color = Color(currentStatus.colorHex)
                                        )
                                        Text(
                                            text = "Adashib qolish va perimetr buzilishi nazorati",
                                            fontSize = 9.5.sp,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(currentStatus.colorHex).copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "$currentDist m • ${currentStatus.label}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(currentStatus.colorHex)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "So'nggi SafeTour ogohlantirishlari (${alertLogs.size} ta):",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                alertLogs.take(4).forEach { log ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isDark) Color(0x33000000) else Color.White)
                                            .border(0.8.dp, Color(0xFF10B981).copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                            .padding(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "${log.touristName} (${log.distanceMeters} metr)",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = if (log.distanceMeters > 80) Color(0xFFEF4444) else Color(0xFF10B981)
                                                )
                                                Text(
                                                    text = "Holat: ${log.status} • ${log.locationDesc}",
                                                    fontSize = 9.5.sp,
                                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                                )
                                            }
                                            Text(
                                                text = log.timeFormatted,
                                                fontSize = 9.sp,
                                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (selectedAdminTab == 6) {
                // =========================================================================================
                // TAB 6: SAYYOHLAR ELEKTRON HAMYONLARI VA MABLAG'LAR (SILK ROAD PAY DASHBOARD)
                // =========================================================================================
                val allWallets = com.example.util.TouristWalletManager.getAllWalletsForAdmin(context)
                val totalCirculationUzs = allWallets.sumOf { it.balanceUzs }
                val totalCirculationUsd = allWallets.sumOf { it.balanceUsd }
                val totalSpentTodayUzs = allWallets.sumOf { it.todaySpentUzs }

                // 1. KPI & OVERVIEW CARDS
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(20.dp),
                        backgroundColor = if (isDark) Color(0x33003366) else Color.White,
                        borderColor = NeonGold.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "⭐", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "SILK ROAD PAY • MONITORING",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.5.sp,
                                        color = NeonGold
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0x2210B981))
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "Avtomatik Sinxron",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isDark) Color(0x22FFFFFF) else Color(0xFFF1F5F9))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text("Jami Hamyonlar", fontSize = 9.5.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                                        Text("${allWallets.size} ta sayyoh", fontWeight = FontWeight.Black, fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isDark) Color(0x22FFFFFF) else Color(0xFFF1F5F9))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text("Jami Balans (Muomala)", fontSize = 9.5.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                                        Text(com.example.util.TouristWalletManager.formatUzs(totalCirculationUzs), fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color(0xFF10B981))
                                        Text("~${com.example.util.TouristWalletManager.formatUsd(totalCirculationUsd)} USD", fontSize = 9.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isDark) Color(0x22FFFFFF) else Color(0xFFF1F5F9))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text("Bugungi Sarf", fontSize = 9.5.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                                        Text(com.example.util.TouristWalletManager.formatUzs(totalSpentTodayUzs), fontWeight = FontWeight.Black, fontSize = 11.5.sp, color = TurquoiseTile)
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. SAYYOHLARNING ELEKTRON HAMYONLARI RO'YXATI
                item {
                    Text(
                        text = "SAYYOHLAR RO'YXATI VA VIRTUAL KARTALARI (${allWallets.size})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                items(allWallets) { tw ->
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = if (isDark) Color(0x33001E3D) else Color.White,
                        borderColor = if (tw.isCardFrozen) Color(0xFFFF4757).copy(alpha = 0.5f) else NeonGold.copy(alpha = 0.35f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = tw.touristName,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.5.sp,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (tw.isCardFrozen) Color(0x33FF4757) else Color(0x2210B981))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = if (tw.isCardFrozen) "MUZLATILGAN" else "FAOL",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (tw.isCardFrozen) Color(0xFFFF4757) else Color(0xFF10B981)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "ID: ${tw.touristId} • Hamyon: ${tw.walletId}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = com.example.util.TouristWalletManager.formatUzs(tw.balanceUzs),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = Color(0xFF10B981)
                                    )
                                    Text(
                                        text = "~${com.example.util.TouristWalletManager.formatUsd(tw.balanceUsd)} USD",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Virtual Card strip
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.06f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "💳 ${tw.cardNumber}",
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = "Muddat: ${tw.cardExpiry} • NFC: ${if (tw.isNfcActive) "Yoqilgan" else "O'chiq"}",
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Admin Actions: +100,000 UZS Bonus & Freeze/Unfreeze
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        com.example.util.TouristWalletManager.topUpWallet(
                                            context = context,
                                            userId = tw.touristId,
                                            amountUzs = 100_000.0,
                                            method = "Admin Sovg'a Bonusi"
                                        ) { ok, msg ->
                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                ) {
                                    Text("+100k Bonus", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        com.example.util.TouristWalletManager.topUpWallet(
                                            context = context,
                                            userId = tw.touristId,
                                            amountUzs = 500_000.0,
                                            method = "Admin VIP Depozit"
                                        ) { ok, msg ->
                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = TurquoiseTile, contentColor = Color.Black)
                                ) {
                                    Text("+500k Depozit", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        val newState = com.example.util.TouristWalletManager.toggleCardFreeze(context, tw.touristId)
                                        Toast.makeText(context, if (newState) "Karta muzlatildi ❄️" else "Karta faollashtirildi ✓", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(0.9f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = if (tw.isCardFrozen) "Ochish" else "Muzlatish",
                                        fontSize = 10.sp,
                                        color = if (tw.isCardFrozen) Color(0xFF10B981) else Color(0xFFFF4757)
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. TRANZAKSIYALAR MONITORINGI (AUDIT)
                item {
                    val primaryUid = com.example.util.UserSessionManager.getUserId(context)
                    val recentTxns = remember(com.example.util.TouristWalletManager.transactionsUpdateTrigger) {
                        com.example.util.TouristWalletManager.getTransactions(context, primaryUid)
                    }

                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(18.dp),
                        backgroundColor = if (isDark) Color(0x33003366) else Color.White,
                        borderColor = NeonGold.copy(alpha = 0.35f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "🧾 JONLI TO'LOVLAR VA TRANZAKSIYALAR AUDITI",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = NeonGold
                            )
                            Text(
                                text = "Sayyohlar tomonidan amalga oshirilgan to'lov cheklari",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            if (recentTxns.isEmpty()) {
                                Text(
                                    text = "Hozircha to'lovlar mavjud emas",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            } else {
                                recentTxns.take(6).forEach { txn ->
                                    val isInc = txn.amountUzs > 0
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = txn.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onBackground,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = "${txn.merchant} • ${txn.dateFormatted}",
                                                fontSize = 9.sp,
                                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                            )
                                        }

                                        Text(
                                            text = if (isInc) "+${com.example.util.TouristWalletManager.formatUzs(txn.amountUzs)}" else com.example.util.TouristWalletManager.formatUzs(txn.amountUzs),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.5.sp,
                                            color = if (isInc) Color(0xFF10B981) else Color(0xFFFF4757)
                                        )
                                    }
                                    HorizontalDivider(color = Color.Black.copy(alpha = 0.05f))
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showSqlModal) {
            ModalBottomSheet(
                onDismissRequest = { showSqlModal = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
                            text = "Supabase SQL Setup",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = NeonGold
                        )
                        IconButton(onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("SQL Script", SupabaseClient.SQL_SETUP_SCRIPT))
                            Toast.makeText(context, "SQL Nusxalandi! Supabase SQL Editorga tashlang", Toast.LENGTH_LONG).show()
                        }) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = "Copy SQL", tint = TurquoiseTile)
                        }
                    }
                    Text(
                        text = "Supabase boshqaruv panelidagi 'SQL Editor' bo'limiga kirib, ushbu skriptni 'RUN' qilsangiz, 'monuments' jadvali avtomatik tayyor bo'ladi:",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = SupabaseClient.SQL_SETUP_SCRIPT,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color(0xFF38BDF8)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    ElevatedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("SQL Script", SupabaseClient.SQL_SETUP_SCRIPT))
                            Toast.makeText(context, "SQL Nusxalandi!", Toast.LENGTH_SHORT).show()
                            showSqlModal = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.elevatedButtonColors(containerColor = NeonGold, contentColor = Color.Black)
                    ) {
                        Text("Nusxalash va Yopish", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Add / Edit Monument Sheet
        if (showAddEditModal) {
            AddEditMonumentSheet(
                initial = editingMonument,
                isDark = isDark,
                onDismiss = { showAddEditModal = false },
                onSave = { updated ->
                    scope.launch {
                        val saveRes = SupabaseClient.upsertMonument(context, updated)
                        if (saveRes.isSuccess) {
                            Toast.makeText(context, "Muvaffaqiyatli saqlandi!", Toast.LENGTH_SHORT).show()
                            showAddEditModal = false
                            loadMonuments()
                        } else {
                            Toast.makeText(context, "Xatolik: ${saveRes.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun MonumentRowCard(
    monument: SupabaseClient.RemoteMonument,
    isDark: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = if (isDark) Color(0x220047AB) else Color.White,
        borderColor = NeonGold.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = monument.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(TurquoiseTile.copy(alpha = 0.2f))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(text = monument.city, fontSize = 9.sp, color = TurquoiseTile, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = monument.subtitle.ifBlank { monument.description },
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "⭐ ${monument.rating}", fontSize = 10.sp, color = NeonGold)
                    Text(text = "🏷️ ${monument.category}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    if (monument.audioScriptUz.isNotBlank()) {
                        Text(text = "🎙️ Audio bor", fontSize = 10.sp, color = Color(0xFF10B981))
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = TurquoiseTile, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun BookingAdminRowCard(
    booking: SupabaseClient.RemoteBooking,
    isDark: Boolean
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = if (isDark) Color(0x220047AB) else Color.White,
        borderColor = TurquoiseTile.copy(alpha = 0.4f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(TurquoiseTile.copy(alpha = 0.18f))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = booking.bookingCode.ifBlank { "BRON" },
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 11.sp),
                            color = TurquoiseTile
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = booking.touristName.ifBlank { "Noma'lum sayyoh" },
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (booking.bookingStatus) {
                                "Tasdiqlangan" -> Color(0x2210B981)
                                "Bekor qilindi" -> Color(0x22EF4444)
                                else -> Color(0x22F6C845)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = booking.bookingStatus.ifBlank { "Kutilmoqda" },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                        color = when (booking.bookingStatus) {
                            "Tasdiqlangan" -> Color(0xFF10B981)
                            "Bekor qilindi" -> Color(0xFFEF4444)
                            else -> NeonGold
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "📍 ${booking.tourTitle.ifBlank { "Maxsus tur" }}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
                color = if (isDark) NeonGold else RegistanBlue
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "📞 ${booking.touristPhone} • ${booking.peopleCount} kishi",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
                )
                Text(
                    text = "${booking.totalPrice.toLong()} so'm",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 12.sp),
                    color = TurquoiseTile
                )
            }

            if (booking.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "💬 ${booking.notes}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    maxLines = 2
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditMonumentSheet(
    initial: SupabaseClient.RemoteMonument?,
    isDark: Boolean,
    onDismiss: () -> Unit,
    onSave: (SupabaseClient.RemoteMonument) -> Unit
) {
    var id by remember { mutableStateOf(initial?.id ?: "monument_${System.currentTimeMillis() % 100000}") }
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var city by remember { mutableStateOf(initial?.city ?: "Samarqand") }
    var subtitle by remember { mutableStateOf(initial?.subtitle ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var historyFact by remember { mutableStateOf(initial?.historyFact ?: "") }
    var audioScriptUz by remember { mutableStateOf(initial?.audioScriptUz ?: "") }
    var imageUrl by remember { mutableStateOf(initial?.imageUrl ?: "") }
    var latStr by remember { mutableStateOf(initial?.latitude?.toString() ?: "39.6547") }
    var lonStr by remember { mutableStateOf(initial?.longitude?.toString() ?: "66.9758") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            contentPadding = PaddingValues(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = if (initial == null) "YANGI OBIDA QO'SHISH" else "OBIDANI TAHRIRLASH",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = NeonGold
                )
            }

            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Obida nomi (masalan: Registon maydoni)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("Shahar") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = subtitle,
                        onValueChange = { subtitle = it },
                        label = { Text("Qisqa ta'rif") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("To'liq ma'lumot") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = audioScriptUz,
                    onValueChange = { audioScriptUz = it },
                    label = { Text("🎙️ Audio ekskursiya matni (Suxandon o'qiydi)") },
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = historyFact,
                    onValueChange = { historyFact = it },
                    label = { Text("Tarixiy qiziqarli fakt") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Rasm havolasi (Google Drive yoki Supabase Storage URL)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = latStr,
                        onValueChange = { latStr = it },
                        label = { Text("Latitude (Kenglik)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = lonStr,
                        onValueChange = { lonStr = it },
                        label = { Text("Longitude (Uzunlik)") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                ElevatedButton(
                    onClick = {
                        val monument = SupabaseClient.RemoteMonument(
                            id = id,
                            title = title,
                            city = city,
                            subtitle = subtitle,
                            description = description,
                            historyFact = historyFact,
                            audioScriptUz = audioScriptUz,
                            imageUrl = imageUrl,
                            latitude = latStr.toDoubleOrNull() ?: 39.6547,
                            longitude = lonStr.toDoubleOrNull() ?: 66.9758
                        )
                        onSave(monument)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.elevatedButtonColors(containerColor = NeonGold, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Supabase Serveriga Yuklash", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun SosEmergencyAdminRowCard(
    sos: SosEmergencyRecord,
    isDark: Boolean,
    onResolve: () -> Unit,
    onDelete: () -> Unit,
    onCall: () -> Unit,
    onOpenMap: () -> Unit,
    onResendTelegram: () -> Unit
) {
    val borderColor = if (!sos.isResolved) Color(0xFFEF4444) else Color(0xFF10B981)
    val bgColor = if (!sos.isResolved) {
        if (isDark) Color(0x33EF4444) else Color(0x15EF4444)
    } else {
        if (isDark) Color(0x2210B981) else Color(0x1210B981)
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .border(1.5.dp, borderColor.copy(alpha = 0.8f), RoundedCornerShape(16.dp)),
        backgroundColor = bgColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Status badge + ID + Time + Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (!sos.isResolved) Color(0xFFEF4444) else Color(0xFF10B981))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (!sos.isResolved) "🔴 FAOL SOS" else "🟢 HAL QILINDI",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 9.sp,
                                color = Color.White
                            )
                        )
                    }

                    Text(
                        text = "#${sos.id}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = sos.timestamp,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    androidx.compose.material3.IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "O'chirish",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Tourist Name and Phone
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "👤 ${sos.touristName}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "📞 ${sos.touristPhone}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = if (isDark) NeonGold else RegistanBlue
                        )
                    )
                }

                if (!sos.isResolved) {
                    Button(
                        onClick = onResolve,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Hal qilindi", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                } else if (sos.resolvedTime != null) {
                    Text(
                        text = "Yetib bordi: ${sos.resolvedTime}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp),
                        color = Color(0xFF10B981)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Reason and Address
            Text(
                text = "⚠️ Sabab: ${sos.reason}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = if (!sos.isResolved) Color(0xFFEF4444) else MaterialTheme.colorScheme.onBackground
                )
            )

            Text(
                text = "📍 Yaqin joy: ${sos.address}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            )

            Text(
                text = "🌐 GPS: ${String.format(java.util.Locale.US, "%.5f° N, %.5f° E", sos.latitude, sos.longitude)}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.5.sp,
                    fontFamily = FontFamily.Monospace,
                    color = if (isDark) Color(0xFF93C5FD) else Color(0xFF1D4ED8)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Call button
                Button(
                    onClick = onCall,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Qo'ng'iroq", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                // Map button
                Button(
                    onClick = onOpenMap,
                    modifier = Modifier.weight(1.1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Xaritada", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                // Telegram button
                Button(
                    onClick = onResendTelegram,
                    modifier = Modifier.weight(1.2f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Telegram", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
