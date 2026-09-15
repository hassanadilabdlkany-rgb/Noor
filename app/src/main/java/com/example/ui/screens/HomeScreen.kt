package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Ayah
import com.example.data.model.PrayerTime
import com.example.data.repository.IslamicDataProvider
import com.example.data.repository.QuranDataProvider
import com.example.ui.theme.IslamicEmerald
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldDark
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.UiState

@Composable
fun HomeScreen(
    uiState: UiState,
    onNavigateTab: (AppTab) -> Unit,
    onOpenSurah: (Int) -> Unit,
    onShowTafsir: (Ayah) -> Unit,
    onTogglePrayerLog: (String) -> Unit,
    onTapTasbih: () -> Unit,
    onOpenCityDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val dailyAyah = remember {
        QuranDataProvider.getSurahAyahs(1).firstOrNull() ?: Ayah(
            1, 1, 1,
            "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
            "أبتدئ قراءتي مستعينا باسم الله الرحمن الرحيم.",
            "البسملة آية من الفاتحة ومن كل سورة خلا التوبة."
        )
    }
    val dailyHadith = remember { IslamicDataProvider.nawawiHadiths.first() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_content"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card with Mosque Illustration & Prayer Countdown
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .testTag("hero_mosque_card"),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_mosque),
                        contentDescription = "مسجد مضاء",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Gradient Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.3f),
                                        Color(0xFF0A2E1E).copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = "نور الإسلامي",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${uiState.selectedCity.nameArabic} • ${uiState.prayerSchedule.hijriDateArabic}",
                                    fontSize = 12.sp,
                                    color = IslamicGold,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            IconButton(
                                onClick = onOpenCityDialog,
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "تغيير المدينة",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        val nextPrayer = uiState.prayerSchedule.times.getOrNull(uiState.prayerSchedule.nextPrayerIndex)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.18f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(IslamicGold),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AccessTime,
                                            contentDescription = "الصلاة القادمة",
                                            tint = Color(0xFF3E2C00),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "الصلاة القادمة: صلاة ${nextPrayer?.arabicName ?: "الفجر"}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "الساعة ${nextPrayer?.timeFormatted ?: "04:30"}",
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.85f)
                                        )
                                    }
                                }
                                Surface(
                                    color = Color(0xFF0F5132),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "متبقي: ${uiState.prayerSchedule.remainingFormatted}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF86EFAC),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Horizontal Prayer Times Cards Row
        item {
            Text(
                text = "مواقيت صلاة اليوم (${uiState.selectedCity.nameArabic})",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(uiState.prayerSchedule.times) { prayer ->
                    PrayerTimeMiniCard(prayer = prayer)
                }
            }
        }

        // Quick Feature Tiles Grid
        item {
            Text(
                text = "الوصول السريع",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickTile(
                    title = "المصحف",
                    subtitle = "قراءة وتدبر",
                    icon = Icons.Default.MenuBook,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    iconTint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(AppTab.QURAN) }
                )
                QuickTile(
                    title = "أذكار الصباح",
                    subtitle = "حصن المسلم",
                    icon = Icons.Default.WbSunny,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(AppTab.ADHKAR) }
                )
                QuickTile(
                    title = "المسبحة",
                    subtitle = "تسبيح ذكي",
                    icon = Icons.Default.Spa,
                    color = Color(0xFFE0F2FE),
                    iconTint = Color(0xFF0369A1),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(AppTab.ADHKAR) }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickTile(
                    title = "بوصلة القبلة",
                    subtitle = "${uiState.selectedCity.qiblaAngle.toInt()}° دقيقة",
                    icon = Icons.Default.Explore,
                    color = Color(0xFFFEF3C7),
                    iconTint = Color(0xFFB45309),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(AppTab.PRAYERS) }
                )
                QuickTile(
                    title = "الأربعون النووية",
                    subtitle = "شرح وفوائد",
                    icon = Icons.Default.AutoStories,
                    color = Color(0xFFDCFCE7),
                    iconTint = Color(0xFF15803D),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(AppTab.LIBRARY) }
                )
                QuickTile(
                    title = "سورة الكهف",
                    subtitle = "سورة رقم 18",
                    icon = Icons.Default.Star,
                    color = Color(0xFFF3E8FF),
                    iconTint = Color(0xFF7E22CE),
                    modifier = Modifier.weight(1f),
                    onClick = { onOpenSurah(18) }
                )
            }
        }

        // Daily Ayah with Tafsir
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_ayah_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "آية اليوم المباركة",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "سورة الفاتحة : 1",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = dailyAyah.textArabic,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = dailyAyah.tafsirSaadi,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { onShowTafsir(dailyAyah) }) {
                            Text("تفسير ابن كثير", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(dailyAyah.textArabic))
                        }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }

        // Daily 5 Prayers & Sunan Checklist Tracker
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_prayers_tracker_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "مُتتبع عبادات اليوم",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "حافظ على الصلوات الخمس والسنن الراتبة",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        val completedCount = listOf(
                            uiState.todayPrayerLog.fajr,
                            uiState.todayPrayerLog.dhuhr,
                            uiState.todayPrayerLog.asr,
                            uiState.todayPrayerLog.maghrib,
                            uiState.todayPrayerLog.isha,
                            uiState.todayPrayerLog.duha,
                            uiState.todayPrayerLog.morningAzkar,
                            uiState.todayPrayerLog.eveningAzkar
                        ).count { it }

                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "$completedCount / 8 منجز",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PrayerCheckChip("الفجر", uiState.todayPrayerLog.fajr) { onTogglePrayerLog("fajr") }
                        PrayerCheckChip("الظهر", uiState.todayPrayerLog.dhuhr) { onTogglePrayerLog("dhuhr") }
                        PrayerCheckChip("العصر", uiState.todayPrayerLog.asr) { onTogglePrayerLog("asr") }
                        PrayerCheckChip("المغرب", uiState.todayPrayerLog.maghrib) { onTogglePrayerLog("maghrib") }
                        PrayerCheckChip("العشاء", uiState.todayPrayerLog.isha) { onTogglePrayerLog("isha") }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PrayerCheckChip("صلاة الضحى", uiState.todayPrayerLog.duha) { onTogglePrayerLog("duha") }
                        PrayerCheckChip("أذكار الصباح", uiState.todayPrayerLog.morningAzkar) { onTogglePrayerLog("morningAzkar") }
                        PrayerCheckChip("أذكار المساء", uiState.todayPrayerLog.eveningAzkar) { onTogglePrayerLog("eveningAzkar") }
                    }
                }
            }
        }

        // Quick Tasbih Tap Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTapTasbih() }
                    .testTag("quick_tasbih_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "المسبحة السريعة",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = uiState.tasbihDhikrPhrase,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "انقر للتسبيح • الهدف: ${uiState.tasbihTarget}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${uiState.tasbihCount}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Hadith of the Day Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_hadith_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "حديث اليوم النبوي",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = IslamicGoldDark
                        )
                        Text(
                            text = dailyHadith.narrator,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = dailyHadith.hadithText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = dailyHadith.explanation,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun PrayerTimeMiniCard(prayer: PrayerTime) {
    val isHighlighted = prayer.isNext
    val bgColor by animateColorAsState(
        if (isHighlighted) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surface
    )

    Card(
        modifier = Modifier
            .width(85.dp)
            .padding(vertical = 2.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(if (isHighlighted) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = prayer.arabicName,
                fontSize = 12.sp,
                fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Medium,
                color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = prayer.timeFormatted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            if (isHighlighted) {
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = "القادمة",
                        fontSize = 9.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun QuickTile(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(84.dp),
        shape = RoundedCornerShape(16.dp),
        color = color
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun PrayerCheckChip(
    title: String,
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        onClick = onToggle,
        shape = RoundedCornerShape(10.dp),
        color = if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isChecked) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal,
                color = if (isChecked) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
