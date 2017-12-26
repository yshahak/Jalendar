package com.thedroidboy.jalendar;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;
import com.thedroidboy.jalendar.model.Day;
import com.thedroidboy.jalendar.model.DayDAO;
import com.thedroidboy.jalendar.model.Month;
import com.thedroidboy.jalendar.model.MonthDAO;

import java.util.List;

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
        LiveData<Month> monthLiveData = monthDAO.getMonth(jewCalendar.monthHashCode());
        if (monthLiveData.getValue() != null){
            addDaysToMonth(monthLiveData.getValue());
            return monthLiveData;
        }
        Month month = new Month(jewCalendar);
        Log.d(TAG, "getMonth: " + month.getMonthHebLabel());
        final MutableLiveData<Month> data = new MutableLiveData<>();
        new Thread(() -> {
            insertMonth(month);
            insertMonthDays(month.getDayList());
        }).start();
        data.setValue(month);
        return data;
    }

    private void addDaysToMonth(Month month) {
        long start = month.getStartMonthInMsIncludeOffset();
        long end = month.getEndMonthInMsIncludeOffset();
        List<Day> monthDays = dayDAO.getDaysInSegmant(start, end);
        month.setDayList(monthDays);
    }
}
