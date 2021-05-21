package com.batoulapps.adhan.data;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateComponents {
  public final int year;
  public final int month;
  public final int day;

  /**
   * Convenience method that returns a DateComponents from a given Date
   * @param date the date
   * @return the DateComponents (according to the default device timezone)
   */
  public static DateComponents from(Date date) {
    Calendar calendar = GregorianCalendar.getInstance();
    calendar.setTime(date);
    return new DateComponents(calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
  }

  public static DateComponents from(LocalDate date){
    return new DateComponents(date.getYear(),date.getMonthValue(),date.getDayOfMonth());
  }

  public DateComponents(int year, int month, int day) {
    this.year = year;
    this.month = month;
    this.day = day;
  }

  /**
   * Convenience method that returns a DateComponents from a given
   * Date that was constructed from UTC based components
   * @param date the date
   * @return the DateComponents (according to UTC)
   */
  public static DateComponents fromUTC(Date date) {
    Calendar calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
    calendar.setTime(date);
    return new DateComponents(calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
  }
}
