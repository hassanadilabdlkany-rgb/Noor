package com.example.data.repository

import com.example.data.model.CityLocation
import com.example.data.model.PrayerTime
import java.util.Calendar
import java.util.Locale
import kotlin.math.*

object PrayerCalculator {

    data class PrayerScheduleResult(
        val times: List<PrayerTime>,
        val nextPrayerIndex: Int,
        val remainingFormatted: String,
        val hijriDateArabic: String,
        val gregorianDateFormatted: String
    )

    fun calculatePrayersForCity(
        city: CityLocation,
        calendar: Calendar = Calendar.getInstance()
    ): PrayerScheduleResult {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Day of Year
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        // Solar Declination delta (approximate in radians)
        val b = 2 * PI * (dayOfYear - 81) / 365.0
        val declination = Math.toRadians(23.45 * sin(b))

        // Equation of Time (in minutes)
        val b2 = 2 * PI * (dayOfYear - 1) / 365.0
        val eqTime = 229.18 * (0.000075 + 0.001868 * cos(b2) - 0.032077 * sin(b2) - 0.014615 * cos(2 * b2) - 0.040849 * sin(2 * b2))

        val latRad = Math.toRadians(city.latitude)
        val timeZone = city.timeZoneOffsetHours

        // Solar Noon (Dhuhr)
        val noonMinutes = 720.0 - 4.0 * city.longitude - eqTime + (timeZone * 60.0)

        // Sunrise and Sunset (solar angle = -0.8333 deg)
        val sunAltRad = Math.toRadians(-0.8333)
        val cosHourAngleSun = (sin(sunAltRad) - sin(latRad) * sin(declination)) / (cos(latRad) * cos(declination))
        val hourAngleSun = Math.toDegrees(acos(cosHourAngleSun.coerceIn(-1.0, 1.0)))

        // Fajr (solar angle = -18.5 deg for Umm al-Qura / General Islamic standard)
        val fajrAltRad = Math.toRadians(-18.5)
        val cosHourAngleFajr = (sin(fajrAltRad) - sin(latRad) * sin(declination)) / (cos(latRad) * cos(declination))
        val hourAngleFajr = Math.toDegrees(acos(cosHourAngleFajr.coerceIn(-1.0, 1.0)))

        // Isha (solar angle = -18.0 deg or 90 minutes)
        val ishaAltRad = Math.toRadians(-18.0)
        val cosHourAngleIsha = (sin(ishaAltRad) - sin(latRad) * sin(declination)) / (cos(latRad) * cos(declination))
        val hourAngleIsha = Math.toDegrees(acos(cosHourAngleIsha.coerceIn(-1.0, 1.0)))

        // Asr (Shafi'i shadow = 1 + shadow at noon)
        val noonAngle = abs(latRad - declination)
        val asrAltRad = atan(1.0 / (1.0 + tan(noonAngle)))
        val cosHourAngleAsr = (sin(asrAltRad) - sin(latRad) * sin(declination)) / (cos(latRad) * cos(declination))
        val hourAngleAsr = Math.toDegrees(acos(cosHourAngleAsr.coerceIn(-1.0, 1.0)))

        val fajrMin = noonMinutes - (hourAngleFajr * 4.0)
        val sunriseMin = noonMinutes - (hourAngleSun * 4.0)
        val dhuhrMin = noonMinutes
        val asrMin = noonMinutes + (hourAngleAsr * 4.0)
        val maghribMin = noonMinutes + (hourAngleSun * 4.0)
        val ishaMin = noonMinutes + (hourAngleIsha * 4.0)

        val currentMinutesOfDay = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)

        val prayerSchedule = listOf(
            PrayerTime("Fajr", "الفجر", formatMinutes(fajrMin), isPassed = currentMinutesOfDay >= fajrMin),
            PrayerTime("Sunrise", "الشروق", formatMinutes(sunriseMin), isPassed = currentMinutesOfDay >= sunriseMin),
            PrayerTime("Dhuhr", "الظهر", formatMinutes(dhuhrMin), isPassed = currentMinutesOfDay >= dhuhrMin),
            PrayerTime("Asr", "العصر", formatMinutes(asrMin), isPassed = currentMinutesOfDay >= asrMin),
            PrayerTime("Maghrib", "المغرب", formatMinutes(maghribMin), isPassed = currentMinutesOfDay >= maghribMin),
            PrayerTime("Isha", "العشاء", formatMinutes(ishaMin), isPassed = currentMinutesOfDay >= ishaMin)
        )

        // Find next prayer
        val minuteList = listOf(fajrMin, sunriseMin, dhuhrMin, asrMin, maghribMin, ishaMin)
        var nextIdx = 0
        for (i in minuteList.indices) {
            if (currentMinutesOfDay < minuteList[i]) {
                nextIdx = i
                break
            }
            if (i == minuteList.lastIndex) {
                // Next is Fajr tomorrow
                nextIdx = 0
            }
        }

        val nextTimeMin = if (nextIdx == 0 && currentMinutesOfDay >= ishaMin) {
            fajrMin + 1440 // Tomorrow's fajr
        } else {
            minuteList[nextIdx]
        }

        val diffMinutes = (nextTimeMin - currentMinutesOfDay).toInt().coerceAtLeast(0)
        val remHours = diffMinutes / 60
        val remMins = diffMinutes % 60
        val remainingFormatted = if (remHours > 0) {
            "$remHours س و $remMins د"
        } else {
            "$remMins دقيقة"
        }

        val updatedPrayers = prayerSchedule.mapIndexed { idx, p ->
            p.copy(isNext = idx == nextIdx)
        }

        // Hijri Approximation
        val hijriArabic = getApproximateHijriDate(calendar)
        val gregorianFormatted = String.format(Locale.getDefault(), "%02d/%02d/%d", day, month, year)

        return PrayerScheduleResult(
            times = updatedPrayers,
            nextPrayerIndex = nextIdx,
            remainingFormatted = remainingFormatted,
            hijriDateArabic = hijriArabic,
            gregorianDateFormatted = gregorianFormatted
        )
    }

    private fun formatMinutes(minutes: Double): String {
        val totalMins = (minutes % 1440 + 1440) % 1440
        val hours = (totalMins / 60).toInt()
        val mins = (totalMins % 60).toInt()
        return String.format(Locale.getDefault(), "%02d:%02d", hours, mins)
    }

    private fun getApproximateHijriDate(cal: Calendar): String {
        val hijriMonths = listOf(
            "محرم", "صفر", "ربيع الأول", "ربيع الآخر",
            "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
            "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
        )
        // Approximate calculation for display
        val epochDay = cal.timeInMillis / (1000 * 60 * 60 * 24)
        val hijriEpoch = 1948439 // Julian Day equivalent offset
        val julianDay = epochDay + 2440588
        val l = julianDay - hijriEpoch
        val n = ((l - 1) / 10631).toInt()
        val l2 = l - 10631 * n + 354
        val j = (((10985 - l2) / 5316) * ((50 * l2) / 17719) + (l2 / 5670) * ((43 * l2) / 15238)).toInt()
        val l3 = l2 - (((30 - j) / 15) * ((17719 * j) / 50) + (j / 16) * ((15238 * j) / 43)).toInt() + 29
        val m = ((24 * l3) / 709).toInt()
        val d = l3 - ((709 * m) / 24)
        val y = 30 * n + j - 30

        val safeMonth = ((m - 1).coerceIn(0, 11))
        val safeDay = d.coerceIn(1, 30)
        return "$safeDay ${hijriMonths[safeMonth]} $y هـ"
    }
}
