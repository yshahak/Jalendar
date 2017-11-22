package com.thedroidboy.jalendar;

import android.util.Log;
import android.util.SparseArray;

/**
 * Created by Yaakov Shahak
 * on 21/11/2017.
 */

public class JewCalendarPool {

    private final static String TAG = "JewCalendarPool";

    private static SparseArray<JewCalendar> jewCalendarMap = new SparseArray<>();

    public static JewCalendar obtain(int position) {
        JewCalendar jewCalendar = jewCalendarMap.get(position);
        if (jewCalendar == null) {
            Log.d(TAG, "obtain: new jewcalendar for " + position);
            jewCalendar = new JewCalendar(position);
            jewCalendarMap.put(position, jewCalendar);
        }
        return jewCalendar;
    }

    public static void release(int position){
        jewCalendarMap.put(position, null);
    }
}
