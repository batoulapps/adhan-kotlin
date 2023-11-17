package com.batoulapps.adhan2

import com.batoulapps.adhan2.HighLatitudeRule.MIDDLE_OF_THE_NIGHT
import com.batoulapps.adhan2.HighLatitudeRule.SEVENTH_OF_THE_NIGHT
import com.batoulapps.adhan2.HighLatitudeRule.TWILIGHT_ANGLE
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

class CalculationParametersTest {

  @Test
  fun testNightPortion() {
    var parameters = CalculationParameters(fajrAngle = 18.0, ishaAngle = 18.0, highLatitudeRule = MIDDLE_OF_THE_NIGHT)
    val coordinates = Coordinates(latitude = 0.0, longitude = 0.0)
    assertTrue { abs(parameters.nightPortions(coordinates).fajr - 0.5) <= 0.001 }
    assertTrue { abs(parameters.nightPortions(coordinates).isha - 0.5) <= 0.001 }

    parameters = CalculationParameters(fajrAngle = 18.0, ishaAngle = 18.0, highLatitudeRule = SEVENTH_OF_THE_NIGHT)
    assertTrue { abs(parameters.nightPortions(coordinates).fajr - (1.0 / 7.0)) <= 0.001 }
    assertTrue { abs(parameters.nightPortions(coordinates).isha - (1.0 / 7.0)) <= 0.001 }

    parameters = CalculationParameters(fajrAngle = 10.0, ishaAngle = 15.0, highLatitudeRule = TWILIGHT_ANGLE)
    assertTrue { abs(parameters.nightPortions(coordinates).fajr - (10.0 / 60.0)) <= 0.001 }
    assertTrue { abs(parameters.nightPortions(coordinates).isha - (15.0 / 60.0)) <= 0.001 }
  }
}