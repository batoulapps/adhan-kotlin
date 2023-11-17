package com.batoulapps.adhan2

import com.batoulapps.adhan2.model.Rounding

/**
 * Standard calculation methods for calculating prayer times
 */
enum class CalculationMethod {
  /**
   * Muslim World League
   * Uses Fajr angle of 18 and an Isha angle of 17
   */
  MUSLIM_WORLD_LEAGUE,

  /**
   * Egyptian General Authority of Survey
   * Uses Fajr angle of 19.5 and an Isha angle of 17.5
   */
  EGYPTIAN,

  /**
   * University of Islamic Sciences, Karachi
   * Uses Fajr angle of 18 and an Isha angle of 18
   */
  KARACHI,

  /**
   * Umm al-Qura University, Makkah
   * Uses a Fajr angle of 18.5 and an Isha angle of 90. Note: You should add a +30 minute custom
   * adjustment of Isha during Ramadan.
   */
  UMM_AL_QURA,

  /**
   * The Gulf Region
   * Uses Fajr and Isha angles of 18.2 degrees.
   */
  DUBAI,

  /**
   * Moonsighting Committee
   * Uses a Fajr angle of 18 and an Isha angle of 18. Also uses seasonal adjustment values.
   */
  MOON_SIGHTING_COMMITTEE,

  /**
   * Referred to as the ISNA method
   * This method is included for completeness, but is not recommended.
   * Uses a Fajr angle of 15 and an Isha angle of 15.
   */
  NORTH_AMERICA,

  /**
   * Kuwait
   * Uses a Fajr angle of 18 and an Isha angle of 17.5
   */
  KUWAIT,

  /**
   * Qatar
   * Modified version of Umm al-Qura that uses a Fajr angle of 18.
   */
  QATAR,

  /**
   * Singapore
   * Uses a Fajr angle of 20 and an Isha angle of 18
   */
  SINGAPORE,

  /**
   * Diyanet İşleri Başkanlığı, Turkey
   * Uses a Fajr angle of 18 and an Isha angle of 17
   */
  TURKEY,

  /**
   * The default value for [CalculationParameters.method] when initializing a
   * [CalculationParameters] object. Sets a Fajr angle of 0 and an Isha angle of 0.
   */
  OTHER;

  /**
   * Return the CalculationParameters for the given method
   * @return CalculationParameters for the given Calculation method
   */
  val parameters: CalculationParameters
    get() = when (this) {
      MUSLIM_WORLD_LEAGUE -> {
        CalculationParameters(fajrAngle = 18.0, ishaAngle = 17.0, method = this,
          methodAdjustments = PrayerAdjustments(dhuhr = 1)
        )
      }
      EGYPTIAN -> {
        CalculationParameters(fajrAngle = 19.5, ishaAngle = 17.5, method = this,
          methodAdjustments = PrayerAdjustments(dhuhr = 1)
        )
      }
      KARACHI -> {
        CalculationParameters(fajrAngle = 18.0, ishaAngle = 18.0, method = this,
          methodAdjustments = PrayerAdjustments(dhuhr = 1)
        )
      }
      UMM_AL_QURA -> {
        CalculationParameters(fajrAngle = 18.5, ishaInterval = 90, method = this)
      }
      DUBAI -> {
        CalculationParameters(fajrAngle = 18.2, ishaAngle = 18.2, method = this,
          methodAdjustments = PrayerAdjustments(sunrise = -3, dhuhr = 3, asr = 3, maghrib = 3)
        )
      }
      MOON_SIGHTING_COMMITTEE -> {
        CalculationParameters(fajrAngle = 18.0, ishaAngle = 18.0, method = this,
          methodAdjustments = PrayerAdjustments(dhuhr = 5, maghrib = 3)
        )
      }
      NORTH_AMERICA -> {
        CalculationParameters(fajrAngle = 15.0, ishaAngle = 15.0, method = this,
          methodAdjustments = PrayerAdjustments(dhuhr = 1)
        )
      }
      KUWAIT -> {
        CalculationParameters(fajrAngle = 18.0, ishaAngle = 17.5, method = this)
      }
      QATAR -> {
        CalculationParameters(fajrAngle = 18.0, ishaInterval = 90, method = this)
      }
      SINGAPORE -> {
        CalculationParameters(fajrAngle = 20.0, ishaAngle = 18.0, method = this,
          methodAdjustments = PrayerAdjustments(dhuhr = 1),
          rounding = Rounding.UP
        )
      }
      TURKEY -> {
        CalculationParameters(fajrAngle = 18.0, ishaAngle = 17.0, method = this,
          methodAdjustments = PrayerAdjustments(sunrise = -7, dhuhr = 5, asr = 4, maghrib = 7)
        )
      }
      OTHER -> {
        CalculationParameters(fajrAngle = 0.0, ishaAngle = 0.0, method = this)
      }
    }
}