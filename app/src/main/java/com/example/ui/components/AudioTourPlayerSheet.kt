package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppLanguage
import com.example.model.AudioTourItem
import com.example.model.AudioTourRepository
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.SilkGold
import com.example.ui.theme.TurquoiseTile
import com.example.util.AmbientSoundSynthesizer
import com.example.util.ArAudioNarratorManager
import com.example.util.StudioEqualizerPreset
import com.example.util.VoicePersona

@Composable
fun AudioTourPlayerSheet(
    initialTour: AudioTourItem = AudioTourRepository.tourList.first(),
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    var selectedTour by remember { mutableStateOf(initialTour) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentProgress by remember { mutableFloatStateOf(0f) }
    var playbackSpeed by remember { mutableFloatStateOf(0.88f) }
    var isAmbientEnabled by remember { mutableStateOf(true) }
    var showVoiceSettingsDialog by remember { mutableStateOf(false) }

    val audioNarrator = remember { ArAudioNarratorManager(context) }
    var activePersona by remember { mutableStateOf(audioNarrator.currentPersona) }
    var activePreset by remember { mutableStateOf(audioNarrator.currentPreset) }

    DisposableEffect(Unit) {
        onDispose {
            audioNarrator.stop()
            audioNarrator.shutdown()
            AmbientSoundSynthesizer.stop()
        }
    }

    fun playCurrentTour() {
        isPlaying = true
        audioNarrator.currentPersona = activePersona
        audioNarrator.currentPreset = activePreset
        audioNarrator.isAmbientMusicEnabled = isAmbientEnabled
        AmbientSoundSynthesizer.isEnabled = isAmbientEnabled
        val speechText = "${selectedTour.title}. ${selectedTour.city}. ${selectedTour.transcript}. Tarixiy sir: ${selectedTour.historicalFact}"
        audioNarrator.speak(
            text = speechText,
            lang = AppLanguage.currentLanguage,
            speechRate = playbackSpeed,
            pitch = audioNarrator.customPitch ?: activePersona.pitch
        ) { speaking, progress ->
            isPlaying = speaking
            currentProgress = progress
        }
    }

    LaunchedEffect(selectedTour) {
        currentProgress = 0f
        playCurrentTour()
    }

    Dialog(
        onDismissRequest = {
            audioNarrator.stop()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF0A162C) else Color.White
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
                                    imageVector = Icons.Filled.Headphones,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "MILLIY JONLI AUDIO GID",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    ),
                                    color = if (isDark) NeonGold else SilkGold
                                )
                                Text(
                                    text = "🔊 Maksimal kuchaytirilgan tiniq ovoz",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = TurquoiseTile
                                )
                            }
                        }

                        IconButton(onClick = {
                            audioNarrator.stop()
                            onDismiss()
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "Yopish")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tour Selector Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(AudioTourRepository.tourList) { tour ->
                            val isSel = tour.id == selectedTour.id
                            val itemBgModifier = if (isSel) {
                                Modifier.background(Brush.horizontalGradient(listOf(RegistanBlue, Color(0xFF0D47A1))))
                            } else {
                                Modifier.background(if (isDark) Color(0x330047AB) else Color(0xFFF0F4FC))
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .then(itemBgModifier)
                                    .border(
                                        1.dp,
                                        if (isSel) NeonGold else Color(0x33888888),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        selectedTour = tour
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = tour.monumentName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSel) FontWeight.Black else FontWeight.Medium,
                                        fontSize = 11.sp
                                    ),
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Voice Narrator Persona Row
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "🎙️ DIKTOR:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.sp,
                                        letterSpacing = 0.6.sp
                                    ),
                                    color = NeonGold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = activePersona.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = TurquoiseTile
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x2A00E676))
                                    .border(1.dp, Color(0xFF00E676).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                    .clickable { showVoiceSettingsDialog = true }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Tune,
                                        contentDescription = null,
                                        tint = Color(0xFF00E676),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Ovozni Sozlash",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF00E676)
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(VoicePersona.entries) { persona ->
                                val isSelected = persona == activePersona
                                val personaBg = if (isSelected) {
                                    Modifier.background(Brush.horizontalGradient(listOf(RegistanBlue, Color(0xFF0D47A1))))
                                } else {
                                    Modifier.background(if (isDark) Color(0x330047AB) else Color(0xFFF1F5F9))
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .then(personaBg)
                                        .border(
                                            1.dp,
                                            if (isSelected) NeonGold else Color(0x22888888),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            activePersona = persona
                                            audioNarrator.currentPersona = persona
                                            audioNarrator.customPitch = persona.pitch * activePreset.pitchMultiplier
                                            audioNarrator.customSpeed = persona.speechRate * activePreset.speedMultiplier
                                            if (isPlaying) {
                                                playCurrentTour()
                                            }
                                        }
                                        .padding(horizontal = 8.dp, vertical = 5.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = persona.iconEmoji, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = persona.title.split(" ").first(),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                                            ),
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Quick Studio Equalizer Presets Row
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(StudioEqualizerPreset.entries) { preset ->
                                val isSelected = preset == activePreset
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) NeonGold else Color(0x22888888))
                                        .border(
                                            1.dp,
                                            if (isSelected) Color.Black else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            activePreset = preset
                                            audioNarrator.currentPreset = preset
                                            audioNarrator.customPitch = activePersona.pitch * preset.pitchMultiplier
                                            audioNarrator.customSpeed = activePersona.speechRate * preset.speedMultiplier
                                            if (isPlaying) {
                                                playCurrentTour()
                                            }
                                        }
                                        .padding(horizontal = 7.dp, vertical = 3.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = preset.iconEmoji, fontSize = 10.sp)
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = preset.title,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal
                                            ),
                                            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Main Playing Card with Waveform
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.verticalGradient(
                                    if (isDark) listOf(Color(0xFF142445), Color(0xFF091428))
                                    else listOf(Color(0xFFEBF3FF), Color(0xFFD8E7FA))
                                )
                            )
                            .border(1.dp, NeonGold.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = selectedTour.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 15.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = "${selectedTour.narratorName} • ${selectedTour.city}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = TurquoiseTile
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0x33D4AF37))
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "100% VOLUME 🔊",
                                        color = NeonGold,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Waveform Visualizer
                            AudioWaveformVisualizer(isPlaying = isPlaying)

                            Spacer(modifier = Modifier.height(8.dp))

                            // Progress Slider
                            Slider(
                                value = currentProgress,
                                onValueChange = { currentProgress = it },
                                colors = SliderDefaults.colors(
                                    thumbColor = NeonGold,
                                    activeTrackColor = NeonGold,
                                    inactiveTrackColor = Color(0x33888888)
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val currentSec = (selectedTour.durationSeconds * currentProgress).toInt()
                                Text(
                                    text = String.format("%02d:%02d", currentSec / 60, currentSec % 60),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = String.format("%02d:%02d", selectedTour.durationSeconds / 60, selectedTour.durationSeconds % 60),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                )
                            }

                            // Controls Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Speed button
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isDark) Color(0x33FFFFFF) else Color.White)
                                        .clickable {
                                            playbackSpeed = if (playbackSpeed == 0.95f) 1.15f else 0.95f
                                            if (isPlaying) playCurrentTour()
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${playbackSpeed}x",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        color = TurquoiseTile
                                    )
                                }

                                // -10s
                                IconButton(onClick = { currentProgress = (currentProgress - 0.05f).coerceAtLeast(0f) }) {
                                    Icon(Icons.Filled.Replay10, contentDescription = "-10s", tint = NeonGold)
                                }

                                // Play / Pause
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Brush.horizontalGradient(listOf(NeonGold, SilkGold)))
                                        .clickable {
                                            if (isPlaying) {
                                                audioNarrator.stop()
                                                isPlaying = false
                                            } else {
                                                playCurrentTour()
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                        contentDescription = "Play/Pause",
                                        tint = Color.Black,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                // +10s
                                IconButton(onClick = { currentProgress = (currentProgress + 0.05f).coerceAtMost(1f) }) {
                                    Icon(Icons.Filled.Forward10, contentDescription = "+10s", tint = NeonGold)
                                }

                                // Ambient Sound Mode (Sharqona Dutor & Nay Synthesizer)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isAmbientEnabled) NeonGold.copy(alpha = 0.2f) else Color.Transparent)
                                        .border(1.dp, if (isAmbientEnabled) NeonGold else Color(0x33888888), RoundedCornerShape(8.dp))
                                        .clickable {
                                            isAmbientEnabled = !isAmbientEnabled
                                            audioNarrator.isAmbientMusicEnabled = isAmbientEnabled
                                            AmbientSoundSynthesizer.isEnabled = isAmbientEnabled
                                            if (!isAmbientEnabled) {
                                                AmbientSoundSynthesizer.stop()
                                            } else if (isPlaying) {
                                                AmbientSoundSynthesizer.start()
                                            }
                                        }
                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.MusicNote,
                                            contentDescription = null,
                                            tint = if (isAmbientEnabled) NeonGold else Color.Gray,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = if (isAmbientEnabled) "Dutor & Nay" else "Fonsiz",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = if (isAmbientEnabled) NeonGold else Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Transcript / History Excerpt
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isDark) Color(0x330047AB) else Color(0xFFF6F9FD))
                            .padding(10.dp)
                    ) {
                        LazyColumn {
                            item {
                                Text(
                                    text = "📜 MATN & TARIXIY DALIL:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.sp,
                                        color = NeonGold
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = selectedTour.transcript,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "💡 Tarixiy sir: ${selectedTour.historicalFact}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TurquoiseTile
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showVoiceSettingsDialog) {
            AudioVoiceSettingsDialog(
                narratorManager = audioNarrator,
                onDismiss = {
                    showVoiceSettingsDialog = false
                    activePersona = audioNarrator.currentPersona
                    activePreset = audioNarrator.currentPreset
                    isAmbientEnabled = audioNarrator.isAmbientMusicEnabled
                    if (isPlaying) {
                        playCurrentTour()
                    }
                },
                isDark = isDark
            )
        }
    }
}

@Composable
fun AudioWaveformVisualizer(isPlaying: Boolean) {
    val heights = listOf(12, 22, 16, 32, 24, 38, 20, 36, 28, 42, 30, 34, 18, 28, 22, 36, 14, 26, 32, 18)
    val infiniteTransition = rememberInfiniteTransition(label = "WaveformAnim")

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            heights.forEachIndexed { index, baseHeight ->
                val animatedHeight by infiniteTransition.animateFloat(
                    initialValue = if (isPlaying) (baseHeight * 0.35f).coerceAtLeast(6f) else 6f,
                    targetValue = if (isPlaying) baseHeight.toFloat() else 6f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 240 + (index * 35), easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "WaveHeight_$index"
                )

                Box(
                    modifier = Modifier
                        .width(4.5.dp)
                        .height(animatedHeight.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.verticalGradient(
                                if (isPlaying) listOf(NeonGold, TurquoiseTile, SilkGold)
                                else listOf(Color(0x55888888), Color(0x33888888))
                            )
                        )
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("BASS 60Hz", fontSize = 8.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text("VOCAL 1.2kHz", fontSize = 8.sp, color = if (isPlaying) TurquoiseTile else Color.Gray, fontWeight = FontWeight.Bold)
            Text("TREBLE 8kHz", fontSize = 8.sp, color = if (isPlaying) NeonGold else Color.Gray, fontWeight = FontWeight.Bold)
            Text("44.1kHz • HI-RES", fontSize = 8.sp, color = if (isPlaying) Color(0xFF00E676) else Color.Gray, fontWeight = FontWeight.Black)
        }
    }
}
