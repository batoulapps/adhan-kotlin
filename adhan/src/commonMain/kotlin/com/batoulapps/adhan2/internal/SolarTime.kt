package com.batoulapps.adhan2.internal

import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.data.DateComponents
import com.batoulapps.adhan2.internal.Astronomical.altitudeOfCelestialBody
import com.batoulapps.adhan2.internal.Astronomical.approximateTransit
import com.batoulapps.adhan2.internal.Astronomical.correctedHourAngle
import com.batoulapps.adhan2.internal.Astronomical.correctedTransit
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.tan

class SolarTime(date: DateComponents, coordinates: Coordinates) {
  val transit: Double
  val sunrise: Double
  val sunset: Double

  private val observer: Coordinates
  private val solar: SolarCoordinates
  private val prevSolar: SolarCoordinates
  private val nextSolar: SolarCoordinates
  private val approximateTransit: Double

  init {
    val julianDate = CalendricalHelper.julianDay(date.year, date.month, date.day)
    prevSolar = SolarCoordinates(julianDate - 1)
    solar = SolarCoordinates(julianDate)
    nextSolar = SolarCoordinates(julianDate + 1)
    approximateTransit = approximateTransit(
      coordinates.longitude,
      solar.apparentSiderealTime, solar.rightAscension
    )
    val solarAltitude = -50.0 / 60.0
    observer = coordinates
    transit = correctedTransit(
      approximateTransit, coordinates.longitude,
      solar.apparentSiderealTime, solar.rightAscension, prevSolar.rightAscension,
      nextSolar.rightAscension
    )
    sunrise = correctedHourAngle(
      approximateTransit, solarAltitude,
      coordinates, false, solar.apparentSiderealTime, solar.rightAscension,
      prevSolar.rightAscension, nextSolar.rightAscension, solar.declination,
      prevSolar.declination, nextSolar.declination
    )
    sunset = correctedHourAngle(
      approximateTransit, solarAltitude,
      coordinates, true, solar.apparentSiderealTime, solar.rightAscension,
      prevSolar.rightAscension, nextSolar.rightAscension, solar.declination,
      prevSolar.declination, nextSolar.declination
    )
  }

  fun timeForSolarAngle(angle: Double, afterTransit: Boolean): Double {
    return correctedHourAngle(
      approximateTransit, angle, coordinates = observer,
      afterTransit, solar.apparentSiderealTime, solar.rightAscension,
      prevSolar.rightAscension, nextSolar.rightAscension, solar.declination,
      prevSolar.declination, nextSolar.declination
    )
  }

  // hours from transit
  fun afternoon(shadowLength: ShadowLength): Double {
    val tangent: Double = abs(observer.latitude - solar.declination)
    val inverse: Double =
      shadowLength.shadowLength + tan(tangent.toRadians())
    val angle: Double = atan(1.0 / inverse).toDegrees()

    // A valid afternoon time requires that the sun's disc is fully above
    // the horizon. The hourAngle calculation is based on the midpoint of
    // the sun's disc.
    val solarAngularDiameter = 32.0 / 60.0
    if (angle <= solarAngularDiameter / 2) {
      return Double.NaN
    }

    val maxAltitude = altitudeOfCelestialBody(observer.latitude, solar.declination, 0.0)

    // Confirm the sun's maximum altitude on this day actually reaches the computed angle.
    if (maxAltitude < angle) {
      return Double.NaN
    }

    // Confirm the resulting time is after solar transit (noon) to ensure this is afternoon.
    val result = timeForSolarAngle(angle, true)
    if (result <= transit) {
      return Double.NaN
    }

    return result
  }
}