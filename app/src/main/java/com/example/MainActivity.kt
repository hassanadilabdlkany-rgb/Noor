package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AudioRecitationPlayerBar
import com.example.ui.components.CitySelectionDialog
import com.example.ui.components.IslamicBottomNavBar
import com.example.ui.components.IslamicTopAppBar
import com.example.ui.components.TafsirBottomSheet
import com.example.ui.screens.*
import com.example.ui.theme.NoorAlIslamiTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.IslamicViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: IslamicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.data.repository.QuranDatabaseHelper.init(applicationContext)
        enableEdgeToEdge()

        setContent {
            NoorAlIslamiTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                var showCityDialog by remember { mutableStateOf(false) }

                if (uiState.currentTab == AppTab.QURAN) {
                    BackHandler {
                        viewModel.selectTab(AppTab.HOME)
                    }
                    AyatMushafScreen(
                        viewModel = viewModel,
                        uiState = uiState
                    )
                } else if (uiState.selectedSurah != null) {
                    BackHandler {
                        viewModel.closeSurahReader()
                    }
                    QuranReaderScreen(
                        surah = uiState.selectedSurah!!,
                        ayahs = uiState.currentSurahAyahs,
                        uiState = uiState,
                        onBack = { viewModel.closeSurahReader() },
                        onShowTafsir = { ayah -> viewModel.showTafsirForAyah(ayah) },
                        onToggleBookmark = { surah, ayah -> viewModel.toggleBookmark(surah, ayah) },
                        onTogglePageBookmark = { page, surah -> viewModel.togglePageBookmark(page, surah) },
                        onSaveReadingProgress = { surah, ayah, page -> viewModel.saveReadingProgress(surah, ayah, page) },
                        onNextSurah = { viewModel.nextSurah() },
                        onPreviousSurah = { viewModel.previousSurah() },
                        onJumpToPage = { page -> viewModel.openPage(page) },
                        onPlayAudio = { surah -> viewModel.playRecitation(surah) },
                        onChangeFontSize = { delta -> viewModel.changeQuranFontSize(delta) }
                    )
                } else {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("main_app_scaffold"),
                        topBar = {
                            IslamicTopAppBar(
                                currentTab = uiState.currentTab,
                                selectedCity = uiState.selectedCity,
                                hijriDate = uiState.prayerSchedule.hijriDateArabic,
                                onCityClick = { showCityDialog = true }
                            )
                        },
                        bottomBar = {
                            Column {
                                AudioRecitationPlayerBar(
                                    audioState = uiState.audioState,
                                    onTogglePlayPause = { viewModel.toggleAudioPlayPause() },
                                    onClose = { viewModel.stopAudio() }
                                )
                                IslamicBottomNavBar(
                                    currentTab = uiState.currentTab,
                                    onTabSelected = { tab -> viewModel.selectTab(tab) }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (uiState.currentTab) {
                                AppTab.HOME -> HomeScreen(
                                    uiState = uiState,
                                    onNavigateTab = { tab -> viewModel.selectTab(tab) },
                                    onOpenSurah = { surahId ->
                                        val surah = uiState.surahs.find { it.id == surahId }
                                        if (surah != null) viewModel.openSurah(surah)
                                    },
                                    onShowTafsir = { ayah -> viewModel.showTafsirForAyah(ayah) },
                                    onTogglePrayerLog = { prayerKey -> viewModel.togglePrayerLog(prayerKey) },
                                    onTapTasbih = { viewModel.tapTasbih() },
                                    onOpenCityDialog = { showCityDialog = true }
                                )
                                AppTab.QURAN -> QuranScreen(
                                    uiState = uiState,
                                    onSearchChange = { q -> viewModel.searchQuran(q) },
                                    onSurahSelected = { surah -> viewModel.openSurah(surah) },
                                    onPlayAudio = { surah -> viewModel.playRecitation(surah) },
                                    onChangeReciter = { reciter -> viewModel.changeReciter(reciter) },
                                    onOpenPage = { page -> viewModel.openPage(page) },
                                    onDeleteBookmark = { bookmark -> viewModel.deleteBookmark(bookmark) },
                                    onAddTodayPage = { viewModel.addTodayPageRead() },
                                    onRemoveTodayPage = { viewModel.removeTodayPageRead() }
                                )
                                AppTab.PRAYERS -> PrayersAndQiblaScreen(
                                    uiState = uiState,
                                    onOpenCityDialog = { showCityDialog = true }
                                )
                                AppTab.ADHKAR -> AdhkarAndTasbihScreen(
                                    uiState = uiState,
                                    onCategorySelected = { cat -> viewModel.selectAdhkarCategory(cat) },
                                    onIncrementDhikr = { item -> viewModel.incrementDhikrItem(item) },
                                    onResetDhikr = { item -> viewModel.resetDhikrItem(item) },
                                    onTapTasbih = { viewModel.tapTasbih() },
                                    onResetTasbih = { viewModel.resetTasbihSession() },
                                    onSetTasbihPhrase = { phrase -> viewModel.setTasbihPhrase(phrase) },
                                    onSetTasbihTarget = { target -> viewModel.setTasbihTarget(target) }
                                )
                                AppTab.LIBRARY -> LibraryAndFamilyScreen(
                                    uiState = uiState,
                                    onSetSubTab = { index -> viewModel.setLibrarySubTab(index) },
                                    onAnswerQuiz = { ans -> viewModel.answerQuizQuestion(ans) },
                                    onNextQuizQuestion = { viewModel.nextQuizQuestion() },
                                    onRestartQuiz = { viewModel.restartQuiz() },
                                    onCalculateZakat = { c, g, s, b, d -> viewModel.calculateZakat(c, g, s, b, d) },
                                    onUpdateKhatmah = { juz, pages -> viewModel.updateKhatmahProgress(juz, pages) }
                                )
                            }
                        }
                    }
                }

                // City Selection Modal Dialog
                if (showCityDialog) {
                    CitySelectionDialog(
                        currentCity = uiState.selectedCity,
                        onCitySelected = { city -> viewModel.selectCity(city) },
                        onDismiss = { showCityDialog = false }
                    )
                }

                // Tafsir Sheet
                if (uiState.activeAyahForTafsir != null) {
                    TafsirBottomSheet(
                        ayah = uiState.activeAyahForTafsir!!,
                        onDismiss = { viewModel.showTafsirForAyah(null) }
                    )
                }
            }
        }
    }
}
