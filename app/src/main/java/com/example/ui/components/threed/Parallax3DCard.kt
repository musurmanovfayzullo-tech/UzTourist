package com.example.ui.components.threed

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NeonGold
import com.example.ui.theme.SilkGold
import com.example.ui.theme.TurquoiseTile
import kotlinx.coroutines.launch

/**
 * Parallax3DContainer: Wraps content in an interactive 3D perspective plane.
 * When touched or dragged, it tilts in 3D space with specular holographic light reflection.
 */
@Composable
fun Parallax3DContainer(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(22.dp),
    maxTiltAngle: Float = 14f,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val scope = rememberCoroutineScope()

    val rotXAnim = remember { Animatable(0f) }
    val rotYAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(1f) }

    var glareOffset by remember { mutableStateOf(Offset(0.5f, 0.5f)) }
    var isTouching by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationX = rotXAnim.value
                rotationY = rotYAnim.value
                scaleX = scaleAnim.value
                scaleY = scaleAnim.value
                cameraDistance = 14f * density
                shadowElevation = if (isTouching) 20.dp.toPx() else 6.dp.toPx()
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        isTouching = true
                        val normX = (offset.x / size.width.toFloat()) - 0.5f
                        val normY = (offset.y / size.height.toFloat()) - 0.5f
                        glareOffset = Offset(offset.x / size.width.toFloat(), offset.y / size.height.toFloat())

                        scope.launch {
                            rotXAnim.animateTo(-normY * maxTiltAngle, spring(stiffness = Spring.StiffnessMedium))
                        }
                        scope.launch {
                            rotYAnim.animateTo(normX * maxTiltAngle, spring(stiffness = Spring.StiffnessMedium))
                        }
                        scope.launch {
                            scaleAnim.animateTo(1.03f, spring(stiffness = Spring.StiffnessMedium))
                        }

                        val released = tryAwaitRelease()
                        isTouching = false

                        scope.launch {
                            rotXAnim.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
                        }
                        scope.launch {
                            rotYAnim.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
                        }
                        scope.launch {
                            scaleAnim.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
                        }

                        if (released && onClick != null) {
                            onClick()
                        }
                    }
                )
            }
            .drawWithContent {
                drawContent()
                if (isTouching) {
                    // Specular holographic sheen across the card surface
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                if (isDark) NeonGold.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.5f),
                                TurquoiseTile.copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * glareOffset.x, size.height * glareOffset.y),
                            radius = size.width.coerceAtLeast(size.height) * 0.75f
                        )
                    )
                }
            }
    ) {
        content()
    }
}
