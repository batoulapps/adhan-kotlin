package com.batoulapps.adhan2

import com.batoulapps.adhan2.CalculationMethod.KARACHI
import com.batoulapps.adhan2.CalculationMethod.MOON_SIGHTING_COMMITTEE
import com.batoulapps.adhan2.CalculationMethod.MUSLIM_WORLD_LEAGUE
import com.batoulapps.adhan2.CalculationMethod.NORTH_AMERICA
import com.batoulapps.adhan2.HighLatitudeRule.TWILIGHT_ANGLE
import com.batoulapps.adhan2.Madhab.HANAFI
import com.batoulapps.adhan2.Prayer.ASR
import com.batoulapps.adhan2.Prayer.DHUHR
import com.batoulapps.adhan2.Prayer.FAJR
import com.batoulapps.adhan2.Prayer.ISHA
import com.batoulapps.adhan2.Prayer.MAGHRIB
import com.batoulapps.adhan2.Prayer.NONE
import com.batoulapps.adhan2.Prayer.SUNRISE
import com.batoulapps.adhan2.data.DateComponents
import com.batoulapps.adhan2.internal.TestUtils.addSeconds
import com.batoulapps.adhan2.internal.TestUtils.makeDate
import com.batoulapps.adhan2.internal.TestUtils.pad
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PrayerTimesTest {

  @Test
  fun testDaysSinceSolstice() {
    daysSinceSolsticeTest(11,  year = 2016,  month = 1,  day = 1, latitude = 1.0)
    daysSinceSolsticeTest(10,  year = 2015,  month = 12,  day = 31, latitude = 1.0)
    daysSinceSolsticeTest(10,  year = 2016,  month = 12,  day = 31, latitude = 1.0)
    daysSinceSolsticeTest(0,  year = 2016,  month = 12,  day = 21, latitude = 1.0)
    daysSinceSolsticeTest(1,  year = 2016,  month = 12,  day = 22, latitude = 1.0)
    daysSinceSolsticeTest(71,  year = 2016,  month = 3,  day = 1, latitude = 1.0)
    daysSinceSolsticeTest(70,  year = 2015,  month = 3,  day = 1, latitude = 1.0)
    daysSinceSolsticeTest(365,  year = 2016,  month = 12,  day = 20, latitude = 1.0)
    daysSinceSolsticeTest(364,  year = 2015,  month = 12,  day = 20, latitude = 1.0)
    daysSinceSolsticeTest(0,  year = 2015,  month = 6,  day = 21, latitude = -1.0)
    daysSinceSolsticeTest(0,  year = 2016,  month = 6,  day = 21, latitude = -1.0)
    daysSinceSolsticeTest(364,  year = 2015,  month = 6,  day = 20, latitude = -1.0)
    daysSinceSolsticeTest(365,  year = 2016,  month = 6,  day = 20, latitude = -1.0)
  }

  private fun daysSinceSolsticeTest(value: Int, year: Int, month: Int, day: Int, latitude: Double) {
    // For Northern Hemisphere start from December 21
    // (DYY=0 for December 21, and counting forward, DYY=11 for January 1 and so on).
    // For Southern Hemisphere start from June 21
    // (DYY=0 for June 21, and counting forward)
    val localDateTime = makeDate(year, month, day)
    val dayOfYear: Int = localDateTime.dayOfYear
    assertEquals(value, PrayerTimes.daysSinceSolstice(dayOfYear, localDateTime.year, latitude))
  }

  private fun stringifyAtTimezone(time: Instant, zoneId: String): String {
    val timeZone = TimeZone.of(zoneId)
    val localDateTime = time.toLocalDateTime(timeZone)

    // hour is 0-23
    val initialHour = localDateTime.hour
    val mappedHour = when {
      initialHour == 0 -> 12
      initialHour > 12 -> initialHour - 12
      else -> initialHour
    }
    val hour = pad(mappedHour)
    val minutes = pad(localDateTime.minute)
    val amPM = if (initialHour >= 12) "PM" else "AM"
    return "$hour:$minutes $amPM"
  }

  @Test
  fun testPrayerTimes() {
    val date = DateComponents(2015, 7, 12)
    val params = NORTH_AMERICA.parameters.copy(madhab = HANAFI)

    val coordinates = Coordinates(35.7750, -78.6336)
    val prayerTimes = PrayerTimes(coordinates, date, params)

    val zoneId = "America/New_York"
    assertEquals("04:42 AM", stringifyAtTimezone(prayerTimes.fajr, zoneId))
    assertEquals("06:08 AM", stringifyAtTimezone(prayerTimes.sunrise, zoneId))
    assertEquals("01:21 PM", stringifyAtTimezone(prayerTimes.dhuhr, zoneId))
    assertEquals("06:22 PM", stringifyAtTimezone(prayerTimes.asr, zoneId))
    assertEquals("08:32 PM", stringifyAtTimezone(prayerTimes.maghrib, zoneId))
    assertEquals("09:57 PM", stringifyAtTimezone(prayerTimes.isha, zoneId))
  }

  @Test
  fun testOffsets() {
    val date = DateComponents(2015, 12, 1)
    val coordinates = Coordinates(35.7750, -78.6336)

    val zoneId = "America/New_York"
    val parameters = MUSLIM_WORLD_LEAGUE.parameters

    var prayerTimes = PrayerTimes(coordinates, date, parameters)
    assertEquals("05:35 AM", stringifyAtTimezone(prayerTimes.fajr, zoneId))
    assertEquals("07:06 AM", stringifyAtTimezone(prayerTimes.sunrise, zoneId))
    assertEquals("12:05 PM", stringifyAtTimezone(prayerTimes.dhuhr, zoneId))
    assertEquals("02:42 PM", stringifyAtTimezone(prayerTimes.asr, zoneId))
    assertEquals("05:01 PM", stringifyAtTimezone(prayerTimes.maghrib, zoneId))
    assertEquals("06:26 PM", stringifyAtTimezone(prayerTimes.isha, zoneId))

    val params = parameters.copy(
      prayerAdjustments = parameters.prayerAdjustments.copy(
        fajr = 10,
        sunrise = 10,
        dhuhr = 10,
        asr = 10,
        maghrib = 10,
        isha = 10
      )
    )
    prayerTimes = PrayerTimes(coordinates, date, params)
    assertEquals("05:45 AM", stringifyAtTimezone(prayerTimes.fajr, zoneId))
    assertEquals("07:16 AM", stringifyAtTimezone(prayerTimes.sunrise, zoneId))
    assertEquals("12:15 PM", stringifyAtTimezone(prayerTimes.dhuhr, zoneId))
    assertEquals("02:52 PM", stringifyAtTimezone(prayerTimes.asr, zoneId))
    assertEquals("05:11 PM", stringifyAtTimezone(prayerTimes.maghrib, zoneId))
    assertEquals("06:36 PM", stringifyAtTimezone(prayerTimes.isha, zoneId))

    prayerTimes = PrayerTimes(coordinates, date,
      params.copy(prayerAdjustments = PrayerAdjustments()))
    assertEquals("05:35 AM", stringifyAtTimezone(prayerTimes.fajr, zoneId))
    assertEquals("07:06 AM", stringifyAtTimezone(prayerTimes.sunrise, zoneId))
    assertEquals("12:05 PM", stringifyAtTimezone(prayerTimes.dhuhr, zoneId))
    assertEquals("02:42 PM", stringifyAtTimezone(prayerTimes.asr, zoneId))
    assertEquals("05:01 PM", stringifyAtTimezone(prayerTimes.maghrib, zoneId))
    assertEquals("06:26 PM", stringifyAtTimezone(prayerTimes.isha, zoneId))
  }

  @Test
  fun testMoonsightingMethod() {
    val date = DateComponents(2016, 1, 31)
    val coordinates = Coordinates(35.7750, -78.6336)
    val prayerTimes = PrayerTimes(
      coordinates, date, MOON_SIGHTING_COMMITTEE.parameters
    )

    val zoneId = "America/New_York"
    assertEquals("05:48 AM", stringifyAtTimezone(prayerTimes.fajr, zoneId))
    assertEquals("07:16 AM", stringifyAtTimezone(prayerTimes.sunrise, zoneId))
    assertEquals("12:33 PM", stringifyAtTimezone(prayerTimes.dhuhr, zoneId))
    assertEquals("03:20 PM", stringifyAtTimezone(prayerTimes.asr, zoneId))
    assertEquals("05:43 PM", stringifyAtTimezone(prayerTimes.maghrib, zoneId))
    assertEquals("07:05 PM", stringifyAtTimezone(prayerTimes.isha, zoneId))
  }

  @Test
  fun testMoonsightingMethodHighLat() {
    // Values from http://www.moonsighting.com/pray.php
    val date = DateComponents(2016, 1, 1)
    val parameters = MOON_SIGHTING_COMMITTEE.parameters.copy(madhab = HANAFI)
    val coordinates = Coordinates(59.9094, 10.7349)

    val zoneId = "Europe/Oslo"
    val prayerTimes = PrayerTimes(coordinates, date, parameters)
    assertEquals("07:34 AM", stringifyAtTimezone(prayerTimes.fajr, zoneId))
    assertEquals("09:19 AM", stringifyAtTimezone(prayerTimes.sunrise, zoneId))
    assertEquals("12:25 PM", stringifyAtTimezone(prayerTimes.dhuhr, zoneId))
    assertEquals("01:36 PM", stringifyAtTimezone(prayerTimes.asr, zoneId))
    assertEquals("03:25 PM", stringifyAtTimezone(prayerTimes.maghrib, zoneId))
    assertEquals("05:02 PM", stringifyAtTimezone(prayerTimes.isha, zoneId))
  }

  @Test
  fun testTimeForPrayer() {
    val components = DateComponents(2016, 7, 1)
    val parameters = MUSLIM_WORLD_LEAGUE.parameters.copy(
      madhab = HANAFI, highLatitudeRule = TWILIGHT_ANGLE)
    val coordinates = Coordinates(59.9094, 10.7349)

    val p = PrayerTimes(coordinates, components, parameters)
    assertEquals(p.fajr, p.timeForPrayer(FAJR))
    assertEquals(p.sunrise, p.timeForPrayer(SUNRISE))
    assertEquals(p.dhuhr, p.timeForPrayer(DHUHR))
    assertEquals(p.asr, p.timeForPrayer(ASR))
    assertEquals(p.maghrib, p.timeForPrayer(MAGHRIB))
    assertEquals(p.isha, p.timeForPrayer(ISHA))

    assertNull(p.timeForPrayer(NONE))
  }

  @Test
  fun testCurrentPrayer() {
    val components = DateComponents(2015, 9, 1)
    val parameters = KARACHI.parameters.copy(madhab = HANAFI, highLatitudeRule = TWILIGHT_ANGLE)
    val coordinates = Coordinates(33.720817, 73.090032)

    val p = PrayerTimes(coordinates, components, parameters)
    assertEquals(NONE, p.currentPrayer(addSeconds(p.fajr, -1)))
    assertEquals(FAJR, p.currentPrayer(p.fajr))
    assertEquals(FAJR, p.currentPrayer(addSeconds(p.fajr, 1)))
    assertEquals(SUNRISE, p.currentPrayer(addSeconds(p.sunrise, 1)))
    assertEquals(DHUHR, p.currentPrayer(addSeconds(p.dhuhr, 1)))
    assertEquals(ASR, p.currentPrayer(addSeconds(p.asr, 1)))
    assertEquals(MAGHRIB, p.currentPrayer(addSeconds(p.maghrib, 1)))
    assertEquals(ISHA, p.currentPrayer(addSeconds(p.isha, 1)))
  }

  @Test
  fun testNextPrayer() {
    val components = DateComponents(2015, 9, 1)
    val parameters = KARACHI.parameters.copy(madhab = HANAFI, highLatitudeRule = TWILIGHT_ANGLE)
    val coordinates = Coordinates(33.720817, 73.090032)

    val p = PrayerTimes(coordinates, components, parameters)
    assertEquals(FAJR, p.nextPrayer(addSeconds(p.fajr, -1)))
    assertEquals(SUNRISE, p.nextPrayer(p.fajr))
    assertEquals(SUNRISE, p.nextPrayer(addSeconds(p.fajr, 1)))
    assertEquals(DHUHR, p.nextPrayer(addSeconds(p.sunrise, 1)))
    assertEquals(ASR, p.nextPrayer(addSeconds(p.dhuhr, 1)))
    assertEquals(MAGHRIB, p.nextPrayer(addSeconds(p.asr, 1)))
    assertEquals(ISHA, p.nextPrayer(addSeconds(p.maghrib, 1)))
    assertEquals(NONE, p.nextPrayer(addSeconds(p.isha, 1)))
  }
}