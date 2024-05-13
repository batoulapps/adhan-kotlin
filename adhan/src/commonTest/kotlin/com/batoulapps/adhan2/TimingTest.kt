package com.batoulapps.adhan2

import com.batoulapps.adhan2.CalculationMethod.DUBAI
import com.batoulapps.adhan2.CalculationMethod.EGYPTIAN
import com.batoulapps.adhan2.CalculationMethod.KARACHI
import com.batoulapps.adhan2.CalculationMethod.KUWAIT
import com.batoulapps.adhan2.CalculationMethod.MOON_SIGHTING_COMMITTEE
import com.batoulapps.adhan2.CalculationMethod.MUSLIM_WORLD_LEAGUE
import com.batoulapps.adhan2.CalculationMethod.NORTH_AMERICA
import com.batoulapps.adhan2.CalculationMethod.OTHER
import com.batoulapps.adhan2.CalculationMethod.QATAR
import com.batoulapps.adhan2.CalculationMethod.SINGAPORE
import com.batoulapps.adhan2.CalculationMethod.TURKEY
import com.batoulapps.adhan2.CalculationMethod.UMM_AL_QURA
import com.batoulapps.adhan2.HighLatitudeRule.MIDDLE_OF_THE_NIGHT
import com.batoulapps.adhan2.HighLatitudeRule.SEVENTH_OF_THE_NIGHT
import com.batoulapps.adhan2.HighLatitudeRule.TWILIGHT_ANGLE
import com.batoulapps.adhan2.Madhab.HANAFI
import com.batoulapps.adhan2.Madhab.SHAFI
import com.batoulapps.adhan2.data.DateComponents
import com.batoulapps.adhan2.data.TimingFile
import com.batoulapps.adhan2.data.TimingParameters
import com.batoulapps.adhan2.internal.TestUtils.getDateComponents
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath

class TimingTest {

  @Test
  fun testTimes() {
    val json = Json { ignoreUnknownKeys = true }
    val testUtil = TestUtil()

    val root = (testUtil.environmentVariable("ADHAN_ROOT") ?: "").toPath()

    // disable time verification tests for platforms without filesystem support
    // currently, this is just wasm
    val fs = testUtil.fileSystem() ?: return

    val jsonPath = root.resolve("Shared/Times/")
    assertTrue(fs.exists(jsonPath), "Json Path Does not Exist: $jsonPath")

    val dir = fs.list(jsonPath)
    dir.forEach { path ->
      val byteString = fs.read(path) {
        readByteString()
      }
      val contents = byteString.utf8()
      val timingFile = json.decodeFromString<TimingFile>(contents)
      validateTimingFile(timingFile)
    }
  }

  private fun validateTimingFile(timingFile: TimingFile) {
    val coordinates = Coordinates(timingFile.params.latitude, timingFile.params.longitude)
    val parameters = parseParameters(timingFile.params)

    for ((date, fajr, sunrise, dhuhr, asr, maghrib, isha) in timingFile.times) {
      val dateComponents: DateComponents = getDateComponents(date)
      val prayerTimes = PrayerTimes(coordinates, dateComponents, parameters)
      val fajrDifference =
        getDifferenceInMinutes(prayerTimes.fajr, fajr, timingFile.params.timezone)
      assertTrue { fajrDifference <= timingFile.variance }
      val sunriseDifference =
        getDifferenceInMinutes(prayerTimes.sunrise, sunrise, timingFile.params.timezone)
      assertTrue { sunriseDifference <= timingFile.variance }
      val dhuhrDifference =
        getDifferenceInMinutes(prayerTimes.dhuhr, dhuhr, timingFile.params.timezone)
      assertTrue { dhuhrDifference <= timingFile.variance }
      val asrDifference = getDifferenceInMinutes(prayerTimes.asr, asr, timingFile.params.timezone)
      assertTrue { asrDifference <= timingFile.variance }
      val maghribDifference =
        getDifferenceInMinutes(prayerTimes.maghrib, maghrib, timingFile.params.timezone)
      assertTrue { maghribDifference <= timingFile.variance }
      val ishaDifference =
        getDifferenceInMinutes(prayerTimes.isha, isha, timingFile.params.timezone)
      assertTrue { ishaDifference <= timingFile.variance }
    }
  }

  private fun getDifferenceInMinutes(
    prayerTime: Instant,
    jsonTime: String,
    timezone: String
  ): Long {
    val (time, amPm) = jsonTime.split(" ")
    val (hours, minutes) = time.split(":").map { it.toInt() }
    val resolvedHour = when {
      amPm == "PM" && hours == 12 -> 12
      amPm == "PM" -> hours + 12
      amPm == "AM" && hours == 12 -> 0
      else -> hours
    }

    val targetTimeZone = TimeZone.of(timezone)
    val calculatedDateTime = prayerTime.toLocalDateTime(targetTimeZone)

    val referenceDateTime = LocalDateTime(
      // PrayerTimes is in UTC, so we need calculatedDateTime here instead
      calculatedDateTime.year,
      calculatedDateTime.monthNumber,
      calculatedDateTime.dayOfMonth,
      resolvedHour,
      minutes
    )


    val referenceInstant = referenceDateTime.toInstant(targetTimeZone)
    val calculatedInstant = calculatedDateTime.toInstant(targetTimeZone)

    return abs(calculatedInstant.toEpochMilliseconds() - referenceInstant.toEpochMilliseconds()) / (60 * 1000)
  }

  private fun parseParameters(timingParameters: TimingParameters): CalculationParameters {
    val method: CalculationMethod = when (timingParameters.method) {
      "MuslimWorldLeague" -> MUSLIM_WORLD_LEAGUE
      "Egyptian" -> EGYPTIAN
      "Karachi" -> KARACHI
      "UmmAlQura" -> UMM_AL_QURA
      "Dubai" -> DUBAI
      "MoonsightingCommittee" -> MOON_SIGHTING_COMMITTEE
      "NorthAmerica" -> NORTH_AMERICA
      "Kuwait" -> KUWAIT
      "Qatar" -> QATAR
      "Singapore" -> SINGAPORE
      "Turkey" -> TURKEY
      else -> OTHER
    }

    val parameters = method.parameters
    val madhab = if ("Shafi" == timingParameters.madhab) {
      SHAFI
    } else if ("Hanafi" == timingParameters.madhab) {
      HANAFI
    } else {
      parameters.madhab
    }

    val highLatitudeRule = if ("SeventhOfTheNight" == timingParameters.highLatitudeRule) {
      SEVENTH_OF_THE_NIGHT
    } else if ("TwilightAngle" == timingParameters.highLatitudeRule) {
      TWILIGHT_ANGLE
    } else {
      MIDDLE_OF_THE_NIGHT
    }
    return method.parameters.copy(madhab = madhab, highLatitudeRule = highLatitudeRule)
  }
}