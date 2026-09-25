package com.example.ui.components.threed

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.SilkGold
import com.example.ui.theme.TurquoiseTile
import kotlin.math.cos
import kotlin.math.sin

// 3D Coordinate Point
data class Point3D(val x: Float, val y: Float, val z: Float)

// 3D Polygon Face
data class Face3D(
    val indices: List<Int>,
    val baseColor: Color,
    val isWireframeOnly: Boolean = false
)

enum class Monument3DModel(val title: String, val city: String, val icon: String) {
    REGISTAN_PISHTAQ("Registon Majmuasi", "Samarqand", "🏛️"),
    KALYAN_MINARET("Minorai Kalon", "Buxoro", "🗼"),
    ICHAN_QALA_GATE("Ota Darvoza", "Xiva", "🏰"),
    CHOR_MINOR("Chor Minor", "Buxoro", "🕌")
}

enum class Render3DMode(val label: String) {
    HOLOGRAPHIC_AZURE("Hologramma"),
    SOLID_GOLD_SHADE("Zardo'z 3D"),
    MATRIX_WIREFRAME("To'rli Karkas")
}

@Composable
fun Interactive3DMonumentViewer(
    modifier: Modifier = Modifier,
    initialModel: Monument3DModel = Monument3DModel.REGISTAN_PISHTAQ,
    onModelChange: ((Monument3DModel) -> Unit)? = null
) {
    val isDark = isSystemInDarkTheme()
    var currentModel by remember { mutableStateOf(initialModel) }
    var renderMode by remember { mutableStateOf(Render3DMode.HOLOGRAPHIC_AZURE) }
    var autoRotate by remember { mutableStateOf(true) }

    // 3D Rotation angles (in radians)
    var rotX by remember { mutableFloatStateOf(-0.35f) }
    var rotY by remember { mutableFloatStateOf(0.45f) }
    var zoomScale by remember { mutableFloatStateOf(1.0f) }

    // Ambient floating animation & Holographic scanner beam
    val infiniteTransition = rememberInfiniteTransition(label = "HoloLaser")
    val autoRotDelta by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "AutoRot"
    )

    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ScanBeam"
    )

    val currentRotY = if (autoRotate) (rotY + autoRotDelta) % 6.28318f else rotY

    // 3D Geometry vertices & faces for selected landmark
    val (vertices, faces) = remember(currentModel) {
        generate3DGeometryForMonument(currentModel)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(
                if (isDark) Brush.verticalGradient(
                    listOf(Color(0xFF040A18), Color(0xFF02102B), Color(0xFF010612))
                ) else Brush.verticalGradient(
                    listOf(Color(0xFFF1F6FD), Color(0xFFE2EDFC), Color(0xFFD6E4F8))
                )
            )
            .border(
                1.2.dp,
                if (isDark) Brush.linearGradient(listOf(NeonGold, TurquoiseTile, Color(0x330047AB)))
                else Brush.linearGradient(listOf(SilkGold, TurquoiseTile, Color(0x440047AB))),
                RoundedCornerShape(26.dp)
            )
            .shadow(16.dp, RoundedCornerShape(26.dp))
    ) {
        // 3D Gesture Viewport Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { autoRotate = false },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            rotY += dragAmount.x * 0.012f
                            rotX = (rotX - dragAmount.y * 0.012f).coerceIn(-1.2f, 1.2f)
                        }
                    )
                }
        ) {
            val canvasW = size.width
            val canvasH = size.height
            val centerX = canvasW / 2f
            val centerY = canvasH / 2f + 15f
            val baseScale = (canvasW.coerceAtMost(canvasH) * 0.28f) * zoomScale

            // Draw Celestial Ground Grid in 3D perspective
            draw3DGroundGrid(
                centerX = centerX,
                centerY = centerY,
                rotX = rotX,
                rotY = currentRotY,
                scale = baseScale,
                isDark = isDark
            )

            // Project 3D vertices to 2D screen coordinates
            val projectedPoints = vertices.map { v ->
                projectPoint3D(v, rotX, currentRotY, baseScale, centerX, centerY)
            }

            // Draw 3D polygon faces sorted by average depth (Z-buffer approximation)
            val sortedFaces = faces.map { face ->
                val avgZ = face.indices.map { vertices[it] }
                    .map { calculateTransformedZ(it, rotX, currentRotY) }
                    .average().toFloat()
                face to avgZ
            }.sortedBy { it.second }

            for ((face, _) in sortedFaces) {
                if (face.indices.size >= 3) {
                    val path = Path()
                    val firstPt = projectedPoints[face.indices[0]]
                    path.moveTo(firstPt.x, firstPt.y)
                    for (i in 1 until face.indices.size) {
                        val pt = projectedPoints[face.indices[i]]
                        path.lineTo(pt.x, pt.y)
                    }
                    path.close()

                    when (renderMode) {
                        Render3DMode.HOLOGRAPHIC_AZURE -> {
                            // Holographic azure tint with glowing edges
                            drawPath(
                                path = path,
                                color = face.baseColor.copy(alpha = if (isDark) 0.22f else 0.35f),
                                style = Fill
                            )
                            drawPath(
                                path = path,
                                color = TurquoiseTile.copy(alpha = 0.85f),
                                style = Stroke(width = 1.6f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                        }
                        Render3DMode.SOLID_GOLD_SHADE -> {
                            // Zardo'zi luxury gold metallic shader
                            drawPath(
                                path = path,
                                color = face.baseColor.copy(alpha = 0.65f),
                                style = Fill
                            )
                            drawPath(
                                path = path,
                                color = NeonGold,
                                style = Stroke(width = 2.0f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                        }
                        Render3DMode.MATRIX_WIREFRAME -> {
                            // High-tech matrix wireframe
                            drawPath(
                                path = path,
                                color = Color(0xFF00E5FF).copy(alpha = 0.9f),
                                style = Stroke(width = 1.4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                        }
                    }
                }
            }

            // Draw glowing vertices (3D node points)
            projectedPoints.forEach { pt ->
                drawCircle(
                    color = if (renderMode == Render3DMode.SOLID_GOLD_SHADE) NeonGold else Color(0xFF64FFDA),
                    radius = 3.2f,
                    center = Offset(pt.x, pt.y)
                )
            }

            // Draw Holographic Laser Scan Line
            val scanY = canvasH * scanLineY
            drawLine(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        TurquoiseTile.copy(alpha = 0.8f),
                        Color.White.copy(alpha = 0.95f),
                        TurquoiseTile.copy(alpha = 0.8f),
                        Color.Transparent
                    )
                ),
                start = Offset(20f, scanY),
                end = Offset(canvasW - 20f, scanY),
                strokeWidth = 2.2f
            )
        }

        // Top Overlay: Title & Hologram Badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "3D INTERAKTIV HOLOGRAMMA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        ),
                        color = if (isDark) TurquoiseTile else RegistanBlue
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${currentModel.icon} ${currentModel.title} • ${currentModel.city}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isDark) Color.White else Color(0xFF0D1B2A)
                )
                Text(
                    text = "Barmog'ingiz bilan 360° aylantiring",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp
                    ),
                    color = if (isDark) Color(0xFF90CAF9) else Color(0xFF4A6572)
                )
            }

            // Quick reset & auto-rotate toggle
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (autoRotate) NeonGold.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.2f))
                        .border(1.dp, if (autoRotate) NeonGold else Color(0x33888888), CircleShape)
                        .clickable { autoRotate = !autoRotate },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.RotateRight,
                        contentDescription = "Auto aylantirish",
                        tint = if (autoRotate) NeonGold else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.2f))
                        .border(1.dp, Color(0x33888888), CircleShape)
                        .clickable {
                            rotX = -0.35f
                            rotY = 0.45f
                            zoomScale = 1.0f
                            autoRotate = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Asliga qaytarish",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Bottom Controls: Model Selector Chips & Shading Mode
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            // Model Selector Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Monument3DModel.values().forEach { model ->
                    val isSel = model == currentModel
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSel) Brush.horizontalGradient(listOf(RegistanBlue, Color(0xFF002266)))
                                else Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.35f), Color.Black.copy(alpha = 0.35f)))
                            )
                            .border(
                                1.dp,
                                if (isSel) NeonGold else Color(0x22888888),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                currentModel = model
                                onModelChange?.invoke(model)
                            }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${model.icon} ${model.title.split(" ").first()}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 9.sp
                            ),
                            color = if (isSel) NeonGold else Color.White.copy(alpha = 0.8f),
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Shading style buttons (Hologram / Gold / Wireframe)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Render3DMode.values().forEach { mode ->
                        val isModeSel = mode == renderMode
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isModeSel) TurquoiseTile.copy(alpha = 0.3f)
                                    else Color.Transparent
                                )
                                .border(
                                    0.8.dp,
                                    if (isModeSel) TurquoiseTile else Color(0x33888888),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { renderMode = mode }
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = mode.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = if (isModeSel) TurquoiseTile else Color.LightGray
                            )
                        }
                    }
                }

                // Zoom in / Zoom out buttons
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.3f))
                            .clickable { zoomScale = (zoomScale - 0.15f).coerceAtLeast(0.6f) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ZoomOut,
                            contentDescription = "Kichraytirish",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.3f))
                            .clickable { zoomScale = (zoomScale + 0.15f).coerceAtMost(1.8f) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ZoomIn,
                            contentDescription = "Kattalashtirish",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

// 3D Helper: Project 3D vector to 2D screen with rotation matrix & perspective
private fun projectPoint3D(
    p: Point3D,
    rotX: Float,
    rotY: Float,
    scale: Float,
    centerX: Float,
    centerY: Float
): Offset {
    // Rotate around Y axis
    val cosY = cos(rotY)
    val sinY = sin(rotY)
    val x1 = p.x * cosY + p.z * sinY
    val y1 = p.y
    val z1 = -p.x * sinY + p.z * cosY

    // Rotate around X axis
    val cosX = cos(rotX)
    val sinX = sin(rotX)
    val x2 = x1
    val y2 = y1 * cosX - z1 * sinX
    val z2 = y1 * sinX + z1 * cosX

    // Perspective projection formula: d / (d + z)
    val cameraDist = 4.5f
    val perspective = cameraDist / (cameraDist + z2)

    val screenX = centerX + x2 * scale * perspective
    val screenY = centerY - y2 * scale * perspective // Screen Y is inverted
    return Offset(screenX, screenY)
}

private fun calculateTransformedZ(p: Point3D, rotX: Float, rotY: Float): Float {
    val sinY = sin(rotY)
    val cosY = cos(rotY)
    val z1 = -p.x * sinY + p.z * cosY
    val sinX = sin(rotX)
    val cosX = cos(rotX)
    return p.y * sinX + z1 * cosX
}

// Draw 3D floor perspective grid
private fun DrawScope.draw3DGroundGrid(
    centerX: Float,
    centerY: Float,
    rotX: Float,
    rotY: Float,
    scale: Float,
    isDark: Boolean
) {
    val gridColor = if (isDark) TurquoiseTile.copy(alpha = 0.15f) else Color(0x330047AB)
    val gridRange = -2.0f..2.0f
    val step = 0.5f

    var curr = gridRange.start
    while (curr <= gridRange.endInclusive) {
        val startPt = projectPoint3D(Point3D(curr, -1.1f, -2.0f), rotX, rotY, scale, centerX, centerY)
        val endPt = projectPoint3D(Point3D(curr, -1.1f, 2.0f), rotX, rotY, scale, centerX, centerY)
        drawLine(gridColor, startPt, endPt, strokeWidth = 1f)

        val startPt2 = projectPoint3D(Point3D(-2.0f, -1.1f, curr), rotX, rotY, scale, centerX, centerY)
        val endPt2 = projectPoint3D(Point3D(2.0f, -1.1f, curr), rotX, rotY, scale, centerX, centerY)
        drawLine(gridColor, startPt2, endPt2, strokeWidth = 1f)

        curr += step
    }
}

// Generate 3D polygonal geometry for Silk Road Landmarks
private fun generate3DGeometryForMonument(model: Monument3DModel): Pair<List<Point3D>, List<Face3D>> {
    val vertices = mutableListOf<Point3D>()
    val faces = mutableListOf<Face3D>()

    when (model) {
        Monument3DModel.REGISTAN_PISHTAQ -> {
            // Main Central Portal (Pishtaq)
            vertices.add(Point3D(-0.7f, -1.0f, 0.2f)) // 0 Bottom left
            vertices.add(Point3D(0.7f, -1.0f, 0.2f))  // 1 Bottom right
            vertices.add(Point3D(0.7f, 0.9f, 0.2f))   // 2 Top right
            vertices.add(Point3D(-0.7f, 0.9f, 0.2f))  // 3 Top left

            vertices.add(Point3D(-0.7f, -1.0f, -0.4f)) // 4 Back bottom left
            vertices.add(Point3D(0.7f, -1.0f, -0.4f))  // 5 Back bottom right
            vertices.add(Point3D(0.7f, 0.9f, -0.4f))   // 6 Back top right
            vertices.add(Point3D(-0.7f, 0.9f, -0.4f))  // 7 Back top left

            // Front Arch Cutout (Iwan Portal)
            vertices.add(Point3D(-0.35f, -1.0f, 0.21f)) // 8
            vertices.add(Point3D(0.35f, -1.0f, 0.21f))  // 9
            vertices.add(Point3D(0.35f, 0.35f, 0.21f))  // 10
            vertices.add(Point3D(0.0f, 0.65f, 0.21f))   // 11 Arch apex
            vertices.add(Point3D(-0.35f, 0.35f, 0.21f)) // 12

            // Left Minaret (Cylinder-like polygon)
            vertices.add(Point3D(-1.05f, -1.0f, 0.0f)) // 13
            vertices.add(Point3D(-0.85f, -1.0f, 0.2f)) // 14
            vertices.add(Point3D(-0.85f, -1.0f, -0.2f)) // 15
            vertices.add(Point3D(-1.0f, 1.4f, 0.0f))   // 16 Minaret top left
            vertices.add(Point3D(-0.9f, 1.4f, 0.15f))  // 17
            vertices.add(Point3D(-0.9f, 1.4f, -0.15f)) // 18

            // Right Minaret
            vertices.add(Point3D(1.05f, -1.0f, 0.0f)) // 19
            vertices.add(Point3D(0.85f, -1.0f, 0.2f)) // 20
            vertices.add(Point3D(0.85f, -1.0f, -0.2f)) // 21
            vertices.add(Point3D(1.0f, 1.4f, 0.0f))   // 22 Minaret top right
            vertices.add(Point3D(0.9f, 1.4f, 0.15f))  // 23
            vertices.add(Point3D(0.9f, 1.4f, -0.15f)) // 24

            // Central Azure Dome behind pishtaq
            vertices.add(Point3D(0.0f, 1.35f, -0.2f)) // 25 Dome top
            vertices.add(Point3D(-0.4f, 0.85f, -0.1f)) // 26
            vertices.add(Point3D(0.4f, 0.85f, -0.1f))  // 27
            vertices.add(Point3D(0.0f, 0.85f, -0.45f)) // 28

            // Faces
            faces.add(Face3D(listOf(0, 1, 2, 3), RegistanBlue))
            faces.add(Face3D(listOf(1, 5, 6, 2), Color(0xFF003380)))
            faces.add(Face3D(listOf(4, 0, 3, 7), Color(0xFF003380)))
            faces.add(Face3D(listOf(3, 2, 6, 7), SilkGold))
            faces.add(Face3D(listOf(8, 9, 10, 11, 12), Color(0xFF001133))) // Inner arch
            faces.add(Face3D(listOf(13, 14, 17, 16), TurquoiseTile))
            faces.add(Face3D(listOf(13, 15, 18, 16), TurquoiseTile))
            faces.add(Face3D(listOf(19, 20, 23, 22), TurquoiseTile))
            faces.add(Face3D(listOf(19, 21, 24, 22), TurquoiseTile))
            faces.add(Face3D(listOf(26, 27, 25), Color(0xFF00B4D8))) // Ribbed Dome
            faces.add(Face3D(listOf(27, 28, 25), Color(0xFF0077B6)))
            faces.add(Face3D(listOf(28, 26, 25), Color(0xFF023E8A)))
        }
        Monument3DModel.KALYAN_MINARET -> {
            // Tapering 3D Kalyan Minaret (octagon rings)
            val rings = 5
            val segments = 8
            for (r in 0..rings) {
                val y = -1.0f + r * (2.4f / rings)
                val radius = 0.55f - r * 0.05f
                for (s in 0 until segments) {
                    val angle = s * (6.28318f / segments)
                    vertices.add(Point3D(cos(angle) * radius, y, sin(angle) * radius))
                }
            }
            // Lantern Dome Top
            vertices.add(Point3D(0f, 1.65f, 0f)) // Apex

            // Connect rings to create 3D faces
            for (r in 0 until rings) {
                val ringOffset = r * segments
                val nextRingOffset = (r + 1) * segments
                val color = if (r % 2 == 0) SilkGold else Color(0xFFB08D57)
                for (s in 0 until segments) {
                    val nextS = (s + 1) % segments
                    faces.add(
                        Face3D(
                            listOf(
                                ringOffset + s,
                                ringOffset + nextS,
                                nextRingOffset + nextS,
                                nextRingOffset + s
                            ),
                            color
                        )
                    )
                }
            }
            // Top Dome faces
            val topRingOffset = rings * segments
            val apexIndex = vertices.size - 1
            for (s in 0 until segments) {
                val nextS = (s + 1) % segments
                faces.add(
                    Face3D(
                        listOf(topRingOffset + s, topRingOffset + nextS, apexIndex),
                        TurquoiseTile
                    )
                )
            }
        }
        Monument3DModel.ICHAN_QALA_GATE -> {
            // Twin watchtowers and crenellated wall
            // Left Tower
            vertices.add(Point3D(-0.9f, -1.0f, 0.2f))
            vertices.add(Point3D(-0.5f, -1.0f, 0.2f))
            vertices.add(Point3D(-0.5f, 0.8f, 0.2f))
            vertices.add(Point3D(-0.9f, 0.8f, 0.2f))

            vertices.add(Point3D(-0.9f, -1.0f, -0.3f))
            vertices.add(Point3D(-0.5f, -1.0f, -0.3f))
            vertices.add(Point3D(-0.5f, 0.8f, -0.3f))
            vertices.add(Point3D(-0.9f, 0.8f, -0.3f))

            // Right Tower
            vertices.add(Point3D(0.5f, -1.0f, 0.2f))
            vertices.add(Point3D(0.9f, -1.0f, 0.2f))
            vertices.add(Point3D(0.9f, 0.8f, 0.2f))
            vertices.add(Point3D(0.5f, 0.8f, 0.2f))

            vertices.add(Point3D(0.5f, -1.0f, -0.3f))
            vertices.add(Point3D(0.9f, -1.0f, -0.3f))
            vertices.add(Point3D(0.9f, 0.8f, -0.3f))
            vertices.add(Point3D(0.5f, 0.8f, -0.3f))

            // Central Arch
            vertices.add(Point3D(-0.5f, 0.4f, 0.0f))
            vertices.add(Point3D(0.5f, 0.4f, 0.0f))
            vertices.add(Point3D(0.0f, 0.7f, 0.0f))

            faces.add(Face3D(listOf(0, 1, 2, 3), Color(0xFFC29B38)))
            faces.add(Face3D(listOf(4, 5, 6, 7), Color(0xFF9E7C25)))
            faces.add(Face3D(listOf(8, 9, 10, 11), Color(0xFFC29B38)))
            faces.add(Face3D(listOf(12, 13, 14, 15), Color(0xFF9E7C25)))
            faces.add(Face3D(listOf(16, 17, 18), TurquoiseTile))
        }
        Monument3DModel.CHOR_MINOR -> {
            // Central body
            vertices.add(Point3D(-0.4f, -1.0f, 0.3f))
            vertices.add(Point3D(0.4f, -1.0f, 0.3f))
            vertices.add(Point3D(0.4f, 0.2f, 0.3f))
            vertices.add(Point3D(-0.4f, 0.2f, 0.3f))

            vertices.add(Point3D(-0.4f, -1.0f, -0.3f))
            vertices.add(Point3D(0.4f, -1.0f, -0.3f))
            vertices.add(Point3D(0.4f, 0.2f, -0.3f))
            vertices.add(Point3D(-0.4f, 0.2f, -0.3f))

            // 4 Turquoise Corner Minarets
            val minPositions = listOf(
                Point3D(-0.65f, -1.0f, 0.5f),
                Point3D(0.65f, -1.0f, 0.5f),
                Point3D(0.65f, -1.0f, -0.5f),
                Point3D(-0.65f, -1.0f, -0.5f)
            )
            minPositions.forEach { base ->
                val idx = vertices.size
                vertices.add(base)
                vertices.add(Point3D(base.x + 0.15f, base.y, base.z))
                vertices.add(Point3D(base.x + 0.15f, 0.9f, base.z))
                vertices.add(Point3D(base.x, 0.9f, base.z))
                vertices.add(Point3D(base.x + 0.07f, 1.25f, base.z)) // Dome tip
                faces.add(Face3D(listOf(idx, idx + 1, idx + 2, idx + 3), Color(0xFFC29B38)))
                faces.add(Face3D(listOf(idx + 3, idx + 2, idx + 4), TurquoiseTile))
            }
            faces.add(Face3D(listOf(0, 1, 2, 3), Color(0xFFB58E30)))
            faces.add(Face3D(listOf(4, 5, 6, 7), Color(0xFF8B6C20)))
        }
    }
    return vertices to faces
}
