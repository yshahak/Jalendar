package com.thedroidboy.jalendar;

import android.arch.lifecycle.LiveData;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import com.thedroidboy.jalendar.calendars.google.GoogleManager;
import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;
import com.thedroidboy.jalendar.model.Day;
import com.thedroidboy.jalendar.model.DayDAO;
import com.thedroidboy.jalendar.model.Month;
import com.thedroidboy.jalendar.model.MonthDAO;

import java.util.List;

import static com.thedroidboy.jalendar.calendars.google.Contract.INSTANCE_PROJECTION;

/**
 * Created by $Yaakov Shahak on 12/7/2017.
 */

public class MonthRepoImpl implements MonthRepo {

    public static final String TAG = MonthRepoImpl.class.getSimpleName();
    private final MonthDAO monthDAO;
    private final DayDAO dayDAO;

    public MonthRepoImpl(MonthDAO monthDAO, DayDAO dayDAO) {
        this.monthDAO = monthDAO;
        this.dayDAO = dayDAO;
    }

    @Override
    public void insertMonth(Month month) {
        monthDAO.insertMonth(month);
    }

    @Override
    public void insertMonthDays(List<Day> monthDays) {
        dayDAO.insertMonthDays(monthDays);
    }

    @Override
    public LiveData<List<Month>> getMonthes(int monthHashCode, int sum) {
        return monthDAO.getMonthSegment(monthHashCode, sum);
    }

    @Override
    public LiveData<Month> getMonth(JewCalendar jewCalendar) {
        int monthCode = jewCalendar.monthHashCode();
        LiveData<Month> monthLiveData = monthDAO.getMonth(monthCode);
        Month month = monthLiveData.getValue();
        if (month != null){
            addDaysToMonth(month);
        }
        return monthLiveData;
    }

    @Override
    public void pullMonth(JewCalendar jewCalendar, LiveData<Month> monthLiveData) {
        Log.d(TAG, "getMonth: didn't found one in db");
        Month month = new Month(jewCalendar);
        new Thread(() -> {
            insertMonth(month);
            insertMonthDays(month.getDayList());
        }).start();
//        monthLiveData.setValue(month);
    }

    private void addDaysToMonth(Month month) {
        long start = month.getStartMonthInMsIncludeOffset();
        long end = month.getEndMonthInMsIncludeOffset();
        List<Day> monthDays = dayDAO.getDaysInSegmant(start, end);
        for (Day monthDay : monthDays) {
            boolean outOfMonthRange = monthDay.getStartDayInMillis() < month.getStartMonthInMs() || monthDay.getEndDayInMillis() > month.getEndMonthInMs();
            monthDay.setOutOfMonthRange(outOfMonthRange);
        }
        month.setDayList(monthDays);
    }

    @Override
    public Cursor getMonthEventsCursor(Context context, long start, long end) {
        Uri uri = GoogleManager.getInstanceUriForInterval(start, end);
        String WHERE_CALENDARS_SELECTED = CalendarContract.Calendars.VISIBLE + " = ? "; //AND " +
        String[] WHERE_CALENDARS_ARGS = {"1"};//
        ContentResolver cr = context.getContentResolver();
        return cr.query(uri, INSTANCE_PROJECTION, WHERE_CALENDARS_SELECTED, WHERE_CALENDARS_ARGS,
                CalendarContract.Events.DTSTART + " ASC");
    }
}
