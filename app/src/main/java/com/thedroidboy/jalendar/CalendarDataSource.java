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
        JewCalendar jewCalendar = new JewCalendar();
        for (int i = 0 ; i < count ; i++){
            jewCalendar.shiftMonth(startPosition - MonthVM.INITIAL_OFFSET);

//            jewCalendar.setMonthDays();

            Log.d(TAG, jewCalendar.getMonthName());
            MonthVM monthVM = new MonthVM();
            monthVM.setMonthHebName(jewCalendar.getMonthName());
            monthVM.setDaysInMonth(jewCalendar.getDaysInJewishMonth());
            jewCalendar.setOffsets();
            monthVM.setHeadOffset(jewCalendar.getHeadOffset());
            monthVM.setTrailOffset(jewCalendar.getTrailOffset());
//            monthVM.setDayList(jewCalendar.getMonthDays());
            monthVMS.add(monthVM);
            startPosition++;
        }
        return monthVMS;
    }
}
