package com.thedroidboy.jalendar;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import com.thedroidboy.jalendar.calendars.google.EventsHelper;
import com.thedroidboy.jalendar.calendars.google.GoogleManager;
import com.thedroidboy.jalendar.model.Day;

import java.util.List;

/**
 * Created by Yaakov Shahak on 12/30/2017.
 */

public class GoogleEventsLoader extends AsyncTaskLoader<List<Day>> {

    private final CalendarRepo calendarRepo;
    private final List<Day> dayList;
    private Cursor cursor;
    private final ForceLoadContentObserver observer;


    public GoogleEventsLoader(Context context, CalendarRepo calendarRepo, List<Day> dayList) {
        super(context);
        this.calendarRepo = calendarRepo;
        this.dayList = dayList;
        this.observer = new ForceLoadContentObserver();
    }

    @Override
    public List<Day> loadInBackground() {

        if (dayList != null && dayList.size() > 0) {
            long first = dayList.get(0).getStartDayInMillis();
            long last = dayList.get(dayList.size() - 1).getEndDayInMillis();
            this.cursor =  calendarRepo.getMonthEventsCursor(getContext(), first, last);
            // this is to force a reload when the content change
            this.cursor.registerContentObserver(this.observer);
            this.cursor.setNotificationUri(getContext().getContentResolver(),  GoogleManager.getInstanceUriForInterval(first, last));
            EventsHelper.bindCursorToDayList(dayList, cursor);
        }
        return dayList;
    }

    @Override
    protected void onStartLoading() {
        if (cursor == null || takeContentChanged()) {
            forceLoad();
        } else {
            deliverResult(dayList);
        }
    }
}
