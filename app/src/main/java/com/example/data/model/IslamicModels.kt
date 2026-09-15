package com.example.data.model

data class Surah(
    val id: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val revelationType: RevelationType,
    val versesCount: Int,
    val juzNumber: Int,
    val pageNumber: Int,
    val ayahs: List<Ayah> = emptyList()
)

enum class RevelationType(val arabicName: String) {
    MECCAN("مكية"),
    MEDINAN("مدنية")
}

data class Ayah(
    val id: Int,
    val surahId: Int,
    val numberInSurah: Int,
    val textArabic: String,
    val tafsirSaadi: String,
    val tafsirIbnKathir: String,
    val translationEnglish: String = ""
)

data class QuranPageInfo(
    val pageNumber: Int,
    val surahId: Int,
    val surahName: String,
    val juzNumber: Int
)

data class Reciter(
    val id: String,
    val nameArabic: String,
    val styleArabic: String
)

data class PrayerTime(
    val name: String,
    val arabicName: String,
    val timeFormatted: String,
    val isNext: Boolean = false,
    val isPassed: Boolean = false
)

data class CityLocation(
    val nameArabic: String,
    val countryArabic: String,
    val latitude: Double,
    val longitude: Double,
    val timeZoneOffsetHours: Double,
    val qiblaAngle: Float // Degrees clockwise from North
)

data class DhikrCategory(
    val id: String,
    val nameArabic: String,
    val iconEmoji: String,
    val description: String,
    val totalItems: Int
)

data class DhikrItem(
    val id: Int,
    val categoryId: String,
    val textArabic: String,
    val countTarget: Int,
    val currentCount: Int = 0,
    val rewardVirtue: String,
    val sourceHadith: String
)

data class NawawiHadith(
    val number: Int,
    val titleArabic: String,
    val narrator: String,
    val hadithText: String,
    val explanation: String,
    val benefits: List<String>
)

data class ProphetStory(
    val id: Int,
    val prophetName: String,
    val title: String,
    val summary: String,
    val fullStory: String,
    val lessons: List<String>
)

data class IslamicQuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class WuduStep(
    val stepNumber: Int,
    val titleArabic: String,
    val descriptionArabic: String,
    val duaOrSunnah: String
)

data class SalahStep(
    val stepNumber: Int,
    val nameArabic: String,
    val descriptionArabic: String,
    val recitationArabic: String
)
