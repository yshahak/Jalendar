package com.thedroidboy.jalendar.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListProvider;
import android.arch.paging.PagedList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.DiffCallback;

import com.thedroidboy.jalendar.CalendarDataSource;
import com.thedroidboy.jalendar.JewCalendar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yaakov Shahak
 * on 05/11/2017.
 */

public class MonthVM extends ViewModel {

    public static final int INITIAL_OFFSET = 500;
    private LiveData<PagedList<MonthVM>> monthList;
    CalendarDataSource calendarDataSource;


    private String monthHebName;
    private int daysInMonth, headOffset, trailOffset;
    private List<Day> dayList = new ArrayList<>();

    public void init() {
        monthList = new LivePagedListProvider<Integer, MonthVM>() {
            @Override
            protected DataSource<Integer, MonthVM> createDataSource() {
                calendarDataSource = new CalendarDataSource();
                return calendarDataSource;
            }
        }.create(INITIAL_OFFSET, new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(3)
                .setInitialLoadSizeHint(3)
                .build());
    }


    public void setMonthHebName(String monthHebName) {
        this.monthHebName = monthHebName;
    }

    public void setHeadOffset(int headOffset) {
        this.headOffset = headOffset;
    }

    public void setTrailOffset(int trailOffset) {
        this.trailOffset = trailOffset;
    }

    public void setDaysInMonth(int daysInMonth) {
        this.daysInMonth = daysInMonth;
    }

    public int getDaysInMonth() {
        return daysInMonth;
    }

    public LiveData<PagedList<MonthVM>> getMonthList() {
        return monthList;
    }


    public String getMonthHebName() {
        return monthHebName;
    }

    public List<Day> getDayList() {
        return dayList;
    }

    public int getHeadOffset() {
        return headOffset;
    }

    public int getTrailOffset() {
        return trailOffset;
    }

    public void setMonthDays(JewCalendar monthCalendar) {
        int currentDayOfMonth = monthCalendar.getJewishDayOfMonth();
        monthCalendar.setJewishDayOfMonth(1);
        int headOffset = getHeadOffset();
        monthCalendar.shiftDay(headOffset * (-1));
        for (int i = 0; i < headOffset; i++) {
            Day day = new Day(monthCalendar);
            day.setOutOfMonthRange(true);
            day.setBackgroundColor(Color.GRAY);
            monthCalendar.shiftDay(1);
            if (dayList.size() == 0) {
                day.setBeginAndEnd(monthCalendar);
            } else {
                day.setBeginAndEnd(dayList.get(dayList.size() - 1));
            }
            dayList.add(day);
        }
        int daysSum = monthCalendar.getDaysInJewishMonth();
        for (int i = 1; i <= daysSum; i++) {
            monthCalendar.setJewishDayOfMonth(i);
            Day day = new Day(monthCalendar);
            if (dayList.size() == 0) {
                day.setBeginAndEnd(monthCalendar);
            } else {
                day.setBeginAndEnd(dayList.get(dayList.size() - 1));
            }
            dayList.add(day);
        }
        int monthTrailOffset = monthCalendar.getMonthTrailOffset();
        monthCalendar.shiftDay(1);
        for (int i = 0; i < monthTrailOffset; i++) {
            Day day = new Day(monthCalendar);
            day.setOutOfMonthRange(true);
            day.setBeginAndEnd(dayList.get(dayList.size() - 1));
            monthCalendar.shiftDay(1);
            dayList.add(day);
            day.setBackgroundColor(Color.GRAY);
        }
        monthCalendar.shiftMonthBackword();
        monthCalendar.setJewishDayOfMonth(currentDayOfMonth);
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