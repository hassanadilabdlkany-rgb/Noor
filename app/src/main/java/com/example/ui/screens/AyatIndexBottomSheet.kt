package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookmarkEntity
import com.example.data.model.Surah
import com.example.data.repository.AyatDataProvider
import com.example.data.repository.QuranDataProvider
import com.example.ui.theme.IslamicEmerald
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldDark
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyatIndexBottomSheet(
    uiState: UiState,
    currentPage: Int,
    onDismiss: () -> Unit,
    onNavigateToPage: (Int) -> Unit,
    onPinRibbon: (color: String, page: Int, surahName: String) -> Unit,
    onClearRibbon: (color: String) -> Unit,
    onDeleteBookmark: (BookmarkEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    // 0: السور, 1: الختمة, 2: الفواصل, 3: مميزة بنجمة, 4: الملاحظات
    var selectedTab by remember { mutableIntStateOf(0) }
    // Inside tab 0: 0: السور, 1: الأرباع
    var surahSubPill by remember { mutableIntStateOf(0) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color(0xFF1E2022),
        modifier = modifier.testTag("ayat_index_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
        ) {
            // Header: Title & Close Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (selectedTab) {
                        0 -> "الفهرس"
                        1 -> "الختمة"
                        2 -> "الفواصل"
                        3 -> "مميزة بنجمة"
                        else -> "الملاحظات"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق",
                        tint = Color.LightGray
                    )
                }
            }

            // Sub-pills for "الفهرس" (السور | الأرباع)
            if (selectedTab == 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        color = Color(0xFF2C2F33),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Row(modifier = Modifier.padding(4.dp)) {
                            // الأرباع
                            Surface(
                                onClick = { surahSubPill = 1 },
                                color = if (surahSubPill == 1) Color(0xFF00ADB5) else Color.Transparent,
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = "الأرباع",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (surahSubPill == 1) Color.White else Color.Gray,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                                )
                            }
                            // السور
                            Surface(
                                onClick = { surahSubPill = 0 },
                                color = if (surahSubPill == 0) Color(0xFF00ADB5) else Color.Transparent,
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = "السور",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (surahSubPill == 0) Color.White else Color.Gray,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Main Content Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (selectedTab) {
                    0 -> {
                        if (surahSubPill == 0) {
                            // السور List
                            SurahsIndexList(
                                surahs = uiState.surahs,
                                currentPage = currentPage,
                                onSurahClick = { surah ->
                                    onNavigateToPage(surah.pageNumber)
                                    onDismiss()
                                }
                            )
                        } else {
                            // الأرباع List
                            QuartersIndexList(
                                quarters = AyatDataProvider.quranQuarters,
                                onQuarterClick = { quarter ->
                                    onNavigateToPage(quarter.pageNumber)
                                    onDismiss()
                                }
                            )
                        }
                    }
                    1 -> {
                        // الختمة Tab
                        KhatmahTabContent(
                            uiState = uiState,
                            onStartKhatmah = { /* Managed via dialog */ }
                        )
                    }
                    2 -> {
                        // الفواصل Tab (4 colored ribbons)
                        RibbonsTabContent(
                            uiState = uiState,
                            currentPage = currentPage,
                            onNavigateToPage = { page ->
                                onNavigateToPage(page)
                                onDismiss()
                            },
                            onPinRibbon = onPinRibbon,
                            onClearRibbon = onClearRibbon
                        )
                    }
                    3 -> {
                        // مميزة بنجمة Tab
                        StarredVersesTabContent(
                            bookmarks = uiState.bookmarks.filter { it.isStarred },
                            onNavigateToPage = { page ->
                                onNavigateToPage(page)
                                onDismiss()
                            },
                            onDelete = onDeleteBookmark
                        )
                    }
                    4 -> {
                        // الملاحظات Tab
                        NotesTabContent(
                            bookmarks = uiState.bookmarks.filter { it.note.isNotEmpty() },
                            onNavigateToPage = { page ->
                                onNavigateToPage(page)
                                onDismiss()
                            },
                            onDelete = onDeleteBookmark
                        )
                    }
                }
            }

            // Bottom 5 Tabs Navigation Bar matching the video exactly!
            HorizontalDivider(color = Color(0xFF2C2F33), thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .background(Color(0xFF181A1B))
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    title = "الملاحظات",
                    icon = Icons.Default.Description,
                    isSelected = selectedTab == 4,
                    onClick = { selectedTab = 4 }
                )
                BottomNavItem(
                    title = "مميزة بنجمة",
                    icon = Icons.Default.Star,
                    isSelected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
                BottomNavItem(
                    title = "الفواصل",
                    icon = Icons.Default.Bookmark,
                    isSelected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                BottomNavItem(
                    title = "الختمة",
                    icon = Icons.Default.CheckCircleOutline,
                    isSelected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                BottomNavItem(
                    title = "السور",
                    icon = Icons.Default.MenuBook,
                    isSelected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val activeColor = Color(0xFF00ADB5)
    val inactiveColor = Color(0xFF8E9297)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) activeColor else inactiveColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) activeColor else inactiveColor
        )
    }
}

// ---------------------------------------------------------------------------
// 1. Surahs List Grouped by Juz
// ---------------------------------------------------------------------------
@Composable
private fun SurahsIndexList(
    surahs: List<Surah>,
    currentPage: Int,
    onSurahClick: (Surah) -> Unit
) {
    val currentSurah = remember(currentPage) { QuranDataProvider.getSurahByPage(currentPage) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Group Surahs by Juz
        var lastJuz = -1
        surahs.forEach { surah ->
            if (surah.juzNumber != lastJuz) {
                lastJuz = surah.juzNumber
                val juzArabic = getJuzArabicName(lastJuz)
                item(key = "juz_$lastJuz") {
                    Text(
                        text = juzArabic,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF8E9297),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }
            }

            item(key = "surah_${surah.id}") {
                val isSelected = surah.id == currentSurah.id
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSurahClick(surah) }
                        .padding(horizontal = 24.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = surah.nameArabic,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "الصفحة ${surah.pageNumber} • آياتها ${surah.versesCount} • ${surah.revelationType.arabicName}",
                            fontSize = 11.sp,
                            color = Color(0xFF8E9297)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Color(0xFF00ADB5) else Color(0xFF2C2F33)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${surah.id}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 2. Quarters List (الأرباع)
// ---------------------------------------------------------------------------
@Composable
private fun QuartersIndexList(
    quarters: List<com.example.data.repository.QuranQuarter>,
    onQuarterClick: (com.example.data.repository.QuranQuarter) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        var lastJuz = -1
        quarters.forEach { quarter ->
            if (quarter.juzNumber != lastJuz) {
                lastJuz = quarter.juzNumber
                val juzArabic = getJuzArabicName(lastJuz)
                item(key = "q_juz_$lastJuz") {
                    Text(
                        text = juzArabic,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF8E9297),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }
            }

            item(key = "quarter_${quarter.number}") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onQuarterClick(quarter) }
                        .padding(horizontal = 24.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = quarter.firstWords,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "${quarter.surahName}: ${quarter.ayahNumber} • الصفحة ${quarter.pageNumber}",
                            fontSize = 11.sp,
                            color = Color(0xFF8E9297)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2C2F33)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${quarter.number}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00ADB5)
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 3. Khatmah Tab Content
// ---------------------------------------------------------------------------
@Composable
private fun KhatmahTabContent(
    uiState: UiState,
    onStartKhatmah: () -> Unit
) {
    val khatmah = uiState.khatmahPlan

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "حدّد وردك اليومي أو المدة التي تريد ختم القرآن فيها، وتابع ختمتك في شهر رمضان وطوال العام.",
                fontSize = 14.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                color = Color.LightGray
            )

            Button(
                onClick = onStartKhatmah,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00ADB5)),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(48.dp)
            ) {
                Text(
                    text = "بدء ختمة جديدة",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = Color(0xFF2C2F33),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "الختمة الحالية",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("الصفحات المقروءة:", color = Color.Gray, fontSize = 12.sp)
                        Text("${khatmah.completedPages} / 604", color = Color(0xFF00ADB5), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("آخر موضع قراءة:", color = Color.Gray, fontSize = 12.sp)
                        Text("سورة ${khatmah.lastReadSurahName} (ص ${khatmah.lastReadPage})", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 4. Ribbons Tab Content (4 colored bookmarks)
// ---------------------------------------------------------------------------
@Composable
private fun RibbonsTabContent(
    uiState: UiState,
    currentPage: Int,
    onNavigateToPage: (Int) -> Unit,
    onPinRibbon: (String, Int, String) -> Unit,
    onClearRibbon: (String) -> Unit
) {
    val ribbons = listOf(
        Triple("red", "الأحمر", Color(0xFFE53935)),
        Triple("yellow", "الأصفر", Color(0xFFFFB300)),
        Triple("green", "الأخضر", Color(0xFF43A047)),
        Triple("blue", "الأزرق", Color(0xFF1E88E5))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("الفواصل", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            Text(
                text = "تحرير",
                color = Color(0xFF00ADB5),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { /* edit mode */ }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        ribbons.forEach { (code, name, color) ->
            val bookmark = uiState.bookmarks.find { it.ribbonColor == code }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable {
                        if (bookmark != null) {
                            onNavigateToPage(bookmark.pageNumber)
                        } else {
                            val surah = QuranDataProvider.getSurahByPage(currentPage)
                            onPinRibbon(code, currentPage, surah.nameArabic)
                        }
                    },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2F33))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = name,
                            tint = color,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (bookmark != null) {
                                Text(
                                    text = "سورة ${bookmark.surahName} • الصفحة ${bookmark.pageNumber}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            } else {
                                Text(
                                    text = "اضغط لتثبيت الفاصل في الصفحة الحالية (ص $currentPage)",
                                    fontSize = 11.sp,
                                    color = Color(0xFF00ADB5)
                                )
                            }
                        }
                    }

                    if (bookmark != null) {
                        IconButton(
                            onClick = { onClearRibbon(code) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "إزالة الفاصل",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 5. Starred Verses Tab Content
// ---------------------------------------------------------------------------
@Composable
private fun StarredVersesTabContent(
    bookmarks: List<BookmarkEntity>,
    onNavigateToPage: (Int) -> Unit,
    onDelete: (BookmarkEntity) -> Unit
) {
    if (bookmarks.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.StarOutline,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(44.dp)
                )
                Text(
                    text = "لا توجد آيات مميزة بنجمة",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(bookmarks, key = { it.id }) { bookmark ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToPage(bookmark.pageNumber) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2F33))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "سورة ${bookmark.surahName} • آية ${bookmark.ayahNumber}",
                                color = Color(0xFF00ADB5),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            IconButton(
                                onClick = { onDelete(bookmark) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                        }
                        if (bookmark.ayahText.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = bookmark.ayahText,
                                color = Color.White,
                                fontSize = 13.sp,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 6. Notes Tab Content
// ---------------------------------------------------------------------------
@Composable
private fun NotesTabContent(
    bookmarks: List<BookmarkEntity>,
    onNavigateToPage: (Int) -> Unit,
    onDelete: (BookmarkEntity) -> Unit
) {
    if (bookmarks.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(44.dp)
                )
                Text(
                    text = "لا توجد ملاحظات",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(bookmarks, key = { it.id }) { bookmark ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToPage(bookmark.pageNumber) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2F33))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "ملاحظة - ص ${bookmark.pageNumber}",
                                color = Color(0xFF00ADB5),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            IconButton(
                                onClick = { onDelete(bookmark) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = bookmark.note,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

private fun getJuzArabicName(juz: Int): String {
    val names = listOf(
        "الجزء الأول", "الجزء الثاني", "الجزء الثالث", "الجزء الرابع", "الجزء الخامس",
        "الجزء السادس", "الجزء السابع", "الجزء الثامن", "الجزء التاسع", "الجزء العاشر",
        "الجزء الحادي عشر", "الجزء الثاني عشر", "الجزء الثالث عشر", "الجزء الرابع عشر", "الجزء الخامس عشر",
        "الجزء السادس عشر", "الجزء السابع عشر", "الجزء الثامن عشر", "الجزء التاسع عشر", "الجزء العشرون",
        "الجزء الحادي والعشرون", "الجزء الثاني والعشرون", "الجزء الثالث والعشرون", "الجزء الرابع والعشرون", "الجزء الخامس والعشرون",
        "الجزء السادس والعشرون", "الجزء السابع والعشرون", "الجزء الثامن والعشرون", "الجزء التاسع والعشرون", "الجزء الثلاثون"
    )
    return names.getOrElse(juz - 1) { "الجزء $juz" }
}
