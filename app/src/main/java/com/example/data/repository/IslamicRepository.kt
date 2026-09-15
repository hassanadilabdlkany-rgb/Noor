package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class IslamicRepository(
    private val dao: IslamicDao
) {
    // Quran
    fun getAllSurahs(): List<Surah> = QuranDataProvider.surahList

    fun getSurahById(id: Int): Surah? = QuranDataProvider.surahList.find { it.id == id }

    fun getAyahsForSurah(surahId: Int): List<Ayah> = QuranDataProvider.getSurahAyahs(surahId)

    fun getBookmarks(): Flow<List<BookmarkEntity>> = dao.getAllBookmarks()

    fun getPageBookmarks(): Flow<List<BookmarkEntity>> = dao.getPageBookmarks()

    fun getAyahBookmarks(): Flow<List<BookmarkEntity>> = dao.getAyahBookmarks()

    suspend fun addBookmark(surahId: Int, surahName: String, ayahNumber: Int, text: String, pageNumber: Int = 1, juzNumber: Int = 1) {
        dao.insertBookmark(
            BookmarkEntity(
                surahId = surahId,
                surahName = surahName,
                ayahNumber = ayahNumber,
                ayahText = text,
                pageNumber = pageNumber,
                juzNumber = juzNumber,
                isPageBookmark = false
            )
        )
    }

    suspend fun addPageBookmark(pageNumber: Int, surahId: Int, surahName: String, juzNumber: Int, note: String = "") {
        dao.insertBookmark(
            BookmarkEntity(
                surahId = surahId,
                surahName = surahName,
                ayahNumber = 1,
                ayahText = "صفحة رقم $pageNumber من سورة $surahName",
                pageNumber = pageNumber,
                juzNumber = juzNumber,
                isPageBookmark = true,
                note = note
            )
        )
    }

    suspend fun removeBookmark(surahId: Int, ayahNumber: Int) {
        dao.deleteBookmark(surahId, ayahNumber)
    }

    suspend fun removePageBookmark(pageNumber: Int) {
        dao.deletePageBookmark(pageNumber)
    }

    suspend fun setRibbonBookmark(color: String, pageNumber: Int, surahName: String) {
        dao.deleteRibbonBookmark(color)
        val surah = QuranDataProvider.getSurahByPage(pageNumber)
        val juz = QuranDataProvider.getJuzForPage(pageNumber)
        dao.insertBookmark(
            BookmarkEntity(
                surahId = surah.id,
                surahName = surahName,
                ayahNumber = 1,
                ayahText = "فاصل ملون ($color) - ص $pageNumber",
                pageNumber = pageNumber,
                juzNumber = juz,
                isPageBookmark = true,
                ribbonColor = color
            )
        )
    }

    suspend fun clearRibbonBookmark(color: String) {
        dao.deleteRibbonBookmark(color)
    }

    suspend fun toggleStarAyah(surahId: Int, surahName: String, ayahNumber: Int, ayahText: String, pageNumber: Int, juzNumber: Int) {
        dao.insertBookmark(
            BookmarkEntity(
                surahId = surahId,
                surahName = surahName,
                ayahNumber = ayahNumber,
                ayahText = ayahText,
                pageNumber = pageNumber,
                juzNumber = juzNumber,
                isStarred = true
            )
        )
    }

    suspend fun removeBookmarkById(id: Long) {
        dao.deleteBookmarkById(id)
    }

    suspend fun isBookmarked(surahId: Int, ayahNumber: Int): Boolean {
        return dao.isBookmarked(surahId, ayahNumber)
    }

    suspend fun isPageBookmarked(pageNumber: Int): Boolean {
        return dao.isPageBookmarked(pageNumber)
    }

    // Adhkar
    fun getAdhkarCategories(): List<DhikrCategory> = IslamicDataProvider.adhkarCategories

    fun getAdhkarByCategory(categoryId: String): List<DhikrItem> = IslamicDataProvider.getAdhkarByCategory(categoryId)

    // Hadith & Library
    fun getNawawiHadiths(): List<NawawiHadith> = IslamicDataProvider.nawawiHadiths

    fun getProphetStories(): List<ProphetStory> = IslamicDataProvider.prophetStories

    fun getQuizQuestions(): List<IslamicQuizQuestion> = IslamicDataProvider.quizQuestions

    fun getWuduSteps(): List<WuduStep> = IslamicDataProvider.wuduSteps

    fun getSalahSteps(): List<SalahStep> = IslamicDataProvider.salahSteps

    fun getReciters(): List<Reciter> = IslamicDataProvider.reciters

    fun getCities(): List<CityLocation> = IslamicDataProvider.availableCities

    // Daily Prayers Log
    fun getTodayKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getTodayPrayerLog(): Flow<DailyPrayerLogEntity?> {
        return dao.getPrayerLog(getTodayKey())
    }

    suspend fun savePrayerLog(log: DailyPrayerLogEntity) {
        dao.savePrayerLog(log)
    }

    // Khatmah
    fun getKhatmahPlan(): Flow<KhatmahPlanEntity?> = dao.getKhatmahPlan()

    suspend fun saveKhatmahPlan(plan: KhatmahPlanEntity) {
        dao.saveKhatmahPlan(plan)
    }

    // Tasbih
    fun getTasbihRecord(): Flow<TasbihRecordEntity?> = dao.getTasbihRecord()

    suspend fun saveTasbihRecord(record: TasbihRecordEntity) {
        dao.saveTasbihRecord(record)
    }
}
