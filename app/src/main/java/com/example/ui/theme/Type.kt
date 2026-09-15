package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.sp

// ==========================================
// Islamic & Arabic Inspired Typography System
// ==========================================

// Standard Material 3 Typography tailored for balanced Arabic & English rendering
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 54.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
        textDirection = TextDirection.Rtl
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 42.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.Rtl
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 34.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.Rtl
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.Rtl
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.Rtl
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.Rtl
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.Rtl
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
        textDirection = TextDirection.Rtl
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp,
        textDirection = TextDirection.Rtl
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.25.sp,
        textDirection = TextDirection.Rtl
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.25.sp,
        textDirection = TextDirection.Rtl
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.4.sp,
        textDirection = TextDirection.Rtl
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        textDirection = TextDirection.Rtl
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        textDirection = TextDirection.Rtl
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        textDirection = TextDirection.Rtl
    )
)

// Dedicated Islamic Specialized Typography Styles
@Immutable
data class IslamicTypographyTokens(
    val quranAyahUthmani: TextStyle,
    val surahHeaderTitle: TextStyle,
    val basmalahCalligraphy: TextStyle,
    val hadithMatnArabic: TextStyle,
    val prayerCountdownDigits: TextStyle,
    val prayerCardTime: TextStyle,
    val tasbihCounterBig: TextStyle,
    val tafsirExegesis: TextStyle,
    val hijriCalendarHeader: TextStyle
)

val IslamicTypography = IslamicTypographyTokens(
    quranAyahUthmani = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Right,
        textDirection = TextDirection.Rtl
    ),
    surahHeaderTitle = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 36.sp,
        textAlign = TextAlign.Center,
        textDirection = TextDirection.Rtl
    ),
    basmalahCalligraphy = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 34.sp,
        textAlign = TextAlign.Center,
        textDirection = TextDirection.Rtl
    ),
    hadithMatnArabic = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        lineHeight = 30.sp,
        textAlign = TextAlign.Right,
        textDirection = TextDirection.Rtl
    ),
    prayerCountdownDigits = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = 1.sp,
        textAlign = TextAlign.Center
    ),
    prayerCardTime = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.5.sp
    ),
    tasbihCounterBig = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 58.sp,
        lineHeight = 64.sp,
        letterSpacing = (-1).sp,
        textAlign = TextAlign.Center
    ),
    tafsirExegesis = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 26.sp,
        textAlign = TextAlign.Right,
        textDirection = TextDirection.Rtl
    ),
    hijriCalendarHeader = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        textDirection = TextDirection.Rtl
    )
)

val LocalIslamicTypography = staticCompositionLocalOf { IslamicTypography }
