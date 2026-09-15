package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ==========================================
// Islamic Color Palette: Emerald, Gold, Lapis & Parchment
// ==========================================

// Emerald & Verdant Greens (symbol of serenity, vitality, and Islamic heritage)
val IslamicEmerald = Color(0xFF0F5132)
val IslamicEmeraldMedium = Color(0xFF157347)
val IslamicEmeraldLight = Color(0xFF198754)
val IslamicEmeraldDark = Color(0xFF0A3622)
val IslamicEmeraldContainer = Color(0xFFD1E7DD)
val IslamicOnEmeraldContainer = Color(0xFF051B11)
val IslamicDeepForest = Color(0xFF052C1A)

// Royal Gold & Illuminated Calligraphy Accents (Tazhib / تذهيب)
val IslamicGold = Color(0xFFD4AF37)
val IslamicGoldAntique = Color(0xFFC59B27)
val IslamicGoldDark = Color(0xFFA67C00)
val IslamicGoldLight = Color(0xFFF3E5AB)
val IslamicGoldContainer = Color(0xFFFFF3CD)
val IslamicOnGoldContainer = Color(0xFF664D03)
val IslamicGoldWarm = Color(0xFFE5A910)
val IslamicGoldGlow = Color(0xFFFFE082)

// Lapis Lazuli, Teal & Turquoise (Islamic ceramic & mosaic tilework / الزليج)
val IslamicLapis = Color(0xFF1B4965)
val IslamicTeal = Color(0xFF087990)
val IslamicTurquoise = Color(0xFF0D9488)
val IslamicTealContainer = Color(0xFFCFF4FC)
val IslamicOnTealContainer = Color(0xFF055160)

// Warm Alabaster, Ivory & Sand Parchment (Canvas / الورق العتيق)
val WarmIvoryBackground = Color(0xFFFBF9F5)
val SoftSurface = Color(0xFFFFFFFF)
val SoftSurfaceVariant = Color(0xFFF3EFE6)
val TextPrimary = Color(0xFF19241E)
val TextSecondary = Color(0xFF4B5D54)
val TextMuted = Color(0xFF7D8F86)
val BorderSubtle = Color(0xFFE4DDD0)
val BorderGoldSubtle = Color(0xFFDFD1B3)

// Dark Theme Palette (Obsidian Forest, Midnight Velvet & Luminous Accents)
val DarkBackground = Color(0xFF0A130F)
val DarkSurface = Color(0xFF122019)
val DarkSurfaceVariant = Color(0xFF192C23)
val DarkSurfaceCard = Color(0xFF15261F)
val DarkTextPrimary = Color(0xFFF0F5F2)
val DarkTextSecondary = Color(0xFFA2B6AB)
val DarkTextMuted = Color(0xFF6B8074)
val DarkEmeraldPrimary = Color(0xFF4ADE80)
val DarkEmeraldContainer = Color(0xFF0F5132)
val DarkGoldPrimary = Color(0xFFFACC15)
val DarkGoldContainer = Color(0xFF5C4400)
val DarkOutline = Color(0xFF284436)

// Special Spiritual & Quranic Tones
val KaabaCharcoal = Color(0xFF1F2421)
val MeccaGold = Color(0xFFFFD700)
val FajrDawnBlue = Color(0xFF1E3A8A)
val MaghribAmber = Color(0xFFEA580C)
val QiblaGreen = Color(0xFF10B981)
val BookmarkRibbonRed = Color(0xFFDC2626)

// Luxury Gradients
object IslamicGradients {
    val emeraldLush = Brush.linearGradient(
        colors = listOf(IslamicEmeraldDark, IslamicEmerald, IslamicEmeraldMedium)
    )
    val royalGold = Brush.linearGradient(
        colors = listOf(IslamicGoldDark, IslamicGold, IslamicGoldWarm, IslamicGoldLight)
    )
    val meccaNight = Brush.verticalGradient(
        colors = listOf(Color(0xFF0D1B14), Color(0xFF1A3326), Color(0xFF0D1B14))
    )
    val cardParchment = Brush.linearGradient(
        colors = listOf(SoftSurface, SoftSurfaceVariant)
    )
    val goldBorder = Brush.linearGradient(
        colors = listOf(IslamicGold.copy(alpha = 0.6f), IslamicGoldLight.copy(alpha = 0.2f), IslamicGold.copy(alpha = 0.6f))
    )
}

@Immutable
data class ExtendedIslamicColors(
    val quranAyahNumber: Color,
    val goldAccent: Color,
    val goldAccentGlow: Color,
    val emeraldAccent: Color,
    val qiblaIndicator: Color,
    val prayerCountdownBg: Color,
    val tasbihBeadActive: Color,
    val cardSurfaceGlow: Color,
    val decorativeBorder: Color
)

val LocalIslamicColors = staticCompositionLocalOf {
    ExtendedIslamicColors(
        quranAyahNumber = IslamicGoldDark,
        goldAccent = IslamicGold,
        goldAccentGlow = IslamicGoldGlow,
        emeraldAccent = IslamicEmerald,
        qiblaIndicator = QiblaGreen,
        prayerCountdownBg = IslamicEmeraldDark,
        tasbihBeadActive = IslamicEmeraldMedium,
        cardSurfaceGlow = IslamicGoldContainer,
        decorativeBorder = BorderGoldSubtle
    )
}
