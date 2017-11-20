package com.thedroidboy.jalendar;

import net.sourceforge.zmanim.hebrewcalendar.HebrewDateFormatter;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by Yaakov Shahak
 * on 19/11/2017.
 */

public class TestJewishCalendar {

    private JewCalendar calendar;

    @Before
    public void init() {
        calendar = new JewCalendar(4);
    }

    private HebrewDateFormatter hebrewDateFormatter = new HebrewDateFormatter();

    {
        hebrewDateFormatter.setHebrewFormat(true);
    }

    @Test
    public void testShiftMonth() {
        int offset = -19;
        print(hebrewDateFormatter.format(calendar));
        print(hebrewDateFormatter.format(new JewCalendar(offset)));
    }

    @Test
    public void testGetMonthHead() {
        int currentDayOfMonth = calendar.getJewishDayOfMonth();
        int remain = currentDayOfMonth % 7;
        int currentDayOfWeek = calendar.getDayOfWeek();

        print(hebrewDateFormatter.format(calendar));
        print("head:" + Math.abs(remain - currentDayOfWeek));

    }

    @Test
    public void testGetMonthTrail() {
        int totalDays = calendar.getDaysInJewishMonth();
        int currentDayOfMonth = calendar.getJewishDayOfMonth();
        int currentDayOfWeek = calendar.getDayOfWeek();
        int remain = (totalDays - currentDayOfMonth + currentDayOfWeek) % 7;
        print(hebrewDateFormatter.format(calendar));
        print("trail:" + (7- remain));

    }

    private void print(String content) {
        System.out.println(content);
    }
}
