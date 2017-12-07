package com.thedroidboy.jalendar;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;
import com.thedroidboy.jalendar.model.Month;
import com.thedroidboy.jalendar.model.MonthDAO;

import java.util.List;

/**
 * Created by $Yaakov Shahak on 12/7/2017.
 */

class MonthRepoImpl implements MonthRepo {

    private final MonthDAO monthDAO;
    private final JewCalendar jewCalendar;

    MonthRepoImpl(MonthDAO monthDAO, JewCalendar jewCalendar) {
        this.monthDAO = monthDAO;
        this.jewCalendar = jewCalendar;
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
    public LiveData<Month> getMonth(int monthHashCode) {
        LiveData<Month> monthLiveData = monthDAO.getMonth(monthHashCode);
        if (monthLiveData != null){
            return monthLiveData;
        }
        Month month = new Month(jewCalendar);
        final MutableLiveData<Month> data = new MutableLiveData<>();
        data.setValue(month);
        return data;
    }
}
