package com.thedroidboy.jalendar;

import android.arch.paging.DataSource;
import android.arch.paging.TiledDataSource;
import android.util.Log;

import com.thedroidboy.jalendar.adapters.PagerAdapterMonth;
import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;
import com.thedroidboy.jalendar.model.Month;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yaakov Shahak
 * on 05/11/2017.
 */

public class CalendarDataSource extends TiledDataSource<Month> {

    private static final String TAG = CalendarDataSource.class.getSimpleName();

    @Override
    public int countItems() {
        return DataSource.COUNT_UNDEFINED;
    }

    @Override
    public List<Month> loadRange(int startPosition, int count) {
        Log.d(TAG, "positions;" + startPosition + "|" + count);
        List<Month> months = new ArrayList<>();
        for (int i = 0 ; i < count ; i++){
            JewCalendar jewCalendar = new JewCalendar(startPosition - PagerAdapterMonth.INITIAL_OFFSET);
            Log.d(TAG, jewCalendar.getHebMonthName());
            Month month = new Month(jewCalendar);
            months.add(month);
            startPosition++;
        }
        return months;
    }
}
