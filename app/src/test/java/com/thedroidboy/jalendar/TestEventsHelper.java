package com.thedroidboy.jalendar;

import org.junit.Test;

import java.util.Calendar;
import java.util.HashMap;

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
//        EventInstanceForDay eventInstanceForDay = new EventInstanceForDay(1, "test", false, calendar.getTimeInMillis(),
//                calendar.getTimeInMillis() + TimeUnit.HOURS.toMillis(1), 0, "");
////        EventsHelper.computeEventHourRange(hourMap, start, end, eventInstanceForDay);
//        for (int i  =0 ; i < 24; i++){
//            if (hourMap.get(i) != null){
//                System.out.println("hour=" + i + " |count=" + hourMap.get(i));
//            }
//        }
    }
}
