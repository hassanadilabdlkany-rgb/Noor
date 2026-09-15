package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IslamicDao {
    // Bookmarks
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE isPageBookmark = 1 ORDER BY pageNumber ASC")
    fun getPageBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE isPageBookmark = 0 ORDER BY timestamp DESC")
    fun getAyahBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE ribbonColor != ''")
    fun getRibbonBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE ribbonColor = :color LIMIT 1")
    suspend fun getRibbonBookmark(color: String): BookmarkEntity?

    @Query("DELETE FROM bookmarks WHERE ribbonColor = :color")
    suspend fun deleteRibbonBookmark(color: String)

    @Query("SELECT * FROM bookmarks WHERE isStarred = 1 ORDER BY timestamp DESC")
    fun getStarredVerses(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE note != '' ORDER BY timestamp DESC")
    fun getNotes(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity): Long

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmarkById(id: Long)

    @Query("DELETE FROM bookmarks WHERE surahId = :surahId AND ayahNumber = :ayahNumber AND isPageBookmark = 0")
    suspend fun deleteBookmark(surahId: Int, ayahNumber: Int)

    @Query("DELETE FROM bookmarks WHERE pageNumber = :pageNumber AND isPageBookmark = 1")
    suspend fun deletePageBookmark(pageNumber: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE surahId = :surahId AND ayahNumber = :ayahNumber AND isPageBookmark = 0)")
    suspend fun isBookmarked(surahId: Int, ayahNumber: Int): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE pageNumber = :pageNumber AND isPageBookmark = 1)")
    suspend fun isPageBookmarked(pageNumber: Int): Boolean

    // Daily Prayer Logs
    @Query("SELECT * FROM daily_prayer_logs WHERE dateKey = :dateKey")
    fun getPrayerLog(dateKey: String): Flow<DailyPrayerLogEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePrayerLog(log: DailyPrayerLogEntity)

    // Khatmah
    @Query("SELECT * FROM khatmah_plan WHERE id = 1")
    fun getKhatmahPlan(): Flow<KhatmahPlanEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveKhatmahPlan(plan: KhatmahPlanEntity)

    // Tasbih
    @Query("SELECT * FROM tasbih_records WHERE id = 1")
    fun getTasbihRecord(): Flow<TasbihRecordEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTasbihRecord(record: TasbihRecordEntity)
}
