package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.SilkGold
import com.example.ui.theme.TurquoiseTile
import com.example.util.AmbientSoundSynthesizer
import com.example.util.ArAudioNarratorManager
import com.example.util.StudioEqualizerPreset
import com.example.util.VoicePersona

@Composable
fun AudioVoiceSettingsDialog(
    narratorManager: ArAudioNarratorManager,
    onDismiss: () -> Unit,
    isDark: Boolean = true
) {
    val context = LocalContext.current
    var selectedPersona by remember { mutableStateOf(narratorManager.currentPersona) }
    var selectedPreset by remember { mutableStateOf(narratorManager.currentPreset) }
    var selectedAmbienceStyle by remember { mutableStateOf(AmbientSoundSynthesizer.currentStyle) }
    var currentPitch by remember { mutableFloatStateOf(narratorManager.customPitch ?: narratorManager.currentPersona.pitch) }
    var currentSpeed by remember { mutableFloatStateOf(narratorManager.customSpeed ?: narratorManager.currentPersona.speechRate) }
    var isAmbientEnabled by remember { mutableStateOf(narratorManager.isAmbientMusicEnabled) }
    var ambientVolume by remember { mutableFloatStateOf(AmbientSoundSynthesizer.volumeLevel) }
    var isTestingVoice by remember { mutableStateOf(false) }

    val sampleSpeechText = "Registon maydoni Samarqand shahrining yuragidir. XIV-XV asrlarda Amir Temur va Mirzo Ulug'bek davrida barpo etilgan bu me'moriy mo'jiza butun jahonga mashhur."

    Dialog(
        onDismissRequest = {
            narratorManager.stop()
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
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(26.dp)),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF091428) else Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
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
                                    .background(Brush.horizontalGradient(listOf(NeonGold, SilkGold))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.GraphicEq,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "YAGONA STANDART OVOZ",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        letterSpacing = 0.8.sp
                                    ),
                                    color = if (isDark) NeonGold else SilkGold
                                )
                                Text(
                                    text = "🎙️ Salobatli Davlat Suxandoni (Chuqur Bariton)",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = TurquoiseTile
                                )
                            }
                        }

                        IconButton(onClick = {
                            narratorManager.stop()
                            onDismiss()
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "Yopish")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 1. Google Neural Voice Status Card
                        item {
                            val isGoogle = narratorManager.isGoogleEngineAvailable
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isGoogle) Color(0x2200E676) else Color(0x22FFA000))
                                    .border(
                                        1.dp,
                                        if (isGoogle) Color(0xFF00E676).copy(alpha = 0.5f) else Color(0xFFFFA000).copy(alpha = 0.5f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (isGoogle) "✅ Google Neural Audio: Faol" else "⚠️ Google Speech Tavsiya Etiladi",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                ),
                                                color = if (isGoogle) Color(0xFF00E676) else Color(0xFFFFB300)
                                            )
                                        }
                                        Text(
                                            text = if (isGoogle) "Yuqori tiniqlikdagi WaveNet fonetik sintezi ulandi" else "Eng tiniq talaffuz uchun Google Nutq Xizmati kerak",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                        )
                                    }

                                    if (!isGoogle) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFFFA000))
                                                .clickable { narratorManager.openGoogleTtsPlayStore() }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Filled.Download, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text("O'rnatish", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Studio Sound Presets
                        item {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "STUDIYA EKVALAYZERI (PRESETLAR):",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.sp,
                                        letterSpacing = 0.6.sp
                                    ),
                                    color = NeonGold
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(StudioEqualizerPreset.entries) { preset ->
                                        val isPresetSelected = selectedPreset == preset
                                        val bg = if (isPresetSelected) {
                                            Modifier.background(Brush.horizontalGradient(listOf(RegistanBlue, Color(0xFF0D47A1))))
                                        } else {
                                            Modifier.background(if (isDark) Color(0xFF13203C) else Color(0xFFF1F5F9))
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .then(bg)
                                                .border(
                                                    1.dp,
                                                    if (isPresetSelected) NeonGold else Color(0x22888888),
                                                    RoundedCornerShape(10.dp)
                                                )
                                                .clickable {
                                                    selectedPreset = preset
                                                    narratorManager.currentPreset = preset
                                                    currentPitch = selectedPersona.pitch * preset.pitchMultiplier
                                                    currentSpeed = selectedPersona.speechRate * preset.speedMultiplier
                                                    narratorManager.customPitch = currentPitch
                                                    narratorManager.customSpeed = currentSpeed
                                                    if (isTestingVoice) {
                                                        narratorManager.stop()
                                                        isTestingVoice = false
                                                    }
                                                }
                                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(preset.iconEmoji, fontSize = 13.sp)
                                                Spacer(modifier = Modifier.width(5.dp))
                                                Column {
                                                    Text(
                                                        text = preset.title,
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            fontSize = 10.sp,
                                                            fontWeight = if (isPresetSelected) FontWeight.Black else FontWeight.Medium
                                                        ),
                                                        color = if (isPresetSelected) Color.White else MaterialTheme.colorScheme.onBackground
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 3. The Single Standard Solemn Voice Card
                        item {
                            Text(
                                text = "ILOVA STANDART OVOZI:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.6.sp
                                ),
                                color = NeonGold
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Brush.horizontalGradient(listOf(RegistanBlue, Color(0xFF072147))))
                                    .border(1.5.dp, NeonGold, RoundedCornerShape(16.dp))
                                    .padding(14.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = "🎙️", fontSize = 26.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = "Salobatli Davlat Suxandoni",
                                                        style = MaterialTheme.typography.titleSmall.copy(
                                                            fontWeight = FontWeight.Black,
                                                            fontSize = 14.sp
                                                        ),
                                                        color = Color.White
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(NeonGold)
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = "STANDART",
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                fontSize = 9.sp,
                                                                fontWeight = FontWeight.Black
                                                            ),
                                                            color = Color.Black
                                                        )
                                                    }
                                                }
                                                Text(
                                                    text = "Chuqur vazmin bariton, nufuzli hujjatli film diktsiyasi",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                    color = Color.White.copy(alpha = 0.85f)
                                                )
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(NeonGold),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    // Acoustic tuning badges
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf(
                                            "🏛️ Hujjatli film uslubi",
                                            "🔊 Chuqur bariton",
                                            "⚡ Vazmin sur'at (0.88x)"
                                        ).forEach { badge ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(0x33000000))
                                                    .border(0.8.dp, SilkGold.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                                            ) {
                                                Text(
                                                    text = badge,
                                                    fontSize = 9.5.sp,
                                                    color = SilkGold,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Test Voice Button
                        item {
                            Button(
                                onClick = {
                                    if (isTestingVoice) {
                                        narratorManager.stop()
                                        isTestingVoice = false
                                    } else {
                                        isTestingVoice = true
                                        narratorManager.currentPersona = selectedPersona
                                        narratorManager.currentPreset = selectedPreset
                                        narratorManager.customPitch = currentPitch
                                        narratorManager.customSpeed = currentSpeed
                                        narratorManager.isAmbientMusicEnabled = isAmbientEnabled
                                        narratorManager.speak(
                                            text = sampleSpeechText,
                                            speechRate = currentSpeed,
                                            pitch = currentPitch
                                        ) { speaking, _ ->
                                            isTestingVoice = speaking
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isTestingVoice) Color(0xFFE53935) else TurquoiseTile
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = if (isTestingVoice) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isTestingVoice) "Ovozni To'xtatish" else "▶ Salobatli Ovozni Sinab Ko'rish",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Reset to Solemn Standard Button
                        item {
                            TextButton(
                                onClick = {
                                    selectedPersona = VoicePersona.PROFESSIONAL_MALE
                                    selectedPreset = StudioEqualizerPreset.STUDIO_BARITONE
                                    currentPitch = 0.80f
                                    currentSpeed = 0.88f
                                    narratorManager.currentPersona = VoicePersona.PROFESSIONAL_MALE
                                    narratorManager.currentPreset = StudioEqualizerPreset.STUDIO_BARITONE
                                    narratorManager.customPitch = 0.80f
                                    narratorManager.customSpeed = 0.88f
                                    if (isTestingVoice) {
                                        narratorManager.stop()
                                        isTestingVoice = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Tune,
                                    contentDescription = null,
                                    tint = NeonGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Standart Salobatli Sozlamaga Qaytarish (0.80x / 0.88x)",
                                    fontSize = 10.5.sp,
                                    color = NeonGold,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Fine tuning: Pitch Slider
                        item {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Tembr Balandligi (Pitch)",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = String.format("%.2fx", currentPitch),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = NeonGold
                                        )
                                    )
                                }
                                Slider(
                                    value = currentPitch,
                                    onValueChange = {
                                        currentPitch = it
                                        narratorManager.customPitch = it
                                    },
                                    valueRange = 0.70f..1.35f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = NeonGold,
                                        activeTrackColor = NeonGold
                                    )
                                )
                            }
                        }

                        // Fine tuning: Speed Slider
                        item {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "O'qish Tezligi (Speed)",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = String.format("%.2fx", currentSpeed),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = TurquoiseTile
                                        )
                                    )
                                }
                                Slider(
                                    value = currentSpeed,
                                    onValueChange = {
                                        currentSpeed = it
                                        narratorManager.customSpeed = it
                                    },
                                    valueRange = 0.75f..1.25f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = TurquoiseTile,
                                        activeTrackColor = TurquoiseTile
                                    )
                                )
                            }
                        }

                        // Ambient Music Setting (Dutor, Nay, Caravan)
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isDark) Color(0xFF13203C) else Color(0xFFF1F5F9))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Filled.MusicNote,
                                                contentDescription = null,
                                                tint = NeonGold,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = "Sharqona Fon Musiqasi",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp
                                                    ),
                                                    color = MaterialTheme.colorScheme.onBackground
                                                )
                                                Text(
                                                    text = "Dutor chertishlari & Mayin nay sadosi",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                    color = Color.Gray
                                                )
                                            }
                                        }

                                        Switch(
                                            checked = isAmbientEnabled,
                                            onCheckedChange = {
                                                isAmbientEnabled = it
                                                narratorManager.isAmbientMusicEnabled = it
                                                AmbientSoundSynthesizer.isEnabled = it
                                                if (!it) {
                                                    AmbientSoundSynthesizer.stop()
                                                }
                                            },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = NeonGold,
                                                checkedTrackColor = RegistanBlue
                                            )
                                        )
                                    }

                                    if (isAmbientEnabled) {
                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Ambience Style selector
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            AmbientSoundSynthesizer.AmbienceStyle.entries.forEach { style ->
                                                val isStyleSelected = selectedAmbienceStyle == style
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(if (isStyleSelected) NeonGold else Color(0x22888888))
                                                        .clickable {
                                                            selectedAmbienceStyle = style
                                                            AmbientSoundSynthesizer.currentStyle = style
                                                        }
                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Text(
                                                        text = "${style.icon} ${style.title}",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isStyleSelected) Color.Black else MaterialTheme.colorScheme.onBackground
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Musiqa ovozi balandligi:",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                color = Color.Gray
                                            )
                                            Text(
                                                text = "${(ambientVolume * 100).toInt()}%",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 10.sp,
                                                    color = NeonGold
                                                )
                                            )
                                        }
                                        Slider(
                                            value = ambientVolume,
                                            onValueChange = {
                                                ambientVolume = it
                                                AmbientSoundSynthesizer.volumeLevel = it
                                            },
                                            valueRange = 0.05f..0.50f,
                                            colors = SliderDefaults.colors(
                                                thumbColor = NeonGold,
                                                activeTrackColor = NeonGold
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // Open System TTS settings button
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { narratorManager.openInstallTtsData() },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x55888888))
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Download,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Ovoz Yuklash",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }

                                OutlinedButton(
                                    onClick = { narratorManager.openTtsSettings() },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x55888888))
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Settings,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "TTS Sozlamalari",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Apply Button
                    Button(
                        onClick = {
                            narratorManager.currentPersona = selectedPersona
                            narratorManager.currentPreset = selectedPreset
                            narratorManager.customPitch = currentPitch
                            narratorManager.customSpeed = currentSpeed
                            narratorManager.isAmbientMusicEnabled = isAmbientEnabled
                            narratorManager.stop()
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGold),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Saqlash & Tanlash",
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
