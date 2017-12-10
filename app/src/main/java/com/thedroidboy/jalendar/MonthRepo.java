package com.thedroidboy.jalendar;

import android.arch.lifecycle.LiveData;

import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;
import com.thedroidboy.jalendar.model.Month;

import java.util.List;

/**
 * Created by $Yaakov Shahak on 12/7/2017.
 */

public interface MonthRepo {

    void insertMonth(Month month);

    LiveData<List<Month>> getMonthes(int monthHashCode, int sum);

    LiveData<Month> getMonth(JewCalendar jewCalendar);

}
