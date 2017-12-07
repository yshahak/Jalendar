package com.thedroidboy.jalendar.model;

import android.arch.lifecycle.ViewModel;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Color;

import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yaakov Shahak
 * on 05/11/2017.
 */
@Entity
public class MonthVM extends ViewModel {

    private static final String TAG = "MonthVM";

    @PrimaryKey
    private final int monthHashCode;
    private final String monthHebLabel;
    private final String monthEnLabel;
    private final int daysInMonth, headOffset, trailOffset;
    @Ignore
    private String monthHebName;
    @Ignore
    private String monthYearName;
    @Ignore
    private List<Day> dayList = new ArrayList<>();

    public MonthVM(int monthHashCode, String monthHebLabel, String monthEnLabel, int daysInMonth, int headOffset, int trailOffset) {
        this.monthHashCode = monthHashCode;
        this.monthHebLabel = monthHebLabel;
        this.monthEnLabel = monthEnLabel;
        this.daysInMonth = daysInMonth;
        this.headOffset = headOffset;
        this.trailOffset = trailOffset;
    }

    public MonthVM(JewCalendar jewCalendar) {
        this.monthHashCode = jewCalendar.monthHashCode();
        this.monthHebLabel = jewCalendar.getHebMonthName() + " " + jewCalendar.getYearHebName();
        this.monthEnLabel = jewCalendar.getEnMonthName() + " " + jewCalendar.getYearEnName();
        this.daysInMonth = jewCalendar.getDaysInJewishMonth();
        this.headOffset = jewCalendar.getMonthHeadOffset();
        this.trailOffset = jewCalendar.getMonthTrailOffset();

        setMonthHebName(jewCalendar.getHebMonthName());
        setYearHebName(jewCalendar.getYearHebName());
        setMonthDays(jewCalendar);
    }


    public void setMonthHebName(String monthHebName) {
        this.monthHebName = monthHebName;
    }

    public void setYearHebName(String year) {
        this.monthYearName = year;
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
        int loazyDay = monthCalendar.getGregorianDayOfMonth();
        int daysInLoaziMonth = monthCalendar.getLastDayOfGregorianMonth();
        int headOffset = getHeadOffset();
        int daysInPrevMonth = monthCalendar.getDaysInPreviousMonth();
        for (int i = daysInPrevMonth - headOffset; i < daysInPrevMonth; i++) {
            Day day = new Day(i + 1);
            day.setOutOfMonthRange(true);
            day.setBackgroundColor(Color.GRAY);
            day.setBackgroundColor(Color.GRAY);
            if (dayList.size() == 0) {
                day.setBeginAndEnd(monthCalendar, headOffset);
            } else {
                day.setBeginAndEnd(dayList.get(dayList.size() - 1));
            }
            day.setLoazyDayOfMonth(loazyDay++);
            if (loazyDay == daysInLoaziMonth){
                loazyDay = 1;
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
            day.setLoazyDayOfMonth(loazyDay++);
            if (loazyDay == daysInLoaziMonth){
                loazyDay = 1;
            }
            dayList.add(day);
        }
        int monthTrailOffset = monthCalendar.getMonthTrailOffset();
        int i = 1;
        for (; i <= monthTrailOffset; i++) {
            Day day = new Day(i);
            day.setOutOfMonthRange(true);
            day.setBeginAndEnd(dayList.get(dayList.size() - 1));
            day.setLoazyDayOfMonth(loazyDay++);
            if (loazyDay == daysInLoaziMonth){
                loazyDay = 1;
            }
            dayList.add(day);
            day.setBackgroundColor(Color.GRAY);
        }
        while (dayList.size() < 42){
            Day day = new Day(i);
            day.setOutOfMonthRange(true);
            day.setBeginAndEnd(dayList.get(dayList.size() - 1));
            day.setLoazyDayOfMonth(loazyDay++);
            if (loazyDay == daysInLoaziMonth){
                loazyDay = 1;
            }
            dayList.add(day);
            day.setBackgroundColor(Color.GRAY);
            i++;
        }
        monthCalendar.setJewishDayOfMonth(currentDay);
//        Log.d(TAG, "setMonthDays: took " + (System.currentTimeMillis() - start) + " ms");
    }


}
