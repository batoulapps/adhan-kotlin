package com.batoulapps.adhan;

import com.batoulapps.adhan.data.DateComponents;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Example {

  public static void main(String[] args) {
    final Coordinates coordinates = new Coordinates(-7.123123, 107.12341);
    final DateComponents dateComponents = DateComponents.from(new Date());
    final CalculationParameters parameters =
        CalculationMethod.MUSLIM_WORLD_LEAGUE.getParameters();

    SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");

    PrayerTimes prayerTimes = new PrayerTimes(coordinates, dateComponents, parameters);
    System.out.println("Next: " + formatter.format(prayerTimes.timeForNextPrayer()));
    System.out.println("Fajr: " + formatter.format(prayerTimes.fajr));
    System.out.println("Sunrise: " + formatter.format(prayerTimes.sunrise));
    System.out.println("Dhuhr: " + formatter.format(prayerTimes.dhuhr));
    System.out.println("Asr: " + formatter.format(prayerTimes.asr));
    System.out.println("Maghrib: " + formatter.format(prayerTimes.maghrib));
    System.out.println("Isha: " + formatter.format(prayerTimes.isha));
  }
}
