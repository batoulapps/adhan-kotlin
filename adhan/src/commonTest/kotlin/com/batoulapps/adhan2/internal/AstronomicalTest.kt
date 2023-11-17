package com.batoulapps.adhan2.internal

import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.data.CalendarUtil.isLeapYear
import com.batoulapps.adhan2.data.DateComponents
import com.batoulapps.adhan2.data.TimeComponents
import com.batoulapps.adhan2.internal.Astronomical.altitudeOfCelestialBody
import com.batoulapps.adhan2.internal.Astronomical.apparentObliquityOfTheEcliptic
import com.batoulapps.adhan2.internal.Astronomical.apparentSolarLongitude
import com.batoulapps.adhan2.internal.Astronomical.approximateTransit
import com.batoulapps.adhan2.internal.Astronomical.ascendingLunarNodeLongitude
import com.batoulapps.adhan2.internal.Astronomical.correctedHourAngle
import com.batoulapps.adhan2.internal.Astronomical.correctedTransit
import com.batoulapps.adhan2.internal.Astronomical.interpolate
import com.batoulapps.adhan2.internal.Astronomical.interpolateAngles
import com.batoulapps.adhan2.internal.Astronomical.meanLunarLongitude
import com.batoulapps.adhan2.internal.Astronomical.meanObliquityOfTheEcliptic
import com.batoulapps.adhan2.internal.Astronomical.meanSiderealTime
import com.batoulapps.adhan2.internal.Astronomical.meanSolarAnomaly
import com.batoulapps.adhan2.internal.Astronomical.meanSolarLongitude
import com.batoulapps.adhan2.internal.Astronomical.nutationInLongitude
import com.batoulapps.adhan2.internal.Astronomical.nutationInObliquity
import com.batoulapps.adhan2.internal.Astronomical.solarEquationOfTheCenter
import com.batoulapps.adhan2.internal.CalendricalHelper.julianCentury
import com.batoulapps.adhan2.internal.CalendricalHelper.julianDay
import com.batoulapps.adhan2.internal.DoubleUtil.unwindAngle
import kotlinx.datetime.DateTimeUnit
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AstronomicalTest {

  @Test
  fun testSolarCoordinates() {
    // values from Astronomical Algorithms page 165
    var jd = julianDay( /* year */1992,  /* month */10,  /* day */13)
    var solar = SolarCoordinates( /* julianDay */jd)

    var T = julianCentury( /* julianDay */jd)
    var L0 = meanSolarLongitude( /* julianCentury */T)
    var ε0 = meanObliquityOfTheEcliptic( /* julianCentury */T)
    val εapp = apparentObliquityOfTheEcliptic( /* julianCentury */
      T,  /* meanObliquityOfTheEcliptic */ε0
    )
    val M = meanSolarAnomaly( /* julianCentury */T)
    val C = solarEquationOfTheCenter( /* julianCentury */
      T,  /* meanAnomaly */M
    )
    val λ = apparentSolarLongitude( /* julianCentury */
      T,  /* meanLongitude */L0
    )
    val δ = solar.declination
    val α = unwindAngle(solar.rightAscension)

    //    assertTrue { abs(params.fajrAngle - 18) <= 0.000001 }

    assertTrue { abs(T - -0.072183436) <= 0.00000000001 }
    assertTrue { abs(L0 - 201.80720) <= 0.00001 }
    assertTrue { abs(ε0 - 23.44023) <= 0.00001 }
    assertTrue { abs(εapp - 23.43999) <= 0.00001 }
    assertTrue { abs(M - 278.99397) <= 0.00001 }
    assertTrue { abs(C - -1.89732) <= 0.00001 }

    // lower accuracy than desired
    assertTrue { abs(λ - 199.90895) <= 0.00002 }
    assertTrue { abs(δ - -7.78507) <= 0.00001 }
    assertTrue { abs(α - 198.38083) <= 0.00001 }

    // values from Astronomical Algorithms page 88
    jd = julianDay( /* year */1987,  /* month */4,  /* day */10)
    solar = SolarCoordinates( /* julianDay */jd)
    T = julianCentury( /* julianDay */jd)
    val θ0 = meanSiderealTime( /* julianCentury */T)
    val θapp = solar.apparentSiderealTime
    val Ω = ascendingLunarNodeLongitude( /* julianCentury */T)
    ε0 = meanObliquityOfTheEcliptic( /* julianCentury */T)
    L0 = meanSolarLongitude( /* julianCentury */T)
    val Lp = meanLunarLongitude( /* julianCentury */T)
    val ΔΨ = nutationInLongitude( /* julianCentury */T,  /* solarLongitude */
      L0,  /* lunarLongitude */Lp,  /* ascendingNode */Ω
    )
    val Δε = nutationInObliquity( /* julianCentury */T,  /* solarLongitude */
      L0,  /* lunarLongitude */Lp,  /* ascendingNode */Ω
    )
    val ε = ε0 + Δε

    assertTrue { abs(θ0 - 197.693195) <= 0.000001 }
    assertTrue { abs(θapp - 197.6922295833) <= 0.0001 }

    // values from Astronomical Algorithms page 148
    assertTrue { abs(Ω - 11.2531) <= 0.0001 }
    assertTrue { abs(ΔΨ - -0.0010522) <= 0.0001 }
    assertTrue { abs(Δε - 0.0026230556) <= 0.00001 }
    assertTrue { abs(ε0 - 23.4409463889) <= 0.000001 }
    assertTrue { abs(ε - 23.4435694444) <= 0.00001 }
  }

  @Test
  fun testRightAscensionEdgeCase() {
    lateinit var previousTime: SolarTime
    val coordinates = Coordinates(35 + 47.0 / 60.0, -78 - 39.0 / 60.0)
    for (i in 0..364) {
      val time = SolarTime(
        TestUtils.makeDateWithOffset(2016, 1, 1, i, DateTimeUnit.DAY), coordinates
      )

      if (i > 0) {
        // transit from one day to another should not differ more than one minute
        assertTrue(abs(time.transit - previousTime.transit) < (1.0 / 60.0))

        // sunrise and sunset from one day to another should not differ more than two minutes
        assertTrue(abs(time.sunrise - previousTime.sunrise) < (2.0 / 60.0))
        assertTrue(abs(time.sunset - previousTime.sunset) < (2.0 / 60.0))
      }
      previousTime = time
    }
  }

  @Test
  fun testAltitudeOfCelestialBody() {
    val φ = 38 + 55 / 60.0 + 17.0 / 3600
    val δ = -6 - 43 / 60.0 - 11.61 / 3600
    val H = 64.352133
    val h = altitudeOfCelestialBody( /* observerLatitude */
      φ,  /* declination */δ,  /* localHourAngle */H
    )
    assertTrue { abs(h - 15.1249) <= 0.0001 }
  }

  @Test
  fun testTransitAndHourAngle() {
    // values from Astronomical Algorithms page 103
    val longitude = -71.0833
    val Θ = 177.74208
    val α1 = 40.68021
    val α2 = 41.73129
    val α3 = 42.78204
    val m0 = approximateTransit(
      longitude,  /* siderealTime */
      Θ,  /* rightAscension */α2
    )
    assertTrue { abs(m0 - 0.81965) <= 0.00001 }
    val transit = correctedTransit( /* approximateTransit */
      m0, longitude,  /* siderealTime */Θ,  /* rightAscension */
      α2,  /* previousRightAscension */α1,  /* nextRightAscension */
      α3
    ) / 24
    assertTrue { abs(transit - 0.81980) <= 0.00001 }
    val δ1 = 18.04761
    val δ2 = 18.44092
    val δ3 = 18.82742
    val rise = correctedHourAngle( /* approximateTransit */m0,  /* angle */
      -0.5667, Coordinates( /* latitude */42.3333, longitude),  /* afterTransit */
      false,  /* siderealTime */Θ,  /* rightAscension */
      α2,  /* previousRightAscension */α1,  /* nextRightAscension */
      α3,  /* declination */δ2,  /* previousDeclination */
      δ1,  /* nextDeclination */δ3
    ) / 24
    assertTrue { abs(rise - 0.51766) <= 0.00001 }
  }

  @Test
  fun testSolarTime() {
    /*
     * Comparison values generated from
     * http://aa.usno.navy.mil/rstt/onedaytable?form=1&ID=AA&year=2015&month=7&day=12&state=NC&place=raleigh
     */
    val coordinates = Coordinates(35 + 47.0 / 60.0, -78 - 39.0 / 60.0)
    val solar = SolarTime(DateComponents(2015, 7, 12), coordinates)
    val transit = solar.transit
    val sunrise = solar.sunrise
    val sunset = solar.sunset
    val twilightStart = solar.timeForSolarAngle(-6.0,  /* afterTransit */false)
    val twilightEnd = solar.timeForSolarAngle(-6.0,  /* afterTransit */true)
    val invalid = solar.timeForSolarAngle(-36.0,  /* afterTransit */true)

    assertEquals("9:38", timeString(twilightStart))
    assertEquals("10:08", timeString(sunrise))
    assertEquals("17:20", timeString(transit))
    assertEquals("24:32", timeString(sunset))
    assertEquals("25:02", timeString(twilightEnd))
    assertEquals("", timeString(invalid))
  }

  private fun timeString(whence: Double): String {
    val components = TimeComponents.fromDouble(whence) ?: return ""
    val minutes = (components.minutes + (components.seconds / 60.0).roundToInt())
    val paddedMinutes = if (minutes < 10) "0$minutes" else "$minutes"
    return "${components.hours}:$paddedMinutes"
  }

  @Test
  fun testCalendricalDate() {
    // generated from http://aa.usno.navy.mil/data/docs/RS_OneYear.php for KUKUIHAELE, HAWAII
    val coordinates = Coordinates( /* latitude */
      20 + 7.0 / 60.0,  /* longitude */-155.0 - 34.0 / 60.0
    )
    val day1solar = SolarTime(DateComponents(2015, 4,  /* day */2), coordinates)
    val day2solar = SolarTime(DateComponents(2015, 4, 3), coordinates)
    val day1 = day1solar.sunrise
    val day2 = day2solar.sunrise
    assertEquals("16:15", timeString(day1))
    assertEquals("16:14", timeString(day2))
  }

  @Test
  fun testInterpolation() {
    // values from Astronomical Algorithms page 25
    val interpolatedValue = interpolate( /* value */0.877366,  /* previousValue */
      0.884226,  /* nextValue */0.870531,  /* factor */4.35 / 24
    )
    assertTrue { abs(interpolatedValue - 0.876125) <= 0.000001 }
    val i1 = interpolate(1.0, -1.0, 3.0,  /* factor */0.6)
    assertTrue { abs(i1 - 2.2) <= 0.000001 }
  }

  @Test
  fun testAngleInterpolation() {
    val i1 = interpolateAngles(1.0, -1.0, 3.0,  /* factor */0.6)
    assertTrue { abs(i1 - 2.2) <= 0.000001 }
    val i2 = interpolateAngles(1.0, 359.0, 3.0,  /* factor */0.6)
    assertTrue { abs(i2 - 2.2) <= 0.000001 }
  }

  @Test
  fun testJulianDay() {
    /*
     * Comparison values generated from http://aa.usno.navy.mil/data/docs/JulianDate.php
     */
    assertTrue { abs(julianDay( /* year */2010,  /* month */1,  /* day */2) - 2455198.500000) <= 0.00001 }
    assertTrue { abs(julianDay( /* year */2011,  /* month */2,  /* day */4) - 2455596.500000) <= 0.00001 }
    assertTrue { abs(julianDay( /* year */2012,  /* month */3,  /* day */6) - 2455992.500000) <= 0.00001 }
    assertTrue { abs(julianDay( /* year */2013,  /* month */4,  /* day */8) - 2456390.500000) <= 0.00001 }
    assertTrue { abs(julianDay( /* year */2014,  /* month */5,  /* day */10) - 2456787.500000) <= 0.00001 }
    assertTrue { abs(julianDay( /* year */2015,  /* month */6,  /* day */12) - 2457185.500000) <= 0.00001 }
    assertTrue { abs(julianDay( /* year */2016,  /* month */7,  /* day */14) - 2457583.500000) <= 0.00001 }
    assertTrue { abs(julianDay( /* year */2017,  /* month */8,  /* day */16) - 2457981.500000) <= 0.00001 }
    assertTrue { abs(julianDay( /* year */2018,  /* month */9,  /* day */18) - 2458379.500000) <= 0.00001 }
    assertTrue { abs(julianDay( /* year */2019,  /* month */10,  /* day */20) - 2458776.500000) <= 0.00001 }
    assertTrue { abs(julianDay( /* year */2020,  /* month */11,  /* day */22) - 2459175.500000) <= 0.00001 }
    assertTrue { abs(julianDay( /* year */2021,  /* month */12,  /* day */24) - 2459572.500000) <= 0.00001 }

    val jdVal = 2457215.67708333
    assertTrue {
      abs(julianDay( /* year */2015,  /* month */7,  /* day */12,  /* hours */4.25) - jdVal) <= 0.000001
    }

    val components = TestUtils.makeDate(year = 2015, month = 7, day = 12, hour = 4, minute = 15)
    assertTrue { abs(julianDay(components) - jdVal) <= 0.000001 }
    assertTrue{ abs(julianDay( year = 2015,  month = 7,  day = 12, hours = 8.0) - 2457215.833333) <=  0.000001 }
    assertTrue{ abs(julianDay( year = 1992,  month = 10,  day = 13, hours = 0.0) - 2448908.5) <= 0.000001 }
  }


  @Test
  fun testJulianHours() {
    val j1 = julianDay( /* year */2010,  /* month */1,  /* day */3)
    val j2 = julianDay( /* year */2010,  /* month */
      1,  /* day */1, 48.0
    )
    assertTrue { abs(j1 - j2) <= 0.0000001 }
  }

  @Test
  fun testLeapYear() {
    assertFalse(isLeapYear(2015))
    assertTrue(isLeapYear(2016))
    assertTrue(isLeapYear(1600))
    assertTrue(isLeapYear(2000))
    assertTrue(isLeapYear(2400))
    assertFalse(isLeapYear(1700))
    assertFalse(isLeapYear(1800))
    assertFalse(isLeapYear(1900))
    assertFalse(isLeapYear(2100))
    assertFalse(isLeapYear(2200))
    assertFalse(isLeapYear(2300))
    assertFalse(isLeapYear(2500))
    assertFalse(isLeapYear(2600))
  }
}