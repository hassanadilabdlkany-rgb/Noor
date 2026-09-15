package com.example.data.repository

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.data.model.Ayah
import com.example.data.model.RevelationType
import com.example.data.model.Surah
import java.io.File
import java.io.FileOutputStream

data class AyahSearchResult(
    val ayahId: Int,
    val surahId: Int,
    val surahName: String,
    val numberInSurah: Int,
    val page: Int,
    val textArabic: String,
    val tafsir: String
)

object QuranDatabaseHelper {
    private const val DB_NAME = "quran.db"
    private var database: SQLiteDatabase? = null
    @Volatile
    private var isInitialized = false

    @Synchronized
    fun init(context: Context) {
        if (isInitialized && database?.isOpen == true) return
        try {
            val dbFile = context.getDatabasePath(DB_NAME)
            val parent = dbFile.parentFile
            if (parent != null && !parent.exists()) {
                parent.mkdirs()
            }

            val assetManager = context.assets
            // Verify if assets contain the database
            val assetExists = try {
                val list = assetManager.list("databases") ?: emptyArray()
                list.contains(DB_NAME)
            } catch (e: Exception) {
                false
            }

            val shouldCopy = assetExists && (!dbFile.exists() || dbFile.length() < 1_000_000L)

            if (shouldCopy) {
                Log.d("QuranDatabaseHelper", "Copying quran.db from assets to ${dbFile.absolutePath}...")
                val tempFile = File(parent, "$DB_NAME.tmp")
                assetManager.open("databases/$DB_NAME").use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }
                if (tempFile.exists() && tempFile.length() > 0) {
                    if (dbFile.exists()) {
                        dbFile.delete()
                    }
                    tempFile.renameTo(dbFile)
                }
            }

            if (dbFile.exists() && dbFile.length() > 0) {
                database = SQLiteDatabase.openDatabase(
                    dbFile.absolutePath,
                    null,
                    SQLiteDatabase.OPEN_READONLY
                )
                isInitialized = true
                Log.d("QuranDatabaseHelper", "quran.db opened successfully (${dbFile.length()} bytes)")
            } else {
                Log.w("QuranDatabaseHelper", "quran.db not available on device, using fallback data")
            }
        } catch (e: Exception) {
            Log.e("QuranDatabaseHelper", "Error initializing quran.db", e)
        }
    }

    fun isReady(): Boolean = isInitialized && database?.isOpen == true

    fun getAyahsForPage(page: Int): List<Ayah> {
        val db = database ?: return emptyList()
        val ayahs = mutableListOf<Ayah>()
        try {
            val cursor = db.rawQuery(
                "SELECT id, surah_id, number_in_surah, text_uthmani, tafsir_muyassar FROM ayahs WHERE page = ? ORDER BY id ASC",
                arrayOf(page.toString())
            )
            cursor.use { c ->
                val idIdx = c.getColumnIndexOrThrow("id")
                val surahIdx = c.getColumnIndexOrThrow("surah_id")
                val numIdx = c.getColumnIndexOrThrow("number_in_surah")
                val textIdx = c.getColumnIndexOrThrow("text_uthmani")
                val tafsirIdx = c.getColumnIndexOrThrow("tafsir_muyassar")

                while (c.moveToNext()) {
                    ayahs.add(
                        Ayah(
                            id = c.getInt(idIdx),
                            surahId = c.getInt(surahIdx),
                            numberInSurah = c.getInt(numIdx),
                            textArabic = c.getString(textIdx),
                            tafsirSaadi = c.getString(tafsirIdx),
                            tafsirIbnKathir = c.getString(tafsirIdx)
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("QuranDatabaseHelper", "Error querying ayahs for page $page", e)
        }
        return ayahs
    }

    fun getAyahsForSurah(surahId: Int): List<Ayah> {
        val db = database ?: return emptyList()
        val ayahs = mutableListOf<Ayah>()
        try {
            val cursor = db.rawQuery(
                "SELECT id, surah_id, number_in_surah, text_uthmani, tafsir_muyassar FROM ayahs WHERE surah_id = ? ORDER BY number_in_surah ASC",
                arrayOf(surahId.toString())
            )
            cursor.use { c ->
                val idIdx = c.getColumnIndexOrThrow("id")
                val surahIdx = c.getColumnIndexOrThrow("surah_id")
                val numIdx = c.getColumnIndexOrThrow("number_in_surah")
                val textIdx = c.getColumnIndexOrThrow("text_uthmani")
                val tafsirIdx = c.getColumnIndexOrThrow("tafsir_muyassar")

                while (c.moveToNext()) {
                    ayahs.add(
                        Ayah(
                            id = c.getInt(idIdx),
                            surahId = c.getInt(surahIdx),
                            numberInSurah = c.getInt(numIdx),
                            textArabic = c.getString(textIdx),
                            tafsirSaadi = c.getString(tafsirIdx),
                            tafsirIbnKathir = c.getString(tafsirIdx)
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("QuranDatabaseHelper", "Error querying ayahs for surah $surahId", e)
        }
        return ayahs
    }

    fun searchAyahs(query: String, limit: Int = 50): List<AyahSearchResult> {
        val db = database ?: return emptyList()
        val cleaned = normalizeSearchText(query)
        if (cleaned.isBlank()) return emptyList()

        val results = mutableListOf<AyahSearchResult>()
        try {
            val cursor = db.rawQuery(
                """
                SELECT a.id, a.surah_id, a.number_in_surah, a.page, a.text_uthmani, a.tafsir_muyassar, s.name_ar 
                FROM ayahs a 
                JOIN surahs s ON a.surah_id = s.id 
                WHERE a.text_clean LIKE ? 
                ORDER BY a.id ASC LIMIT ?
                """.trimIndent(),
                arrayOf("%$cleaned%", limit.toString())
            )
            cursor.use { c ->
                while (c.moveToNext()) {
                    results.add(
                        AyahSearchResult(
                            ayahId = c.getInt(0),
                            surahId = c.getInt(1),
                            numberInSurah = c.getInt(2),
                            page = c.getInt(3),
                            textArabic = c.getString(4),
                            tafsir = c.getString(5),
                            surahName = c.getString(6)
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("QuranDatabaseHelper", "Error searching ayahs for query '$query'", e)
        }
        return results
    }

    fun getPageBenefits(page: Int): List<String> {
        val db = database ?: return emptyList()
        val benefits = mutableListOf<String>()
        try {
            val cursor = db.rawQuery(
                "SELECT benefit_1, benefit_2, benefit_3 FROM page_benefits WHERE page = ?",
                arrayOf(page.toString())
            )
            cursor.use { c ->
                if (c.moveToNext()) {
                    val b1 = c.getString(0)
                    val b2 = c.getString(1)
                    val b3 = c.getString(2)
                    if (!b1.isNullOrBlank()) benefits.add(b1)
                    if (!b2.isNullOrBlank()) benefits.add(b2)
                    if (!b3.isNullOrBlank()) benefits.add(b3)
                }
            }
        } catch (e: Exception) {
            Log.e("QuranDatabaseHelper", "Error fetching page benefits for page $page", e)
        }
        return benefits
    }

    fun normalizeSearchText(input: String): String {
        return input
            .replace(Regex("[\\u0610-\\u061A\\u064B-\\u065F\\u0670\\u06D6-\\u06DC\\u06DF-\\u06E8\\u06EA-\\u06ED]"), "")
            .replace(Regex("[إأآٱ]"), "ا")
            .replace("ة", "ه")
            .replace(Regex("[يى]"), "ي")
            .trim()
    }
}
