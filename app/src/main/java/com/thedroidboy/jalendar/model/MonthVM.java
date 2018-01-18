package com.thedroidboy.jalendar.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.thedroidboy.jalendar.CalendarRepo;

/**
 * Created by $Yaakov Shahak on 12/7/2017.
 */

public class MonthVM extends ViewModel {

    private LiveData<Month> month;

    public void init(int position, CalendarRepo monthRepo){
        if (this.month != null) {
            return;
        }
        this.month = monthRepo.getMonthByPosition(position);
    }

    public LiveData<Month> getMonth() {
        return month;
    }
}