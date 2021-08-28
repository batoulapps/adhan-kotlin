package com.batoulapps.adhan2.data

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlin.math.floor

class TimeComponents private constructor(val hours: Int, val minutes: Int, val seconds: Int) {

  fun dateComponents(date: DateComponents): LocalDateTime {
    val localDateTime = LocalDateTime(date.year, date.month, date.day, 0, minutes, seconds)
    return CalendarUtil.add(localDateTime, hours, DateTimeUnit.HOUR)
  }

  companion object {
    fun fromDouble(value: Double): TimeComponents? {
      if (value.isInfinite() || value.isNaN()) {
        return null
      }
      val hours: Double = floor(value)
      val minutes: Double = floor((value - hours) * 60.0)
      val seconds: Double = floor((value - (hours + minutes / 60.0)) * 60 * 60)
      return TimeComponents(hours.toInt(), minutes.toInt(), seconds.toInt())
    }
  }
}