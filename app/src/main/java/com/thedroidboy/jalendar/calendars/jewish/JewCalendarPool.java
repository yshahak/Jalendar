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
    private static Thread thread;
    private static Handler handler;

    public static void init(){
        thread = new Thread(){
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
            if (jewCalendarMap.get(position + 2) == null || jewCalendarMap.get(position + 3) == null || jewCalendarMap.get(position + 4) == null) {
                handler.post(() -> {
                    instantiateNewCalendar(position + 2);
                    instantiateNewCalendar(position + 3);
                    instantiateNewCalendar(position + 4);
                });
            }
            if (jewCalendarMap.get(position - 2) == null || jewCalendarMap.get(position - 3) == null || jewCalendarMap.get(position - 4) == null) {
                handler.post(() -> {
                    instantiateNewCalendar(position - 2);
                    instantiateNewCalendar(position - 3);
                    instantiateNewCalendar(position - 4);
                });
            }
        }

    }

    private static void instantiateNewCalendar(int position) {
        if (jewCalendarMap.get(position) != null){
            return;
        }
        Log.d(TAG, "instantiateNewCalendar new calendar for " + position);
        JewCalendar jewCalendar = new JewCalendar(position);
        jewCalendarMap.put(position, jewCalendar);
    }

    public static void release(int position){
        jewCalendarMap.put(position, null);
    }
}
