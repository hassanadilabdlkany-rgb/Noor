package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ayah
import com.example.data.model.Surah
import com.example.ui.theme.IslamicEmerald
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldDark
import com.example.ui.viewmodel.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranReaderScreen(
    surah: Surah,
    ayahs: List<Ayah>,
    uiState: UiState,
    onBack: () -> Unit,
    onShowTafsir: (Ayah) -> Unit,
    onToggleBookmark: (Surah, Ayah) -> Unit,
    onTogglePageBookmark: (Int, Surah) -> Unit = { _, _ -> },
    onSaveReadingProgress: (Surah, Int, Int) -> Unit = { _, _, _ -> },
    onNextSurah: () -> Unit = {},
    onPreviousSurah: () -> Unit = {},
    onJumpToPage: (Int) -> Unit = {},
    onPlayAudio: (Surah) -> Unit,
    onChangeFontSize: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var copiedAyahId by remember { mutableStateOf<Int?>(null) }
    var showJumpToPageDialog by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    // Check if the current page has a page bookmark
    val isPageBookmarked = remember(uiState.bookmarks, uiState.currentReaderPage) {
        uiState.bookmarks.any { it.isPageBookmark && it.pageNumber == uiState.currentReaderPage }
    }

    // Check if this Surah & Ayah is the current last read position
    val isCurrentLastRead = remember(uiState.khatmahPlan, surah.id) {
        uiState.khatmahPlan.lastReadSurahId == surah.id
    }

    // Auto-scroll to target ayah if requested
    LaunchedEffect(uiState.targetAyahToScroll) {
        uiState.targetAyahToScroll?.let { targetNumber ->
            val index = ayahs.indexOfFirst { it.numberInSurah == targetNumber }
            if (index >= 0) {
                // index + 1 accounting for the header item
                listState.animateScrollToItem(index + 1)
            }
        }
    }

    // Clear feedback snackbar after 2.5 seconds
    LaunchedEffect(snackbarMessage) {
        if (snackbarMessage != null) {
            delay(2500)
            snackbarMessage = null
        }
    }

    Scaffold(
        modifier = modifier.testTag("quran_reader_screen"),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "سورة ${surah.nameArabic}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = IslamicGold.copy(alpha = 0.18f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "ص ${uiState.currentReaderPage}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = IslamicGoldDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${surah.revelationType.arabicName} • ${surah.versesCount} آية • الجزء ${uiState.currentReaderJuz}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("reader_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع"
                            )
                        }
                    },
                    actions = {
                        // Bookmark Page Action
                        IconButton(
                            onClick = {
                                onTogglePageBookmark(uiState.currentReaderPage, surah)
                                snackbarMessage = if (isPageBookmarked) {
                                    "تمت إزالة إشارة الصفحة ${uiState.currentReaderPage}"
                                } else {
                                    "تم حفظ إشارة مرجعية للصفحة ${uiState.currentReaderPage} 🔖"
                                }
                            },
                            modifier = Modifier.testTag("bookmark_page_button")
                        ) {
                            Icon(
                                imageVector = if (isPageBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "حفظ إشارة للصفحة",
                                tint = if (isPageBookmarked) IslamicGoldDark else MaterialTheme.colorScheme.primary
                            )
                        }

                        // Jump to page dialog
                        IconButton(
                            onClick = { showJumpToPageDialog = true },
                            modifier = Modifier.testTag("jump_to_page_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FindInPage,
                                contentDescription = "انتقال لصفحة",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Font Size Controls
                        IconButton(onClick = { onChangeFontSize(-2f) }) {
                            Icon(Icons.Default.TextDecrease, contentDescription = "تصغير الخط")
                        }
                        IconButton(onClick = { onChangeFontSize(2f) }) {
                            Icon(Icons.Default.TextIncrease, contentDescription = "تكبير الخط")
                        }

                        // Audio
                        IconButton(onClick = { onPlayAudio(surah) }) {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = "استماع للسورة",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Subtle reading progress indicator across top
                val progressFraction = if (surah.versesCount > 0) {
                    ((listState.firstVisibleItemIndex.toFloat() / maxOf(1, ayahs.size)).coerceIn(0f, 1f))
                } else 0f
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = IslamicGold,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                )
            }
        },
        bottomBar = {
            // Navigation Bar for Previous/Next Surah & Jump Page
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Surah Button
                    TextButton(
                        onClick = onPreviousSurah,
                        enabled = surah.id > 1,
                        modifier = Modifier.testTag("prev_surah_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "السورة السابقة",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("السابقة", fontSize = 13.sp)
                    }

                    // Quick Page Indicator / Jump Action
                    FilledTonalButton(
                        onClick = { showJumpToPageDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "صفحة ${uiState.currentReaderPage} / 604",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    // Next Surah Button
                    TextButton(
                        onClick = onNextSurah,
                        enabled = surah.id < 114,
                        modifier = Modifier.testTag("next_surah_button")
                    ) {
                        Text("التالية", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "السورة التالية",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .testTag("quran_reader_list"),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
            ) {
                // Reading Tracker & Surah Header Banner
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Quick Action Card: Track Progress & Bookmark Page
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = if (isPageBookmarked) IslamicGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "صفحة ${uiState.currentReaderPage}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isPageBookmarked) IslamicGoldDark else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "الجزء ${uiState.currentReaderJuz}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    // Bookmark Page button
                                    OutlinedButton(
                                        onClick = {
                                            onTogglePageBookmark(uiState.currentReaderPage, surah)
                                            snackbarMessage = if (isPageBookmarked) {
                                                "تمت إزالة إشارة الصفحة ${uiState.currentReaderPage}"
                                            } else {
                                                "تم حفظ إشارة مرجعية للصفحة ${uiState.currentReaderPage} 🔖"
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = if (isPageBookmarked) IslamicGoldDark else MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Icon(
                                            imageVector = if (isPageBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isPageBookmarked) "إشارة محفوظة" else "حفظ الصفحة",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    // Set as Last Read Position
                                    Button(
                                        onClick = {
                                            val firstAyahNum = ayahs.firstOrNull()?.numberInSurah ?: 1
                                            onSaveReadingProgress(surah, firstAyahNum, uiState.currentReaderPage)
                                            snackbarMessage = "تم تعيين موضع القراءة الحالي: سورة ${surah.nameArabic} 📍"
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isCurrentLastRead) IslamicEmerald else MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Icon(
                                            imageVector = if (isCurrentLastRead) Icons.Default.BookmarkAdded else Icons.Default.PinDrop,
                                            contentDescription = null,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isCurrentLastRead) "موضعك الحالي" else "تثبيت موضع القراءة",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // Surah Header Banner
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "سورة ${surah.nameArabic}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "${surah.nameEnglish} • ترتيب النزول: ${surah.id} • ${surah.versesCount} آية",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                                if (surah.id != 9 && surah.id != 1) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                        fontSize = (uiState.quranFontSize + 2).sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                // Ayahs
                items(ayahs, key = { it.numberInSurah }) { ayah ->
                    val isAyahBookmarked = uiState.bookmarks.any {
                        !it.isPageBookmark && it.surahId == surah.id && it.ayahNumber == ayah.numberInSurah
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ayah_card_${ayah.numberInSurah}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isAyahBookmarked) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Action row for Ayah
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Ayah Number Badge in Circular Islamic Style
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isAyahBookmarked) IslamicGoldDark
                                            else MaterialTheme.colorScheme.primary
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${ayah.numberInSurah}",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Set as Last Read Position on this exact Ayah
                                    IconButton(
                                        onClick = {
                                            onSaveReadingProgress(surah, ayah.numberInSurah, uiState.currentReaderPage)
                                            snackbarMessage = "تم تثبيت موضع القراءة عند الآية ${ayah.numberInSurah} 📍"
                                        },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PinDrop,
                                            contentDescription = "تثبيت موضع القراءة هنا",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(19.dp)
                                        )
                                    }

                                    // Bookmark Ayah
                                    IconButton(
                                        onClick = { onToggleBookmark(surah, ayah) },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isAyahBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                            contentDescription = "حفظ إشارة للآية",
                                            tint = if (isAyahBookmarked) IslamicGoldDark else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    // Tafsir Button
                                    IconButton(
                                        onClick = { onShowTafsir(ayah) },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MenuBook,
                                            contentDescription = "التفسير",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(19.dp)
                                        )
                                    }

                                    // Copy Button
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(ayah.textArabic))
                                            copiedAyahId = ayah.numberInSurah
                                            snackbarMessage = "تم نسخ الآية إلى الحافظة"
                                        },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (copiedAyahId == ayah.numberInSurah) Icons.Default.Check else Icons.Default.ContentCopy,
                                            contentDescription = "نسخ",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Ayah Arabic Text in classical Uthmani font
                            Text(
                                text = ayah.textArabic,
                                fontSize = uiState.quranFontSize.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = (uiState.quranFontSize * 1.75f).sp,
                                textAlign = TextAlign.Right,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Short Tafsir preview / tap for full
                            Surface(
                                onClick = { onShowTafsir(ayah) },
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoStories,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "تفسير السعدي: ${ayah.tafsirSaadi.take(90)}...",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Snackbar Notification for user feedback
            AnimatedVisibility(
                visible = snackbarMessage != null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.inverseSurface,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 6.dp
                ) {
                    Text(
                        text = snackbarMessage ?: "",
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }

    // Jump to Page Dialog
    if (showJumpToPageDialog) {
        var pageInput by remember { mutableStateOf(uiState.currentReaderPage.toString()) }
        var isError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showJumpToPageDialog = false },
            title = {
                Text(
                    text = "انتقال إلى صفحة في المصحف",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "المصحف الشريف مكون من 604 صفحات. أدخل رقم الصفحة التي تريد الانتقال إليها:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = pageInput,
                        onValueChange = { input ->
                            val filtered = input.filter { it.isDigit() }
                            pageInput = filtered
                            val pageNum = filtered.toIntOrNull()
                            isError = pageNum == null || pageNum < 1 || pageNum > 604
                        },
                        label = { Text("رقم الصفحة (1 - 604)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = isError,
                        supportingText = {
                            if (isError) {
                                Text("يرجى إدخال رقم صفحة صحيح بين 1 و 604", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Quick Jump Suggestions
                    Text("صفحات شائعة:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(1 to "الفاتحة", 2 to "البقرة", 293 to "الكهف", 440 to "يس", 562 to "الملك").forEach { (pg, name) ->
                            SuggestionChip(
                                onClick = { pageInput = pg.toString(); isError = false },
                                label = { Text("$name ($pg)", fontSize = 11.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pageNum = pageInput.toIntOrNull()
                        if (pageNum != null && pageNum in 1..604) {
                            showJumpToPageDialog = false
                            onJumpToPage(pageNum)
                        }
                    },
                    enabled = !isError && pageInput.isNotEmpty()
                ) {
                    Text("انتقال")
                }
            },
            dismissButton = {
                TextButton(onClick = { showJumpToPageDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
