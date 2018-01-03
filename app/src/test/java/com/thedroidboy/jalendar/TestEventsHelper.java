package com.thedroidboy.jalendar;

import com.thedroidboy.jalendar.calendars.google.EventInstance;

import org.junit.Test;

import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Yaakov Shahak
 * on 03/01/2018.
 */

public class TestEventsHelper {

    @Test
    public void testComputeParallelEvents(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 1);
        HashMap<Integer, Integer> hourMap = new HashMap<>();
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        EventInstance eventInstance = new EventInstance(1, "test", false, calendar.getTimeInMillis(),
                calendar.getTimeInMillis() + TimeUnit.HOURS.toMillis(1), 0, "");
//        EventsHelper.computeEventHourRange(hourMap, start, end, eventInstance);
        for (int i  =0 ; i < 24; i++){
            if (hourMap.get(i) != null){
                System.out.println("hour=" + i + " |count=" + hourMap.get(i));
            }
        }
    }
}
