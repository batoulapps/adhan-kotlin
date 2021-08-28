package com.batoulapps.adhan.internal

import com.batoulapps.adhan.data.CalendarUtil
import com.batoulapps.adhan.data.CalendarUtil.toUtcInstant
import com.batoulapps.adhan.data.DateComponents
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

object TestUtils {

  fun makeDate(
    year: Int,
    month: Int,
    day: Int,
    hour: Int = 0,
    minute: Int = 0,
    second: Int = 0
  ) = LocalDateTime(
    year = year,
    monthNumber = month,
    dayOfMonth = day,
    hour = hour,
    minute = minute,
    second = second
  )

  fun pad(value: Int): String {
    return if (value < 10) {
      "0$value"
    } else {
      value.toString()
    }
  }

  fun getDateComponents(date: String): DateComponents {
    val pieces = date.split("-").toTypedArray()
    val year = pieces[0].toInt()
    val month = pieces[1].toInt()
    val day = pieces[2].toInt()
    return DateComponents(year, month, day)
  }

  fun addSeconds(gmtInstant: Instant, offset: Int): Instant {
    return CalendarUtil.add(gmtInstant, offset, DateTimeUnit.SECOND).toUtcInstant()
  }

  fun makeDateWithOffset(year: Int, month: Int, day: Int, offset: Int, dateTimeUnit: DateTimeUnit): DateComponents {
    val localDateTime = LocalDateTime(year = year, monthNumber = month, dayOfMonth = day, hour = 0, minute = 0)
    val instant = localDateTime.toInstant(TimeZone.UTC)
    val updatedInstant = instant.plus(offset, dateTimeUnit, TimeZone.UTC)
    val updatedLocalDateTime = updatedInstant.toLocalDateTime(TimeZone.UTC)
    return DateComponents.fromLocalDateTime(updatedLocalDateTime)
  }
}