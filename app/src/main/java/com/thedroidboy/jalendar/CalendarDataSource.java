package com.thedroidboy.jalendar;

import android.arch.paging.DataSource;
import android.arch.paging.TiledDataSource;
import android.util.Log;

import com.thedroidboy.jalendar.model.MonthVM;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yaakov Shahak
 * on 05/11/2017.
 */

public class CalendarDataSource extends TiledDataSource<MonthVM> {

    private static final String TAG = CalendarDataSource.class.getSimpleName();

    @Override
    public int countItems() {
        return DataSource.COUNT_UNDEFINED;
    }

    @Override
    public List<MonthVM> loadRange(int startPosition, int count) {
        Log.d(TAG, "positions;" + startPosition + "|" + count);
        List<MonthVM> monthVMS = new ArrayList<>();
        for (int i = 0 ; i < count ; i++){
            JewCalendar jewCalendar = new JewCalendar(startPosition - MonthVM.INITIAL_OFFSET);
            Log.d(TAG, jewCalendar.getMonthName());
            MonthVM monthVM = new MonthVM(jewCalendar);
            monthVMS.add(monthVM);
            startPosition++;
        }
        return monthVMS;
    }
}
