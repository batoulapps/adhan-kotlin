package com.batoulapps.adhan.internal;

import static com.google.common.truth.Truth.assertThat;

import com.batoulapps.adhan.data.CalendarUtil;
import com.batoulapps.adhan.data.TimeComponents;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeComponentsTest {

    @Test
    public void testTimeComponents() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        final TimeComponents comps1 = TimeComponents.fromDouble(15.199);
        assertThat(comps1).isNotNull();
        final Date date1 = comps1.dateComponents(TestUtils.getDateComponents("2023-04-03"));
        assertThat(comps1.hours).isEqualTo(15);
        assertThat(comps1.minutes).isEqualTo(11);
        assertThat(comps1.seconds).isEqualTo(56);
        assertThat(formatter.format(date1)).isEqualTo("2023-04-03 15:11:56.000");

        final TimeComponents comps2 = TimeComponents.fromDouble(1.0084);
        assertThat(comps2).isNotNull();
        final Date date2 = comps2.dateComponents(TestUtils.getDateComponents("2020-11-19"));
        assertThat(comps2.hours).isEqualTo(1);
        assertThat(comps2.minutes).isEqualTo(0);
        assertThat(comps2.seconds).isEqualTo(30);
        assertThat(formatter.format(date2)).isEqualTo("2020-11-19 01:00:30.000");


        final TimeComponents comps3 = TimeComponents.fromDouble(1.0083);
        assertThat(comps3).isNotNull();
        final Date date3 = comps3.dateComponents(TestUtils.getDateComponents("2023-07-08"));
        assertThat(comps3.hours).isEqualTo(1);
        assertThat(comps3.minutes).isEqualTo(0);
        assertThat(formatter.format(date3)).isEqualTo("2023-07-08 01:00:29.000");

        final TimeComponents comps4 = TimeComponents.fromDouble(2.1);
        assertThat(comps4).isNotNull();
        final Date date4 = comps4.dateComponents(TestUtils.getDateComponents("2023-01-02"));
        assertThat(comps4.hours).isEqualTo(2);
        assertThat(comps4.minutes).isEqualTo(6);
        assertThat(formatter.format(date4)).isEqualTo("2023-01-02 02:06:00.000");

        final TimeComponents comps5 = TimeComponents.fromDouble(3.5);
        assertThat(comps5).isNotNull();
        final Date date5 = comps5.dateComponents(TestUtils.getDateComponents("2019-06-23"));
        assertThat(comps5.hours).isEqualTo(3);
        assertThat(comps5.minutes).isEqualTo(30);
        assertThat(formatter.format(date5)).isEqualTo("2019-06-23 03:30:00.000");
    }

    @Test
    public void testMinuteRounding() {
        final Date comps1 = TestUtils.makeDate(2015, 1, 1, 10, 2, 29);
        final Date rounded1 = CalendarUtil.roundedMinute(comps1);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(rounded1);
        assertThat(calendar.get(Calendar.MINUTE)).isEqualTo(2);
        assertThat(calendar.get(Calendar.SECOND)).isEqualTo(0);

        final Date comps2 = TestUtils.makeDate(2015, 1, 1, 10, 2, 31);
        final Date rounded2 = CalendarUtil.roundedMinute(comps2);
        calendar.setTime(rounded2);
        assertThat(calendar.get(Calendar.MINUTE)).isEqualTo(3);
        assertThat(calendar.get(Calendar.SECOND)).isEqualTo(0);
    }
}
