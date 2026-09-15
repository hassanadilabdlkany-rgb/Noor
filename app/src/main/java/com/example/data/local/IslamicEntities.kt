package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val surahId: Int,
    val surahName: String,
    val ayahNumber: Int,
    val ayahText: String,
    val pageNumber: Int = 1,
    val juzNumber: Int = 1,
    val isPageBookmark: Boolean = false,
    val ribbonColor: String = "", // "red", "yellow", "green", "blue"
    val isStarred: Boolean = false,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_prayer_logs")
data class DailyPrayerLogEntity(
    @PrimaryKey
    val dateKey: String, // YYYY-MM-DD
    val fajr: Boolean = false,
    val dhuhr: Boolean = false,
    val asr: Boolean = false,
    val maghrib: Boolean = false,
    val isha: Boolean = false,
    val duha: Boolean = false,
    val qiyam: Boolean = false,
    val morningAzkar: Boolean = false,
    val eveningAzkar: Boolean = false,
    val quranPagesRead: Int = 0
)

@Entity(tableName = "khatmah_plan")
data class KhatmahPlanEntity(
    @PrimaryKey
    val id: Int = 1,
    val targetDays: Int = 30,
    val currentJuz: Int = 1,
    val completedPages: Int = 0,
    val lastReadSurahId: Int = 1,
    val lastReadSurahName: String = "الفاتحة",
    val lastReadAyah: Int = 1,
    val lastReadPage: Int = 1,
    val lastReadJuz: Int = 1,
    val lastReadTimestamp: Long = System.currentTimeMillis(),
    val dailyPagesGoal: Int = 4,
    val todayPagesRead: Int = 0,
    val startDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasbih_records")
data class TasbihRecordEntity(
    @PrimaryKey
    val id: Int = 1,
    val totalCount: Int = 0,
    val currentDhikr: String = "سبحان الله وبحمده",
    val sessionTarget: Int = 33
)
