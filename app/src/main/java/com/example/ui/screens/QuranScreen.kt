package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.data.model.QuranPageInfo
import com.example.data.model.Reciter
import com.example.data.model.RevelationType
import com.example.data.model.Surah
import com.example.data.repository.IslamicDataProvider
import com.example.data.repository.QuranDataProvider
import com.example.ui.theme.IslamicEmerald
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldDark
import com.example.ui.theme.IslamicLapis
import com.example.ui.viewmodel.UiState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    uiState: UiState,
    onSearchChange: (String) -> Unit,
    onSurahSelected: (Surah) -> Unit,
    onPlayAudio: (Surah) -> Unit,
    onChangeReciter: (Reciter) -> Unit,
    onOpenPage: (Int) -> Unit = {},
    onDeleteBookmark: (BookmarkEntity) -> Unit = {},
    onAddTodayPage: () -> Unit = {},
    onRemoveTodayPage: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTopTab by remember { mutableIntStateOf(0) } // 0: Surahs, 1: Pages Browser, 2: Bookmarks
    var selectedSurahFilter by remember { mutableStateOf("all") } // all, meccan, medinan
    var selectedJuzFilter by remember { mutableIntStateOf(0) } // 0: All, 1..30
    var bookmarkSubFilter by remember { mutableStateOf("all") } // all, pages, ayahs
    var showReciterDialog by remember { mutableStateOf(false) }

    // Filtered Surahs
    val filteredSurahs = remember(uiState.surahs, uiState.searchQuery, selectedSurahFilter, selectedJuzFilter) {
        var list = uiState.surahs
        if (uiState.searchQuery.isNotEmpty()) {
            list = list.filter {
                it.nameArabic.contains(uiState.searchQuery) ||
                it.nameEnglish.contains(uiState.searchQuery, ignoreCase = true) ||
                it.id.toString() == uiState.searchQuery ||
                "جزء ${it.juzNumber}".contains(uiState.searchQuery)
            }
        }
        if (selectedJuzFilter > 0) {
            list = list.filter { it.juzNumber == selectedJuzFilter }
        }
        when (selectedSurahFilter) {
            "meccan" -> list.filter { it.revelationType == RevelationType.MECCAN }
            "medinan" -> list.filter { it.revelationType == RevelationType.MEDINAN }
            else -> list
        }
    }

    // Filtered Pages
    val filteredPages = remember(QuranDataProvider.allPages, uiState.searchQuery, selectedJuzFilter) {
        var list = QuranDataProvider.allPages
        if (selectedJuzFilter > 0) {
            list = list.filter { it.juzNumber == selectedJuzFilter }
        }
        if (uiState.searchQuery.isNotEmpty()) {
            list = list.filter {
                it.pageNumber.toString().contains(uiState.searchQuery) ||
                it.surahName.contains(uiState.searchQuery)
            }
        }
        list
    }

    // Filtered Bookmarks
    val filteredBookmarks = remember(uiState.bookmarks, bookmarkSubFilter, uiState.searchQuery) {
        var list = uiState.bookmarks
        if (bookmarkSubFilter == "pages") {
            list = list.filter { it.isPageBookmark }
        } else if (bookmarkSubFilter == "ayahs") {
            list = list.filter { !it.isPageBookmark }
        }
        if (uiState.searchQuery.isNotEmpty()) {
            list = list.filter {
                it.surahName.contains(uiState.searchQuery) ||
                it.ayahText.contains(uiState.searchQuery) ||
                it.pageNumber.toString() == uiState.searchQuery
            }
        }
        list
    }

    val pageBookmarksCount = remember(uiState.bookmarks) {
        uiState.bookmarks.count { it.isPageBookmark }
    }
    val ayahBookmarksCount = remember(uiState.bookmarks) {
        uiState.bookmarks.count { !it.isPageBookmark }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("quran_screen_content")
    ) {
        // Quran Reading Progress & Daily Wird Banner
        QuranReadingProgressBanner(
            uiState = uiState,
            onContinueReading = {
                val surah = uiState.surahs.find { it.id == uiState.khatmahPlan.lastReadSurahId }
                    ?: uiState.surahs.firstOrNull()
                if (surah != null) {
                    onSurahSelected(surah)
                }
            },
            onAddTodayPage = onAddTodayPage,
            onRemoveTodayPage = onRemoveTodayPage
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Top Tabs: 0: السور, 1: الصفحات (604 صفحة), 2: الإشارات المرجعية
        PrimaryTabRow(
            selectedTabIndex = selectedTopTab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTopTab == 0,
                onClick = { selectedTopTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("السور (${uiState.surahs.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            )
            Tab(
                selected = selectedTopTab == 1,
                onClick = { selectedTopTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FindInPage, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تصفح الصفحات", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            )
            Tab(
                selected = selectedTopTab == 2,
                onClick = { selectedTopTab = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp), tint = IslamicGoldDark)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("الإشارات (${uiState.bookmarks.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar (Available for all tabs)
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchChange,
            placeholder = {
                Text(
                    text = when (selectedTopTab) {
                        0 -> "ابحث عن اسم السورة، رقمها أو الجزء..."
                        1 -> "ابحث برقم الصفحة (1-604) أو السورة..."
                        else -> "ابحث في الإشارات المحفوظة والآيات..."
                    }
                )
            },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "بحث") },
            trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "مسح")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("quran_search_field"),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Tab Content
        when (selectedTopTab) {
            0 -> {
                // SURAHS TAB
                SurahsListContent(
                    filteredSurahs = filteredSurahs,
                    selectedFilter = selectedSurahFilter,
                    onFilterSelect = { selectedSurahFilter = it },
                    selectedJuz = selectedJuzFilter,
                    onJuzSelect = { selectedJuzFilter = it },
                    uiState = uiState,
                    onSurahSelected = onSurahSelected,
                    onPlayAudio = onPlayAudio,
                    onOpenReciterDialog = { showReciterDialog = true }
                )
            }
            1 -> {
                // PAGES BROWSER TAB (604 Pages)
                PagesBrowserContent(
                    pages = filteredPages,
                    uiState = uiState,
                    selectedJuz = selectedJuzFilter,
                    onJuzSelect = { selectedJuzFilter = it },
                    onOpenPage = onOpenPage
                )
            }
            2 -> {
                // BOOKMARKS TAB
                BookmarksContent(
                    bookmarks = filteredBookmarks,
                    subFilter = bookmarkSubFilter,
                    onSubFilterChange = { bookmarkSubFilter = it },
                    pageBookmarksCount = pageBookmarksCount,
                    ayahBookmarksCount = ayahBookmarksCount,
                    onOpenSurah = onSurahSelected,
                    onOpenPage = onOpenPage,
                    onDeleteBookmark = onDeleteBookmark,
                    surahs = uiState.surahs
                )
            }
        }
    }

    // Reciter Selection Modal Dialog
    if (showReciterDialog) {
        AlertDialog(
            onDismissRequest = { showReciterDialog = false },
            title = {
                Text(
                    text = "اختر القارئ المفضل للتلاوة",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    IslamicDataProvider.reciters.forEach { reciter ->
                        val isSelected = uiState.audioState.reciter.id == reciter.id
                        Surface(
                            onClick = {
                                onChangeReciter(reciter)
                                showReciterDialog = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(reciter.nameArabic, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(reciter.styleArabic, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showReciterDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }
}

// ---------------------------------------------------------------------------
// Reading Progress & Daily Wird Banner
// ---------------------------------------------------------------------------
@Composable
private fun QuranReadingProgressBanner(
    uiState: UiState,
    onContinueReading: () -> Unit,
    onAddTodayPage: () -> Unit,
    onRemoveTodayPage: () -> Unit
) {
    val khatmah = uiState.khatmahPlan
    val completedPages = khatmah.completedPages.coerceIn(0, 604)
    val progressPercent = (completedPages.toFloat() / 604f * 100).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quran_progress_banner"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row: Last Read Info & Continue Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "آخر موضع قراءة",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                        )
                        Text(
                            text = "سورة ${khatmah.lastReadSurahName} • ص ${khatmah.lastReadPage} • آية ${khatmah.lastReadAyah}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Button(
                    onClick = onContinueReading,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("continue_reading_button")
                ) {
                    Text("واصل القراءة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Progress Bar for Quran Completion
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "ختمة القرآن: $completedPages من 604 صفحة ($progressPercent%)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "الجزء ${khatmah.lastReadJuz} من 30",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
                LinearProgressIndicator(
                    progress = { (completedPages / 604f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = IslamicGold,
                    trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                )
            }

            // Daily Wird Tracker row
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircleOutline,
                            contentDescription = null,
                            tint = IslamicEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "الورد اليومي: ${khatmah.todayPagesRead} من ${khatmah.dailyPagesGoal} صفحات",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = onRemoveTodayPage,
                            enabled = khatmah.todayPagesRead > 0,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "إنقاص صفحة", modifier = Modifier.size(16.dp))
                        }
                        FilledTonalButton(
                            onClick = onAddTodayPage,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("+1 صفحة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Surahs List Tab Content
// ---------------------------------------------------------------------------
@Composable
private fun SurahsListContent(
    filteredSurahs: List<Surah>,
    selectedFilter: String,
    onFilterSelect: (String) -> Unit,
    selectedJuz: Int,
    onJuzSelect: (Int) -> Unit,
    uiState: UiState,
    onSurahSelected: (Surah) -> Unit,
    onPlayAudio: (Surah) -> Unit,
    onOpenReciterDialog: () -> Unit
) {
    Column {
        // Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    FilterChip(
                        selected = selectedFilter == "all",
                        onClick = { onFilterSelect("all") },
                        label = { Text("جميع السور") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == "meccan",
                        onClick = { onFilterSelect("meccan") },
                        label = { Text("مكية") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == "medinan",
                        onClick = { onFilterSelect("medinan") },
                        label = { Text("مدنية") }
                    )
                }
            }

            IconButton(
                onClick = onOpenReciterDialog,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = "اختيار القارئ",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Juz Filter Row (1 to 30)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        ) {
            item {
                SuggestionChip(
                    onClick = { onJuzSelect(0) },
                    label = { Text("كل الأجزاء", fontSize = 11.sp) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (selectedJuz == 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    )
                )
            }
            items(30) { index ->
                val juzNum = index + 1
                SuggestionChip(
                    onClick = { onJuzSelect(if (selectedJuz == juzNum) 0 else juzNum) },
                    label = { Text("الجزء $juzNum", fontSize = 11.sp) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (selectedJuz == juzNum) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    )
                )
            }
        }

        // Surahs List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("surah_list"),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(filteredSurahs, key = { it.id }) { surah ->
                val isBookmarked = uiState.bookmarks.any { it.surahId == surah.id }
                val isLastRead = uiState.khatmahPlan.lastReadSurahId == surah.id

                SurahListItem(
                    surah = surah,
                    isBookmarked = isBookmarked,
                    isLastRead = isLastRead,
                    onClick = { onSurahSelected(surah) },
                    onPlayClick = { onPlayAudio(surah) }
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Pages Browser Content (1 to 604)
// ---------------------------------------------------------------------------
@Composable
private fun PagesBrowserContent(
    pages: List<QuranPageInfo>,
    uiState: UiState,
    selectedJuz: Int,
    onJuzSelect: (Int) -> Unit,
    onOpenPage: (Int) -> Unit
) {
    Column {
        // Juz filter chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        ) {
            item {
                SuggestionChip(
                    onClick = { onJuzSelect(0) },
                    label = { Text("كل الأجزاء (604 صفحات)", fontSize = 11.sp) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (selectedJuz == 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    )
                )
            }
            items(30) { index ->
                val juzNum = index + 1
                SuggestionChip(
                    onClick = { onJuzSelect(if (selectedJuz == juzNum) 0 else juzNum) },
                    label = { Text("الجزء $juzNum", fontSize = 11.sp) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (selectedJuz == juzNum) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    )
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            modifier = Modifier
                .fillMaxSize()
                .testTag("pages_grid")
        ) {
            items(pages, key = { it.pageNumber }) { pageInfo ->
                val isBookmarked = uiState.bookmarks.any { it.isPageBookmark && it.pageNumber == pageInfo.pageNumber }
                val isCurrentLastRead = uiState.khatmahPlan.lastReadPage == pageInfo.pageNumber

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenPage(pageInfo.pageNumber) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isCurrentLastRead -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                            isBookmarked -> IslamicGold.copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.surface
                        }
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "صفحة ${pageInfo.pageNumber}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (isBookmarked) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "إشارة محفوظة",
                                    tint = IslamicGoldDark,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Text(
                            text = "سورة ${pageInfo.surahName}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "الجزء ${pageInfo.juzNumber}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "قراءة",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Bookmarks Tab Content
// ---------------------------------------------------------------------------
@Composable
private fun BookmarksContent(
    bookmarks: List<BookmarkEntity>,
    subFilter: String,
    onSubFilterChange: (String) -> Unit,
    pageBookmarksCount: Int,
    ayahBookmarksCount: Int,
    onOpenSurah: (Surah) -> Unit,
    onOpenPage: (Int) -> Unit,
    onDeleteBookmark: (BookmarkEntity) -> Unit,
    surahs: List<Surah>
) {
    Column {
        // Sub-filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = subFilter == "all",
                onClick = { onSubFilterChange("all") },
                label = { Text("الكل (${pageBookmarksCount + ayahBookmarksCount})") }
            )
            FilterChip(
                selected = subFilter == "pages",
                onClick = { onSubFilterChange("pages") },
                label = { Text("إشارات الصفحات ($pageBookmarksCount)") },
                leadingIcon = {
                    Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp), tint = IslamicGoldDark)
                }
            )
            FilterChip(
                selected = subFilter == "ayahs",
                onClick = { onSubFilterChange("ayahs") },
                label = { Text("إشارات الآيات ($ayahBookmarksCount)") },
                leadingIcon = {
                    Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            )
        }

        if (bookmarks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(IslamicGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = IslamicGoldDark,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Text(
                        text = "لا توجد إشارات مرجعية محفوظة حالياً",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "يمكنك حفظ إشارات مرجعية لأي صفحة أو آية أثناء القراءة للرجوع إليها في أي وقت بسهولة.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("bookmarks_list"),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(bookmarks, key = { it.id }) { bookmark ->
                    val sdf = remember { SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale.getDefault()) }
                    val formattedDate = remember(bookmark.timestamp) { sdf.format(Date(bookmark.timestamp)) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (bookmark.isPageBookmark) {
                                    onOpenPage(bookmark.pageNumber)
                                } else {
                                    val surah = surahs.find { it.id == bookmark.surahId }
                                    if (surah != null) onOpenSurah(surah)
                                }
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (bookmark.isPageBookmark) IslamicGold.copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = if (bookmark.isPageBookmark) IslamicGoldDark else MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = if (bookmark.isPageBookmark) "🔖 صفحة ${bookmark.pageNumber}" else "آية ${bookmark.ayahNumber}",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "سورة ${bookmark.surahName}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• الجزء ${bookmark.juzNumber}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                IconButton(
                                    onClick = { onDeleteBookmark(bookmark) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "حذف الإشارة",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            if (!bookmark.isPageBookmark && bookmark.ayahText.isNotEmpty()) {
                                Text(
                                    text = bookmark.ayahText,
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = formattedDate,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                FilledTonalButton(
                                    onClick = {
                                        if (bookmark.isPageBookmark) {
                                            onOpenPage(bookmark.pageNumber)
                                        } else {
                                            val surah = surahs.find { it.id == bookmark.surahId }
                                            if (surah != null) onOpenSurah(surah)
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text(
                                        text = if (bookmark.isPageBookmark) "فتح الصفحة" else "قراءة الآية",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
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

// ---------------------------------------------------------------------------
// Surah List Item
// ---------------------------------------------------------------------------
@Composable
fun SurahListItem(
    surah: Surah,
    isBookmarked: Boolean,
    isLastRead: Boolean = false,
    onClick: () -> Unit,
    onPlayClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("surah_item_${surah.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLastRead) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Surah Number Gold Frame
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            if (isLastRead) IslamicEmerald
                            else MaterialTheme.colorScheme.primaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${surah.id}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isLastRead) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "سورة ${surah.nameArabic}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (isLastRead) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = IslamicEmerald.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "آخر قراءة",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IslamicEmerald,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        if (isBookmarked) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "محفوظة",
                                tint = IslamicGoldDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = "${surah.nameEnglish} • ${surah.revelationType.arabicName} • ${surah.versesCount} آية • ص ${surah.pageNumber} • الجزء ${surah.juzNumber}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onPlayClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "استماع",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
