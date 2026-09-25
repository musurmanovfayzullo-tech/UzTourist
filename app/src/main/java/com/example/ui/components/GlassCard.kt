package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GlassBackgroundDark
import com.example.ui.theme.GlassBackgroundLight
import com.example.ui.theme.GlassBorderDark
import com.example.ui.theme.GlassBorderLight
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.SilkGold
import com.example.ui.theme.SilkGoldLight

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(22.dp),
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    borderWidth: Dp = 1.dp,
    elevation: Dp = 6.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bg = backgroundColor ?: if (isDark) GlassBackgroundDark else GlassBackgroundLight
    val borderCol = borderColor ?: if (isDark) GlassBorderDark else GlassBorderLight

    val borderBrush = Brush.linearGradient(
        colors = listOf(
            borderCol,
            borderCol.copy(alpha = 0.15f),
            if (isDark) NeonGold.copy(alpha = 0.4f) else SilkGoldLight.copy(alpha = 0.6f),
            borderCol
        )
    )

    Surface(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = if (isDark) RegistanBlue.copy(alpha = 0.3f) else Color(0x1A0047AB),
                spotColor = if (isDark) SilkGold.copy(alpha = 0.2f) else Color(0x1AD4AF37)
            ),
        shape = shape,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .clip(shape)
                .background(bg)
                .border(BorderStroke(borderWidth, borderBrush), shape)
        ) {
            content()
        }
    }
}

@Composable
fun GlassPillBadge(
    text: String,
    modifier: Modifier = Modifier,
    accentColor: Color = SilkGold,
    textColor: Color = SilkGold
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(accentColor.copy(alpha = 0.15f))
            .border(
                BorderStroke(0.8.dp, accentColor.copy(alpha = 0.45f)),
                RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        androidx.compose.material3.Text(
            text = text,
            color = textColor,
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall
        )
    }
}
