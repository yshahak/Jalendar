package com.thedroidboy.jalendar;

import net.sourceforge.zmanim.hebrewcalendar.HebrewDateFormatter;

import org.junit.Test;

import java.util.Date;

/**
 * Created by Yaakov Shahak
 * on 19/11/2017.
 */

public class TestJewishCalendar {

    private HebrewDateFormatter hebrewDateFormatter = new HebrewDateFormatter();
    {
        hebrewDateFormatter.setHebrewFormat(true);
    }

    @Test
    public void testShiftMonth() {
        JewCalendar calendar = new JewCalendar(new Date());
        int offset = -19;
        print(hebrewDateFormatter.format(calendar));
        print(hebrewDateFormatter.format(new JewCalendar(offset)));
    }

    private void print(String content) {
        System.out.println(content);
    }
}
