package com.batoulapps.adhan2.data

import com.batoulapps.adhan2.model.Rounding
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

object CalendarUtil {
  /**
   * Whether or not a year is a leap year (has 366 days)
   * @param year the year
   * @return whether or not its a leap year
   */
  fun isLeapYear(year: Int): Boolean {
    return year % 4 == 0 && !(year % 100 == 0 && year % 400 != 0)
  }

  /**
   * Date and time with a rounded minute
   * This returns a date with the seconds rounded and added to the minute
   * @param localDateTime the date and time
   * @return the date and time with 0 seconds and minutes including rounded seconds
   */
  fun roundedMinute(localDateTime: LocalDateTime, rounding: Rounding = Rounding.NEAREST): LocalDateTime {
    val originalMinute = localDateTime.minute
    val (minute, second) = when (rounding) {
      Rounding.NEAREST -> originalMinute + (localDateTime.second / 60f).roundToInt() to 0
      Rounding.UP -> originalMinute + ceil(localDateTime.second / 60f).roundToInt() to 0
      Rounding.NONE -> originalMinute to localDateTime.second
    }

    val localDateTimeWithOldMinutes = LocalDateTime(
      year = localDateTime.year,
      monthNumber = localDateTime.monthNumber,
      dayOfMonth = localDateTime.dayOfMonth,
      hour = localDateTime.hour,
      minute = originalMinute,
      second = second
    )

    return if (originalMinute != minute) {
      val delta = minute - originalMinute
      add(localDateTimeWithOldMinutes, delta, DateTimeUnit.MINUTE)
    } else {
      localDateTimeWithOldMinutes
    }
  }

  /**
   * Gets a date for the particular date
   * @param components the date components
   * @return the LocalDateTime with a time set to 00:00:00 at utc
   */
  fun resolveTime(components: DateComponents): LocalDateTime {
    return resolveTime(components.year, components.month, components.day)
  }

  /**
   * Add the specified amount of a unit of time to a particular date
   * @param localDateTime the original date
   * @param amount the amount to add
   * @param dateTimeUnit the field to add it to
   * @return the date with the offset added
   */
  fun add(localDateTime: LocalDateTime, amount: Int, dateTimeUnit: DateTimeUnit): LocalDateTime {
    val timezone = TimeZone.UTC
    val instant = localDateTime.toInstant(timezone)
    return add(instant, amount, dateTimeUnit)
  }

  /**
   * Add the specified amount of a unit of time to a particular date
   * @param instant the gmt instant
   * @param amount the amount to add
   * @param dateTimeUnit the field to add it to
   * @return the date with the offset added
   */
  fun add(instant: Instant, amount: Int, dateTimeUnit: DateTimeUnit): LocalDateTime {
    val timezone = TimeZone.UTC
    val updatedInstant = instant.plus(amount, dateTimeUnit, timezone)
    return updatedInstant.toLocalDateTime(timezone)
  }

  /**
   * Gets a date for the particular date
   * @param year the year
   * @param month the month
   * @param day the day
   * @return a LocalDateTime object with a time set to 00:00:00 at utc
   */
  private fun resolveTime(year: Int, month: Int, day: Int): LocalDateTime {
    return LocalDateTime(year, month, day, 0, 0, 0)
  }

  fun LocalDateTime.toUtcInstant(): Instant = toInstant(TimeZone.UTC)
}