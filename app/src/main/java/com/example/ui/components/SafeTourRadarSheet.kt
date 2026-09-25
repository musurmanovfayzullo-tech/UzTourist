package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonGold
import com.example.ui.theme.TurquoiseTile
import com.example.util.GeofenceStatus
import com.example.util.GpsLocationManager
import com.example.util.GuideInteractionManager
import com.example.util.SafeTourRadarManager
import com.example.util.UserSessionManager
import kotlin.math.cos
import kotlin.math.sin

/**
 * Modern High-Tech SafeTour Georadar Sheet:
 * - Animated rotating radar sweep with distance rings
 * - Live direction pointer and compass bearing towards tour guide
 * - Geofence distance status (Safe, Caution, Breached)
 * - Automatic haptic, tone, and voice alarms on geofence separation
 * - "Men adashdim! (Gidni chaqirish)" emergency dispatch button
 * - Geofence radius perimeter settings
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeTourRadarSheet(
    onDismiss: () -> Unit,
    onOpenChat: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    val touristProfile = remember { UserSessionManager.loadProfile(context) }
    val touristName = if (touristProfile != null && touristProfile.firstName.isNotBlank()) {
        "${touristProfile.firstName} ${touristProfile.lastName}".trim()
    } else {
        "VIP Sayyoh"
    }
    val touristPhone = touristProfile?.phoneNumber ?: "+998 90 123 45 67"
    val touristId = remember { UserSessionManager.getUserId(context) }
    val guideRecord = remember { UserSessionManager.getGuideVerification(context) }
    val guideNumber = guideRecord?.guideNumber ?: "7788"

    // Initialize Radar & TTS
    LaunchedEffect(Unit) {
        SafeTourRadarManager.init(context)
        GpsLocationManager.init(context)
        if (GpsLocationManager.hasLocationPermission(context)) {
            GpsLocationManager.startLocationUpdates(context)
        }
    }

    // Live Radar Data
    val (distanceMeters, bearingDegrees, geofenceStatus) = SafeTourRadarManager.getCurrentRadarData(context)
    val directionDesc = SafeTourRadarManager.getDirectionDescriptionUz(bearingDegrees)

    // Check separation alert
    LaunchedEffect(distanceMeters, geofenceStatus) {
        if (geofenceStatus == GeofenceStatus.BREACHED) {
            SafeTourRadarManager.checkAndTriggerGeofenceAlert(context, touristName, distanceMeters)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF030A17),
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
                    .padding(horizontal = 18.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(geofenceStatus.colorHex),
                                        TurquoiseTile
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.NearMe,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SAFETOUR GEORADAR",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    letterSpacing = 1.sp,
                                    color = Color(geofenceStatus.colorHex)
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0x2210B981))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "JONLI GPS",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }
                        Text(
                            text = "Giddan uzoqlashish xavfsizlik nazorati",
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. STATUS BADGE
                item {
                    val statusBgColor by animateColorAsState(
                        targetValue = when (geofenceStatus) {
                            GeofenceStatus.SAFE -> Color(0x3310B981)
                            GeofenceStatus.CAUTION -> Color(0x33F59E0B)
                            GeofenceStatus.BREACHED -> Color(0x44EF4444)
                        },
                        label = "statusBg"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(statusBgColor)
                            .border(1.5.dp, Color(geofenceStatus.colorHex), RoundedCornerShape(16.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (geofenceStatus == GeofenceStatus.BREACHED) Icons.Filled.Warning else Icons.Filled.Security,
                                    contentDescription = null,
                                    tint = Color(geofenceStatus.colorHex),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = geofenceStatus.label,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = Color(geofenceStatus.colorHex)
                                    )
                                    Text(
                                        text = when (geofenceStatus) {
                                            GeofenceStatus.SAFE -> "Gid bilan xavfsiz yaqinlikdasiz (Perimetr: ${SafeTourRadarManager.safeRadiusRadiusDisplay()}m)"
                                            GeofenceStatus.CAUTION -> "Diqqat: Giddan orqada qolyapsiz, yaqinlashing"
                                            GeofenceStatus.BREACHED -> "OGOHLANTIRISH: Xavfsiz perimetr buzildi! Adashib qolmang"
                                        },
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            Text(
                                text = "$distanceMeters m",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = Color(geofenceStatus.colorHex)
                            )
                        }
                    }
                }

                // 2. RADAR CANVAS SWEEP VIEW
                item {
                    RadarCanvasView(
                        distanceMeters = distanceMeters,
                        maxDistance = 150f,
                        bearingDegrees = bearingDegrees,
                        geofenceStatus = geofenceStatus,
                        safeRadiusMeters = SafeTourRadarManager.safeRadiusMeters
                    )
                }

                // 3. COMPASS DIRECTION & GUIDANCE HUD
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF071936)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TurquoiseTile.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "GID TOMON YO'NALISH",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonGold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = directionDesc,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Gid: ${GuideInteractionManager.currentGuideLocation.guideName} • ${GuideInteractionManager.currentGuideLocation.meetingPointName}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val guidePhone = GuideInteractionManager.currentGuideLocation.guidePhone
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$guidePhone"))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0052D4))
                                ) {
                                    Icon(Icons.Filled.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Qo'ng'iroq", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        onOpenChat()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = TurquoiseTile, contentColor = Color.Black)
                                ) {
                                    Icon(Icons.Filled.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Chat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        val guideLoc = GuideInteractionManager.currentGuideLocation
                                        val gmmIntentUri = Uri.parse("google.navigation:q=${guideLoc.latitude},${guideLoc.longitude}")
                                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                        mapIntent.setPackage("com.google.android.apps.maps")
                                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                                            context.startActivity(mapIntent)
                                        } else {
                                            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${guideLoc.latitude},${guideLoc.longitude}"))
                                            context.startActivity(webIntent)
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFFFFF))
                                ) {
                                    Icon(Icons.Filled.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Xarita", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // 4. "MEN ADASHDIM! (GIDGA SIGNAL)" - EMERGENCY RADAR BUTTON
                item {
                    var isDispatching by remember { mutableStateOf(false) }

                    Button(
                        onClick = {
                            isDispatching = true
                            SafeTourRadarManager.dispatchLostSignalToGuide(
                                context = context,
                                touristName = touristName,
                                touristPhone = touristPhone,
                                touristId = touristId,
                                guideNumber = guideNumber,
                                distanceMeters = distanceMeters,
                                directionDesc = directionDesc
                            ) { ok, msg ->
                                isDispatching = false
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        enabled = !isDispatching
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.NotificationsActive,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isDispatching) "SIGNAL YUBORILMOQDA..." else "🚨 MEN ADASHDIM! GIDNI CHAQIRISH",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.5.sp,
                                letterSpacing = 0.5.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                // 5. GEOFENCE CONFIGURATION (CHIPS)
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Xavfsizlik Perimetri (Geofence chegarasi):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(50, 80, 120).forEach { radius ->
                                val isSelected = SafeTourRadarManager.safeRadiusMeters == radius
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) NeonGold else Color(0x22FFFFFF))
                                        .clickable { SafeTourRadarManager.safeRadiusMeters = radius }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$radius metr",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.Black else Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}

private fun SafeTourRadarManager.safeRadiusRadiusDisplay(): Int = safeRadiusMeters

/**
 * Animated High-Tech Canvas Radar View
 */
@Composable
private fun RadarCanvasView(
    distanceMeters: Int,
    maxDistance: Float = 150f,
    bearingDegrees: Float,
    geofenceStatus: GeofenceStatus,
    safeRadiusMeters: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radarSweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepAngle"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = Modifier
            .size(260.dp)
            .clip(CircleShape)
            .background(Color(0xFF030D1E))
            .border(2.dp, TurquoiseTile.copy(alpha = 0.6f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = (size.width / 2f) - 16f

            // Concentric Range Rings (25m, 50m, 80m, 120m)
            val ringRatios = listOf(0.25f, 0.5f, 0.75f, 1.0f)
            for (ratio in ringRatios) {
                drawCircle(
                    color = Color(0x2238BDF8),
                    radius = maxRadius * ratio,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // Safe Radius Boundary ring (Dashed)
            val safeRatio = (safeRadiusMeters / maxDistance).coerceIn(0.2f, 1.0f)
            drawCircle(
                color = Color(geofenceStatus.colorHex).copy(alpha = 0.6f),
                radius = maxRadius * safeRatio,
                center = center,
                style = Stroke(
                    width = 1.8.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                )
            )

            // Crosshair Axes (N-S, E-W)
            drawLine(
                color = Color(0x3338BDF8),
                start = Offset(center.x, center.y - maxRadius),
                end = Offset(center.x, center.y + maxRadius),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = Color(0x3338BDF8),
                start = Offset(center.x - maxRadius, center.y),
                end = Offset(center.x + maxRadius, center.y),
                strokeWidth = 1.dp.toPx()
            )

            // Rotating Radar Sweep Line
            val sweepRad = Math.toRadians((sweepAngle - 90).toDouble())
            val sweepX = center.x + maxRadius * cos(sweepRad).toFloat()
            val sweepY = center.y + maxRadius * sin(sweepRad).toFloat()
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, TurquoiseTile.copy(alpha = 0.8f)),
                    start = center,
                    end = Offset(sweepX, sweepY)
                ),
                start = center,
                end = Offset(sweepX, sweepY),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Tourist Center Dot (Siz)
            drawCircle(
                color = Color(0xFF38BDF8).copy(alpha = 0.3f),
                radius = 12.dp.toPx() * pulseAlpha,
                center = center
            )
            drawCircle(
                color = Color(0xFF38BDF8),
                radius = 5.dp.toPx(),
                center = center
            )

            // Guide Blip Target
            // Proportional distance on radar canvas
            val distRatio = (distanceMeters / maxDistance).coerceIn(0.12f, 0.95f)
            val guideRadius = maxRadius * distRatio
            val guideAngleRad = Math.toRadians((bearingDegrees - 90).toDouble())
            val guideX = center.x + guideRadius * cos(guideAngleRad).toFloat()
            val guideY = center.y + guideRadius * sin(guideAngleRad).toFloat()
            val guidePos = Offset(guideX, guideY)

            // Guide Target Aura
            drawCircle(
                color = Color(geofenceStatus.colorHex).copy(alpha = pulseAlpha),
                radius = 14.dp.toPx(),
                center = guidePos
            )
            // Guide Target Core
            drawCircle(
                color = Color(geofenceStatus.colorHex),
                radius = 6.dp.toPx(),
                center = guidePos
            )

            // Bearing line from Tourist to Guide
            drawLine(
                color = Color(geofenceStatus.colorHex).copy(alpha = 0.5f),
                start = center,
                end = guidePos,
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
            )
        }

        // Radar Overlay Labels
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("N (Shimol)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.weight(1f))
            Text("S (Janub)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f))
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("W", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.weight(1f))
            Text("E", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.5f))
        }
    }
}
