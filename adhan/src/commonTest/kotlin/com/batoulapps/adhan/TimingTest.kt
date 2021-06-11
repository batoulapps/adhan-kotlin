package com.batoulapps.adhan

import com.batoulapps.adhan.CalculationMethod.DUBAI
import com.batoulapps.adhan.CalculationMethod.EGYPTIAN
import com.batoulapps.adhan.CalculationMethod.KARACHI
import com.batoulapps.adhan.CalculationMethod.KUWAIT
import com.batoulapps.adhan.CalculationMethod.MOON_SIGHTING_COMMITTEE
import com.batoulapps.adhan.CalculationMethod.MUSLIM_WORLD_LEAGUE
import com.batoulapps.adhan.CalculationMethod.NORTH_AMERICA
import com.batoulapps.adhan.CalculationMethod.OTHER
import com.batoulapps.adhan.CalculationMethod.QATAR
import com.batoulapps.adhan.CalculationMethod.SINGAPORE
import com.batoulapps.adhan.CalculationMethod.UMM_AL_QURA
import com.batoulapps.adhan.HighLatitudeRule.MIDDLE_OF_THE_NIGHT
import com.batoulapps.adhan.HighLatitudeRule.SEVENTH_OF_THE_NIGHT
import com.batoulapps.adhan.HighLatitudeRule.TWILIGHT_ANGLE
import com.batoulapps.adhan.Madhab.HANAFI
import com.batoulapps.adhan.Madhab.SHAFI
import com.batoulapps.adhan.data.DateComponents
import com.batoulapps.adhan.data.TimingFile
import com.batoulapps.adhan.data.TimingParameters
import com.batoulapps.adhan.internal.TestUtils.getDateComponents
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okio.ExperimentalFileSystem
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

class TimingTest {

  @ExperimentalFileSystem
  @Test
  fun testTimes() {
    val json = Json { ignoreUnknownKeys = true }
    val fs = FileSystem.SYSTEM

    val jsonPath = "../Shared/Times/".toPath()
    if (!fs.exists(jsonPath)) {
      // skip this test on iOS for now - should consider doing this via expect
      // and actual to avoid risking this not running on jvm, macOS, and others
      return
    }

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