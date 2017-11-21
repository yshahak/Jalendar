package com.thedroidboy.jalendar;

import android.util.SparseArray;

/**
 * Created by Yaakov Shahak
 * on 21/11/2017.
 */

public class JewCalendarPool {

    private static SparseArray<JewCalendar> jewCalendarMap = new SparseArray<>();

    public static JewCalendar obtain(int position) {
        JewCalendar jewCalendar = jewCalendarMap.get(position);
        if (jewCalendar == null) {
            jewCalendar = new JewCalendar(position);
            jewCalendarMap.put(position, jewCalendar);
        }
        return jewCalendar;
    }

    public static void release(int position){
        jewCalendarMap.put(position, null);
    }
}
