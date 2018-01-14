package com.thedroidboy.jalendar.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.thedroidboy.jalendar.CalendarRepo;
import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;

/**
 * Created by $Yaakov Shahak on 12/7/2017.
 */

public class MonthVM extends AndroidViewModel {

    private LiveData<Month> month;
    private JewCalendar calendar;
    private CalendarRepo calendarRepo;

    public MonthVM(@NonNull Application application) {
        super(application);
    }

    public void init(JewCalendar jewCalendar, CalendarRepo calendarRepo){
        if (this.month != null) {
            return;
        }
        this.calendar = jewCalendar;
        this.calendarRepo = calendarRepo;
        this.month = calendarRepo.getMonth(jewCalendar);
    }

    public void pull(){
        calendarRepo.pullMonth(calendar,  month);
    }

    public LiveData<Month> getMonth() {
        return month;
    }
}
//