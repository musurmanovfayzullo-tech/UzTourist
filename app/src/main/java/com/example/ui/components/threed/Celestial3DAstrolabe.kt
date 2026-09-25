package com.example.ui.components.threed

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.SilkGold
import com.example.ui.theme.TurquoiseTile
import kotlin.math.cos
import kotlin.math.sin

/**
 * Celestial 3D Astrolabe / Mirzo Ulug'bek Celestial Globe
 * Renders an authentic 3D rotating Islamic Astrolabe with golden gimbal rings,
 * orbital paths, and glowing star constellation markers in real-time 3D projection.
 */
@Composable
fun Celestial3DAstrolabe(
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    primaryColor: Color = NeonGold,
    secondaryColor: Color = TurquoiseTile
) {
    var manualRotX by remember { mutableFloatStateOf(0.3f) }
    var manualRotY by remember { mutableFloatStateOf(0.4f) }
    var isDragging by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "AstrolabeRot")
    val autoRot by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "AutoRot"
    )

    val currentRotY = if (isDragging) manualRotY else (manualRotY + autoRot) % 6.28318f

    Box(
        modifier = modifier
            .size(size)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = { isDragging = false },
                    onDragCancel = { isDragging = false },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        manualRotY += dragAmount.x * 0.02f
                        manualRotX = (manualRotX - dragAmount.y * 0.02f).coerceIn(-1.0f, 1.0f)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val canvasSize = this.size.width
            val centerX = canvasSize / 2f
            val centerY = canvasSize / 2f
            val radius = canvasSize * 0.44f

            // Outer golden astrolabe rim
            drawCircle(
                brush = Brush.sweepGradient(listOf(primaryColor, secondaryColor, primaryColor)),
                radius = radius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2.2f)
            )

            // 3D Orbital Rings (Equatorial, Meridian, and Ecliptic)
            val ringCount = 3
            val ringAngles = listOf(0f, 1.047f, 2.094f) // 0°, 60°, 120°

            for (angleOffset in ringAngles) {
                val path = Path()
                val steps = 36
                for (i in 0..steps) {
                    val theta = i * (6.28318f / steps)
                    val x3d = cos(theta) * radius * 0.88f
                    val y3d = sin(theta) * radius * 0.88f * cos(angleOffset)
                    val z3d = sin(theta) * radius * 0.88f * sin(angleOffset)

                    // Rotate around X and Y
                    val cosY = cos(currentRotY)
                    val sinY = sin(currentRotY)
                    val x1 = x3d * cosY + z3d * sinY
                    val y1 = y3d
                    val z1 = -x3d * sinY + z3d * cosY

                    val cosX = cos(manualRotX)
                    val sinX = sin(manualRotX)
                    val y2 = y1 * cosX - z1 * sinX
                    val z2 = y1 * sinX + z1 * cosX

                    val pScale = 3.5f / (3.5f + (z2 / radius))
                    val px = centerX + x1 * pScale
                    val py = centerY - y2 * pScale

                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                drawPath(
                    path = path,
                    color = if (angleOffset == 0f) secondaryColor.copy(alpha = 0.85f) else primaryColor.copy(alpha = 0.65f),
                    style = Stroke(width = 1.4f, cap = StrokeCap.Round)
                )
            }

            // Central Glowing Stellar Core
            drawCircle(
                color = Color.White,
                radius = 3.2f,
                center = Offset(centerX, centerY)
            )
            drawCircle(
                color = primaryColor.copy(alpha = 0.4f),
                radius = 6.5f,
                center = Offset(centerX, centerY)
            )
        }
    }
}
