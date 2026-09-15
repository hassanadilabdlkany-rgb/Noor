package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.outlined.RotateLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DhikrItem
import com.example.data.repository.IslamicDataProvider
import com.example.ui.theme.IslamicEmerald
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldDark
import com.example.ui.viewmodel.UiState

@Composable
fun AdhkarAndTasbihScreen(
    uiState: UiState,
    onCategorySelected: (String) -> Unit,
    onIncrementDhikr: (DhikrItem) -> Unit,
    onResetDhikr: (DhikrItem) -> Unit,
    onTapTasbih: () -> Unit,
    onResetTasbih: () -> Unit,
    onSetTasbihPhrase: (String) -> Unit,
    onSetTasbihTarget: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeSubTab by remember { mutableStateOf(0) } // 0: Hisn al-Muslim Adhkar, 1: Smart Digital Tasbih
    val context = LocalContext.current
    val vibrator = remember {
        try {
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        } catch (e: Exception) {
            null
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("adhkar_tasbih_screen")
    ) {
        // Tab selector: Adhkar vs Digital Tasbih
        TabRow(
            selectedTabIndex = activeSubTab,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = activeSubTab == 0,
                onClick = { activeSubTab = 0 },
                text = { Text("حصن المسلم والأذكار", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
            Tab(
                selected = activeSubTab == 1,
                onClick = { activeSubTab = 1 },
                text = { Text("المسبحة الإلكترونية الذكية", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
        }

        if (activeSubTab == 0) {
            HisnAlMuslimSection(
                uiState = uiState,
                onCategorySelected = onCategorySelected,
                onIncrementDhikr = {
                    vibrateBriefly(vibrator)
                    onIncrementDhikr(it)
                },
                onResetDhikr = onResetDhikr
            )
        } else {
            SmartDigitalTasbihSection(
                uiState = uiState,
                onTap = {
                    vibrateBriefly(vibrator)
                    onTapTasbih()
                },
                onReset = onResetTasbih,
                onSetPhrase = onSetTasbihPhrase,
                onSetTarget = onSetTasbihTarget
            )
        }
    }
}

@Composable
fun HisnAlMuslimSection(
    uiState: UiState,
    onCategorySelected: (String) -> Unit,
    onIncrementDhikr: (DhikrItem) -> Unit,
    onResetDhikr: (DhikrItem) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val categories = IslamicDataProvider.adhkarCategories

    Column(modifier = Modifier.fillMaxSize()) {
        // Categories Horizontal Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            items(categories) { category ->
                val isSelected = uiState.selectedAdhkarCategory == category.id
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelected(category.id) },
                    label = { Text("${category.iconEmoji} ${category.nameArabic}") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        // Adhkar Items List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
        ) {
            items(uiState.currentAdhkarItems, key = { it.id }) { dhikr ->
                val isCompleted = dhikr.currentCount >= dhikr.countTarget
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dhikr_card_${dhikr.id}"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCompleted) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Header info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = if (isCompleted) Color(0xFF15803D) else MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (isCompleted) "تم الإنجاز ✓" else "التكرار المطلوب: ${dhikr.countTarget}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCompleted) Color.White else MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onResetDhikr(dhikr) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = "إعادة ضبط", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(
                                    onClick = { clipboardManager.setText(AnnotatedString(dhikr.textArabic)) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Dhikr Arabic Text
                        Text(
                            text = dhikr.textArabic,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 28.sp,
                            textAlign = TextAlign.Right,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Reward Virtue & Hadith Source
                        Text(
                            text = "✨ الفضل: ${dhikr.rewardVirtue}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "📚 المصدر: ${dhikr.sourceHadith}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Tap Counter Button with Linear Progress
                        val progress = if (dhikr.countTarget > 0) dhikr.currentCount.toFloat() / dhikr.countTarget else 0f
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (isCompleted) Color(0xFF15803D) else MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { onIncrementDhikr(dhikr) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("dhikr_tap_button_${dhikr.id}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCompleted) Color(0xFF15803D) else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = if (isCompleted) "مكتمل (${dhikr.currentCount}/${dhikr.countTarget})" else "اضغط للذكر (${dhikr.currentCount} / ${dhikr.countTarget})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SmartDigitalTasbihSection(
    uiState: UiState,
    onTap: () -> Unit,
    onReset: () -> Unit,
    onSetPhrase: (String) -> Unit,
    onSetTarget: (Int) -> Unit
) {
    val phrases = listOf(
        "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
        "سُبْحَانَ اللَّهِ الْعَظِيمِ",
        "الْحَمْدُ لِلَّهِ",
        "لَا إِلَهَ إِلَّا اللَّهُ",
        "اللَّهُ أَكْبَرُ",
        "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
        "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
        "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Selected Dhikr Display Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "الذكر الحالي",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiState.tasbihDhikrPhrase,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Preset Phrase Selector Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(phrases) { phrase ->
                    val isSelected = uiState.tasbihDhikrPhrase == phrase
                    SuggestionChip(
                        onClick = { onSetPhrase(phrase) },
                        label = { Text(phrase, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }
        }

        // Target count selector
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("الهدف: ", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                listOf(33, 99, 100, 1000).forEach { target ->
                    val isSelected = uiState.tasbihTarget == target
                    TextButton(
                        onClick = { onSetTarget(target) },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(
                            text = "$target",
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = if (isSelected) 16.sp else 13.sp
                        )
                    }
                }
            }
        }

        // Giant Circular Interactive Tasbih Counter
        item {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { onTap() }
                    .testTag("tasbih_giant_button"),
                contentAlignment = Alignment.Center
            ) {
                // Outer ring
                Box(
                    modifier = Modifier
                        .size(210.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0A3622)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${uiState.tasbihCount}",
                            fontSize = 54.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "من ${uiState.tasbihTarget}",
                            fontSize = 14.sp,
                            color = IslamicGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "المس للتسبيح",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Total All-Time & Reset
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "إجمالي التسبيحات الكلي: ${uiState.tasbihTotalAllTime}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }

                FilledTonalButton(
                    onClick = onReset,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "تصفير الجلسة", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تصفير", fontSize = 12.sp)
                }
            }
        }
    }
}

private fun vibrateBriefly(vibrator: Vibrator?) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(30)
        }
    } catch (e: Exception) {
        // Vibration not supported or allowed
    }
}
