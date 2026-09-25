package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TouristGroupMember
import com.example.model.WhisperAudioPreset
import com.example.model.WhisperUserRole
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.SilkGold
import com.example.ui.theme.TurquoiseTile
import com.example.util.TourGuideWhisperManager

@Composable
fun TourGuideWhisperView(
    modifier: Modifier = Modifier,
    touristName: String = "VIP Sayyoh",
    assignedNumber: String = "7788"
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        TourGuideWhisperManager.initPreferences(context)
        if (assignedNumber.isNotBlank()) {
            TourGuideWhisperManager.channelCode = "UZ-WHISPER-$assignedNumber"
        }
    }

    val currentRole = TourGuideWhisperManager.userRole
    val isBroadcasting = TourGuideWhisperManager.isBroadcasting
    val isListening = TourGuideWhisperManager.isListening
    val isMuted = TourGuideWhisperManager.isMuted
    val channelCode = TourGuideWhisperManager.channelCode
    val volumeGain = TourGuideWhisperManager.volumeGain
    val currentPreset = TourGuideWhisperManager.selectedPreset
    val waveform = TourGuideWhisperManager.waveformAmplitudes
    val liveDb = TourGuideWhisperManager.liveDecibels
    val members = TourGuideWhisperManager.groupMembers
    val isChimeRinging = TourGuideWhisperManager.isChimeActive
    val isTtsPlaying = TourGuideWhisperManager.isTtsPlaying

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Channel Badge & Share Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF071E43)),
                border = androidx.compose.foundation.BorderStroke(1.dp, TurquoiseTile.copy(alpha = 0.5f))
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF003893)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Podcasts,
                                    contentDescription = null,
                                    tint = NeonGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "GID OVOZLI EFIRI (WHISPER)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = NeonGold
                                )
                                Text(
                                    text = "Kanal: $channelCode",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Copy & Share Channel Action
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = {
                                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    cm.setPrimaryClip(ClipData.newPlainText("Whisper Channel", channelCode))
                                    Toast.makeText(context, "Kanal kodi nusxalandi: $channelCode", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x22FFFFFF))
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = TurquoiseTile,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    val sendIntent = android.content.Intent().apply {
                                        action = android.content.Intent.ACTION_SEND
                                        putExtra(android.content.Intent.EXTRA_TEXT, "🎙️ Samarqand Gid Ovozli Efiriga ulaning: $channelCode")
                                        type = "text/plain"
                                    }
                                    context.startActivity(android.content.Intent.createChooser(sendIntent, "Ulashish"))
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x22FFFFFF))
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Share,
                                    contentDescription = "Share",
                                    tint = NeonGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Status Bar: Connection Info & Earphones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isBroadcasting) Color(0xFFFF1744)
                                        else if (isListening) Color(0xFF00E676)
                                        else Color(0xFF9E9E9E)
                                    )
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = when {
                                    isBroadcasting -> "🔴 JONLI EFIR UZATILMOQDA"
                                    isListening -> "🟢 TINGLANMOQDA (${members.size} kishi)"
                                    else -> "⚪ KUTISH REJIMI"
                                },
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isBroadcasting) Color(0xFFFF8A80) else if (isListening) Color(0xFFB9F6CA) else Color.LightGray
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Headphones,
                                contentDescription = null,
                                tint = TurquoiseTile,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Quloqchin faol",
                                fontSize = 10.sp,
                                color = TurquoiseTile
                            )
                        }
                    }
                }
            }
        }

        // 2. Role Selector (Gid vs Sayyoh)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF030D1E))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(14.dp))
                    .padding(4.dp)
            ) {
                // Gid Broadcaster Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .then(
                            if (currentRole == WhisperUserRole.GUIDE_BROADCASTER)
                                Modifier.background(Brush.horizontalGradient(listOf(Color(0xFF0052D4), Color(0xFF4364F7))))
                            else Modifier.background(Color.Transparent)
                        )
                        .clickable {
                            TourGuideWhisperManager.userRole = WhisperUserRole.GUIDE_BROADCASTER
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎙️ Gid (Efir Uzatish)",
                        fontSize = 11.5.sp,
                        fontWeight = if (currentRole == WhisperUserRole.GUIDE_BROADCASTER) FontWeight.Black else FontWeight.Medium,
                        color = if (currentRole == WhisperUserRole.GUIDE_BROADCASTER) Color.White else Color.Gray
                    )
                }

                // Tourist Listener Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .then(
                            if (currentRole == WhisperUserRole.TOURIST_LISTENER)
                                Modifier.background(Brush.horizontalGradient(listOf(Color(0xFF00B4D8), Color(0xFF0077B6))))
                            else Modifier.background(Color.Transparent)
                        )
                        .clickable {
                            TourGuideWhisperManager.userRole = WhisperUserRole.TOURIST_LISTENER
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎧 Sayyoh (Tinglash)",
                        fontSize = 11.5.sp,
                        fontWeight = if (currentRole == WhisperUserRole.TOURIST_LISTENER) FontWeight.Black else FontWeight.Medium,
                        color = if (currentRole == WhisperUserRole.TOURIST_LISTENER) Color.White else Color.Gray
                    )
                }
            }
        }

        // 3. Dynamic Waveform & Audio Meter Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF081938)),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonGold.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "JONLI AUDIO SPEKTR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SilkGold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x3300E5FF))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "⚡ $liveDb dB • ${if (liveDb > 70) "Baland" else "Optimal"}",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TurquoiseTile
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Animated Visualizer Waveform Bars
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x55030B18))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        waveform.forEach { amp ->
                            val barHeight = (amp * 48).coerceIn(4f, 48f).dp
                            Box(
                                modifier = Modifier
                                    .width(6.dp)
                                    .height(barHeight)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(NeonGold, TurquoiseTile)
                                        )
                                    )
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Real-time Subtitle
                    Text(
                        text = "💬 \"${TourGuideWhisperManager.liveSubtitle}\"",
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = Color(0xFFE2E8F0),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                }
            }
        }

        // 4. Equalizer Presets
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Akustika va Shovqinni Bostirish Rejimlari:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(WhisperAudioPreset.values()) { preset ->
                        val isSelected = currentPreset == preset
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xDD0B3B60) else Color(0x330B3B60))
                                .border(
                                    1.dp,
                                    if (isSelected) NeonGold else Color(0x22FFFFFF),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    TourGuideWhisperManager.selectPreset(context, preset)
                                }
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = preset.iconEmoji, fontSize = 12.sp)
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = preset.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                        color = if (isSelected) NeonGold else Color.White
                                    )
                                }
                                Text(
                                    text = "Shovqin filtri: -${preset.noiseReductionPercent}% • ${preset.latencyMs}ms",
                                    fontSize = 9.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Proximity Distance Radar (Sayyohlar Masofasi)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF04122B)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x3300E5FF))
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Radar,
                                contentDescription = null,
                                tint = TurquoiseTile,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Guruh Masofasi Radari",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "${members.size} sayyoh guruhda",
                            fontSize = 10.5.sp,
                            color = NeonGold
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // Group Member Rows
                    members.forEach { mem ->
                        val isTooFar = mem.distanceMeters > 30
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isTooFar) Color(0x33FF1744) else Color(0x22FFFFFF))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = mem.flagEmoji, fontSize = 14.sp)
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = mem.name,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        if (mem.isHandRaised) {
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                text = "✋ Savol",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFFFFD600)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${mem.country} • ${if (mem.isEarphoneConnected) "🎧 Quloqchinda" else "🔊 Dinamik"}",
                                        fontSize = 9.sp,
                                        color = Color.LightGray
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${mem.distanceMeters} m",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isTooFar) Color(0xFFFF5252) else Color(0xFF69F0AE)
                                )
                                if (isTooFar) {
                                    Spacer(Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Filled.Warning,
                                        contentDescription = "Uzoq",
                                        tint = Color(0xFFFF5252),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Stray Warning Banner if distance > 30m
                    if (members.any { it.distanceMeters > 30 }) {
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x33FF5252))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                tint = Color(0xFFFF8A80),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Diqqat: Ayrim sayyohlar guruhdan 30 metrdan ortiq masofaga uzoqlashdi!",
                                fontSize = 10.sp,
                                color = Color(0xFFFFCDD2),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // 6. Volume Slider with Super Boost (100% - 250%)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF06152F)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF))
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.VolumeUp,
                                contentDescription = null,
                                tint = NeonGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Quloqchin Ovoz Kuchaytirgichi (Super Boost)",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "${(volumeGain * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonGold
                        )
                    }

                    Slider(
                        value = volumeGain,
                        onValueChange = { TourGuideWhisperManager.setVolume(context, it) },
                        valueRange = 0.5f..2.5f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonGold,
                            activeTrackColor = TurquoiseTile,
                            inactiveTrackColor = Color(0x33FFFFFF)
                        )
                    )
                }
            }
        }

        // 7. Interactive Action Control Row (Broadcaster vs Listener)
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (currentRole == WhisperUserRole.GUIDE_BROADCASTER) {
                    // Guide Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Main Broadcast Toggle
                        Button(
                            onClick = {
                                if (isBroadcasting) TourGuideWhisperManager.stopBroadcasting(context)
                                else TourGuideWhisperManager.startBroadcasting(context)
                            },
                            modifier = Modifier
                                .weight(1.3f)
                                .height(48.dp)
                                .then(if (isBroadcasting) Modifier.scale(pulseScale) else Modifier),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isBroadcasting) Color(0xFFD32F2F) else Color(0xFF0077B6)
                            )
                        ) {
                            Icon(
                                imageVector = if (isBroadcasting) Icons.Filled.Stop else Icons.Filled.Mic,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (isBroadcasting) "Efirni Yakunlash" else "Efirni Boshlash",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        // Chime Bell Button (Diqqat signali)
                        Button(
                            onClick = {
                                TourGuideWhisperManager.triggerChime(context, notifyTelegram = true)
                                Toast.makeText(context, "🔔 Barcha sayyohlarga diqqat signali yuborildi!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E2723))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.NotificationsActive,
                                contentDescription = null,
                                tint = NeonGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Diqqat (Chime)",
                                color = NeonGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Mute and TTS Preview Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { TourGuideWhisperManager.toggleMute() },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isMuted) Color(0xFFB71C1C) else Color(0x33FFFFFF)
                            )
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.Filled.MicOff else Icons.Filled.Mic,
                                contentDescription = null,
                                tint = if (isMuted) Color.White else TurquoiseTile,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (isMuted) "Mute Faol" else "Mute",
                                fontSize = 11.sp,
                                color = if (isMuted) Color.White else Color.White
                            )
                        }

                        Button(
                            onClick = {
                                if (isTtsPlaying) TourGuideWhisperManager.stopNarration()
                                else TourGuideWhisperManager.playSampleGuideNarration(context, "uz")
                            },
                            modifier = Modifier
                                .weight(1.2f)
                                .height(40.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x4400E5FF))
                        ) {
                            Icon(
                                imageVector = if (isTtsPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = TurquoiseTile,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (isTtsPlaying) "To'xtatish" else "Test Hikoyasi",
                                fontSize = 11.sp,
                                color = TurquoiseTile,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // Tourist Listener Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Connect / Disconnect Button
                        Button(
                            onClick = {
                                if (isListening) TourGuideWhisperManager.leaveChannel(context)
                                else TourGuideWhisperManager.joinChannel(context, channelCode)
                            },
                            modifier = Modifier
                                .weight(1.3f)
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isListening) Color(0xFF00897B) else Color(0xFF0277BD)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Headphones,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (isListening) "Ulanish Faol" else "Gidga Ulanish",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        // Raise Hand Button (Savol berish)
                        Button(
                            onClick = {
                                TourGuideWhisperManager.raiseHand(context, touristName)
                                Toast.makeText(context, "✋ Gidga savol berish signali yuborildi!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D4037))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PanTool,
                                contentDescription = null,
                                tint = NeonGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Savol Berish",
                                color = NeonGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Test Real Audio Narration
                    Button(
                        onClick = {
                            if (isTtsPlaying) TourGuideWhisperManager.stopNarration()
                            else TourGuideWhisperManager.playSampleGuideNarration(context, "uz")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x4400E5FF))
                    ) {
                        Icon(
                            imageVector = if (isTtsPlaying) Icons.Filled.Stop else Icons.Filled.VolumeUp,
                            contentDescription = null,
                            tint = TurquoiseTile,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (isTtsPlaying) "Ovozni To'xtatish" else "🔊 Jonli Gid Hikoyasini Tinglash (Test Audio)",
                            fontSize = 11.5.sp,
                            color = TurquoiseTile,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(18.dp))
        }
    }
}
