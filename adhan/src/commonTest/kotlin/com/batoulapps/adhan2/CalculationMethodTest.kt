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
import com.batoulapps.adhan2.CalculationMethod.UMM_AL_QURA
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CalculationMethodTest {

  @Test
  fun testCalculationMethods() {
    var params = MUSLIM_WORLD_LEAGUE.parameters
    assertTrue { abs(params.fajrAngle - 18) <= 0.000001 }
    assertTrue { abs(params.ishaAngle - 17) <= 0.000001 }
    assertEquals(0, params.ishaInterval)
    assertEquals(MUSLIM_WORLD_LEAGUE, params.method)

    params = EGYPTIAN.parameters
    assertTrue { abs(params.fajrAngle - 19.5) <= 0.000001 }
    assertTrue { abs(params.ishaAngle - 17.5) <= 0.000001 }
    assertEquals(0, params.ishaInterval)
    assertEquals(EGYPTIAN, params.method)

    params = KARACHI.parameters
    assertTrue { abs(params.fajrAngle - 18) <= 0.000001 }
    assertTrue { abs(params.ishaAngle - 18) <= 0.000001 }
    assertEquals(0, params.ishaInterval)
    assertEquals(KARACHI, params.method)

    params = UMM_AL_QURA.parameters
    assertTrue { abs(params.fajrAngle - 18.5) <= 0.000001 }
    assertTrue { abs(params.ishaAngle - 0) <= 0.000001 }
    assertEquals(90, params.ishaInterval)
    assertEquals(UMM_AL_QURA, params.method)

    params = DUBAI.parameters
    assertTrue { abs(params.fajrAngle - 18.2) <= 0.000001 }
    assertTrue { abs(params.ishaAngle - 18.2) <= 0.000001 }
    assertEquals(0, params.ishaInterval)
    assertEquals(DUBAI, params.method)

    params = MOON_SIGHTING_COMMITTEE.parameters
    assertTrue { abs(params.fajrAngle - 18) <= 0.000001 }
    assertTrue { abs(params.ishaAngle - 18) <= 0.000001 }
    assertEquals(0, params.ishaInterval)
    assertEquals(MOON_SIGHTING_COMMITTEE, params.method)

    params = NORTH_AMERICA.parameters
    assertTrue { abs(params.fajrAngle - 15) <= 0.000001 }
    assertTrue { abs(params.ishaAngle - 15) <= 0.000001 }
    assertEquals(0, params.ishaInterval)
    assertEquals(NORTH_AMERICA, params.method)

    params = KUWAIT.parameters
    assertTrue { abs(params.fajrAngle - 18) <= 0.000001 }
    assertTrue { abs(params.ishaAngle - 17.5) <= 0.000001 }
    assertEquals(0, params.ishaInterval)
    assertEquals(KUWAIT, params.method)

    params = QATAR.parameters
    assertTrue { abs(params.fajrAngle - 18) <= 0.000001 }
    assertTrue { abs(params.ishaAngle - 0) <= 0.000001 }
    assertEquals(90, params.ishaInterval)
    assertEquals(QATAR, params.method)

    params = OTHER.parameters
    assertTrue { abs(params.fajrAngle - 0) <= 0.000001 }
    assertTrue { abs(params.ishaAngle - 0) <= 0.000001 }
    assertEquals(0, params.ishaInterval)
    assertEquals(OTHER, params.method)
  }
}