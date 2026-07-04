package com.batoulapps.adhan2

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DateUtilsTest {

  private val gap = PrayerTimes.MIN_PRAYER_GAP_MS

  private fun t(offsetMs: Long) =
    Instant.fromEpochMilliseconds(1_000_000_000_000L + offsetMs).toLocalDateTime(TimeZone.UTC)

  @Test
  fun timesInOrder_exactGap_returnsTrue() {
    assertTrue(PrayerTimes.timesInOrder(t(0), t(gap)))
  }

  @Test
  fun timesInOrder_moreThanGap_returnsTrue() {
    assertTrue(PrayerTimes.timesInOrder(t(0), t(gap + 1)))
  }

  @Test
  fun timesInOrder_lessThanGap_returnsFalse() {
    assertFalse(PrayerTimes.timesInOrder(t(0), t(gap - 1)))
  }

  @Test
  fun timesInOrder_equal_returnsFalse() {
    assertFalse(PrayerTimes.timesInOrder(t(0), t(0)))
  }

  @Test
  fun timesInOrder_reversed_returnsFalse() {
    assertFalse(PrayerTimes.timesInOrder(t(gap), t(0)))
  }

  @Test
  fun timesInOrder_firstNull_returnsFalse() {
    assertFalse(PrayerTimes.timesInOrder(null, t(0)))
  }

  @Test
  fun timesInOrder_secondNull_returnsFalse() {
    assertFalse(PrayerTimes.timesInOrder(t(0), null))
  }

  @Test
  fun timesInOrder_bothNull_returnsFalse() {
    assertFalse(PrayerTimes.timesInOrder(null, null))
  }
}
