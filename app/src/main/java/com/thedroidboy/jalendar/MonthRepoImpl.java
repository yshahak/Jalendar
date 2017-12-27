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
    public MutableLiveData<Month> getMonth(JewCalendar jewCalendar) {
        int monthCode = jewCalendar.monthHashCode();
        Log.d(TAG, "getMonth: " + monthCode);
        LiveData<Month> monthLiveData = monthDAO.getMonth(monthCode);
        MutableLiveData<Month> mutableLiveData = new MutableLiveData<>();
        Month month = monthLiveData.getValue();
        mutableLiveData.setValue(month);
        if (month != null){
            addDaysToMonth(month);
        }
        return mutableLiveData;
//        Log.d(TAG, "getMonth: didn't found one in db");
//        Month month = new Month(jewCalendar);
//        final MutableLiveData<Month> data = new MutableLiveData<>();
//        new Thread(() -> {
//            insertMonth(month);
//            insertMonthDays(month.getDayList());
//        }).start();
//        data.setValue(month);
//        return data;
    }

    @Override
    public void pullMonth(JewCalendar jewCalendar, MutableLiveData<Month> monthLiveData) {
        Log.d(TAG, "getMonth: didn't found one in db");
        Month month = new Month(jewCalendar);
        new Thread(() -> {
            insertMonth(month);
            insertMonthDays(month.getDayList());
        }).start();
        monthLiveData.setValue(month);
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
}
