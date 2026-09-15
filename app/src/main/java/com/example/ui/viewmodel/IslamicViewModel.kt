package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.BookmarkEntity
import com.example.data.local.DailyPrayerLogEntity
import com.example.data.local.KhatmahPlanEntity
import com.example.data.local.TasbihRecordEntity
import com.example.data.model.*
import com.example.data.repository.IslamicDataProvider
import com.example.data.repository.IslamicRepository
import com.example.data.repository.PrayerCalculator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

enum class AppTab(val titleArabic: String) {
    HOME("الرئيسية"),
    QURAN("المصحف"),
    PRAYERS("الصلاة والقبلة"),
    ADHKAR("الأذكار والمسبحة"),
    LIBRARY("المكتبة والأسرة")
}

data class AudioPlayerState(
    val isPlaying: Boolean = false,
    val surahName: String = "",
    val surahId: Int = 1,
    val ayahNumber: Int = 1,
    val reciter: Reciter = IslamicDataProvider.reciters.first(),
    val progress: Float = 0f
)

data class ZakatState(
    val cash: Double = 0.0,
    val goldGrams: Double = 0.0,
    val silverGrams: Double = 0.0,
    val businessMerchandise: Double = 0.0,
    val debts: Double = 0.0,
    val goldGramPrice: Double = 75.0, // USD/SAR approximate standard
    val silverGramPrice: Double = 1.0,
    val calculatedZakat: Double = 0.0,
    val isNisabReached: Boolean = false
)

data class UiState(
    val currentTab: AppTab = AppTab.HOME,
    val selectedCity: CityLocation = IslamicDataProvider.availableCities.first(), // Sana'a default or Makkah
    val prayerSchedule: PrayerCalculator.PrayerScheduleResult = PrayerCalculator.calculatePrayersForCity(IslamicDataProvider.availableCities.first()),
    // Quran
    val surahs: List<Surah> = emptyList(),
    val searchQuery: String = "",
    val selectedSurah: Surah? = null,
    val currentSurahAyahs: List<Ayah> = emptyList(),
    val activeAyahForTafsir: Ayah? = null,
    val bookmarks: List<BookmarkEntity> = emptyList(),
    val quranFontSize: Float = 22f,
    val currentReaderPage: Int = 1,
    val currentReaderJuz: Int = 1,
    val isCurrentPageBookmarked: Boolean = false,
    val isPureBlackMushaf: Boolean = true,
    val isMushafTafsirMode: Boolean = false,
    val mushafCurrentPage: Int = 1,
    val mushafPreviousPage: Int = 1,
    val targetAyahToScroll: Int? = null,
    val userFeedbackMessage: String? = null,
    // Adhkar
    val selectedAdhkarCategory: String = "morning",
    val currentAdhkarItems: List<DhikrItem> = emptyList(),
    // Digital Tasbih
    val tasbihCount: Int = 0,
    val tasbihTarget: Int = 33,
    val tasbihDhikrPhrase: String = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
    val tasbihTotalAllTime: Int = 0,
    // Daily Tracker
    val todayPrayerLog: DailyPrayerLogEntity = DailyPrayerLogEntity(dateKey = ""),
    // Khatmah
    val khatmahPlan: KhatmahPlanEntity = KhatmahPlanEntity(),
    // Audio Player
    val audioState: AudioPlayerState = AudioPlayerState(),
    // Zakat
    val zakatState: ZakatState = ZakatState(),
    // Quiz
    val currentQuizIndex: Int = 0,
    val quizScore: Int = 0,
    val selectedQuizAnswer: Int? = null,
    val isQuizFinished: Boolean = false,
    // Library sub-tab
    val librarySubTab: Int = 0 // 0: Hadith, 1: Sirah/Stories, 2: Kids/Quiz, 3: Wudu/Salah, 4: Zakat & Khatmah, 5: About
)

class IslamicViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: IslamicRepository
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        com.example.data.repository.QuranDatabaseHelper.init(application.applicationContext)
        val db = AppDatabase.getInstance(application)
        repository = IslamicRepository(db.islamicDao())

        val initialSurahs = repository.getAllSurahs()
        val initialAdhkar = repository.getAdhkarByCategory("morning")

        _uiState.update {
            it.copy(
                surahs = initialSurahs,
                currentAdhkarItems = initialAdhkar,
                todayPrayerLog = DailyPrayerLogEntity(dateKey = repository.getTodayKey())
            )
        }

        observeDatabase()
        startClockPrayerTimer()
    }

    private fun observeDatabase() {
        viewModelScope.launch {
            repository.getBookmarks().collect { bookmarks ->
                _uiState.update { it.copy(bookmarks = bookmarks) }
            }
        }

        viewModelScope.launch {
            repository.getTodayPrayerLog().collect { log ->
                if (log != null) {
                    _uiState.update { it.copy(todayPrayerLog = log) }
                }
            }
        }

        viewModelScope.launch {
            repository.getKhatmahPlan().collect { plan ->
                if (plan != null) {
                    _uiState.update { it.copy(khatmahPlan = plan) }
                }
            }
        }

        viewModelScope.launch {
            repository.getTasbihRecord().collect { record ->
                if (record != null) {
                    _uiState.update {
                        it.copy(
                            tasbihTotalAllTime = record.totalCount,
                            tasbihDhikrPhrase = record.currentDhikr,
                            tasbihTarget = record.sessionTarget
                        )
                    }
                }
            }
        }
    }

    private fun startClockPrayerTimer() {
        viewModelScope.launch {
            while (true) {
                delay(30000) // Update countdown every 30s
                val city = _uiState.value.selectedCity
                val updatedSchedule = PrayerCalculator.calculatePrayersForCity(city)
                _uiState.update { it.copy(prayerSchedule = updatedSchedule) }
            }
        }
    }

    fun selectTab(tab: AppTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun setLibrarySubTab(index: Int) {
        _uiState.update { it.copy(librarySubTab = index) }
    }

    fun selectCity(city: CityLocation) {
        val schedule = PrayerCalculator.calculatePrayersForCity(city)
        _uiState.update {
            it.copy(selectedCity = city, prayerSchedule = schedule)
        }
    }

    fun searchQuran(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun openSurah(surah: Surah, targetAyah: Int? = null, targetPage: Int? = null) {
        val ayahs = repository.getAyahsForSurah(surah.id)
        val page = targetPage ?: surah.pageNumber
        val juz = surah.juzNumber
        viewModelScope.launch {
            val isPageBookmarked = repository.isPageBookmarked(page)
            _uiState.update {
                it.copy(
                    selectedSurah = surah,
                    currentSurahAyahs = ayahs,
                    currentReaderPage = page,
                    currentReaderJuz = juz,
                    mushafCurrentPage = page,
                    currentTab = AppTab.QURAN,
                    isCurrentPageBookmarked = isPageBookmarked,
                    targetAyahToScroll = targetAyah
                )
            }
        }
    }

    fun openPage(pageNumber: Int) {
        val clampedPage = pageNumber.coerceIn(1, 604)
        val surah = com.example.data.repository.QuranDataProvider.getSurahByPage(clampedPage)
        openSurah(surah, targetAyah = 1, targetPage = clampedPage)
    }

    fun nextSurah() {
        val current = _uiState.value.selectedSurah ?: return
        val next = com.example.data.repository.QuranDataProvider.getNextSurah(current.id)
        if (next != null) {
            openSurah(next)
        }
    }

    fun previousSurah() {
        val current = _uiState.value.selectedSurah ?: return
        val prev = com.example.data.repository.QuranDataProvider.getPreviousSurah(current.id)
        if (prev != null) {
            openSurah(prev)
        }
    }

    fun closeSurahReader() {
        _uiState.update {
            it.copy(selectedSurah = null, activeAyahForTafsir = null, targetAyahToScroll = null)
        }
    }

    fun showTafsirForAyah(ayah: Ayah?) {
        _uiState.update { it.copy(activeAyahForTafsir = ayah) }
    }

    fun toggleBookmark(surah: Surah, ayah: Ayah) {
        viewModelScope.launch {
            val isBookmarked = repository.isBookmarked(surah.id, ayah.numberInSurah)
            if (isBookmarked) {
                repository.removeBookmark(surah.id, ayah.numberInSurah)
                _uiState.update { it.copy(userFeedbackMessage = "تمت إزالة علامة الآية") }
            } else {
                repository.addBookmark(
                    surahId = surah.id,
                    surahName = surah.nameArabic,
                    ayahNumber = ayah.numberInSurah,
                    text = ayah.textArabic,
                    pageNumber = surah.pageNumber,
                    juzNumber = surah.juzNumber
                )
                _uiState.update { it.copy(userFeedbackMessage = "تم حفظ علامة الآية بنجاح") }
            }
        }
    }

    fun togglePageBookmark(pageNumber: Int, surah: Surah) {
        viewModelScope.launch {
            val isBookmarked = repository.isPageBookmarked(pageNumber)
            if (isBookmarked) {
                repository.removePageBookmark(pageNumber)
                _uiState.update {
                    it.copy(
                        isCurrentPageBookmarked = false,
                        userFeedbackMessage = "تمت إزالة إشارة الصفحة $pageNumber"
                    )
                }
            } else {
                repository.addPageBookmark(
                    pageNumber = pageNumber,
                    surahId = surah.id,
                    surahName = surah.nameArabic,
                    juzNumber = surah.juzNumber
                )
                _uiState.update {
                    it.copy(
                        isCurrentPageBookmarked = true,
                        userFeedbackMessage = "تم حفظ إشارة مرجعية للصفحة $pageNumber"
                    )
                }
            }
        }
    }

    fun deleteBookmark(bookmark: BookmarkEntity) {
        viewModelScope.launch {
            repository.removeBookmarkById(bookmark.id)
            if (bookmark.isPageBookmark && bookmark.pageNumber == _uiState.value.currentReaderPage) {
                _uiState.update { it.copy(isCurrentPageBookmarked = false) }
            }
        }
    }

    fun setRibbonBookmark(color: String, page: Int, surahName: String) {
        viewModelScope.launch {
            repository.setRibbonBookmark(color, page, surahName)
            _uiState.update { it.copy(userFeedbackMessage = "تم تثبيت الفاصل في الصفحة $page") }
        }
    }

    fun clearRibbonBookmark(color: String) {
        viewModelScope.launch {
            repository.clearRibbonBookmark(color)
            _uiState.update { it.copy(userFeedbackMessage = "تمت إزالة الفاصل") }
        }
    }

    fun toggleMushafTafsirMode() {
        _uiState.update { it.copy(isMushafTafsirMode = !it.isMushafTafsirMode) }
    }

    fun togglePureBlackMushaf(enabled: Boolean) {
        _uiState.update { it.copy(isPureBlackMushaf = enabled) }
    }

    fun setMushafPage(page: Int) {
        val clamped = page.coerceIn(1, 604)
        val current = _uiState.value.mushafCurrentPage
        _uiState.update {
            it.copy(
                mushafPreviousPage = current,
                mushafCurrentPage = clamped,
                currentReaderPage = clamped,
                currentReaderJuz = com.example.data.repository.QuranDataProvider.getJuzForPage(clamped)
            )
        }
    }

    fun toggleStarAyah(surah: Surah, ayah: Ayah) {
        viewModelScope.launch {
            val existing = _uiState.value.bookmarks.find { it.surahId == surah.id && it.ayahNumber == ayah.numberInSurah && it.isStarred }
            if (existing != null) {
                repository.removeBookmarkById(existing.id)
                _uiState.update { it.copy(userFeedbackMessage = "تمت إزالة النجمة") }
            } else {
                repository.toggleStarAyah(
                    surahId = surah.id,
                    surahName = surah.nameArabic,
                    ayahNumber = ayah.numberInSurah,
                    ayahText = ayah.textArabic,
                    pageNumber = surah.pageNumber,
                    juzNumber = surah.juzNumber
                )
                _uiState.update { it.copy(userFeedbackMessage = "تمت إضافة الآية إلى المميزة بنجمة") }
            }
        }
    }

    fun saveReadingProgress(surah: Surah, ayahNumber: Int, pageNumber: Int) {
        viewModelScope.launch {
            val plan = _uiState.value.khatmahPlan.copy(
                lastReadSurahId = surah.id,
                lastReadSurahName = surah.nameArabic,
                lastReadAyah = ayahNumber,
                lastReadPage = pageNumber,
                lastReadJuz = surah.juzNumber,
                lastReadTimestamp = System.currentTimeMillis(),
                completedPages = maxOf(_uiState.value.khatmahPlan.completedPages, pageNumber)
            )
            repository.saveKhatmahPlan(plan)
            _uiState.update {
                it.copy(
                    khatmahPlan = plan,
                    userFeedbackMessage = "تم تعيين موضع القراءة الحالي: سورة ${surah.nameArabic} صفحة $pageNumber"
                )
            }
        }
    }

    fun addTodayPageRead() {
        viewModelScope.launch {
            val current = _uiState.value.khatmahPlan
            val newToday = current.todayPagesRead + 1
            val newCompleted = (current.completedPages + 1).coerceAtMost(604)
            val updated = current.copy(
                todayPagesRead = newToday,
                completedPages = newCompleted
            )
            repository.saveKhatmahPlan(updated)
            _uiState.update { it.copy(khatmahPlan = updated) }
        }
    }

    fun removeTodayPageRead() {
        viewModelScope.launch {
            val current = _uiState.value.khatmahPlan
            if (current.todayPagesRead > 0) {
                val updated = current.copy(
                    todayPagesRead = current.todayPagesRead - 1,
                    completedPages = (current.completedPages - 1).coerceAtLeast(0)
                )
                repository.saveKhatmahPlan(updated)
                _uiState.update { it.copy(khatmahPlan = updated) }
            }
        }
    }

    fun clearFeedbackMessage() {
        _uiState.update { it.copy(userFeedbackMessage = null) }
    }

    fun changeQuranFontSize(delta: Float) {
        _uiState.update {
            val newSize = (it.quranFontSize + delta).coerceIn(16f, 36f)
            it.copy(quranFontSize = newSize)
        }
    }

    fun selectAdhkarCategory(categoryId: String) {
        val items = repository.getAdhkarByCategory(categoryId)
        _uiState.update {
            it.copy(selectedAdhkarCategory = categoryId, currentAdhkarItems = items)
        }
    }

    fun incrementDhikrItem(item: DhikrItem) {
        _uiState.update { state ->
            val updated = state.currentAdhkarItems.map {
                if (it.id == item.id) {
                    val nextCount = if (it.currentCount < it.countTarget) it.currentCount + 1 else it.currentCount
                    it.copy(currentCount = nextCount)
                } else it
            }
            state.copy(currentAdhkarItems = updated)
        }
    }

    fun resetDhikrItem(item: DhikrItem) {
        _uiState.update { state ->
            val updated = state.currentAdhkarItems.map {
                if (it.id == item.id) it.copy(currentCount = 0) else it
            }
            state.copy(currentAdhkarItems = updated)
        }
    }

    // Smart Digital Tasbih
    fun tapTasbih() {
        val current = _uiState.value.tasbihCount + 1
        val total = _uiState.value.tasbihTotalAllTime + 1
        val target = _uiState.value.tasbihTarget
        val newCount = if (target > 0 && current >= target) 0 else current

        _uiState.update {
            it.copy(
                tasbihCount = newCount,
                tasbihTotalAllTime = total
            )
        }

        viewModelScope.launch {
            repository.saveTasbihRecord(
                TasbihRecordEntity(
                    totalCount = total,
                    currentDhikr = _uiState.value.tasbihDhikrPhrase,
                    sessionTarget = target
                )
            )
        }
    }

    fun resetTasbihSession() {
        _uiState.update { it.copy(tasbihCount = 0) }
    }

    fun setTasbihPhrase(phrase: String) {
        _uiState.update { it.copy(tasbihDhikrPhrase = phrase, tasbihCount = 0) }
        viewModelScope.launch {
            repository.saveTasbihRecord(
                TasbihRecordEntity(
                    totalCount = _uiState.value.tasbihTotalAllTime,
                    currentDhikr = phrase,
                    sessionTarget = _uiState.value.tasbihTarget
                )
            )
        }
    }

    fun setTasbihTarget(target: Int) {
        _uiState.update { it.copy(tasbihTarget = target, tasbihCount = 0) }
    }

    // Daily Prayer Tracker
    fun togglePrayerLog(prayerKey: String) {
        val current = _uiState.value.todayPrayerLog
        val updated = when (prayerKey) {
            "fajr" -> current.copy(fajr = !current.fajr)
            "dhuhr" -> current.copy(dhuhr = !current.dhuhr)
            "asr" -> current.copy(asr = !current.asr)
            "maghrib" -> current.copy(maghrib = !current.maghrib)
            "isha" -> current.copy(isha = !current.isha)
            "duha" -> current.copy(duha = !current.duha)
            "qiyam" -> current.copy(qiyam = !current.qiyam)
            "morningAzkar" -> current.copy(morningAzkar = !current.morningAzkar)
            "eveningAzkar" -> current.copy(eveningAzkar = !current.eveningAzkar)
            else -> current
        }
        _uiState.update { it.copy(todayPrayerLog = updated) }
        viewModelScope.launch {
            repository.savePrayerLog(updated.copy(dateKey = repository.getTodayKey()))
        }
    }

    // Khatmah
    fun updateKhatmahProgress(juz: Int, pages: Int) {
        val plan = _uiState.value.khatmahPlan.copy(
            currentJuz = juz.coerceIn(1, 30),
            completedPages = pages.coerceIn(0, 604)
        )
        _uiState.update { it.copy(khatmahPlan = plan) }
        viewModelScope.launch {
            repository.saveKhatmahPlan(plan)
        }
    }

    // Audio Playback Simulation
    fun playRecitation(surah: Surah, reciter: Reciter = _uiState.value.audioState.reciter) {
        _uiState.update {
            it.copy(
                audioState = AudioPlayerState(
                    isPlaying = true,
                    surahName = surah.nameArabic,
                    surahId = surah.id,
                    ayahNumber = 1,
                    reciter = reciter,
                    progress = 0.1f
                )
            )
        }
    }

    fun toggleAudioPlayPause() {
        val current = _uiState.value.audioState
        _uiState.update {
            it.copy(audioState = current.copy(isPlaying = !current.isPlaying))
        }
    }

    fun changeReciter(reciter: Reciter) {
        val current = _uiState.value.audioState
        _uiState.update {
            it.copy(audioState = current.copy(reciter = reciter))
        }
    }

    fun stopAudio() {
        _uiState.update {
            it.copy(audioState = it.audioState.copy(isPlaying = false))
        }
    }

    // Zakat Calculation
    fun calculateZakat(
        cash: Double,
        goldGrams: Double,
        silverGrams: Double,
        business: Double,
        debts: Double
    ) {
        val goldPrice = 75.0 // Gram of 24k gold approximate standard
        val nisabThreshold = 85.0 * goldPrice // Nisab is 85g gold
        val totalAssets = cash + (goldGrams * goldPrice) + (silverGrams * 1.0) + business - debts

        val isReached = totalAssets >= nisabThreshold
        val zakatDue = if (isReached) totalAssets * 0.025 else 0.0

        _uiState.update {
            it.copy(
                zakatState = ZakatState(
                    cash = cash,
                    goldGrams = goldGrams,
                    silverGrams = silverGrams,
                    businessMerchandise = business,
                    debts = debts,
                    calculatedZakat = zakatDue,
                    isNisabReached = isReached
                )
            )
        }
    }

    // Islamic Quiz
    fun answerQuizQuestion(selectedOptionIndex: Int) {
        val state = _uiState.value
        val questions = IslamicDataProvider.quizQuestions
        val currentQ = questions.getOrNull(state.currentQuizIndex) ?: return

        val isCorrect = selectedOptionIndex == currentQ.correctIndex
        val newScore = if (isCorrect) state.quizScore + 1 else state.quizScore

        _uiState.update {
            it.copy(
                selectedQuizAnswer = selectedOptionIndex,
                quizScore = newScore
            )
        }
    }

    fun nextQuizQuestion() {
        val state = _uiState.value
        val questions = IslamicDataProvider.quizQuestions
        if (state.currentQuizIndex + 1 < questions.size) {
            _uiState.update {
                it.copy(
                    currentQuizIndex = it.currentQuizIndex + 1,
                    selectedQuizAnswer = null
                )
            }
        } else {
            _uiState.update {
                it.copy(isQuizFinished = true)
            }
        }
    }

    fun restartQuiz() {
        _uiState.update {
            it.copy(
                currentQuizIndex = 0,
                quizScore = 0,
                selectedQuizAnswer = null,
                isQuizFinished = false
            )
        }
    }
}
