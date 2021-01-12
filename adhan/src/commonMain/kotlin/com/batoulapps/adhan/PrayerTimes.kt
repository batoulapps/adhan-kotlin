package com.batoulapps.adhan

import com.batoulapps.adhan.Prayer.ASR
import com.batoulapps.adhan.Prayer.DHUHR
import com.batoulapps.adhan.Prayer.FAJR
import com.batoulapps.adhan.Prayer.ISHA
import com.batoulapps.adhan.Prayer.MAGHRIB
import com.batoulapps.adhan.Prayer.NONE
import com.batoulapps.adhan.Prayer.SUNRISE
import com.batoulapps.adhan.data.CalendarUtil.add
import com.batoulapps.adhan.data.CalendarUtil.isLeapYear
import com.batoulapps.adhan.data.CalendarUtil.resolveTime
import com.batoulapps.adhan.data.CalendarUtil.roundedMinute
import com.batoulapps.adhan.data.DateComponents
import com.batoulapps.adhan.data.TimeComponents
import com.batoulapps.adhan.internal.SolarTime
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.jvm.JvmOverloads
import kotlin.math.abs
import kotlin.math.roundToInt

data class PrayerTimes(
  val coordinates: Coordinates,
  val dateComponents: DateComponents,
  val calculationParameters: CalculationParameters
) {
  val fajr: LocalDateTime
  val sunrise: LocalDateTime
  val dhuhr: LocalDateTime
  val asr: LocalDateTime
  val maghrib: LocalDateTime
  val isha: LocalDateTime

  @JvmOverloads
  fun currentPrayer(time: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)): Prayer {
    return when {
      time >= isha -> { ISHA }
      time >= maghrib -> { MAGHRIB }
      time >= asr -> { ASR }
      time >= dhuhr -> { DHUHR }
      time >= sunrise -> { SUNRISE }
      time >= fajr -> { FAJR }
      else -> { NONE }
    }
  }

  @JvmOverloads
  fun nextPrayer(time: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)): Prayer {
    return when {
      time >= isha -> { NONE }
      time >= maghrib -> { ISHA }
      time >= asr -> { MAGHRIB }
      time >= dhuhr -> { ASR }
      time >= sunrise -> { DHUHR }
      time >= fajr -> { SUNRISE }
      else -> { FAJR }
    }
  }

  fun timeForPrayer(prayer: Prayer): LocalDateTime? {
    return when (prayer) {
      FAJR -> fajr
      SUNRISE -> sunrise
      DHUHR -> dhuhr
      ASR -> asr
      MAGHRIB -> maghrib
      ISHA -> isha
      NONE -> null
    }
  }

  companion object {
    private fun seasonAdjustedMorningTwilight(
      latitude: Double, day: Int, year: Int, sunrise: LocalDateTime
    ): LocalDateTime {
      val a: Double = 75 + 28.65 / 55.0 * abs(latitude)
      val b: Double = 75 + 19.44 / 55.0 * abs(latitude)
      val c: Double = 75 + 32.74 / 55.0 * abs(latitude)
      val d: Double = 75 + 48.10 / 55.0 * abs(latitude)
      val dyy = daysSinceSolstice(day, year, latitude)
      val adjustment = when {
        dyy < 91 -> { a + (b - a) / 91.0 * dyy }
        dyy < 137 -> { b + (c - b) / 46.0 * (dyy - 91) }
        dyy < 183 -> { c + (d - c) / 46.0 * (dyy - 137) }
        dyy < 229 -> { d + (c - d) / 46.0 * (dyy - 183) }
        dyy < 275 -> { c + (b - c) / 46.0 * (dyy - 229) }
        else -> { b + (a - b) / 91.0 * (dyy - 275) }
      }
      return add(sunrise, -(adjustment * 60.0).roundToInt(), DateTimeUnit.SECOND)
    }

    private fun seasonAdjustedEveningTwilight(
      latitude: Double, day: Int, year: Int, sunset: LocalDateTime
    ): LocalDateTime {
      val a: Double = 75 + 25.60 / 55.0 * abs(latitude)
      val b: Double = 75 + 2.050 / 55.0 * abs(latitude)
      val c: Double = 75 - 9.210 / 55.0 * abs(latitude)
      val d: Double = 75 + 6.140 / 55.0 * abs(latitude)
      val dyy = daysSinceSolstice(day, year, latitude)
      val adjustment = when {
        dyy < 91 -> { a + (b - a) / 91.0 * dyy }
        dyy < 137 -> { b + (c - b) / 46.0 * (dyy - 91) }
        dyy < 183 -> { c + (d - c) / 46.0 * (dyy - 137) }
        dyy < 229 -> { d + (c - d) / 46.0 * (dyy - 183) }
        dyy < 275 -> { c + (b - c) / 46.0 * (dyy - 229) }
        else -> { b + (a - b) / 91.0 * (dyy - 275) }
      }
      return add(sunset, (adjustment * 60.0).roundToInt(), DateTimeUnit.SECOND)
    }

    fun daysSinceSolstice(dayOfYear: Int, year: Int, latitude: Double): Int {
      var daysSinceSolistice: Int
      val northernOffset = 10
      val isLeapYear = isLeapYear(year)
      val southernOffset = if (isLeapYear) 173 else 172
      val daysInYear = if (isLeapYear) 366 else 365
      if (latitude >= 0) {
        daysSinceSolistice = dayOfYear + northernOffset
        if (daysSinceSolistice >= daysInYear) {
          daysSinceSolistice -= daysInYear
        }
      } else {
        daysSinceSolistice = dayOfYear - southernOffset
        if (daysSinceSolistice < 0) {
          daysSinceSolistice += daysInYear
        }
      }
      return daysSinceSolistice
    }
  }

  private fun LocalDateTime.before(other: LocalDateTime): Boolean {
    return toInstant(TimeZone.UTC).toEpochMilliseconds() <
        other.toInstant(TimeZone.UTC).toEpochMilliseconds()
  }

  private fun LocalDateTime.after(other: LocalDateTime): Boolean {
    return toInstant(TimeZone.UTC).toEpochMilliseconds() >
        other.toInstant(TimeZone.UTC).toEpochMilliseconds()
  }

  /**
   * Calculate PrayerTimes
   * @param coordinates the coordinates of the location
   * @param date the date components for that location
   * @param parameters the parameters for the calculation
   */
  init {
    var tempFajr: LocalDateTime? = null
    val tempSunrise: LocalDateTime?
    val tempDhuhr: LocalDateTime?
    var tempAsr: LocalDateTime? = null
    val tempMaghrib: LocalDateTime?
    var tempIsha: LocalDateTime? = null
    val prayerDate: LocalDateTime = resolveTime(dateComponents)

    val dayOfYear: Int = prayerDate.dayOfYear

    val tomorrowDate: LocalDateTime = add(prayerDate, 1, DateTimeUnit.DAY)
    val tomorrow: DateComponents = DateComponents.fromLocalDateTime(tomorrowDate)

    val solarTime = SolarTime(dateComponents, coordinates)
    var timeComponents = TimeComponents.fromDouble(solarTime.transit)
    val transit = timeComponents?.dateComponents(dateComponents)

    timeComponents = TimeComponents.fromDouble(solarTime.sunrise)
    val sunriseComponents = timeComponents?.dateComponents(dateComponents)

    timeComponents = TimeComponents.fromDouble(solarTime.sunset)
    val sunsetComponents = timeComponents?.dateComponents(dateComponents)

    val tomorrowSolarTime = SolarTime(tomorrow, coordinates)
    val tomorrowSunriseComponents = TimeComponents.fromDouble(tomorrowSolarTime.sunrise)

    val error = transit == null || sunriseComponents == null || sunsetComponents == null || tomorrowSunriseComponents == null
    if (!error) {
      tempDhuhr = transit!!
      tempSunrise = sunriseComponents!!
      tempMaghrib = sunsetComponents!!
      timeComponents = TimeComponents.fromDouble(
        solarTime.afternoon(calculationParameters.madhab.shadowLength)
      )

      if (timeComponents != null) {
        tempAsr = timeComponents.dateComponents(dateComponents)
      }

      // get night length
      val tomorrowSunrise = tomorrowSunriseComponents!!.dateComponents(tomorrow)
      val night = tomorrowSunrise.toInstant(TimeZone.UTC).toEpochMilliseconds() -
          sunsetComponents.toInstant(TimeZone.UTC).toEpochMilliseconds()

      timeComponents = TimeComponents.fromDouble(
        solarTime.hourAngle(-calculationParameters.fajrAngle, false))
      if (timeComponents != null) {
        tempFajr = timeComponents.dateComponents(dateComponents)
      }

      if (calculationParameters.method === CalculationMethod.MOON_SIGHTING_COMMITTEE &&
        coordinates.latitude >= 55
      ) {
        tempFajr = add(
          sunriseComponents, -1 * (night / 7000).toInt(), DateTimeUnit.SECOND
        )
      }

      val nightPortions = calculationParameters.nightPortions()

      val safeFajr: LocalDateTime =
        if (calculationParameters.method === CalculationMethod.MOON_SIGHTING_COMMITTEE) {
        seasonAdjustedMorningTwilight(
          coordinates.latitude,
          dayOfYear,
          dateComponents.year,
          sunriseComponents
        )
      } else {
        val portion = nightPortions.fajr
        val nightFraction = (portion * night / 1000).toLong()
        add(
          sunriseComponents, -1 * nightFraction.toInt(), DateTimeUnit.SECOND
        )
      }

      if (tempFajr == null || tempFajr.before(safeFajr)) {
        tempFajr = safeFajr
      }

      // Isha calculation with check against safe value
      if (calculationParameters.ishaInterval > 0) {
        tempIsha = add(tempMaghrib, calculationParameters.ishaInterval * 60, DateTimeUnit.SECOND)
      } else {
        timeComponents = TimeComponents.fromDouble(
          solarTime.hourAngle(-calculationParameters.ishaAngle, true)
        )
        if (timeComponents != null) {
          tempIsha = timeComponents.dateComponents(dateComponents)
        }

        if (calculationParameters.method === CalculationMethod.MOON_SIGHTING_COMMITTEE &&
          coordinates.latitude >= 55
        ) {
          val nightFraction = night / 7000
          tempIsha = add(sunsetComponents, nightFraction.toInt(), DateTimeUnit.SECOND)
        }

        val safeIsha: LocalDateTime = if (calculationParameters.method === CalculationMethod.MOON_SIGHTING_COMMITTEE) {
          seasonAdjustedEveningTwilight(
            coordinates.latitude, dayOfYear, dateComponents.year, sunsetComponents
          )
        } else {
          val portion = nightPortions.isha
          val nightFraction = (portion * night / 1000).toLong()
          add(sunsetComponents, nightFraction.toInt(), DateTimeUnit.SECOND)
        }

        if (tempIsha == null || tempIsha.after(safeIsha)) {
          tempIsha = safeIsha
        }
      }
    } else {
      tempSunrise = null
      tempDhuhr = null
      tempAsr = null
      tempMaghrib = null
    }

    if (error || tempAsr == null) {
      // if we don't have all prayer times then initialization failed
      throw IllegalStateException()
    } else {
      // Assign final times to public struct members with all offsets
      fajr = roundedMinute(
        add(
          add(tempFajr!!, calculationParameters.prayerAdjustments.fajr, DateTimeUnit.MINUTE),
          calculationParameters.methodAdjustments.fajr,
          DateTimeUnit.MINUTE
        )
      )
      sunrise = roundedMinute(
        add(
          add(tempSunrise!!, calculationParameters.prayerAdjustments.sunrise, DateTimeUnit.MINUTE),
          calculationParameters.methodAdjustments.sunrise,
          DateTimeUnit.MINUTE
        )
      )
      dhuhr = roundedMinute(
        add(
          add(tempDhuhr!!, calculationParameters.prayerAdjustments.dhuhr, DateTimeUnit.MINUTE),
          calculationParameters.methodAdjustments.dhuhr,
          DateTimeUnit.MINUTE
        )
      )
      asr = roundedMinute(
        add(
          add(tempAsr, calculationParameters.prayerAdjustments.asr, DateTimeUnit.MINUTE),
          calculationParameters.methodAdjustments.asr,
          DateTimeUnit.MINUTE
        )
      )
      maghrib = roundedMinute(
        add(
          add(tempMaghrib!!, calculationParameters.prayerAdjustments.maghrib, DateTimeUnit.MINUTE),
          calculationParameters.methodAdjustments.maghrib,
          DateTimeUnit.MINUTE
        )
      )
      isha = roundedMinute(
        add(
          add(tempIsha!!, calculationParameters.prayerAdjustments.isha, DateTimeUnit.MINUTE),
          calculationParameters.methodAdjustments.isha,
          DateTimeUnit.MINUTE
        )
      )
    }
  }
}