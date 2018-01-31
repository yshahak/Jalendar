package com.thedroidboy.jalendar;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.database.Cursor;

import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;
import com.thedroidboy.jalendar.model.Day;
import com.thedroidboy.jalendar.model.Month;

import java.util.List;

/**
 * Created by $Yaakov Shahak on 12/7/2017.
 */

public interface CalendarRepo {

    void insertMonth(Month month);

    void pullMonth(JewCalendar jewCalendar);

    void insertMonthDays(List<Day> monthDays);

    List<Month> getMonthes(int monthHashCode, int sum);

    LiveData<Month> getMonthByCalendar(JewCalendar jewCalendar);

    LiveData<Month> getMonthByPosition(int position);

    LiveData<Day> getDayByPosition(int position);

    Cursor getMonthEventsCursor(Context context, long start, long end);

    int getPositionForDay(Day day);

}
