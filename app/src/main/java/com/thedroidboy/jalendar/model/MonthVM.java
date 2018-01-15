package com.thedroidboy.jalendar.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.thedroidboy.jalendar.MonthRepo;
import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;

/**
 * Created by $Yaakov Shahak on 12/7/2017.
 */

public class MonthVM extends AndroidViewModel {

    private LiveData<Month> month;
    private JewCalendar calendar;
    private MonthRepo monthRepo;

    public MonthVM(@NonNull Application application) {
        super(application);
    }

    public void init(JewCalendar jewCalendar, MonthRepo monthRepo){
        if (this.month != null) {
            return;
        }
        this.calendar = jewCalendar;
        this.monthRepo = monthRepo;
        this.month = monthRepo.getMonthByCalendar(jewCalendar);
    }

    public void init(int position, MonthRepo monthRepo){
        if (this.month != null) {
            return;
        }
        this.monthRepo = monthRepo;
        this.month = monthRepo.getMonthByPosition(position);
    }

//    public void pull(){
//        monthRepo.pullMonth(calendar);
//    }

    public LiveData<Month> getMonth() {
        return month;
    }
}
//