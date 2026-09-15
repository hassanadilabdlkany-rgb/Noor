package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CityLocation
import com.example.data.model.SalahStep
import com.example.data.model.WuduStep
import com.example.data.repository.IslamicDataProvider
import com.example.ui.theme.IslamicEmerald
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldDark
import com.example.ui.viewmodel.UiState
import kotlin.math.abs

@Composable
fun PrayersAndQiblaScreen(
    uiState: UiState,
    onOpenCityDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableStateOf(0) } // 0: Times & Qibla, 1: Wudu Guide, 2: Salah Guide
    var simulatedCompassHeading by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("prayers_qibla_screen")
    ) {
        // Section Selector Tabs
        TabRow(
            selectedTabIndex = selectedSection,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedSection == 0,
                onClick = { selectedSection = 0 },
                text = { Text("المواقيت والقبلة", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = selectedSection == 1,
                onClick = { selectedSection = 1 },
                text = { Text("صفة الوضوء", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = selectedSection == 2,
                onClick = { selectedSection = 2 },
                text = { Text("صفة الصلاة", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
        }

        when (selectedSection) {
            0 -> TimesAndQiblaContent(
                uiState = uiState,
                simulatedHeading = simulatedCompassHeading,
                onHeadingChange = { simulatedCompassHeading = it },
                onOpenCityDialog = onOpenCityDialog
            )
            1 -> WuduGuideContent(steps = IslamicDataProvider.wuduSteps)
            2 -> SalahGuideContent(steps = IslamicDataProvider.salahSteps)
        }
    }
}

@Composable
fun TimesAndQiblaContent(
    uiState: UiState,
    simulatedHeading: Float,
    onHeadingChange: (Float) -> Unit,
    onOpenCityDialog: () -> Unit
) {
    val qiblaAngle = uiState.selectedCity.qiblaAngle
    val angleDiff = abs((simulatedHeading - qiblaAngle + 180) % 360 - 180)
    val isFacingQibla = angleDiff < 8f

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // City Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenCityDialog() },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "مواقيت الصلاة في ${uiState.selectedCity.nameArabic}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${uiState.selectedCity.countryArabic} • زاوية القبلة: ${uiState.selectedCity.qiblaAngle.toInt()}° شمالاً",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.EditLocation,
                        contentDescription = "تغيير",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Qibla Compass Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("qibla_compass_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "بوصلة اتجاه القبلة المشرفة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "وجه هاتفك حتى يتطابق المؤشر الذهبي مع اتجاه الكعبة",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Compass Graphic
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .background(if (isFacingQibla) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                            val center = Offset(size.width / 2, size.height / 2)
                            val radius = size.minDimension / 2

                            // Outer dial ring
                            drawCircle(
                                color = Color(0xFFD5CEBE),
                                radius = radius,
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }

                        // Rotating Needle Pointer
                        val needleRotation = qiblaAngle - simulatedHeading
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .rotate(needleRotation),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxHeight().padding(vertical = 16.dp)
                            ) {
                                // Kaaba indicator (Top)
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isFacingQibla) IslamicEmerald else Color.Black),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "🕋",
                                        fontSize = 16.sp
                                    )
                                }

                                // Needle Center Dot
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(IslamicGold)
                                )

                                // South Tail
                                Box(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .height(20.dp)
                                        .background(Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        color = if (isFacingQibla) Color(0xFF15803D) else MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isFacingQibla) "✓ أنت تواجه اتجاه القبلة تماماً الآن!"
                            else "زاوية القبلة: ${qiblaAngle.toInt()}° (التوجيه الحالي: ${simulatedHeading.toInt()}°)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isFacingQibla) Color.White else MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Compass Heading Adjustment Slider
                    Text(
                        text = "محاكاة تدوير البوصلة:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Slider(
                        value = simulatedHeading,
                        onValueChange = onHeadingChange,
                        valueRange = 0f..360f,
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )
                }
            }
        }

        // Full Prayer Times Schedule
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("prayer_schedule_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "جدول أوقات الصلاة الدقيقة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "حسب تقويم أم القرى والرابطة الإسلامية",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    uiState.prayerSchedule.times.forEach { prayer ->
                        val isHighlighted = prayer.isNext
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isHighlighted) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (prayer.name == "Fajr" || prayer.name == "Sunrise") Icons.Default.WbTwilight
                                        else if (prayer.name == "Dhuhr" || prayer.name == "Asr") Icons.Default.WbSunny
                                        else Icons.Default.Nightlight,
                                        contentDescription = null,
                                        tint = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "صلاة ${prayer.arabicName}",
                                        fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isHighlighted) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.padding(end = 8.dp)
                                        ) {
                                            Text(
                                                text = "القادمة",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = prayer.timeFormatted,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WuduGuideContent(steps: List<WuduStep>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
    ) {
        item {
            Text(
                text = "دليل الوضوء الصحيح خطوة بخطوة بالسنن والأدعية",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
        items(steps) { step ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${step.stepNumber}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = step.titleArabic,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = step.descriptionArabic,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "🌟 ${step.duaOrSunnah}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SalahGuideContent(steps: List<SalahStep>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
    ) {
        item {
            Text(
                text = "دليل صفة صلاة النبي ﷺ من التكبير إلى التسليم",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
        items(steps) { step ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${step.stepNumber}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = step.nameArabic,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = step.descriptionArabic,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "📿 يُقال: ${step.recitationArabic}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
