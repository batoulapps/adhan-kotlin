package com.batoulapps.adhan;

import com.batoulapps.adhan.data.CalendarUtil;
import com.batoulapps.adhan.data.DateComponents;

import java.util.Calendar;
import java.util.Date;

public class SunnahTimes {
    /* The midpoint between Maghrib and Fajr */
    public final Date middleOfTheNight;

    /* The beginning of the last third of the period between Maghrib and Fajr,
     a recommended time to perform Qiyam */
    public final Date lastThirdOfTheNight;

    public SunnahTimes(PrayerTimes prayerTimes) {
        final Date currentPrayerTimesDate = CalendarUtil.resolveTime(prayerTimes.dateComponents);
        final Date tomorrowPrayerTimesDate = CalendarUtil.add(currentPrayerTimesDate, 1, Calendar.DATE);
        final PrayerTimes tomorrowPrayerTimes =
                new PrayerTimes(prayerTimes.coordinates,
                        DateComponents.fromUTC(tomorrowPrayerTimesDate),
                        prayerTimes.calculationParameters);

        final int nightDurationInSeconds =
                (int) ((tomorrowPrayerTimes.fajr.getTime() - prayerTimes.maghrib.getTime()) / 1000);
        middleOfTheNight = CalendarUtil.roundedMinute(
                CalendarUtil.add(prayerTimes.maghrib, (int) (nightDurationInSeconds / 2.0), Calendar.SECOND));
        lastThirdOfTheNight =
                CalendarUtil.roundedMinute(
                        CalendarUtil.add(prayerTimes.maghrib,
                                (int) (nightDurationInSeconds * (2.0 / 3.0)),
                                Calendar.SECOND));
    }
}
