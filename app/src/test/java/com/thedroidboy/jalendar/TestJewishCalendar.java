package com.thedroidboy.jalendar;

import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;

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
        calendar = new JewCalendar(-1);
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
        int currentDayOfWeek = calendar.getDayOfWeek();
        int moveToFirst = currentDayOfWeek - currentDayOfMonth + 1;
        while (moveToFirst < 1){
            moveToFirst += 7;
        }
        print("move:" + moveToFirst);
        print(hebrewDateFormatter.format(calendar));
        int head = moveToFirst - 1;
        print("head:" + Math.abs(head));

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
