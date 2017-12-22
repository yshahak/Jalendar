package com.thedroidboy.jalendar;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;
import com.thedroidboy.jalendar.model.Month;
import com.thedroidboy.jalendar.model.MonthDAO;

import java.util.List;

/**
 * Created by $Yaakov Shahak on 12/7/2017.
 */

public class MonthRepoImpl implements MonthRepo {

    public static final String TAG = MonthRepoImpl.class.getSimpleName();
    private final MonthDAO monthDAO;

    public MonthRepoImpl(MonthDAO monthDAO) {
        this.monthDAO = monthDAO;
    }

    @Override
    public void insertMonth(Month month) {
        monthDAO.insertMonth(month);
    }

    @Override
    public LiveData<List<Month>> getMonthes(int monthHashCode, int sum) {
        return monthDAO.getMonthSegment(monthHashCode, sum);
    }

    @Override
    public LiveData<Month> getMonth(JewCalendar jewCalendar) {
        LiveData<Month> monthLiveData = monthDAO.getMonth(jewCalendar.monthHashCode());
        if (monthLiveData.getValue() != null){
            return monthLiveData;
        }
        Month month = new Month(jewCalendar);
        Log.d(TAG, "getMonth: " + month.getMonthHebLabel());
        new Thread(() -> insertMonth(month)).start();
        final MutableLiveData<Month> data = new MutableLiveData<>();
        data.setValue(month);
        return data;
    }
}
