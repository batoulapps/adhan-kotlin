package com.batoulapps.adhan2

import com.batoulapps.adhan2.data.CalendarUtil
import com.batoulapps.adhan2.data.CalendarUtil.add
import com.batoulapps.adhan2.data.CalendarUtil.roundedMinute
import com.batoulapps.adhan2.data.CalendarUtil.toUtcInstant
import com.batoulapps.adhan2.data.DateComponents
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant

class SunnahTimes(prayerTimes: PrayerTimes) {
  /* The midpoint between Maghrib and Fajr */
  val middleOfTheNight: Instant

  /* The beginning of the last third of the period between Maghrib and Fajr,
     a recommended time to perform Qiyam */
  val lastThirdOfTheNight: Instant

  init {
    val currentPrayerTimesDate = CalendarUtil.resolveTime(prayerTimes.dateComponents)
    val tomorrowPrayerTimesDate = add(currentPrayerTimesDate, 1, DateTimeUnit.DAY)
    val tomorrowPrayerTimes = prayerTimes.copy(dateComponents = DateComponents.fromLocalDateTime(tomorrowPrayerTimesDate))

    val nightDurationInSeconds =
      (tomorrowPrayerTimes.fajr.toEpochMilliseconds() -
          prayerTimes.maghrib.toEpochMilliseconds()) / 1000
    middleOfTheNight = roundedMinute(
      add(prayerTimes.maghrib, (nightDurationInSeconds / 2.0).toInt(), DateTimeUnit.SECOND)
    ).toUtcInstant()
    lastThirdOfTheNight = roundedMinute(
      add(
        prayerTimes.maghrib,
        (nightDurationInSeconds * (2.0 / 3.0)).toInt(),
        DateTimeUnit.SECOND
      )
    ).toUtcInstant()
  }
}
