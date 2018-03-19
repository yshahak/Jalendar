package com.thedroidboy.jalendar.calendars.jewish;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;

/**
 * Created by Yaakov Shahak
 * on 21/11/2017.
 */

public class JewCalendarPool {

    private final static String TAG = "JewCalendarPool";

    private final static SparseArray<JewCalendar> jewCalendarMap = new SparseArray<>();
    private static Handler handler;

    public static void init() {
        Thread thread = new Thread() {
            public void run() {
                Looper.prepare();
                handler = new Handler();
                Looper.loop();
            }
        };
        thread.start();
    }

    public static JewCalendar obtain(int position) {
        JewCalendar jewCalendar = jewCalendarMap.get(position);
        if (jewCalendar == null) {
            instantiateNewCalendar(position);
            jewCalendar = jewCalendarMap.get(position);
        }
        prepareAsyncNextItems(position);
        return jewCalendar;
    }

    private static void prepareAsyncNextItems(int position) {
        synchronized (jewCalendarMap) {
            handler.post(() -> {
                instantiateNewCalendar(position + 1);
                instantiateNewCalendar(position + 2);
                instantiateNewCalendar(position + 3);
                instantiateNewCalendar(position + 4);
                instantiateNewCalendar(position + 5);
                instantiateNewCalendar(position + 6);
                instantiateNewCalendar(position + 7);
                instantiateNewCalendar(position + 8);
                instantiateNewCalendar(position + 9);
                instantiateNewCalendar(position + 10);
                instantiateNewCalendar(position - 1);
                instantiateNewCalendar(position - 2);
                instantiateNewCalendar(position - 3);
                instantiateNewCalendar(position - 4);
                instantiateNewCalendar(position - 5);
                instantiateNewCalendar(position - 6);
                instantiateNewCalendar(position - 7);
                instantiateNewCalendar(position - 8);
                instantiateNewCalendar(position - 9);
                instantiateNewCalendar(position - 10);
            });
        }

    }

    private static void instantiateNewCalendar(int position) {
        if (jewCalendarMap.get(position) != null) {
            return;
        }
        JewCalendar jewCalendar = new JewCalendar(position);
        Log.d(TAG, "instantiate calendar for " + position + "\t" + jewCalendar.toString());
        jewCalendarMap.put(position, jewCalendar);
    }

    public static void release(int position) {
        jewCalendarMap.put(position, null);
    }
}
