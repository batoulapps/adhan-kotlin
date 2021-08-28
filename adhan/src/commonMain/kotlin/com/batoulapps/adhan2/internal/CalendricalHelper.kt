package com.batoulapps.adhan2.internal

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object CalendricalHelper {
  /**
   * The Julian Day for a given date
   * @param instant the instant
   * @return the julian day
   */
  fun julianDay(instant: Instant): Double {
    val localDateTime = instant.toLocalDateTime(TimeZone.UTC)
    return julianDay(localDateTime)
  }

  /**
   * The Julian Day for a given date
   * @param date a UTC date
   * @return the julian day
   */
  fun julianDay(date: LocalDateTime): Double {
    return julianDay(
      date.year, date.monthNumber, date.dayOfMonth,
      date.hour + date.minute / 60.0
    )
  }

  /**
   * The Julian Day for a given Gregorian date
   * @param year the year
   * @param month the month
   * @param day the day
   * @param hours hours
   * @return the julian day
   */
  fun julianDay(year: Int, month: Int, day: Int, hours: Double = 0.0): Double {
    /* Equation from Astronomical Algorithms page 60 */

    // NOTE: Integer conversion is done intentionally for the purpose of decimal truncation
    val Y = if (month > 2) year else year - 1
    val M = if (month > 2) month else month + 12
    val D = day + hours / 24
    val A = Y / 100
    val B = 2 - A + A / 4
    val i0 = (365.25 * (Y + 4716)).toInt()
    val i1 = (30.6001 * (M + 1)).toInt()
    return i0 + i1 + D + B - 1524.5
  }

  /**
   * Julian century from the epoch.
   * @param JD the julian day
   * @return the julian century from the epoch
   */
  fun julianCentury(JD: Double): Double {
    /* Equation from Astronomical Algorithms page 163 */
    return (JD - 2451545.0) / 36525
  }
}