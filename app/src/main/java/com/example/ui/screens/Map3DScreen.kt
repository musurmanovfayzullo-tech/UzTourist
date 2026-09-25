package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.model.Destination
import com.example.model.SampleDestinations
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassPillBadge
import com.example.ui.components.GoldGradientButton
import com.example.ui.components.UzbekStarEmblem
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.SilkGold
import com.example.ui.theme.TurquoiseTile
import com.example.util.GpsLocationManager
import com.example.util.YandexMapHelper

data class MapWaypoint(
    val id: String,
    val title: String,
    val city: String,
    val normX: Float, // 0.0 to 1.0 (approximate geographic position on map)
    val normY: Float,
    val elevationMeters: Int,
    val isUnesco: Boolean,
    val destinationRef: Destination?,
    val latitude: Double = 39.6548,
    val longitude: Double = 66.9757,
    val iconEmoji: String = "🕌"
)

@Composable
fun Map3DScreen(
    onNavigateToAr: (Destination) -> Unit,
    onNavigateToPlanner: (Destination?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    // Real-Time GPS Location
    val userLocation by GpsLocationManager.currentLocation.collectAsStateWithLifecycle()

    var mapDisplayMode by remember { mutableStateOf("interactive") } // "interactive" or "3d"

    if (mapDisplayMode == "interactive") {
        InteractiveMapView(
            onNavigateToAr = onNavigateToAr,
            onNavigateToPlanner = onNavigateToPlanner,
            onSwitchTo3DCanvas = { mapDisplayMode = "3d" },
            modifier = modifier
        )
        return
    }

    // 3D Panoramic Waypoints
    val waypoints = remember {
        listOf(
            MapWaypoint("tashkent", "Amir Temur Maydoni", "Toshkent", 0.80f, 0.28f, 480, false, null, 41.3111, 69.2797, "🏙️"),
            MapWaypoint("samarkand", "Registon Maydoni", "Samarqand", 0.58f, 0.48f, 702, true, SampleDestinations.items[0], 39.6548, 66.9757, "🕌"),
            MapWaypoint("shahrisabz", "Oqsaroy Majmuasi", "Shahrisabz", 0.62f, 0.64f, 622, true, SampleDestinations.items[3], 39.0560, 66.8300, "🏛️"),
            MapWaypoint("bukhara", "Poi Kalon & Ark", "Buxoro", 0.40f, 0.54f, 225, true, SampleDestinations.items[1], 39.7758, 64.4158, "🕌"),
            MapWaypoint("khiva", "Ichan Qal'a & Kalta Minor", "Xiva", 0.22f, 0.42f, 100, true, SampleDestinations.items[2], 41.3783, 60.3594, "👑")
        )
    }

    var selectedWaypoint by remember { mutableStateOf(waypoints[1]) }
    var zoomLevel by remember { mutableFloatStateOf(1.0f) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }
    var is3DTilted by remember { mutableStateOf(true) }
    var showTrainRoute by remember { mutableStateOf(true) }

    val animatedTilt by animateFloatAsState(
        targetValue = if (is3DTilted) 25f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "TiltAnimation"
    )

    // Animated bullet train along the high speed corridor
    val trainTransition = rememberInfiniteTransition(label = "AfrasiyobTrain")
    val trainProgress by trainTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "TrainProgress"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF030A18) else Color(0xFFEAF1FB))
    ) {
        // Modern 3D Topographical Map Viewport
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        panOffsetX += dragAmount.x
                        panOffsetY += dragAmount.y
                    }
                }
        ) {
            val totalWidthPx = constraints.maxWidth.toFloat()
            val totalHeightPx = constraints.maxHeight.toFloat()

            // 3D Perspective Transformation Container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationX = animatedTilt
                        rotationZ = rotationAngle
                        scaleX = zoomLevel
                        scaleY = zoomLevel
                        translationX = panOffsetX
                        translationY = panOffsetY
                        cameraDistance = 14f * density
                    }
            ) {
                // 1. High-Resolution Geographic Relief Backdrop
                Image(
                    painter = painterResource(id = R.drawable.uzbekistan_silk_road_map_1787820419715),
                    contentDescription = "O'zbekiston Ipak Yo'li Relyef Xaritasi",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // 2. Ambient Lighting and Soft Elevation Tint
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    if (isDark) Color(0x66001030) else Color(0x22002A66),
                                    if (isDark) Color(0xCC030A18) else Color(0x55001E50)
                                ),
                                radius = totalWidthPx.coerceAtLeast(totalHeightPx) * 0.9f
                            )
                        )
                )

                // 3. High-Speed Rail Corridor & Track Path
                if (showTrainRoute) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        val waypointsCoords = waypoints.map { wp ->
                            Offset(wp.normX * w, wp.normY * h)
                        }

                        val trackPath = Path().apply {
                            moveTo(waypointsCoords[0].x, waypointsCoords[0].y)
                            for (i in 1 until waypointsCoords.size) {
                                val prev = waypointsCoords[i - 1]
                                val curr = waypointsCoords[i]
                                val midX = (prev.x + curr.x) / 2f
                                val midY = (prev.y + curr.y) / 2f
                                quadraticTo(prev.x, prev.y, midX, midY)
                            }
                            val last = waypointsCoords.last()
                            lineTo(last.x, last.y)
                        }

                        // Sleepers / Bed
                        drawPath(
                            path = trackPath,
                            color = if (isDark) Color(0x88D4AF37) else Color(0x880047AB),
                            style = Stroke(
                                width = 10f,
                                cap = StrokeCap.Round,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 10f), 0f)
                            )
                        )

                        // Outer Rail Glow
                        drawPath(
                            path = trackPath,
                            color = TurquoiseTile.copy(alpha = 0.6f),
                            style = Stroke(width = 5f, cap = StrokeCap.Round)
                        )

                        // Inner Gleam Rail
                        drawPath(
                            path = trackPath,
                            color = NeonGold,
                            style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                        )

                        // Calculate Animated Train Position along the 4 track segments
                        val totalSegments = waypointsCoords.size - 1
                        val scaledProgress = trainProgress * totalSegments
                        val segmentIndex = scaledProgress.toInt().coerceIn(0, totalSegments - 1)
                        val segmentFraction = scaledProgress - segmentIndex

                        val pStart = waypointsCoords[segmentIndex]
                        val pEnd = waypointsCoords[segmentIndex + 1]
                        val trainX = pStart.x + (pEnd.x - pStart.x) * segmentFraction
                        val trainY = pStart.y + (pEnd.y - pStart.y) * segmentFraction

                        // Bullet Train Aura
                        drawCircle(
                            color = Color(0x8800E676),
                            radius = 16f,
                            center = Offset(trainX, trainY)
                        )

                        // Bullet Train Head
                        drawCircle(
                            color = Color.White,
                            radius = 9f,
                            center = Offset(trainX, trainY)
                        )

                        drawCircle(
                            color = RegistanBlue,
                            radius = 5f,
                            center = Offset(trainX, trainY)
                        )
                    }
                }

                // 4. Modern 3D Floating Landmark Cards (Anchored to geographic coordinates)
                waypoints.forEach { wp ->
                    val isSelected = selectedWaypoint.id == wp.id

                    val xPos = wp.normX * totalWidthPx
                    val yPos = wp.normY * totalHeightPx

                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    x = (xPos - 70.dp.toPx()).toInt(),
                                    y = (yPos - 85.dp.toPx()).toInt()
                                )
                            }
                            .clickable {
                                selectedWaypoint = wp
                            }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Elevated Modern 3D Glass Badge
                            Box(
                                modifier = Modifier
                                    .shadow(if (isSelected) 16.dp else 8.dp, RoundedCornerShape(16.dp))
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (isSelected) {
                                            if (isDark) Color(0xF0071B3E) else Color(0xF5FFFFFF)
                                        } else {
                                            if (isDark) Color(0xCC05122B) else Color(0xEEFFFFFF)
                                        }
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) NeonGold else TurquoiseTile.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Thumbnail or Category Icon
                                    if (wp.destinationRef != null) {
                                        Image(
                                            painter = painterResource(id = wp.destinationRef.imageRes),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(NeonGold.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = wp.iconEmoji, fontSize = 14.sp)
                                        }
                                    }

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = wp.city,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 11.sp
                                                ),
                                                color = if (isSelected) NeonGold else MaterialTheme.colorScheme.onBackground
                                            )
                                            if (wp.isUnesco) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "★",
                                                    fontSize = 10.sp,
                                                    color = SilkGold
                                                )
                                            }
                                        }
                                        Text(
                                            text = "${wp.elevationMeters}m ASL",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = TurquoiseTile
                                        )
                                    }
                                }
                            }

                            // Anchor Needle Pointing to Map Surface
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(14.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                if (isSelected) NeonGold else TurquoiseTile,
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )

                            // Ground Contact Dot
                            Box(
                                modifier = Modifier
                                    .size(if (isSelected) 8.dp else 5.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) NeonGold else TurquoiseTile)
                            )
                        }
                    }
                }
            }
        }

        // Top Navigation Header: Mode Switcher & Telemetry
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mode Segmented Bar
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(22.dp))
                        .background(if (isDark) Color(0xEE06122B) else Color(0xF5FFFFFF))
                        .border(1.dp, TurquoiseTile.copy(alpha = 0.7f), RoundedCornerShape(22.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // 1. Live GPS Map Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { mapDisplayMode = "interactive" }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Map,
                                contentDescription = null,
                                tint = if (isDark) Color(0xFF90CAF9) else RegistanBlue,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Jonli Xarita",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = if (isDark) Color(0xFF90CAF9) else RegistanBlue
                            )
                        }
                    }

                    // 2. 3D Panorama Button (Active)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(NeonGold, SilkGold)
                                )
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Layers,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "3D Ipak Yo'li",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = Color.Black
                                )
                            )
                        }
                    }
                }

                // Train Corridor Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isDark) Color(0xEE06122B) else Color(0xF5FFFFFF))
                        .border(1.dp, if (showTrainRoute) Color(0xFF00E676) else Color.Gray, RoundedCornerShape(18.dp))
                        .clickable { showTrainRoute = !showTrainRoute }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.DirectionsTransit,
                            contentDescription = null,
                            tint = if (showTrainRoute) Color(0xFF00E676) else Color.Gray,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Afrosiyob 250 km/s",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = if (showTrainRoute) Color(0xFF00E676) else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick City Selector Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(waypoints) { wp ->
                    val isSelected = selectedWaypoint.id == wp.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) {
                                    if (isDark) NeonGold else RegistanBlue
                                } else {
                                    if (isDark) Color(0xDD07122C) else Color.White
                                }
                            )
                            .border(
                                1.dp,
                                if (isSelected) SilkGold else (if (isDark) Color(0x44F6C845) else Color(0x330047AB)),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                selectedWaypoint = wp
                                panOffsetX = 0f
                                panOffsetY = 0f
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("map_chip_${wp.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = wp.iconEmoji, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = wp.city,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                                ),
                                color = if (isSelected) (if (isDark) Color(0xFF1A1C1E) else Color.White) else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }

        // Floating 3D Map Controls (Right Side)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 3D Tilt Toggle
            IconButton(
                onClick = { is3DTilted = !is3DTilted },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xDD07122C) else Color.White)
                    .border(1.dp, if (is3DTilted) NeonGold else TurquoiseTile, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Layers,
                    contentDescription = "3D Perspektiva",
                    tint = if (is3DTilted) NeonGold else TurquoiseTile,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Compass Rotate
            IconButton(
                onClick = { rotationAngle = (rotationAngle + 45f) % 360f },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xDD07122C) else Color.White)
                    .border(1.dp, TurquoiseTile, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.RotateRight,
                    contentDescription = "Xaritani burish",
                    tint = TurquoiseTile,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Zoom In
            IconButton(
                onClick = { zoomLevel = (zoomLevel + 0.2f).coerceAtMost(2.5f) },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xDD07122C) else Color.White)
                    .border(1.dp, TurquoiseTile, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.ZoomIn,
                    contentDescription = "Kattalashtirish",
                    tint = TurquoiseTile,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Zoom Out
            IconButton(
                onClick = { zoomLevel = (zoomLevel - 0.2f).coerceAtLeast(0.7f) },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xDD07122C) else Color.White)
                    .border(1.dp, TurquoiseTile, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.ZoomOut,
                    contentDescription = "Kichiklashtirish",
                    tint = TurquoiseTile,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Reset Camera
            IconButton(
                onClick = {
                    zoomLevel = 1.0f
                    rotationAngle = 0f
                    panOffsetX = 0f
                    panOffsetY = 0f
                },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xDD07122C) else Color.White)
                    .border(1.dp, NeonGold, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.NearMe,
                    contentDescription = "Markazga qaytish",
                    tint = NeonGold,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Bottom Selected Waypoint Detail Card
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 84.dp)
        ) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("map_waypoint_detail_card"),
                shape = RoundedCornerShape(24.dp),
                backgroundColor = if (isDark) Color(0xF406132D) else Color(0xFAFFFFFF),
                borderColor = if (isDark) NeonGold.copy(alpha = 0.7f) else RegistanBlue.copy(alpha = 0.3f),
                elevation = 14.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Optional Destination Photo Banner
                    if (selectedWaypoint.destinationRef != null) {
                        val dest = selectedWaypoint.destinationRef!!
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(115.dp)
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            Image(
                                painter = painterResource(id = dest.imageRes),
                                contentDescription = dest.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color(0xB0000000))
                                        )
                                    )
                            )
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xDD000000))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = null,
                                            tint = NeonGold,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "${dest.rating}",
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                                if (dest.unescoYear != null) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xDDA16207))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "UNESCO ${dest.unescoYear}",
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Title & Elevation Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = selectedWaypoint.city.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        letterSpacing = 1.2.sp,
                                        fontWeight = FontWeight.Black
                                    ),
                                    color = if (isDark) NeonGold else SilkGold
                                )

                                if (selectedWaypoint.isUnesco) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0x22D4AF37))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "UNESCO",
                                            color = SilkGold,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }

                            Text(
                                text = selectedWaypoint.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        GlassPillBadge(
                            text = "${selectedWaypoint.elevationMeters}m ASL",
                            accentColor = TurquoiseTile,
                            textColor = TurquoiseTile
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Afrasiyob Train & Travel Telemetry
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDark) Color(0x330047AB) else Color(0xFFF0F6FF))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DirectionsTransit,
                            contentDescription = null,
                            tint = TurquoiseTile,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Afrosiyob tezyurar poyezdi • Toshkentdan 2 soat 15 daqiqa",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Navigation & AR Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. AR Guide Button
                        if (selectedWaypoint.destinationRef != null) {
                            Box(
                                modifier = Modifier
                                    .weight(1.1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF059669), Color(0xFF10B981))
                                        )
                                    )
                                    .clickable { onNavigateToAr(selectedWaypoint.destinationRef!!) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.CameraAlt,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "AR Ko'rish",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.sp,
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                        }

                        // 2. Yandex Go Taxi
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.horizontalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF8F00)))
                                )
                                .clickable {
                                    YandexMapHelper.orderYandexTaxi(
                                        context,
                                        selectedWaypoint.latitude,
                                        selectedWaypoint.longitude,
                                        selectedWaypoint.title
                                    )
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.DirectionsCar,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Yandex Go",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        color = Color.Black
                                    )
                                )
                            }
                        }

                        // 3. Yandex / Google Maps
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isDark) Color(0x440047AB) else Color(0xFFE8F0FE))
                                .border(1.dp, TurquoiseTile.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                            .clickable {
                                YandexMapHelper.openInYandexMaps(
                                    context,
                                    selectedWaypoint.latitude,
                                    selectedWaypoint.longitude,
                                    selectedWaypoint.title
                                )
                            }
                            .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Xarita",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (isDark) Color.White else RegistanBlue
                                )
                            )
                        }

                        // 4. AI Planner
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isDark) Color(0x33FFD700) else Color(0xFFFFF3CD))
                                .border(1.dp, NeonGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                                .clickable { onNavigateToPlanner(selectedWaypoint.destinationRef) }
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "AI",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = if (isDark) NeonGold else SilkGold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
