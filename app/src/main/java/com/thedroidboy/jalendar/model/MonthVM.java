package com.thedroidboy.jalendar.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.thedroidboy.jalendar.MonthRepo;
import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;

/**
 * Created by $Yaakov Shahak on 12/7/2017.
 */

public class MonthVM extends ViewModel {

    private LiveData<Month> month;

    public void init(JewCalendar jewCalendar, MonthRepo monthRepo){
        if (this.month != null) {
            return;
        }
        this.month = monthRepo.getMonth(jewCalendar);
    }

    public LiveData<Month> getMonth() {
        return month;
    }
}
//