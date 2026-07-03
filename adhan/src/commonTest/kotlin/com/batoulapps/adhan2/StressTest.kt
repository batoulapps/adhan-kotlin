package com.batoulapps.adhan2

import com.batoulapps.adhan2.CalculationMethod.MUSLIM_WORLD_LEAGUE
import com.batoulapps.adhan2.data.CalendarUtil
import com.batoulapps.adhan2.data.DateComponents
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.datetime.DateTimeUnit

class StressTest {

  // Sweeps every 5° latitude × every 10° longitude × every day of 2025 and verifies two things:
  //
  // 1. Outside the polar circle (|lat| <= 66.55°): prayer times must always be computable.
  //    A failure here indicates the sun geometry is broken for a location where valid times
  //    should always exist.
  //
  // 2. Everywhere: when prayer times are returned, all six must be in strictly ascending order.
  //    Inside the polar circle, failure to compute is acceptable (the sun may not rise or set),
  //    but any times that are returned must still be ordered correctly.
  @Test
  fun testPrayerTimesOrderedAcrossGlobalCoordinatesAndAllDaysOf2025() {
    val params = MUSLIM_WORLD_LEAGUE.parameters
    val violations = mutableListOf<String>()

    for (lat in -90..90 step 5) {
      for (lon in -180..180 step 10) {
        val coordinates = try {
          Coordinates(lat.toDouble(), lon.toDouble())
        } catch (e: IllegalArgumentException) {
          continue
        }

        val inPolarCircle = Math.abs(lat) > POLAR_CIRCLE_LATITUDE

        var currentDateTime = CalendarUtil.resolveTime(DateComponents(2025, 1, 1))
        for (i in 0 until 365) {
          val date = DateComponents.fromLocalDateTime(currentDateTime)
          currentDateTime = CalendarUtil.add(currentDateTime, 1, DateTimeUnit.DAY)

          val p = try {
            PrayerTimes(coordinates, date, params)
          } catch (e: IllegalStateException) {
            if (!inPolarCircle) {
              val loc = "(${lat}, ${lon}) ${date.year}-${date.month}-${date.day}"
              violations.add("$loc: unexpected failure outside polar circle")
            }
            continue
          }

          val loc = "(${lat}, ${lon}) ${date.year}-${date.month}-${date.day}"
          if (p.fajr >= p.sunrise) violations.add("$loc: fajr >= sunrise")
          if (p.sunrise >= p.dhuhr) violations.add("$loc: sunrise >= dhuhr")
          if (p.dhuhr >= p.asr) violations.add("$loc: dhuhr >= asr")
          if (p.asr >= p.maghrib) violations.add("$loc: asr >= maghrib")
          if (p.maghrib >= p.isha) violations.add("$loc: maghrib >= isha")
        }
      }
    }

    assertTrue(violations.isEmpty(), "Violations found:\n${violations.joinToString("\n")}")
  }

  companion object {
    private const val POLAR_CIRCLE_LATITUDE = 66.55
  }
}
