package com.thedroidboy.jalendar.model;

import android.arch.lifecycle.ViewModel;
import android.graphics.Color;

import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yaakov Shahak
 * on 05/11/2017.
 */

public class MonthVM extends ViewModel {

    private static final String TAG = "MonthVM";

    public static final int INITIAL_OFFSET = 500;


    private String monthHebName, monthYearName;
    private int daysInMonth, headOffset, trailOffset;
    private List<Day> dayList = new ArrayList<>();

    public MonthVM(JewCalendar jewCalendar) {
        setMonthHebName(jewCalendar.getMonthName());
        setYearHebName(jewCalendar.getYearName());
        setDaysInMonth(jewCalendar.getDaysInJewishMonth());
        setHeadOffset(jewCalendar.getMonthHeadOffset());
        setTrailOffset(jewCalendar.getMonthTrailOffset());
        setMonthDays(jewCalendar);
    }

    public void setMonthHebName(String monthHebName) {
        this.monthHebName = monthHebName;
    }

    public void setYearHebName(String year) {
        this.monthYearName = year;
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

    public String getMonthHebName() {
        return monthHebName;
    }

    public String getYearHebName() {
        return monthYearName;
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
        int currentDay = monthCalendar.getJewishDayOfMonth();
        monthCalendar.setJewishDayOfMonth(1);
        int headOffset = getHeadOffset();
        int daysInPrevMonth = monthCalendar.getDaysInPreviousMonth();
        for (int i = daysInPrevMonth - headOffset; i < daysInPrevMonth; i++) {
            Day day = new Day(i);
            day.setOutOfMonthRange(true);
            day.setBackgroundColor(Color.GRAY);
            day.setBackgroundColor(Color.GRAY);
            if (dayList.size() == 0) {
                day.setBeginAndEnd(monthCalendar);
            } else {
                day.setBeginAndEnd(dayList.get(dayList.size() - 1));
            }
            dayList.add(day);
        }
        int daysSum = monthCalendar.getDaysInJewishMonth();
        for (int i = 1; i <= daysSum; i++) {
            Day day = new Day(i);
            if (dayList.size() == 0) {
                day.setBeginAndEnd(monthCalendar);
            } else {
                day.setBeginAndEnd(dayList.get(dayList.size() - 1));
            }
            dayList.add(day);
        }
        int monthTrailOffset = monthCalendar.getMonthTrailOffset();
        int i = 1;
        for (; i <= monthTrailOffset; i++) {
            Day day = new Day(i);
            day.setOutOfMonthRange(true);
            day.setBeginAndEnd(dayList.get(dayList.size() - 1));
            dayList.add(day);
            day.setBackgroundColor(Color.GRAY);
        }
        while (dayList.size() < 42){
            Day day = new Day(i);
            day.setOutOfMonthRange(true);
            day.setBeginAndEnd(dayList.get(dayList.size() - 1));
            dayList.add(day);
            day.setBackgroundColor(Color.GRAY);
            i++;
        }
        monthCalendar.setJewishDayOfMonth(currentDay);
//        Log.d(TAG, "setMonthDays: took " + (System.currentTimeMillis() - start) + " ms");
    }

}
