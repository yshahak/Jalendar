package com.thedroidboy.jalendar;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import com.thedroidboy.jalendar.calendars.google.EventsHelper;
import com.thedroidboy.jalendar.model.Day;

import java.util.List;

/**
 * Created by Yaakov Shahak on 12/30/2017.
 */

public class GoogleEventsLoader extends AsyncTaskLoader<List<Day>> {

    private final MonthRepo monthRepo;
    private final List<Day> dayList;
    private Cursor cursor;

    public GoogleEventsLoader(Context context, MonthRepo monthRepo, List<Day> dayList) {
        super(context);
        this.monthRepo = monthRepo;
        this.dayList = dayList;
    }

    @Override
    public List<Day> loadInBackground() {
        if (dayList != null && dayList.size() > 0) {
            long first = dayList.get(0).getStartDayInMillis();
            long last = dayList.get(dayList.size() - 1).getEndDayInMillis();
            this.cursor =  monthRepo.getMonthEventsCursor(getContext(), first, last);
            EventsHelper.bindCursorToDayList(dayList, cursor);
            EventsHelper.computeParallelEventsForDayList(dayList);
        }
        return dayList;
    }

    @Override
    protected void onStartLoading() {
        if (cursor == null) {
            forceLoad();
        } else {
            deliverResult(dayList);
        }
    }
}
