package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Reciter
import com.example.data.repository.AyatDataProvider
import com.example.data.repository.QuranDataProvider
import com.example.ui.viewmodel.UiState

// ---------------------------------------------------------------------------
// 1. Reciter Selection Dialog ("اختيار القارئ")
// ---------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyatReciterDialog(
    currentReciter: Reciter,
    onSelectReciter: (Reciter) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF131416)),
            color = Color(0xFF131416)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "اختيار القارئ",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color(0xFF00ADB5))
                    }
                }

                HorizontalDivider(color = Color(0xFF232528), thickness = 1.dp)

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    // التفسير
                    item {
                        Text(
                            text = "التفسير",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8E9297),
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                        )
                    }
                    val tafsirReciters = AyatDataProvider.allReciters.take(1)
                    items(tafsirReciters, key = { it.id }) { reciter ->
                        ReciterItemRow(
                            reciter = reciter,
                            isSelected = currentReciter.id == reciter.id,
                            onClick = {
                                onSelectReciter(reciter)
                                onDismiss()
                            }
                        )
                    }

                    // التلاوات
                    item {
                        Text(
                            text = "التلاوات",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8E9297),
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                        )
                    }
                    val audioReciters = AyatDataProvider.allReciters.drop(1).dropLast(1)
                    items(audioReciters, key = { it.id }) { reciter ->
                        ReciterItemRow(
                            reciter = reciter,
                            isSelected = currentReciter.id == reciter.id,
                            onClick = {
                                onSelectReciter(reciter)
                                onDismiss()
                            }
                        )
                    }

                    // الإنجليزية
                    item {
                        Text(
                            text = "الإنجليزية",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8E9297),
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                        )
                    }
                    val englishReciters = AyatDataProvider.allReciters.takeLast(1)
                    items(englishReciters, key = { it.id }) { reciter ->
                        ReciterItemRow(
                            reciter = reciter,
                            isSelected = currentReciter.id == reciter.id,
                            onClick = {
                                onSelectReciter(reciter)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReciterItemRow(
    reciter: Reciter,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFF00ADB5),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = reciter.nameArabic,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) Color(0xFF00ADB5) else Color.White
                )
                if (reciter.styleArabic.isNotEmpty()) {
                    Text(
                        text = reciter.styleArabic,
                        fontSize = 11.sp,
                        color = Color(0xFF8E9297)
                    )
                }
            }
        }

        Icon(
            imageVector = if (isSelected) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
            contentDescription = null,
            tint = if (isSelected) Color(0xFF00ADB5) else Color(0xFF5A5D61),
            modifier = Modifier.size(22.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// 2. Today's Verse Dialog ("اليوم")
// ---------------------------------------------------------------------------
@Composable
fun AyatTodayVerseDialog(
    hijriDate: String,
    onDismiss: () -> Unit
) {
    var isTafsirExpanded by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF131416)),
            color = Color(0xFF131416)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "اليوم",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color(0xFF00ADB5))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Date Badges (Hijri & Gregorian)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF232528))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("ربيع الآخر", fontSize = 11.sp, color = Color(0xFF8E9297))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("٤", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF232528))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("سبتمبر", fontSize = 11.sp, color = Color(0xFF8E9297))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("١٥", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Share icon row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("آية اليوم", fontSize = 12.sp, color = Color(0xFF8E9297))
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString("﴿ إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ ۝٥ ﴾"))
                            isCopied = true
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isCopied) Icons.Default.Check else Icons.Default.Share,
                            contentDescription = "مشاركة",
                            tint = Color(0xFF00ADB5),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Card with Verse
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2023))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "﴿ إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ ۝٥ ﴾",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 36.sp,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (isTafsirExpanded) "إخفاء التفسير" else "التفسير",
                            color = Color(0xFF00ADB5),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { isTafsirExpanded = !isTafsirExpanded }
                        )

                        AnimatedVisibility(visible = isTafsirExpanded) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                Text(
                                    text = "تفسير السعدي: نخصك وحدك بالعبادة والطاعة، ونخصك وحدك بطلب العون والتوفيق في سائر أمورنا. وتقديم المفعول يفيد الحصر والقصر، أي لا نعبد غيرك ولا نستعين بسواك، فتحقق التوحيد الخالص.",
                                    fontSize = 13.sp,
                                    lineHeight = 22.sp,
                                    color = Color.LightGray,
                                    textAlign = TextAlign.Right
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 3. Search Dialog ("البحث")
// ---------------------------------------------------------------------------
@Composable
fun AyatSearchDialog(
    onNavigateToPage: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    // Instant surah results
    val matchingSurahs = remember(searchQuery) {
        if (searchQuery.trim().isEmpty()) emptyList()
        else {
            val q = searchQuery.trim()
            QuranDataProvider.surahList.filter {
                it.nameArabic.contains(q) || it.id.toString() == q
            }
        }
    }

    // Instant ayah results across the whole Quran
    val matchingAyahs = remember(searchQuery) {
        if (searchQuery.trim().length < 2) emptyList()
        else {
            if (com.example.data.repository.QuranDatabaseHelper.isReady()) {
                com.example.data.repository.QuranDatabaseHelper.searchAyahs(searchQuery.trim(), limit = 60)
            } else emptyList()
        }
    }

    val pageNumberQuery = searchQuery.trim().toIntOrNull()?.takeIf { it in 1..604 }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF131416)),
            color = Color(0xFF131416)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Search Input Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color(0xFF00ADB5)
                        )
                    }

                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("ابحث في القرآن الكريم...", color = Color.Gray) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF00ADB5),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "مسح", tint = Color.Gray)
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFF232528), thickness = 1.dp)

                LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    // Page Jump
                    if (pageNumberQuery != null) {
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onNavigateToPage(pageNumberQuery)
                                        onDismiss()
                                    }
                                    .padding(horizontal = 20.dp, vertical = 12.dp),
                                color = Color(0xFF1E2023),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "الانتقال مباشرة إلى الصفحة $pageNumberQuery",
                                        color = Color(0xFF00ADB5),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = null,
                                        tint = Color(0xFF00ADB5)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // Matching Surahs
                    if (matchingSurahs.isNotEmpty()) {
                        item {
                            Text(
                                text = "السور (${matchingSurahs.size})",
                                color = Color(0xFF8E9297),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                        }
                        items(matchingSurahs) { surah ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onNavigateToPage(surah.pageNumber)
                                        onDismiss()
                                    }
                                    .padding(horizontal = 24.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "سورة ${surah.nameArabic}",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "صفحة ${surah.pageNumber}",
                                    color = Color(0xFF00ADB5),
                                    fontSize = 13.sp
                                )
                            }
                            HorizontalDivider(color = Color(0xFF1E2023), thickness = 0.5.dp)
                        }
                    }

                    // Matching Ayahs
                    if (matchingAyahs.isNotEmpty()) {
                        item {
                            Text(
                                text = "الآيات (${matchingAyahs.size})",
                                color = Color(0xFF8E9297),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                        }
                        items(matchingAyahs) { res ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onNavigateToPage(res.page)
                                        onDismiss()
                                    }
                                    .padding(horizontal = 20.dp, vertical = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${res.surahName}: آية ${res.numberInSurah}",
                                        color = Color(0xFF00ADB5),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "صفحة ${res.page}",
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "﴿ ${res.textArabic} ﴾",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    lineHeight = 24.sp
                                )
                            }
                            HorizontalDivider(color = Color(0xFF1E2023), thickness = 0.5.dp)
                        }
                    }

                    // Empty state
                    if (searchQuery.isNotEmpty() && matchingSurahs.isEmpty() && matchingAyahs.isEmpty() && pageNumberQuery == null) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 60.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "لا توجد نتائج مطابقة لـ \"$searchQuery\"",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 4. Settings Dialog ("الإعدادات")
// ---------------------------------------------------------------------------
@Composable
fun AyatSettingsDialog(
    isPureBlack: Boolean,
    onTogglePureBlack: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var isDarkBackground by remember { mutableStateOf(isPureBlack) }
    var isDetailedFont by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF131416)),
            color = Color(0xFF131416)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color(0xFF00ADB5)
                        )
                    }
                    Text(
                        text = "الإعدادات",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                HorizontalDivider(color = Color(0xFF232528), thickness = 1.dp)

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    item {
                        Text("الواجهة", color = Color(0xFF8E9297), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp))
                        Text("المظهر", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("خلفية سوداء للمظهر الداكن", color = Color.White, fontSize = 14.sp)
                                Text("خلفية مصحف سوداء تماماً عند تفعيل المظهر الداكن.", color = Color.Gray, fontSize = 11.sp)
                            }
                            Checkbox(
                                checked = isDarkBackground,
                                onCheckedChange = {
                                    isDarkBackground = it
                                    onTogglePureBlack(it)
                                },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF00ADB5))
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("مظهر الخط", color = Color(0xFF8E9297), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("التفصيل الموضوعي", color = Color.White, fontSize = 14.sp)
                            Checkbox(
                                checked = isDetailedFont,
                                onCheckedChange = { isDetailedFont = it },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF00ADB5))
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("أخرى", color = Color(0xFF8E9297), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 12.dp))
                        Text("التذكيرات", color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(vertical = 10.dp))
                        Text("استعادة نسخة احتياطية", color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(vertical = 10.dp))
                        Text("إنشاء نسخة احتياطية", color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(vertical = 10.dp))
                        Text("عن تطبيق آية", color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(vertical = 10.dp))
                    }
                }
            }
        }
    }
}
