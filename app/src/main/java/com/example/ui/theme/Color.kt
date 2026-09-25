package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary Colors - Modern Silk Road Palette
val RegistanBlue = Color(0xFF0047AB)       // Deep Royal Registan Blue
val RegistanBlueDark = Color(0xFF002D6B)   // Midnight Silk Blue
val RegistanBlueLight = Color(0xFF1E6AFF)  // Bright Azure Accent
val TurquoiseTile = Color(0xFF00A896)      // Samarkand Turquoise Mosaic
val TurquoiseGlaze = Color(0xFF00C49F)     // Glazed Dome Turquoise
val LapisLazuli = Color(0xFF0A2463)        // Deep Lapis Gemstone

// Secondary & Accent Colors - Silk Road Gold
val SilkGold = Color(0xFFD4AF37)           // Silk Road Antique Gold
val SilkGoldLight = Color(0xFFFFDF73)      // Bright Golden Shimmer
val SilkGoldDark = Color(0xFF997D1E)       // Burnished Bronze Gold
val NeonGold = Color(0xFFF6C845)           // Vibrant Neon Gold
val GoldenSand = Color(0xFFF3E5AB)         // Kyzylkum Desert Sand

// Background & Glassmorphism Surfaces
val SurfaceLight = Color(0xFFF5F7FA)       // Soft modern light canvas
val SurfacePureWhite = Color(0xFFFFFFFF)   // Pure crisp white
val GlassBackgroundLight = Color(0xE6FFFFFF)// Frosted glass light
val GlassBorderLight = Color(0x66D4AF37)   // Golden glass border light

// Dark Mode Colors
val MidnightCanvas = Color(0xFF050B1A)     // Deep Registan Night Canvas
val MidnightSurface = Color(0xFF0C162E)    // Elevated Night Blue Surface
val MidnightSurfaceVariant = Color(0xFF132247)
val GlassBackgroundDark = Color(0xD90A1530) // Frosted glass dark
val GlassBorderDark = Color(0x66F6C845)    // Neon gold border dark

// Neutral & Status
val TextPrimaryLight = Color(0xFF0A1128)
val TextSecondaryLight = Color(0xFF5E6E82)
val TextPrimaryDark = Color(0xFFF0F4F8)
val TextSecondaryDark = Color(0xFF94A3B8)
val AccentRuby = Color(0xFFD81E5B)         // Pomegranate Ruby
val AccentEmerald = Color(0xFF10B981)      // Fergana Emerald Green

// Premium Gold Brushes
val GoldGradientBrush = Brush.horizontalGradient(
    colors = listOf(SilkGoldDark, SilkGold, SilkGoldLight, SilkGold)
)

val BlueGoldGradientBrush = Brush.linearGradient(
    colors = listOf(RegistanBlue, RegistanBlueDark, Color(0xFF07122C))
)

val GlassOverlayBrushLight = Brush.verticalGradient(
    colors = listOf(Color(0xE6FFFFFF), Color(0xCCF5F7FA))
)

val GlassOverlayBrushDark = Brush.verticalGradient(
    colors = listOf(Color(0xE6101F3D), Color(0xD9071024))
)
