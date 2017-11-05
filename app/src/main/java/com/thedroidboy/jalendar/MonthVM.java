package com.thedroidboy.jalendar;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListProvider;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.DiffCallback;

/**
 * Created by Yaakov Shahak
 * on 05/11/2017.
 */

public class MonthVM extends ViewModel {

    private LiveData<PagedList<MonthVM>> monthList;
    CalendarDataSource calendarDataSource;

    private String monthHebName;

    public void init(){
        monthList = new LivePagedListProvider<Integer, MonthVM>() {
            @Override
            protected DataSource<Integer, MonthVM> createDataSource() {
                calendarDataSource = new CalendarDataSource();
                return calendarDataSource;
            }
        }.create(0, new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(5)
                .setInitialLoadSizeHint(5)
                .build());
    }

    public LiveData<PagedList<MonthVM>> getMonthList() {
        return monthList;
    }

    public void setMonthHebName(String monthHebName) {
        this.monthHebName = monthHebName;
    }

    public String getMonthHebName() {
        return monthHebName;
    }

    public static DiffCallback<MonthVM> DIFF_CALLBACK = new DiffCallback<MonthVM>() {

        @Override
        public boolean areItemsTheSame(@NonNull MonthVM oldItem, @NonNull MonthVM newItem) {
            return oldItem.getMonthHebName().equals(newItem.getMonthHebName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull MonthVM oldItem, @NonNull MonthVM newItem) {
            return oldItem.equals(newItem);
        }
    };
}
