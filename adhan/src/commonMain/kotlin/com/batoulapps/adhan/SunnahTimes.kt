package com.batoulapps.adhan

import com.batoulapps.adhan.data.CalendarUtil
import com.batoulapps.adhan.data.CalendarUtil.add
import com.batoulapps.adhan.data.CalendarUtil.roundedMinute
import com.batoulapps.adhan.data.DateComponents
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.TimeZone.Companion
import kotlinx.datetime.toInstant

class SunnahTimes(prayerTimes: PrayerTimes) {
  /* The midpoint between Maghrib and Fajr */
  val middleOfTheNight: LocalDateTime

  /* The beginning of the last third of the period between Maghrib and Fajr,
     a recommended time to perform Qiyam */
  val lastThirdOfTheNight: LocalDateTime

  init {
    val currentPrayerTimesDate = CalendarUtil.resolveTime(prayerTimes.dateComponents)
    val tomorrowPrayerTimesDate = add(currentPrayerTimesDate, 1, DateTimeUnit.DAY)
    val tomorrowPrayerTimes = prayerTimes.copy(dateComponents = DateComponents.fromLocalDateTime(tomorrowPrayerTimesDate))

    val nightDurationInSeconds =
      (tomorrowPrayerTimes.fajr.toInstant(TimeZone.UTC).toEpochMilliseconds() -
          prayerTimes.maghrib.toInstant(Companion.UTC).toEpochMilliseconds()) / 1000
    middleOfTheNight = roundedMinute(
      add(prayerTimes.maghrib, (nightDurationInSeconds / 2.0).toInt(), DateTimeUnit.SECOND)
    )
    lastThirdOfTheNight = roundedMinute(
      add(
        prayerTimes.maghrib,
        (nightDurationInSeconds * (2.0 / 3.0)).toInt(),
        DateTimeUnit.SECOND
      )
    )
  }
}
