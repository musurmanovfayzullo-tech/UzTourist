package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonGold,
    onPrimary = TextPrimaryLight,
    primaryContainer = RegistanBlue,
    onPrimaryContainer = SilkGoldLight,
    secondary = SilkGold,
    onSecondary = TextPrimaryLight,
    secondaryContainer = MidnightSurfaceVariant,
    onSecondaryContainer = SilkGoldLight,
    tertiary = TurquoiseGlaze,
    onTertiary = TextPrimaryLight,
    background = MidnightCanvas,
    onBackground = TextPrimaryDark,
    surface = MidnightSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = MidnightSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = GlassBorderDark,
    outlineVariant = Color(0x33F6C845)
)

private val LightColorScheme = lightColorScheme(
    primary = RegistanBlue,
    onPrimary = SurfacePureWhite,
    primaryContainer = Color(0xFFE8F0FE),
    onPrimaryContainer = RegistanBlueDark,
    secondary = SilkGold,
    onSecondary = SurfacePureWhite,
    secondaryContainer = Color(0xFFFFF9E6),
    onSecondaryContainer = SilkGoldDark,
    tertiary = TurquoiseTile,
    onTertiary = SurfacePureWhite,
    background = SurfaceLight,
    onBackground = TextPrimaryLight,
    surface = SurfacePureWhite,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFEEF2F6),
    onSurfaceVariant = TextSecondaryLight,
    outline = GlassBorderLight,
    outlineVariant = Color(0x22D4AF37)
)

@Composable
fun UzTouristTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
