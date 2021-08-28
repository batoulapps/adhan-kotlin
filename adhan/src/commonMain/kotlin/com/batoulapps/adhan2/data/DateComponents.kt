package com.batoulapps.adhan2.data

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DateComponents(val year: Int, val month: Int, val day: Int) {
  companion object {
    /**
     * Convenience method that returns a DateComponents from a given [Instant]
     * @param instant the current instant
     * @return the [DateComponents] (according to the default device timezone)
     */
    fun from(instant: Instant): DateComponents {
      val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
      return fromLocalDateTime(localDateTime)
    }

    /**
     * Convenience method that returns a DateComponents from a given [LocalDateTime]
     * @param date the date
     * @return the DateComponents
     */
    fun fromLocalDateTime(date: LocalDateTime): DateComponents {
      return DateComponents(date.year, date.monthNumber, date.dayOfMonth)
    }
  }
}