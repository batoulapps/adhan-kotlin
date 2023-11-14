package com.batoulapps.adhan2.internal

import com.batoulapps.adhan2.internal.Astronomical.apparentObliquityOfTheEcliptic
import com.batoulapps.adhan2.internal.Astronomical.apparentSolarLongitude
import com.batoulapps.adhan2.internal.Astronomical.ascendingLunarNodeLongitude
import com.batoulapps.adhan2.internal.Astronomical.meanLunarLongitude
import com.batoulapps.adhan2.internal.Astronomical.meanObliquityOfTheEcliptic
import com.batoulapps.adhan2.internal.Astronomical.meanSiderealTime
import com.batoulapps.adhan2.internal.Astronomical.meanSolarLongitude
import com.batoulapps.adhan2.internal.Astronomical.nutationInLongitude
import com.batoulapps.adhan2.internal.Astronomical.nutationInObliquity
import com.batoulapps.adhan2.internal.DoubleUtil.unwindAngle
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

internal class SolarCoordinates(julianDay: Double) {
  /**
   * The declination of the sun, the angle between
   * the rays of the Sun and the plane of the Earth's
   * equator, in degrees.
   */
  val declination: Double

  /**
   * Right ascension of the Sun, the angular distance on the
   * celestial equator from the vernal equinox to the hour circle,
   * in degrees.
   */
  val rightAscension: Double

  /**
   * Apparent sidereal time, the hour angle of the vernal
   * equinox, in degrees.
   */
  val apparentSiderealTime: Double

  init {
    val T: Double = CalendricalHelper.julianCentury(julianDay)
    val L0 = meanSolarLongitude( /* julianCentury */T)
    val Lp = meanLunarLongitude( /* julianCentury */T)
    val Ω = ascendingLunarNodeLongitude( /* julianCentury */T)
    val λ: Double = apparentSolarLongitude( /* julianCentury*/T,  /* meanLongitude */L0).toRadians()
    val θ0 = meanSiderealTime( /* julianCentury */T)
    val ΔΨ =
      nutationInLongitude( /* julianCentury */T,  /* solarLongitude */L0,  /* lunarLongitude */
        Lp,  /* ascendingNode */Ω
      )
    val Δε =
      nutationInObliquity( /* julianCentury */T,  /* solarLongitude */L0,  /* lunarLongitude */
        Lp,  /* ascendingNode */Ω
      )
    val ε0 = meanObliquityOfTheEcliptic( /* julianCentury */T)
    val εapp: Double =
      apparentObliquityOfTheEcliptic( /* julianCentury */
        T,  /* meanObliquityOfTheEcliptic */ε0
      ).toRadians()

    /* Equation from Astronomical Algorithms page 165 */
    declination =
      asin(sin(εapp) * sin(λ)).toDegrees()

    /* Equation from Astronomical Algorithms page 165 */
    rightAscension = unwindAngle(
      atan2(cos(εapp) * sin(λ), cos(λ)).toDegrees()
    )

    /* Equation from Astronomical Algorithms page 88 */
    apparentSiderealTime =
      θ0 + ΔΨ * 3600 * cos((ε0 + Δε).toRadians()) / 3600
  }
}