package com.batoulapps.adhan;

import com.batoulapps.adhan.data.TimingFile;
import com.batoulapps.adhan.data.TimingInfo;
import com.batoulapps.adhan.data.TimingParameters;
import com.batoulapps.adhan.data.DateComponents;
import com.batoulapps.adhan.internal.TestUtils;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import okio.Okio;

import static com.google.common.truth.Truth.assertThat;

public class TimingTest {

  private static final String PATH = "../Shared/Times/";

  private JsonAdapter<TimingFile> jsonAdapter;

  @Before
  public void setup() {
    Moshi moshi = new Moshi.Builder().build();
    jsonAdapter = moshi.adapter(TimingFile.class);
  }

  @Test
  public void testTimes() throws Exception {
    File timingDirectory = new File(PATH);
    File[] files = timingDirectory.listFiles(new FileFilter() {
      public boolean accept(File pathname) {
        return pathname.getName().endsWith(".json");
      }
    });

    for (File timingFile : files) {
      testTimingFile(timingFile);
    }
  }

  private void testTimingFile(File jsonFile) throws Exception {
    System.out.println("testing timings for " + jsonFile.getName());
    TimingFile timingFile = jsonAdapter.fromJson(Okio.buffer(Okio.source(jsonFile)));
    assertThat(timingFile).isNotNull();

    Coordinates coordinates = new Coordinates(
        timingFile.params.latitude, timingFile.params.longitude);
    CalculationParameters parameters = parseParameters(timingFile.params);

    for (TimingInfo info : timingFile.times) {
      DateComponents dateComponents = TestUtils.getDateComponents(info.date);
      PrayerTimes prayerTimes = new PrayerTimes(coordinates, dateComponents, parameters);
      long fajrDifference = getDifferenceInMinutes(prayerTimes.fajr, info.fajr, timingFile.params.timezone);
      assertThat(fajrDifference).isAtMost(timingFile.variance);
      long sunriseDifference = getDifferenceInMinutes(prayerTimes.sunrise, info.sunrise, timingFile.params.timezone);
      assertThat(sunriseDifference).isAtMost(timingFile.variance);
      long dhuhrDifference = getDifferenceInMinutes(prayerTimes.dhuhr, info.dhuhr, timingFile.params.timezone);
      assertThat(dhuhrDifference).isAtMost(timingFile.variance);
      long asrDifference = getDifferenceInMinutes(prayerTimes.asr, info.asr, timingFile.params.timezone);
      assertThat(asrDifference).isAtMost(timingFile.variance);
      long maghribDifference = getDifferenceInMinutes(prayerTimes.maghrib, info.maghrib, timingFile.params.timezone);
      assertThat(maghribDifference).isAtMost(timingFile.variance);
      long ishaDifference = getDifferenceInMinutes(prayerTimes.isha, info.isha, timingFile.params.timezone);
      assertThat(ishaDifference).isAtMost(timingFile.variance);
    }
  }

  private long getDifferenceInMinutes(Date prayerTime, String jsonTime, String timezone) {
    SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
    formatter.setTimeZone(TimeZone.getTimeZone(timezone));

    Date referenceTime;
    try {
      final Calendar parsedCalendar = Calendar.getInstance(TimeZone.getTimeZone(timezone));
      parsedCalendar.setTime(formatter.parse(jsonTime));

      final Calendar referenceCalendar = Calendar.getInstance(TimeZone.getTimeZone(timezone));
      referenceCalendar.setTime(prayerTime);
      referenceCalendar.set(Calendar.HOUR, parsedCalendar.get(Calendar.HOUR));
      referenceCalendar.set(Calendar.MINUTE, parsedCalendar.get(Calendar.MINUTE));
      referenceCalendar.set(Calendar.AM_PM, parsedCalendar.get(Calendar.AM_PM));
      referenceTime = referenceCalendar.getTime();
    } catch (Exception e) {
      referenceTime = new Date();
    }

    return Math.abs((prayerTime.getTime() - referenceTime.getTime()) / (60 * 1000));
  }

  private CalculationParameters parseParameters(TimingParameters timingParameters) {
    final CalculationMethod method;
    switch (timingParameters.method) {
      case "MuslimWorldLeague": {
        method = CalculationMethod.MUSLIM_WORLD_LEAGUE;
        break;
      }
      case "Egyptian": {
        method = CalculationMethod.EGYPTIAN;
        break;
      }
      case "Karachi": {
        method = CalculationMethod.KARACHI;
        break;
      }
      case "UmmAlQura": {
        method = CalculationMethod.UMM_AL_QURA;
        break;
      }
      case "Dubai": {
        method = CalculationMethod.DUBAI;
        break;
      }
      case "MoonsightingCommittee": {
        method = CalculationMethod.MOON_SIGHTING_COMMITTEE;
        break;
      }
      case "NorthAmerica": {
        method = CalculationMethod.NORTH_AMERICA;
        break;
      }
      case "Kuwait": {
        method = CalculationMethod.KUWAIT;
        break;
      }
      case "Qatar": {
        method = CalculationMethod.QATAR;
        break;
      }
      case "Singapore": {
        method = CalculationMethod.SINGAPORE;
        break;
      }
      default: {
        method = CalculationMethod.OTHER;
      }
    }

    CalculationParameters parameters = method.getParameters();
    if ("Shafi".equals(timingParameters.madhab)) {
      parameters.madhab = Madhab.SHAFI;
    } else if ("Hanafi".equals(timingParameters.madhab)) {
      parameters.madhab = Madhab.HANAFI;
    }

    if ("SeventhOfTheNight".equals(timingParameters.highLatitudeRule)) {
      parameters.highLatitudeRule = HighLatitudeRule.SEVENTH_OF_THE_NIGHT;
    } else if ("TwilightAngle".equals(timingParameters.highLatitudeRule)) {
      parameters.highLatitudeRule = HighLatitudeRule.TWILIGHT_ANGLE;
    } else {
      parameters.highLatitudeRule = HighLatitudeRule.MIDDLE_OF_THE_NIGHT;
    }

    return parameters;
  }

}
