package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// ==========================================
// Islamic Material 3 Color Schemes
// ==========================================

private val DarkIslamicColorScheme = darkColorScheme(
    primary = DarkEmeraldPrimary,
    onPrimary = Color(0xFF00381C),
    primaryContainer = IslamicEmeraldDark,
    onPrimaryContainer = Color(0xFF86EFAC),
    
    secondary = DarkGoldPrimary,
    onSecondary = Color(0xFF422B00),
    secondaryContainer = DarkGoldContainer,
    onSecondaryContainer = Color(0xFFFEF08A),
    
    tertiary = IslamicTurquoise,
    onTertiary = Color(0xFF00363A),
    tertiaryContainer = Color(0xFF004F55),
    onTertiaryContainer = Color(0xFF6FF7E8),
    
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    
    outline = DarkOutline,
    outlineVariant = Color(0xFF1E382C),
    scrim = Color.Black
)

private val LightIslamicColorScheme = lightColorScheme(
    primary = IslamicEmerald,
    onPrimary = Color.White,
    primaryContainer = IslamicEmeraldContainer,
    onPrimaryContainer = IslamicOnEmeraldContainer,
    
    secondary = IslamicGoldDark,
    onSecondary = Color.White,
    secondaryContainer = IslamicGoldContainer,
    onSecondaryContainer = IslamicOnGoldContainer,
    
    tertiary = IslamicTeal,
    onTertiary = Color.White,
    tertiaryContainer = IslamicTealContainer,
    onTertiaryContainer = IslamicOnTealContainer,
    
    background = WarmIvoryBackground,
    onBackground = TextPrimary,
    surface = SoftSurface,
    onSurface = TextPrimary,
    surfaceVariant = SoftSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    
    outline = BorderSubtle,
    outlineVariant = BorderGoldSubtle,
    scrim = Color.Black
)

private val LightExtendedColors = ExtendedIslamicColors(
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

private val DarkExtendedColors = ExtendedIslamicColors(
    quranAyahNumber = DarkGoldPrimary,
    goldAccent = DarkGoldPrimary,
    goldAccentGlow = Color(0xFFFDE047),
    emeraldAccent = DarkEmeraldPrimary,
    qiblaIndicator = Color(0xFF34D399),
    prayerCountdownBg = Color(0xFF0F261B),
    tasbihBeadActive = DarkEmeraldPrimary,
    cardSurfaceGlow = DarkGoldContainer,
    decorativeBorder = DarkOutline
)

@Composable
fun NoorAlIslamiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkIslamicColorScheme else LightIslamicColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(
        LocalIslamicColors provides extendedColors,
        LocalIslamicTypography provides IslamicTypography
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = IslamicShapes,
            content = content
        )
    }
}

// Unified Design System accessor for easy usage across Composables
object IslamicTheme {
    val colors: ExtendedIslamicColors
        @Composable
        @ReadOnlyComposable
        get() = LocalIslamicColors.current

    val typography: IslamicTypographyTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalIslamicTypography.current

    val gradients = IslamicGradients
    
    val shapes = IslamicShapes
}
