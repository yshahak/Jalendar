package com.thedroidboy.jalendar.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.thedroidboy.jalendar.CalendarRepo;

/**
 * Created by Yaakov Shahak
 * on 24/12/2017.
 */

public class DayVM extends ViewModel {

    private LiveData<Day> dayLiveData;

    public void init(int position, CalendarRepo calendarRepo){
        if (this.dayLiveData != null) {
            return;
        }
        this.dayLiveData = calendarRepo.getDayByPosition(position);
    }

    public LiveData<Day> getDayLiveData() {
        return dayLiveData;
    }
}
