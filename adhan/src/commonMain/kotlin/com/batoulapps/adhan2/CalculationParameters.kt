package com.batoulapps.adhan2

/**
 * Parameters used for PrayerTime calculation customization
 *
 * Note that, for many cases, you can use {@link CalculationMethod#getParameters()} to get a
 * pre-computed set of calculation parameters depending on one of the available
 * {@link CalculationMethod}.
 */
data class CalculationParameters(
  // The angle of the sun used to calculate fajr
  val fajrAngle: Double = 0.0,

  // The angle of the sun used to calculate isha
  val ishaAngle: Double = 0.0,

  // Minutes after Maghrib (if set, the time for Isha will be Maghrib plus IshaInterval)
  val ishaInterval: Int = 0,

  // The method used to do the calculation
  val method: CalculationMethod = CalculationMethod.OTHER,

  // The madhab used to calculate Asr
  val madhab: Madhab = Madhab.SHAFI,

  // Rules for placing bounds on Fajr and Isha for high latitude areas
  val highLatitudeRule: HighLatitudeRule = HighLatitudeRule.MIDDLE_OF_THE_NIGHT,

  // Used to optionally add or subtract a set amount of time from each prayer time
  val prayerAdjustments: PrayerAdjustments = PrayerAdjustments(),

  // Used for method adjustments
  val methodAdjustments: PrayerAdjustments = PrayerAdjustments()
) {

  data class NightPortions constructor(val fajr: Double, val isha: Double)

  fun nightPortions(): NightPortions {
    return when (this.highLatitudeRule) {
      HighLatitudeRule.MIDDLE_OF_THE_NIGHT -> {
        NightPortions(1.0 / 2.0, 1.0 / 2.0)
      }
      HighLatitudeRule.SEVENTH_OF_THE_NIGHT -> {
        NightPortions(1.0 / 7.0, 1.0 / 7.0)
      }
      HighLatitudeRule.TWILIGHT_ANGLE -> {
        NightPortions(this.fajrAngle / 60.0, this.ishaAngle / 60.0)
      }
    }
  }
}
