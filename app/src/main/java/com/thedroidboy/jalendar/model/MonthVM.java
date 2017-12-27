package com.thedroidboy.jalendar.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.thedroidboy.jalendar.MonthRepo;
import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;

/**
 * Created by $Yaakov Shahak on 12/7/2017.
 */

public class MonthVM extends ViewModel {

    private MutableLiveData<Month> month;
    private JewCalendar calendar;
    private MonthRepo monthRepo;

    public void init(JewCalendar jewCalendar, MonthRepo monthRepo){
        if (this.month != null) {
            return;
        }
        this.calendar = jewCalendar;
        this.monthRepo = monthRepo;
        this.month = monthRepo.getMonth(jewCalendar);
    }

    public void pull(){
        monthRepo.pullMonth(calendar,  month);
    }

    public LiveData<Month> getMonth() {
        return month;
    }
}
//