package com.thedroidboy.jalendar;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;
import com.thedroidboy.jalendar.model.Day;
import com.thedroidboy.jalendar.model.Month;

import java.util.List;

/**
 * Created by $Yaakov Shahak on 12/7/2017.
 */

public interface MonthRepo {

    void insertMonth(Month month);

    void pullMonth(JewCalendar jewCalendar, MutableLiveData<Month> monthLiveData);

    void insertMonthDays(List<Day> monthDays);

    LiveData<List<Month>> getMonthes(int monthHashCode, int sum);

    MutableLiveData<Month> getMonth(JewCalendar jewCalendar);

}
