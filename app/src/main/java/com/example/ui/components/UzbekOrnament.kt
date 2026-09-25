package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.SilkGold
import com.example.ui.theme.TurquoiseTile
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * An eight-pointed Islamic geometric star (Khatam / Rub el Hizb)
 * drawn dynamically on Canvas with Silk Road Gold & Registan Blue accents.
 */
@Composable
fun UzbekStarEmblem(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    primaryColor: Color = SilkGold,
    secondaryColor: Color = TurquoiseTile,
    isRotating: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "StarRotation")
    val rotation by if (isRotating) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(20000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "RotationAngle"
        )
    } else {
        rememberInfiniteTransition(label = "Static").animateFloat(
            initialValue = 0f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(tween(1000)),
            label = "StaticAngle"
        )
    }

    Canvas(modifier = modifier.size(size)) {
        val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
        val radius = size.toPx() * 0.44f
        val innerRadius = radius * 0.58f

        rotate(rotation, pivot = center) {
            // First Square
            val squareHalf = radius * 0.707f
            drawRect(
                color = primaryColor.copy(alpha = 0.25f),
                topLeft = Offset(center.x - squareHalf, center.y - squareHalf),
                size = Size(squareHalf * 2, squareHalf * 2),
                style = Stroke(width = 2.dp.toPx())
            )

            // Second Square rotated 45 degrees
            rotate(45f, pivot = center) {
                drawRect(
                    color = primaryColor,
                    topLeft = Offset(center.x - squareHalf, center.y - squareHalf),
                    size = Size(squareHalf * 2, squareHalf * 2),
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Central Rosette Dot
            drawCircle(
                color = secondaryColor,
                radius = 3.dp.toPx(),
                center = center
            )
            drawCircle(
                color = primaryColor,
                radius = innerRadius * 0.45f,
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )
        }
    }
}

/**
 * Ikat / Atlas Wave Pattern Header Accent
 */
@Composable
fun IkatPatternDivider(
    modifier: Modifier = Modifier,
    color: Color = SilkGold.copy(alpha = 0.5f),
    height: Dp = 12.dp
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val h = size.height
        val step = 20.dp.toPx()
        val path = Path()

        var x = 0f
        var up = true
        path.moveTo(0f, h / 2)
        while (x < width) {
            val nextX = x + step / 2
            val nextY = if (up) 2f else h - 2f
            path.lineTo(nextX, nextY)
            up = !up
            x += step / 2
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = 1.8.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}

/**
 * AR Geometric Target Reticle with animated scanning circle and corner brackets
 */
@Composable
fun ArTargetReticle(
    modifier: Modifier = Modifier,
    color: Color = NeonGold,
    scanProgress: Float = 0.5f
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val bracketLen = w * 0.22f
        val strokeW = 2.5.dp.toPx()

        // 4 Corner brackets
        // Top-Left
        drawLine(color, Offset(0f, 0f), Offset(bracketLen, 0f), strokeW, StrokeCap.Round)
        drawLine(color, Offset(0f, 0f), Offset(0f, bracketLen), strokeW, StrokeCap.Round)

        // Top-Right
        drawLine(color, Offset(w, 0f), Offset(w - bracketLen, 0f), strokeW, StrokeCap.Round)
        drawLine(color, Offset(w, 0f), Offset(w, bracketLen), strokeW, StrokeCap.Round)

        // Bottom-Left
        drawLine(color, Offset(0f, h), Offset(bracketLen, h), strokeW, StrokeCap.Round)
        drawLine(color, Offset(0f, h), Offset(0f, h - bracketLen), strokeW, StrokeCap.Round)

        // Bottom-Right
        drawLine(color, Offset(w, h), Offset(w - bracketLen, h), strokeW, StrokeCap.Round)
        drawLine(color, Offset(w, h), Offset(w, h - bracketLen), strokeW, StrokeCap.Round)

        // Center crosshair
        val cx = w / 2f
        val cy = h / 2f
        val crossLen = 14.dp.toPx()
        drawLine(color.copy(alpha = 0.6f), Offset(cx - crossLen, cy), Offset(cx + crossLen, cy), 1.5.dp.toPx())
        drawLine(color.copy(alpha = 0.6f), Offset(cx, cy - crossLen), Offset(cx, cy + crossLen), 1.5.dp.toPx())

        // Pulsing scan radar circle
        drawCircle(
            color = color.copy(alpha = 0.35f),
            radius = (w / 2f) * scanProgress,
            center = Offset(cx, cy),
            style = Stroke(width = 1.5.dp.toPx())
        )
    }
}
