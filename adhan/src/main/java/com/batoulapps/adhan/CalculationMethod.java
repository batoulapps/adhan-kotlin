package com.batoulapps.adhan;

/**
 * Standard calculation methods for calculating prayer times
 */
public enum CalculationMethod {
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
   * Uses a Fajr angle of 18.5 and an Isha interval of 90 minutes.
   * Note: You should add a +30 minute custom adjustment of Isha during Ramadan.
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
   * Shia Ithna Ashari, Leva Institute, Qum
   * Uses Fajr angle of 16, Maghrib angle of 4 and Isha angle of 14
   */
  JAFARI,

  /**
   * Shia, Institute of Geophysics, University of Tehran
   * Uses Fajr angle of 17.7, Maghrib angle of 4.5 and Isha angle of 14
   */
  TEHRAN,

  /**
   * Gulf region
   * Modified version of Umm al-Qura that uses a Fajr angle of 19.5.
   */
  GULF,

  /**
   * Union Organization Islamic de France
   * Uses a Fajr angle of 12 and an Isha angle of 12.
   */
  FRANCE,

  /**
   * Diyanet İşleri Başkanlığı, Turkey
   * Uses a Fajr angle of 18 and an Isha angle of 17.
   */
  TURKEY,

  /**
   * Spiritual Administration of Muslims of Russia
   * Uses a Fajr angle of 16 and an Isha angle of 15.
   */
  RUSSIA,

  /**
   * The default value for {@link CalculationParameters#method} when initializing a
   * {@link CalculationParameters} object. Sets a Fajr angle of 0 and an Isha angle of 0.
   */
  OTHER;

  /**
   * Return the CalculationParameters for the given method
   * @return CalculationParameters for the given Calculation method
   */
  public CalculationParameters getParameters() {
    switch (this) {
      case MUSLIM_WORLD_LEAGUE: {
        return new CalculationParameters(18.0, 17.0, this)
            .withMethodAdjustments(new PrayerAdjustments(0, 0, 1, 0, 0, 0));
      }
      case EGYPTIAN: {
        return new CalculationParameters(19.5, 17.5, this)
            .withMethodAdjustments(new PrayerAdjustments(0, 0, 1, 0, 0, 0));
      }
      case KARACHI: {
        return new CalculationParameters(18.0, 18.0, this)
            .withMethodAdjustments(new PrayerAdjustments(0, 0, 1, 0, 0, 0));
      }
      case UMM_AL_QURA: {
        return new CalculationParameters(18.5, 90, this);
      }
      case DUBAI: {
        return new CalculationParameters(18.2, 18.2, this)
            .withMethodAdjustments(new PrayerAdjustments(0, -3, 3, 3, 3, 0));
      }
      case MOON_SIGHTING_COMMITTEE: {
        return new CalculationParameters(18.0, 18.0, this)
            .withMethodAdjustments(new PrayerAdjustments(0, 0, 5, 0, 3, 0));
      }
      case NORTH_AMERICA: {
        return new CalculationParameters(15.0, 15.0, this)
            .withMethodAdjustments(new PrayerAdjustments(0, 0, 1, 0, 0, 0));
      }
      case KUWAIT: {
        return new CalculationParameters(18.0, 17.5, this);
      }
      case QATAR: {
        return new CalculationParameters(18.0, 90, this);
      }
      case SINGAPORE: {
        return new CalculationParameters(20.0, 18.0, this)
            .withMethodAdjustments(new PrayerAdjustments(0, 0, 1, 0, 0, 0));
      }
      case JAFARI: {
        return new CalculationParameters(16.0, 14.0, 4.0, this);
      }
      case TEHRAN: {
        return new CalculationParameters(17.7, 14.0, 4.5, this);
      }
      case GULF: {
        return new CalculationParameters(19.5, 90, this);
      }
      case FRANCE: {
        return new CalculationParameters(12.0, 12.0, this);
      }
      case TURKEY: {
        return new CalculationParameters(18.0, 17.0, this);
      }
      case RUSSIA: {
        return new CalculationParameters(16.0, 15.0, this);
      }
      case OTHER: {
        return new CalculationParameters(0.0, 0.0, this);
      }
      default: {
        throw new IllegalArgumentException("Invalid CalculationMethod");
      }
    }
  }
}
