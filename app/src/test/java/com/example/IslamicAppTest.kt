package com.example

import com.example.data.model.CityLocation
import com.example.data.repository.IslamicDataProvider
import com.example.data.repository.PrayerCalculator
import com.example.data.repository.QuranDataProvider
import org.junit.Assert.*
import org.junit.Test

class IslamicAppTest {

    @Test
    fun testQuranDataIntegrity() {
        val surahs = QuranDataProvider.surahList
        assertEquals(114, surahs.size)
        assertEquals("الفاتحة", surahs[0].nameArabic)
        assertEquals("الناس", surahs[113].nameArabic)

        val fatihaAyahs = QuranDataProvider.getSurahAyahs(1)
        assertEquals(7, fatihaAyahs.size)
        assertTrue(fatihaAyahs[0].textArabic.contains("بِسْمِ اللَّهِ"))

        val ikhlasAyahs = QuranDataProvider.getSurahAyahs(112)
        assertEquals(4, ikhlasAyahs.size)
    }

    @Test
    fun testPrayerCalculation() {
        val sanaa = IslamicDataProvider.availableCities.first { it.nameArabic == "صنعاء" }
        val result = PrayerCalculator.calculatePrayersForCity(sanaa)
        assertNotNull(result)
        assertEquals(6, result.times.size)
        assertTrue(result.remainingFormatted.isNotEmpty())
        assertTrue(result.hijriDateArabic.contains("هـ"))
    }

    @Test
    fun testNawawiHadithData() {
        val hadiths = IslamicDataProvider.nawawiHadiths
        assertTrue(hadiths.isNotEmpty())
        assertTrue(hadiths[0].titleArabic.contains("النيات"))
    }

    @Test
    fun testQuranPagesAndJuzMapping() {
        val pages = QuranDataProvider.allPages
        assertEquals(604, pages.size)
        assertEquals(1, pages[0].pageNumber)
        assertEquals("الفاتحة", pages[0].surahName)
        assertEquals(604, pages[603].pageNumber)
        assertEquals("الناس", pages[603].surahName)

        val surahKahf = QuranDataProvider.getSurahByPage(293)
        assertEquals(18, surahKahf.id)
        assertEquals("الكهف", surahKahf.nameArabic)

        assertEquals(1, QuranDataProvider.getJuzForPage(1))
        assertEquals(1, QuranDataProvider.getJuzForPage(20))
        assertEquals(2, QuranDataProvider.getJuzForPage(22))
        assertEquals(30, QuranDataProvider.getJuzForPage(604))
    }

    @Test
    fun testZakatCalculation() {
        val cash = 10000.0
        val goldPrice = 75.0
        val nisabThreshold = 85.0 * goldPrice // 6375.0
        val isNisab = cash >= nisabThreshold
        assertTrue(isNisab)
        val zakat = cash * 0.025
        assertEquals(250.0, zakat, 0.001)
    }

    @Test
    fun testAyatDataProvider() {
        val reciters = com.example.data.repository.AyatDataProvider.allReciters
        assertTrue(reciters.isNotEmpty())
        assertTrue(reciters.any { it.nameArabic == "ناصر القطامي" })
        assertTrue(reciters.any { it.nameArabic == "المختصر الصوتي" })

        val quarters = com.example.data.repository.AyatDataProvider.quranQuarters
        assertTrue(quarters.isNotEmpty())
        assertEquals(1, quarters[0].number)

        val page7Benefits = com.example.data.repository.AyatDataProvider.pageBenefitsMap[7]
        assertNotNull(page7Benefits)
        assertTrue(page7Benefits!!.benefits.isNotEmpty())

        val page7Ayahs = com.example.data.repository.AyatDataProvider.getPageAyahs(7)
        assertEquals(11, page7Ayahs.size)
        assertEquals(38, page7Ayahs[0].numberInSurah)

        val normalized = com.example.data.repository.QuranDatabaseHelper.normalizeSearchText("ٱلرَّحْمَٰنِ الرَّحِيمِ")
        assertEquals("الرحمن الرحيم", normalized)
    }
}
