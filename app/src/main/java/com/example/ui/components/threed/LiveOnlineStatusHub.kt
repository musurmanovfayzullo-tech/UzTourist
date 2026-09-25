package com.example.ui.components.threed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.SilkGold
import com.example.ui.theme.TurquoiseTile
import kotlinx.coroutines.delay

data class LiveMonumentTelemetry(
    val name: String,
    val onlineTourists: Int,
    val weatherTemp: String,
    val weatherDesc: String,
    val expressTrainStatus: String
)

@Composable
fun LiveOnlineStatusHub(
    modifier: Modifier = Modifier,
    onOpenLiveDetails: (() -> Unit)? = null
) {
    val isDark = isSystemInDarkTheme()
    var pingMs by remember { mutableIntStateOf(68) }
    var isExpanded by remember { mutableStateOf(false) }

    // Real-time live fluctuation effect
    LaunchedEffect(Unit) {
        while (true) {
            delay(3500)
            pingMs = (58..92).random()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "LivePulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
    )

    val liveData = remember {
        listOf(
            LiveMonumentTelemetry("🏛️ Registon", 348, "+27°C", "Quyoshli", "Afrosiyob: Vaqtida (08:00)"),
            LiveMonumentTelemetry("🗼 Buxoro Kalon", 215, "+29°C", "Musaffo", "Afrosiyob: Vaqtida (11:20)"),
            LiveMonumentTelemetry("🏰 Xiva Ichan Qal'a", 164, "+31°C", "Ochiq havo", "Urganch Ekspress: Aktiv"),
            LiveMonumentTelemetry("🕌 Toshkent Chorsu", 412, "+26°C", "Yengil shabada", "Metro: Har 3 daqiqada")
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (isDark) Brush.horizontalGradient(
                    listOf(Color(0xFF031A38), Color(0xFF022654), Color(0xFF00152F))
                ) else Brush.horizontalGradient(
                    listOf(Color(0xFFE3F2FD), Color(0xFFE8F5E9), Color(0xFFE1F5FE))
                )
            )
            .border(
                1.dp,
                if (isDark) Brush.horizontalGradient(listOf(Color(0xFF00E676), TurquoiseTile, NeonGold))
                else Brush.horizontalGradient(listOf(Color(0xFF00C853), Color(0xFF00B0FF))),
                RoundedCornerShape(18.dp)
            )
            .clickable { isExpanded = !isExpanded }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Column {
            // Main Live Bar Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Pulsating Green Radar Signal
                    Box(
                        modifier = Modifier.size(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(Color(0x5500E676))
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E676))
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "100% ONLAYN JONLI REJIM",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.8.sp
                                ),
                                color = if (isDark) Color(0xFF69F0AE) else Color(0xFF007E33)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0x3300E676))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "$pingMs ms",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = if (isDark) Color(0xFFB9F6CA) else Color(0xFF004D40)
                                )
                            }
                        }
                        Text(
                            text = "Toshkent Bulut Serveri & GPS Dispetcher sinxronlashgan",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.5.sp
                            ),
                            color = if (isDark) Color(0xFF90CAF9) else Color(0xFF37474F)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isDark) Color(0x330047AB) else Color(0xFFBBDEFB))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Sensors,
                            contentDescription = "Live Telemetry",
                            tint = if (isDark) NeonGold else RegistanBlue,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isExpanded) "Yopish" else "Jonli Oqim",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isDark) NeonGold else RegistanBlue
                        )
                    }
                }
            }

            // Expanded Real-Time Tourist & Weather Telemetry Row
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(liveData) { item ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isDark) Color(0x6600173D) else Color.White)
                                    .border(
                                        0.8.dp,
                                        if (isDark) TurquoiseTile.copy(alpha = 0.4f) else Color(0xFFCFD8DC),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Column {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        color = if (isDark) Color.White else RegistanBlue
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.PeopleAlt,
                                            contentDescription = null,
                                            tint = Color(0xFF00E676),
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "${item.onlineTourists} sayyoh onlayn",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = if (isDark) Color(0xFF81C784) else Color(0xFF2E7D32)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.WbSunny,
                                            contentDescription = null,
                                            tint = NeonGold,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "${item.weatherTemp} • ${item.weatherDesc}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp
                                            ),
                                            color = if (isDark) Color(0xFFE0E0E0) else Color(0xFF455A64)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.DirectionsTransit,
                                            contentDescription = null,
                                            tint = TurquoiseTile,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = item.expressTrainStatus,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = TurquoiseTile
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
