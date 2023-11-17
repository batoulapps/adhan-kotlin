package com.batoulapps.adhan2

import com.batoulapps.adhan2.CalculationMethod.KARACHI
import com.batoulapps.adhan2.CalculationMethod.MOON_SIGHTING_COMMITTEE
import com.batoulapps.adhan2.CalculationMethod.MUSLIM_WORLD_LEAGUE
import com.batoulapps.adhan2.CalculationMethod.NORTH_AMERICA
import com.batoulapps.adhan2.HighLatitudeRule.MIDDLE_OF_THE_NIGHT
import com.batoulapps.adhan2.HighLatitudeRule.SEVENTH_OF_THE_NIGHT
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
import com.batoulapps.adhan2.model.Shafaq
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

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
  fun testDiyanet() {
    // values from https://namazvakitleri.diyanet.gov.tr/en-US/9541/prayer-time-for-istanbul
    val date = DateComponents(2020, 4, 16)
    val parameters = CalculationMethod.TURKEY.parameters
    val coordinates = Coordinates(41.005616, 28.976380)

    val zoneId = "Europe/Istanbul"
    val prayerTimes = PrayerTimes(coordinates, date, parameters)
    assertEquals("04:44 AM", stringifyAtTimezone(prayerTimes.fajr, zoneId))
    assertEquals("06:16 AM", stringifyAtTimezone(prayerTimes.sunrise, zoneId))
    assertEquals("01:09 PM", stringifyAtTimezone(prayerTimes.dhuhr, zoneId))
    assertEquals("04:53 PM", stringifyAtTimezone(prayerTimes.asr, zoneId)) // original time 4:52pm
    assertEquals("07:52 PM", stringifyAtTimezone(prayerTimes.maghrib, zoneId))
    assertEquals("09:19 PM", stringifyAtTimezone(prayerTimes.isha, zoneId)) // original time 9:18pm
  }

  @Test
  fun testEgyptian() {
    val date = DateComponents(2020, 1, 1)
    val parameters = CalculationMethod.EGYPTIAN.parameters
    val coordinates = Coordinates(latitude = 30.028703, longitude = 31.249528)

    val zoneId = "Africa/Cairo"
    val prayerTimes = PrayerTimes(coordinates, date, parameters)
    assertEquals("05:18 AM", stringifyAtTimezone(prayerTimes.fajr, zoneId))
    assertEquals("06:51 AM", stringifyAtTimezone(prayerTimes.sunrise, zoneId))
    assertEquals("11:59 AM", stringifyAtTimezone(prayerTimes.dhuhr, zoneId))
    assertEquals("02:47 PM", stringifyAtTimezone(prayerTimes.asr, zoneId))
    assertEquals("05:06 PM", stringifyAtTimezone(prayerTimes.maghrib, zoneId))
    assertEquals("06:29 PM", stringifyAtTimezone(prayerTimes.isha, zoneId))
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

  @Test
  fun testInvalidDate() {
    assertFailsWith<IllegalArgumentException> {
      val date = DateComponents(0, 0, 0)
      PrayerTimes(Coordinates(33.720817, 73.090032), date, MUSLIM_WORLD_LEAGUE.parameters)
    }

    assertFailsWith<IllegalArgumentException> {
      val date = DateComponents(-1, 99, 99)
      PrayerTimes(Coordinates(33.720817, 73.090032), date, MUSLIM_WORLD_LEAGUE.parameters)
    }
  }

  @Test
  fun testInvalidLocation() {
    assertFailsWith<IllegalArgumentException> {
      val date = DateComponents(2019, 1, 1)
      PrayerTimes(Coordinates(999.0, 999.0), date, MUSLIM_WORLD_LEAGUE.parameters)
    }
  }

  @Test
  fun testExtremeLocation() {
    assertFailsWith<IllegalStateException> {
      val date = DateComponents(2018, 1, 1)
      PrayerTimes(Coordinates(71.275009, -156.761368), date, MUSLIM_WORLD_LEAGUE.parameters)
    }

    val date = DateComponents(2018, 3, 1)
    val prayerTimes = PrayerTimes(Coordinates(71.275009, -156.761368), date, MUSLIM_WORLD_LEAGUE.parameters)
    assertNotNull(prayerTimes.fajr)
  }

  @Test
  fun testHighLatitudeRule() {
    val date = DateComponents(2020, 6, 15)
    val parameters = MUSLIM_WORLD_LEAGUE.parameters.copy(highLatitudeRule = MIDDLE_OF_THE_NIGHT)
    val coordinates = Coordinates(latitude = 55.983226, longitude = -3.216649)

    val zoneId = "Europe/London"
    val prayerTimes = PrayerTimes(coordinates, date, parameters)
    assertEquals("01:14 AM", stringifyAtTimezone(prayerTimes.fajr, zoneId))
    assertEquals("04:26 AM", stringifyAtTimezone(prayerTimes.sunrise, zoneId))
    assertEquals("01:14 PM", stringifyAtTimezone(prayerTimes.dhuhr, zoneId))
    assertEquals("05:46 PM", stringifyAtTimezone(prayerTimes.asr, zoneId))
    assertEquals("10:01 PM", stringifyAtTimezone(prayerTimes.maghrib, zoneId))
    assertEquals("01:14 AM", stringifyAtTimezone(prayerTimes.isha, zoneId))

    val seventhParams = parameters.copy(highLatitudeRule = SEVENTH_OF_THE_NIGHT)
    val seventhPrayerTimes = PrayerTimes(coordinates, date, seventhParams)
    assertEquals("03:31 AM", stringifyAtTimezone(seventhPrayerTimes.fajr, zoneId))
    assertEquals("04:26 AM", stringifyAtTimezone(seventhPrayerTimes.sunrise, zoneId))
    assertEquals("01:14 PM", stringifyAtTimezone(seventhPrayerTimes.dhuhr, zoneId))
    assertEquals("05:46 PM", stringifyAtTimezone(seventhPrayerTimes.asr, zoneId))
    assertEquals("10:01 PM", stringifyAtTimezone(seventhPrayerTimes.maghrib, zoneId))
    assertEquals("10:56 PM", stringifyAtTimezone(seventhPrayerTimes.isha, zoneId))

    val twilightParams = parameters.copy(highLatitudeRule = TWILIGHT_ANGLE)
    val twilightPrayerTimes = PrayerTimes(coordinates, date, twilightParams)
    assertEquals("02:31 AM", stringifyAtTimezone(twilightPrayerTimes.fajr, zoneId))
    assertEquals("04:26 AM", stringifyAtTimezone(twilightPrayerTimes.sunrise, zoneId))
    assertEquals("01:14 PM", stringifyAtTimezone(twilightPrayerTimes.dhuhr, zoneId))
    assertEquals("05:46 PM", stringifyAtTimezone(twilightPrayerTimes.asr, zoneId))
    assertEquals("10:01 PM", stringifyAtTimezone(twilightPrayerTimes.maghrib, zoneId))
    assertEquals("11:50 PM", stringifyAtTimezone(twilightPrayerTimes.isha, zoneId))

    val autoHighLatitudeRule = parameters.copy(highLatitudeRule = null)
    val autoPrayerTimes = PrayerTimes(coordinates, date, autoHighLatitudeRule)
    assertEquals(seventhPrayerTimes.fajr, autoPrayerTimes.fajr)
    assertEquals(seventhPrayerTimes.sunrise, autoPrayerTimes.sunrise)
    assertEquals(seventhPrayerTimes.dhuhr, autoPrayerTimes.dhuhr)
    assertEquals(seventhPrayerTimes.asr, autoPrayerTimes.asr)
    assertEquals(seventhPrayerTimes.maghrib, autoPrayerTimes.maghrib)
    assertEquals(seventhPrayerTimes.isha, autoPrayerTimes.isha)
  }

  @Test
  fun testRecommendedHighLatitudeRule() {
    val coords1 = Coordinates(latitude = 45.983226, longitude = -3.216649)
    assertEquals(MIDDLE_OF_THE_NIGHT, HighLatitudeRule.recommendedFor(coords1))

    val coords2 = Coordinates(latitude = 48.983226, longitude = -3.216649)
    assertEquals(SEVENTH_OF_THE_NIGHT, HighLatitudeRule.recommendedFor(coords2))
  }

  @Test
  fun testShafaqGeneral() {
    val parameters = MOON_SIGHTING_COMMITTEE.parameters.copy(shafaq = Shafaq.GENERAL, madhab = HANAFI)
    val coordinates = Coordinates(latitude = 43.494, longitude = -79.844)

    val zoneId = "America/New_York"

    val date = DateComponents(2021, 1, 1)
    val prayerTimes = PrayerTimes(coordinates, date, parameters)
    assertEquals("06:16 AM", stringifyAtTimezone(prayerTimes.fajr, zoneId))
    assertEquals("07:52 AM", stringifyAtTimezone(prayerTimes.sunrise, zoneId))
    assertEquals("12:28 PM", stringifyAtTimezone(prayerTimes.dhuhr, zoneId))
    assertEquals("03:12 PM", stringifyAtTimezone(prayerTimes.asr, zoneId))
    assertEquals("04:57 PM", stringifyAtTimezone(prayerTimes.maghrib, zoneId))
    assertEquals("06:27 PM", stringifyAtTimezone(prayerTimes.isha, zoneId))

    val secondDate = DateComponents(2021, 4, 1)
    val secondPrayerTimes = PrayerTimes(coordinates, secondDate, parameters)
    assertEquals("05:28 AM", stringifyAtTimezone(secondPrayerTimes.fajr, zoneId))
    assertEquals("07:01 AM", stringifyAtTimezone(secondPrayerTimes.sunrise, zoneId))
    assertEquals("01:28 PM", stringifyAtTimezone(secondPrayerTimes.dhuhr, zoneId))
    assertEquals("05:53 PM", stringifyAtTimezone(secondPrayerTimes.asr, zoneId))
    assertEquals("07:49 PM", stringifyAtTimezone(secondPrayerTimes.maghrib, zoneId))
    assertEquals("09:01 PM", stringifyAtTimezone(secondPrayerTimes.isha, zoneId))

    val thirdDate = DateComponents(2021, 7, 1)
    val thirdPrayerTimes = PrayerTimes(coordinates, thirdDate, parameters)
    assertEquals("03:52 AM", stringifyAtTimezone(thirdPrayerTimes.fajr, zoneId))
    assertEquals("05:42 AM", stringifyAtTimezone(thirdPrayerTimes.sunrise, zoneId))
    assertEquals("01:28 PM", stringifyAtTimezone(thirdPrayerTimes.dhuhr, zoneId))
    assertEquals("06:42 PM", stringifyAtTimezone(thirdPrayerTimes.asr, zoneId))
    assertEquals("09:07 PM", stringifyAtTimezone(thirdPrayerTimes.maghrib, zoneId))
    assertEquals("10:22 PM", stringifyAtTimezone(thirdPrayerTimes.isha, zoneId))

    val fourthDate = DateComponents(2021, 11, 1)
    val fourthPrayerTimes = PrayerTimes(coordinates, fourthDate, parameters)
    assertEquals("06:22 AM", stringifyAtTimezone(fourthPrayerTimes.fajr, zoneId))
    assertEquals("07:55 AM", stringifyAtTimezone(fourthPrayerTimes.sunrise, zoneId))
    assertEquals("01:08 PM", stringifyAtTimezone(fourthPrayerTimes.dhuhr, zoneId))
    assertEquals("04:26 PM", stringifyAtTimezone(fourthPrayerTimes.asr, zoneId))
    assertEquals("06:13 PM", stringifyAtTimezone(fourthPrayerTimes.maghrib, zoneId))
    assertEquals("07:35 PM", stringifyAtTimezone(fourthPrayerTimes.isha, zoneId))
  }

  @Test
  fun testShafaqAhmer() {
    val parameters = MOON_SIGHTING_COMMITTEE.parameters.copy(shafaq = Shafaq.AHMER)
    val coordinates = Coordinates(latitude = 43.494, longitude = -79.844)

    val zoneId = "America/New_York"

    val date = DateComponents(2021, 1, 1)
    val prayerTimes = PrayerTimes(coordinates, date, parameters)
    assertEquals("06:16 AM", stringifyAtTimezone(prayerTimes.fajr, zoneId))
    assertEquals("07:52 AM", stringifyAtTimezone(prayerTimes.sunrise, zoneId))
    assertEquals("12:28 PM", stringifyAtTimezone(prayerTimes.dhuhr, zoneId))
    assertEquals("02:37 PM", stringifyAtTimezone(prayerTimes.asr, zoneId))
    assertEquals("04:57 PM", stringifyAtTimezone(prayerTimes.maghrib, zoneId))
    assertEquals("06:07 PM", stringifyAtTimezone(prayerTimes.isha, zoneId)) // value from source is 6:08 PM

    val secondDate = DateComponents(2021, 4, 1)
    val secondPrayerTimes = PrayerTimes(coordinates, secondDate, parameters)
    assertEquals("05:28 AM", stringifyAtTimezone(secondPrayerTimes.fajr, zoneId))
    assertEquals("07:01 AM", stringifyAtTimezone(secondPrayerTimes.sunrise, zoneId))
    assertEquals("01:28 PM", stringifyAtTimezone(secondPrayerTimes.dhuhr, zoneId))
    assertEquals("04:59 PM", stringifyAtTimezone(secondPrayerTimes.asr, zoneId))
    assertEquals("07:49 PM", stringifyAtTimezone(secondPrayerTimes.maghrib, zoneId))
    assertEquals("08:45 PM", stringifyAtTimezone(secondPrayerTimes.isha, zoneId))

    val thirdDate = DateComponents(2021, 7, 1)
    val thirdPrayerTimes = PrayerTimes(coordinates, thirdDate, parameters)
    assertEquals("03:52 AM", stringifyAtTimezone(thirdPrayerTimes.fajr, zoneId))
    assertEquals("05:42 AM", stringifyAtTimezone(thirdPrayerTimes.sunrise, zoneId))
    assertEquals("01:28 PM", stringifyAtTimezone(thirdPrayerTimes.dhuhr, zoneId))
    assertEquals("05:29 PM", stringifyAtTimezone(thirdPrayerTimes.asr, zoneId))
    assertEquals("09:07 PM", stringifyAtTimezone(thirdPrayerTimes.maghrib, zoneId))
    assertEquals("10:19 PM", stringifyAtTimezone(thirdPrayerTimes.isha, zoneId))

    val fourthDate = DateComponents(2021, 11, 1)
    val fourthPrayerTimes = PrayerTimes(coordinates, fourthDate, parameters)
    assertEquals("06:22 AM", stringifyAtTimezone(fourthPrayerTimes.fajr, zoneId))
    assertEquals("07:55 AM", stringifyAtTimezone(fourthPrayerTimes.sunrise, zoneId))
    assertEquals("01:08 PM", stringifyAtTimezone(fourthPrayerTimes.dhuhr, zoneId))
    assertEquals("03:45 PM", stringifyAtTimezone(fourthPrayerTimes.asr, zoneId))
    assertEquals("06:13 PM", stringifyAtTimezone(fourthPrayerTimes.maghrib, zoneId))
    assertEquals("07:15 PM", stringifyAtTimezone(fourthPrayerTimes.isha, zoneId))
  }

  @Test
  fun testShafaqAbyad() {
    val parameters = MOON_SIGHTING_COMMITTEE.parameters.copy(shafaq = Shafaq.ABYAD, madhab = HANAFI)
    val coordinates = Coordinates(latitude = 43.494, longitude = -79.844)

    val zoneId = "America/New_York"

    val date = DateComponents(2021, 1, 1)
    val prayerTimes = PrayerTimes(coordinates, date, parameters)
    assertEquals("06:16 AM", stringifyAtTimezone(prayerTimes.fajr, zoneId))
    assertEquals("07:52 AM", stringifyAtTimezone(prayerTimes.sunrise, zoneId))
    assertEquals("12:28 PM", stringifyAtTimezone(prayerTimes.dhuhr, zoneId))
    assertEquals("03:12 PM", stringifyAtTimezone(prayerTimes.asr, zoneId))
    assertEquals("04:57 PM", stringifyAtTimezone(prayerTimes.maghrib, zoneId))
    assertEquals("06:28 PM", stringifyAtTimezone(prayerTimes.isha, zoneId))

    val secondDate = DateComponents(2021, 4, 1)
    val secondPrayerTimes = PrayerTimes(coordinates, secondDate, parameters)
    assertEquals("05:28 AM", stringifyAtTimezone(secondPrayerTimes.fajr, zoneId))
    assertEquals("07:01 AM", stringifyAtTimezone(secondPrayerTimes.sunrise, zoneId))
    assertEquals("01:28 PM", stringifyAtTimezone(secondPrayerTimes.dhuhr, zoneId))
    assertEquals("05:53 PM", stringifyAtTimezone(secondPrayerTimes.asr, zoneId))
    assertEquals("07:49 PM", stringifyAtTimezone(secondPrayerTimes.maghrib, zoneId))
    assertEquals("09:12 PM", stringifyAtTimezone(secondPrayerTimes.isha, zoneId))

    val thirdDate = DateComponents(2021, 7, 1)
    val thirdPrayerTimes = PrayerTimes(coordinates, thirdDate, parameters)
    assertEquals("03:52 AM", stringifyAtTimezone(thirdPrayerTimes.fajr, zoneId))
    assertEquals("05:42 AM", stringifyAtTimezone(thirdPrayerTimes.sunrise, zoneId))
    assertEquals("01:28 PM", stringifyAtTimezone(thirdPrayerTimes.dhuhr, zoneId))
    assertEquals("06:42 PM", stringifyAtTimezone(thirdPrayerTimes.asr, zoneId))
    assertEquals("09:07 PM", stringifyAtTimezone(thirdPrayerTimes.maghrib, zoneId))
    assertEquals("11:17 PM", stringifyAtTimezone(thirdPrayerTimes.isha, zoneId))

    val fourthDate = DateComponents(2021, 11, 1)
    val fourthPrayerTimes = PrayerTimes(coordinates, fourthDate, parameters)
    assertEquals("06:22 AM", stringifyAtTimezone(fourthPrayerTimes.fajr, zoneId))
    assertEquals("07:55 AM", stringifyAtTimezone(fourthPrayerTimes.sunrise, zoneId))
    assertEquals("01:08 PM", stringifyAtTimezone(fourthPrayerTimes.dhuhr, zoneId))
    assertEquals("04:26 PM", stringifyAtTimezone(fourthPrayerTimes.asr, zoneId))
    assertEquals("06:13 PM", stringifyAtTimezone(fourthPrayerTimes.maghrib, zoneId))
    assertEquals("07:37 PM", stringifyAtTimezone(fourthPrayerTimes.isha, zoneId))
  }
}