package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ayah
import com.example.data.model.Reciter
import com.example.data.model.Surah
import com.example.data.repository.AyatDataProvider
import com.example.data.repository.QuranDataProvider
import com.example.ui.theme.IslamicEmerald
import com.example.ui.theme.IslamicGold
import com.example.ui.viewmodel.IslamicViewModel
import com.example.ui.viewmodel.UiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyatMushafScreen(
    viewModel: IslamicViewModel,
    uiState: UiState,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var isControlsVisible by remember { mutableStateOf(true) }

    // Dialogs state
    var showIndexSheet by remember { mutableStateOf(false) }
    var showReciterDialog by remember { mutableStateOf(false) }
    var showTodayDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Pager for 604 pages (RTL: page 1 is rightmost)
    val pagerState = rememberPagerState(
        initialPage = (uiState.mushafCurrentPage - 1).coerceIn(0, 603),
        pageCount = { 604 }
    )

    // Sync pager state with ViewModel
    LaunchedEffect(pagerState.currentPage) {
        val targetPage = pagerState.currentPage + 1
        if (targetPage != uiState.mushafCurrentPage) {
            viewModel.setMushafPage(targetPage)
        }
    }

    // Scroll to page when ViewModel page changes externally
    LaunchedEffect(uiState.mushafCurrentPage) {
        val targetIndex = (uiState.mushafCurrentPage - 1).coerceIn(0, 603)
        if (pagerState.currentPage != targetIndex) {
            pagerState.scrollToPage(targetIndex)
        }
    }

    val currentPageNumber = pagerState.currentPage + 1
    val currentSurah = remember(currentPageNumber) { QuranDataProvider.getSurahByPage(currentPageNumber) }
    val currentJuz = remember(currentPageNumber) { QuranDataProvider.getJuzForPage(currentPageNumber) }

    // Pure black or dark grey background based on Settings
    val backgroundColor = if (uiState.isPureBlackMushaf) Color(0xFF000000) else Color(0xFF131416)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .testTag("ayat_mushaf_screen")
    ) {
        // Main Horizontal Pager for the Quran Pages
        HorizontalPager(
            state = pagerState,
            reverseLayout = true, // Right-to-Left Arabic book navigation
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isControlsVisible = !isControlsVisible
                }
        ) { pageIndex ->
            val pageNum = pageIndex + 1
            MushafPageContent(
                pageNumber = pageNum,
                isTafsirMode = uiState.isMushafTafsirMode,
                bookmarks = uiState.bookmarks,
                onStarAyah = { ayah ->
                    viewModel.toggleStarAyah(currentSurah, ayah)
                }
            )
        }

        // Hanging Ribbon Bookmark from top edge
        val ribbonBookmark = uiState.bookmarks.find { it.pageNumber == currentPageNumber && it.ribbonColor.isNotEmpty() }
        if (ribbonBookmark != null) {
            val ribbonColor = when (ribbonBookmark.ribbonColor) {
                "red" -> Color(0xFFE53935)
                "yellow" -> Color(0xFFFFB300)
                "green" -> Color(0xFF43A047)
                "blue" -> Color(0xFF1E88E5)
                else -> Color(0xFF00ADB5)
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 40.dp)
                    .width(18.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                    .background(ribbonColor)
            )
        }

        // -------------------------------------------------------------------
        // Animated Top Overlay Bar
        // -------------------------------------------------------------------
        AnimatedVisibility(
            visible = isControlsVisible,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Surface(
                color = Color(0xF0181A1C),
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Action Icons: Home, Settings, Calendar, Search
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.selectTab(com.example.ui.viewmodel.AppTab.HOME) },
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(Icons.Default.Home, contentDescription = "الرئيسية", tint = Color.LightGray)
                        }
                        IconButton(onClick = { showSettingsDialog = true }, modifier = Modifier.size(38.dp)) {
                            Icon(Icons.Default.Settings, contentDescription = "الإعدادات", tint = Color.LightGray)
                        }
                        IconButton(onClick = { showTodayDialog = true }, modifier = Modifier.size(38.dp)) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "اليوم", tint = Color.LightGray)
                        }
                        IconButton(onClick = { showSearchDialog = true }, modifier = Modifier.size(38.dp)) {
                            Icon(Icons.Default.Search, contentDescription = "البحث", tint = Color.LightGray)
                        }
                    }

                    // Center Title
                    Text(
                        text = if (uiState.isMushafTafsirMode) "المختصر" else currentSurah.nameArabic,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    // Right Icon: Index (☰)
                    IconButton(onClick = { showIndexSheet = true }, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.Menu, contentDescription = "الفهرس", tint = Color.LightGray)
                    }
                }
            }
        }

        // -------------------------------------------------------------------
        // Animated Bottom Overlay Bar
        // -------------------------------------------------------------------
        AnimatedVisibility(
            visible = isControlsVisible,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                color = Color(0xF0181A1C),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Row 1: Mode toggle, Undo, Page Scrubber Slider
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tafsir Mode / Mushaf Mode Toggle
                        IconButton(
                            onClick = { viewModel.toggleMushafTafsirMode() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (uiState.isMushafTafsirMode) Icons.Default.MenuBook else Icons.Default.Description,
                                contentDescription = "تبديل وضع التفسير",
                                tint = if (uiState.isMushafTafsirMode) Color(0xFF00ADB5) else Color.LightGray
                            )
                        }

                        // Undo jump button
                        IconButton(
                            onClick = {
                                viewModel.setMushafPage(uiState.mushafPreviousPage)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Undo,
                                contentDescription = "تراجع للصفحة السابقة",
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Current Page Badge
                        Text(
                            text = "$currentPageNumber",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.width(30.dp),
                            textAlign = TextAlign.Center
                        )

                        // Slider 1..604
                        Slider(
                            value = currentPageNumber.toFloat(),
                            onValueChange = { target ->
                                viewModel.setMushafPage(target.toInt())
                            },
                            valueRange = 1f..604f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF00ADB5),
                                activeTrackColor = Color(0xFF00ADB5),
                                inactiveTrackColor = Color(0xFF33373B)
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        // Max Page Badge (٦٠٤)
                        Text(
                            text = "٦٠٤",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }

                    HorizontalDivider(color = Color(0xFF282B2F), thickness = 1.dp)

                    // Row 2: Audio Player & Reciter Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Play/Pause Button
                        IconButton(
                            onClick = {
                                if (uiState.audioState.isPlaying) {
                                    viewModel.toggleAudioPlayPause()
                                } else {
                                    viewModel.playRecitation(currentSurah)
                                }
                            },
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = if (uiState.audioState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "تشغيل الصوت",
                                tint = Color(0xFF00ADB5),
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // Reciter selector pill (e.g. "ناصر القطامي" or "المختصر الصوتي")
                        Surface(
                            onClick = { showReciterDialog = true },
                            color = Color(0xFF23262A),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = uiState.audioState.reciter.nameArabic,
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------------
        // Dialogs Integration
        // -------------------------------------------------------------------
        if (showIndexSheet) {
            AyatIndexBottomSheet(
                uiState = uiState,
                currentPage = currentPageNumber,
                onDismiss = { showIndexSheet = false },
                onNavigateToPage = { page ->
                    viewModel.setMushafPage(page)
                    showIndexSheet = false
                },
                onPinRibbon = { color, page, surahName ->
                    viewModel.setRibbonBookmark(color, page, surahName)
                },
                onClearRibbon = { color ->
                    viewModel.clearRibbonBookmark(color)
                },
                onDeleteBookmark = { bookmark ->
                    viewModel.deleteBookmark(bookmark)
                }
            )
        }

        if (showReciterDialog) {
            AyatReciterDialog(
                currentReciter = uiState.audioState.reciter,
                onSelectReciter = { reciter ->
                    viewModel.changeReciter(reciter)
                },
                onDismiss = { showReciterDialog = false }
            )
        }

        if (showTodayDialog) {
            AyatTodayVerseDialog(
                hijriDate = "٤ ربيع الآخر",
                onDismiss = { showTodayDialog = false }
            )
        }

        if (showSearchDialog) {
            AyatSearchDialog(
                onNavigateToPage = { page ->
                    viewModel.setMushafPage(page)
                    showSearchDialog = false
                },
                onDismiss = { showSearchDialog = false }
            )
        }

        if (showSettingsDialog) {
            AyatSettingsDialog(
                isPureBlack = uiState.isPureBlackMushaf,
                onTogglePureBlack = { enabled ->
                    viewModel.togglePureBlackMushaf(enabled)
                },
                onDismiss = { showSettingsDialog = false }
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Page Content (Mushaf View vs Al-Mukhtasar Tafsir View)
// ---------------------------------------------------------------------------
@Composable
private fun MushafPageContent(
    pageNumber: Int,
    isTafsirMode: Boolean,
    bookmarks: List<com.example.data.local.BookmarkEntity>,
    onStarAyah: (Ayah) -> Unit
) {
    val surah = remember(pageNumber) { QuranDataProvider.getSurahByPage(pageNumber) }
    val juz = remember(pageNumber) { QuranDataProvider.getJuzForPage(pageNumber) }
    val hizbQuarter = remember(pageNumber) { AyatDataProvider.getHizbQuarterLabelForPage(pageNumber) }
    val ayahs = remember(pageNumber) { AyatDataProvider.getPageAyahs(pageNumber) }
    val benefits = remember(pageNumber) { AyatDataProvider.pageBenefitsMap[pageNumber] }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Page Top Header: Right: Juz, Left: Surah
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "الجزء $juz",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF8E9297)
            )
            Text(
                text = surah.nameArabic,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF8E9297)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Center Content: Either Classical Mushaf or Al-Mukhtasar Tafsir
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (!isTafsirMode) {
                // Classical Mushaf Mode
                ClassicalMushafPage(
                    pageNumber = pageNumber,
                    surah = surah,
                    ayahs = ayahs,
                    onStarAyah = onStarAyah
                )
            } else {
                // Al-Mukhtasar fi Tafsir Mode
                AlMukhtasarTafsirPage(
                    pageNumber = pageNumber,
                    ayahs = ayahs,
                    benefits = benefits
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Page Bottom Footer: Right: Hizb quarter, Center: Page Number badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = hizbQuarter,
                fontSize = 11.sp,
                color = Color(0xFF8E9297)
            )

            // Centered Page Number badge
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF23262A)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$pageNumber",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(20.dp))
        }
    }
}

// ---------------------------------------------------------------------------
// 1. Classical Mushaf Page Layout
// ---------------------------------------------------------------------------
@Composable
private fun ClassicalMushafPage(
    pageNumber: Int,
    surah: Surah,
    ayahs: List<Ayah>,
    onStarAyah: (Ayah) -> Unit
) {
    val scrollState = rememberScrollState()
    val isFirstPageOfSurah = surah.pageNumber == pageNumber

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Decorative Surah Frame if this is the start of a Surah
        if (isFirstPageOfSurah) {
            SurahOrnateHeader(surah = surah)
            Spacer(modifier = Modifier.height(12.dp))

            // Basmala (except for Surah At-Tawbah 9)
            if (surah.id != 9) {
                Text(
                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFECEFF1),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Verses Text formatted as continuous Quranic flow
        val continuousText = buildString {
            ayahs.forEach { ayah ->
                val ayahMarker = getArabicNumberSymbol(ayah.numberInSurah)
                append("${ayah.textArabic} $ayahMarker ")
            }
        }

        Text(
            text = continuousText,
            fontSize = if (pageNumber <= 2) 23.sp else 21.sp,
            lineHeight = if (pageNumber <= 2) 42.sp else 38.sp,
            textAlign = TextAlign.Center,
            color = Color(0xFFF0F2F5),
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// 2. Al-Mukhtasar fi Tafsir Mode Layout (shown in video 02:22 - 02:34)
// ---------------------------------------------------------------------------
@Composable
private fun AlMukhtasarTafsirPage(
    pageNumber: Int,
    ayahs: List<Ayah>,
    benefits: com.example.data.repository.PageBenefits?
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ayahs.forEach { ayah ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Ayah Arabic Text with Symbol
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "${ayah.textArabic} ${getArabicNumberSymbol(ayah.numberInSurah)}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 28.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Tafsir Al-Mukhtasar Card
                Surface(
                    color = Color(0xFF1E2023),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "المختصر في التفسير",
                            color = Color(0xFF00ADB5),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ayah.tafsirSaadi,
                            fontSize = 13.sp,
                            lineHeight = 22.sp,
                            color = Color(0xFFCFD3D8),
                            textAlign = TextAlign.Right
                        )
                    }
                }
            }
        }

        // Benefits of the Page Card ("من فوائد الصفحة")
        if (benefits != null && benefits.benefits.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF23272B))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "من فوائد الصفحة $pageNumber",
                        color = Color(0xFF00ADB5),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    benefits.benefits.forEach { benefit ->
                        Text(
                            text = "• $benefit",
                            fontSize = 12.sp,
                            lineHeight = 20.sp,
                            color = Color.LightGray,
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ---------------------------------------------------------------------------
// Ornate Surah Frame Header
// ---------------------------------------------------------------------------
@Composable
private fun SurahOrnateHeader(surah: Surah) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .height(56.dp)
            .border(
                width = 1.5.dp,
                color = Color(0xFF7A6843),
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF2B2519), Color(0xFF1E1A12), Color(0xFF2B2519))
                ),
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "آياتها ${surah.versesCount}",
                fontSize = 10.sp,
                color = Color(0xFFD4AF37)
            )
            Text(
                text = "سُورَةُ ${surah.nameArabic}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF5E6C8)
            )
            Text(
                text = surah.revelationType.arabicName,
                fontSize = 10.sp,
                color = Color(0xFFD4AF37)
            )
        }
    }
}

// Converts a standard number to Arabic ornamental ayah end symbol: ۝١, ۝٢, etc.
private fun getArabicNumberSymbol(number: Int): String {
    val arabicDigits = mapOf(
        '0' to '٠', '1' to '١', '2' to '٢', '3' to '٣', '4' to '٤',
        '5' to '٥', '6' to '٦', '7' to '٧', '8' to '٨', '9' to '٩'
    )
    val converted = number.toString().map { arabicDigits[it] ?: it }.joinToString("")
    return "۝$converted"
}
