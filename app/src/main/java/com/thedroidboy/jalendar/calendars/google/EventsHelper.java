package com.thedroidboy.jalendar.calendars.google;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;
import com.thedroidboy.jalendar.model.Day;
import com.thedroidboy.jalendar.model.Hour;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.thedroidboy.jalendar.calendars.google.Contract.PROJECTION_BEGIN_INDEX;
import static com.thedroidboy.jalendar.calendars.google.Contract.PROJECTION_CALENDAR_DISPLAY_NAME_INDEX;
import static com.thedroidboy.jalendar.calendars.google.Contract.PROJECTION_CALENDAR_TIME_ZONE;
import static com.thedroidboy.jalendar.calendars.google.Contract.PROJECTION_DISPLAY_COLOR_INDEX;
import static com.thedroidboy.jalendar.calendars.google.Contract.PROJECTION_END_INDEX;
import static com.thedroidboy.jalendar.calendars.google.Contract.PROJECTION_EVENT_ID;
import static com.thedroidboy.jalendar.calendars.google.Contract.PROJECTION_TITLE_INDEX;


/**
 * Created by Yaakov Shahak
 * on 05/07/17.
 */

public class EventsHelper {

    public static void computeParallelEventsForDayList(List<Day> dayList){

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        for (Day day : dayList) {
            SparseArray<Hour> hoursMap = new SparseArray<>();
            hoursMap.put(-1, new Hour("all day", new ArrayList<>()));
            int i = 0;
            while (i < 24){
                hoursMap.put(i, new Hour(Integer.toString(i), new ArrayList<>()));
                i++;
            }
            for (EventInstance event : day.getGoogleEventInstances()) {
                computeEventHourRange(hoursMap, start, end, event);
            }
//            for (EventInstance event : day.getGoogleEventInstances()) {
//                computeParallelForEvent(hoursEventsMap, hoursMap, start, end, event);
//            }
            day.setHoursEventsMap(hoursMap);
        }
    }

    private static void computeEventHourRange(SparseArray<Hour> hoursMap, Calendar start, Calendar end, EventInstance event) {

        if (event.isAllDayEvent()){
            List<EventInstance> list = hoursMap.get(-1).getHourEvents();
            list.add(event);
            return;
        }
        int startHour, endHour, endMinutes;
        start.setTimeInMillis(event.getBegin());
        startHour = start.get(Calendar.HOUR_OF_DAY);
        end.setTimeInMillis(event.getEnd());
        endMinutes = end.get(Calendar.MINUTE);
        endHour = end.get(Calendar.HOUR_OF_DAY);
        if (endMinutes > 10 && endHour != 23){
            endHour++;
        }
        do {
            List<EventInstance> list = hoursMap.get(startHour).getHourEvents();
            list.add(event);
        } while (++startHour < endHour);
    }

    private static void computeParallelForEvent(SparseArray<Hour> hoursEventsMap, SparseIntArray hoursMap, Calendar start, Calendar end, EventInstance event){
        int startHour, endHour, endMinutes;
        start.setTimeInMillis(event.getBegin());
        startHour = start.get(Calendar.HOUR_OF_DAY);
        if (event.isAllDayEvent()){
            endHour = 23;
        } else {
            end.setTimeInMillis(event.getEnd());
            endMinutes = end.get(Calendar.MINUTE);
            endHour = end.get(Calendar.HOUR_OF_DAY);
            if (endMinutes > 10 && endHour != 23){
                endHour++;
            }
        }
        int max = 0, count;
        do {
            Hour hour = hoursEventsMap.get(startHour);
            if (hour == null) {
                hour = new Hour(Integer.toString(startHour), new ArrayList<>());
                hoursEventsMap.put(startHour, hour);
            }
            hour.getHourEvents().add(event);
            count = hoursMap.get(startHour);
            if (count > max){
                max = count;
            }
        } while (++startHour < endHour);
        event.setParallelEventsCount(max);
    }



    public static List<EventInstance> getEvents(Cursor cur) {
        List<EventInstance> list = new ArrayList<>();
        while (cur.moveToNext()) {
            EventInstance eventInstance = convertCursorToEvent(cur);
            list.add(eventInstance);
        }
        cur.close();
        return list;
    }

    public static HashMap<Integer, List<EventInstance>> getEventsMap(Cursor cur){
        if (cur == null) {
            return null;
        }
        @SuppressLint("UseSparseArrays")
        HashMap<Integer, List<EventInstance>> map = new HashMap<>();
        if (cur.moveToFirst()){
            do {
                EventInstance eventInstance = convertCursorToEvent(cur);
                int day = eventInstance.getDayOfMonth();
                List<EventInstance> list = map.get(day);
                if (list == null) {
                    list = new ArrayList<>();
                    map.put(day, list);
                }
                list.add(eventInstance);
            }while (cur.moveToNext());
        }
        return map;
    }

    public static void bindCursorToDayList(List<Day> days, Cursor cur){
        if (cur == null) {
            return;
        }
        long startMonth = days.get(0).getStartDayInMillis();
        if (cur.moveToFirst()){
            do {
                EventInstance eventInstance = convertCursorToEvent(cur);
                long startEvent = eventInstance.getBegin();
                long diff = startEvent - startMonth;
                int index = (int) (diff / TimeUnit.DAYS.toMillis(1));
                Day day = days.get((Math.abs(index)));
                day.getGoogleEventInstances().add(eventInstance);
            }while (cur.moveToNext());
        }
    }


    public static EventInstance convertCursorToEvent(Cursor cursor) {
        long eventId = cursor.getLong(PROJECTION_EVENT_ID);
        String title = cursor.getString(PROJECTION_TITLE_INDEX);
        long start = cursor.getLong((PROJECTION_BEGIN_INDEX));
        long end = cursor.getLong((PROJECTION_END_INDEX));
        TimeZone timeZone = TimeZone.getTimeZone(cursor.getString(PROJECTION_CALENDAR_TIME_ZONE));
        long offset = timeZone.getOffset(start);
        String calendarName = cursor.getString(PROJECTION_CALENDAR_DISPLAY_NAME_INDEX);
        int displayColor = cursor.getInt(PROJECTION_DISPLAY_COLOR_INDEX);
//        int calendarColor = cursor.getInt(PROJECTION_CALENDAR_COLOR_INDEX);
        boolean allDayEvent = (end - start) == TimeUnit.DAYS.toMillis(1);
        if (allDayEvent){
//            end = start;
        }
        int dayOfMonth = JewCalendar.getDayOfMonth(start);
        return new EventInstance(eventId, title, allDayEvent, start + offset, end + offset, displayColor, calendarName, dayOfMonth);
    }

    public static String covertDurationToRule(long duration) {
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        String rule = String.format(Locale.US,"PT%SH%SM"
                ,hours
                , TimeUnit.MILLISECONDS.toMinutes(duration - TimeUnit.HOURS.toMillis(hours)));
//        return "PT1H0M";
        return rule;
    }
}
