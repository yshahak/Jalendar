package com.thedroidboy.jalendar.model;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;

/**
 * Created by Yaakov Shahak
 * on 21/11/2017.
 */

public class MonthFactory implements ViewModelProvider.Factory{

    private final JewCalendar jewCalendar;

    public MonthFactory(JewCalendar jewCalendar) {
        this.jewCalendar = jewCalendar;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MonthVM(jewCalendar);
    }
}
