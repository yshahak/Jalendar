package com.thedroidboy.jalendar.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.thedroidboy.jalendar.calendars.jewish.JewCalendar.hebrewHebDateFormatter;

/**
 * Created by Yaakov Shahak
 * on 05/11/2017.
 */
@Entity
public class Month {

    private static final String TAG = "Month";
    public static final long DAY_IN_MS = TimeUnit.DAYS.toMillis(1);

    @PrimaryKey
    private final int monthHashCode;
    private final String monthHebLabel;
    private final String monthEnLabel;
    private final int daysInMonth, headOffset, trailOffset;
    private final long startMonthInMs, endMonthInMs;
    @Ignore
    private String monthHebName;
    @Ignore
    private String monthYearName;
    @Ignore
    private List<Day> dayList = new ArrayList<>();

    public Month(int monthHashCode, String monthHebLabel, String monthEnLabel, int daysInMonth, int headOffset, int trailOffset, long startMonthInMs, long endMonthInMs) {
        this.monthHashCode = monthHashCode;
        this.monthHebLabel = monthHebLabel;
        this.monthEnLabel = monthEnLabel;
        this.daysInMonth = daysInMonth;
        this.headOffset = headOffset;
        this.trailOffset = trailOffset;
        this.startMonthInMs = startMonthInMs;
        this.endMonthInMs = endMonthInMs;
    }

    public Month(JewCalendar jewCalendar) {
        this.monthHashCode = jewCalendar.monthHashCode();
        this.monthHebLabel = jewCalendar.getHebMonthName() + " " + jewCalendar.getYearHebName();
        this.monthEnLabel = jewCalendar.getEnMonthName() + " " + jewCalendar.getYearEnName();
        this.daysInMonth = jewCalendar.getDaysInJewishMonth();
        this.headOffset = jewCalendar.getMonthHeadOffset();
        int trailOffset = jewCalendar.getMonthTrailOffset();
        while (headOffset + daysInMonth + trailOffset < 42) {
            trailOffset += 7;
        }
        this.trailOffset = trailOffset;
        setMonthHebName(jewCalendar.getHebMonthName());
        setYearHebName(jewCalendar.getYearHebName());
        this.startMonthInMs = setMonthDays(jewCalendar);
        this.endMonthInMs = this.startMonthInMs + daysInMonth * DAY_IN_MS;
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

    public int getMonthHashCode() {
        return monthHashCode;
    }

    public String getMonthHebLabel() {
        return monthHebLabel;
    }

    public String getMonthEnLabel() {
        return monthEnLabel;
    }

    public String getMonthYearName() {
        return monthYearName;
    }

    public List<Day> getDayList() {
        return dayList;
    }

    public void setDayList(List<Day> dayList) {
        this.dayList = dayList;
    }

    public int getHeadOffset() {
        return headOffset;
    }

    public int getTrailOffset() {
        return trailOffset;
    }

    public long getStartMonthInMs() {
        return startMonthInMs;
    }

    public long getEndMonthInMs() {
        return endMonthInMs;
    }

    public long getStartMonthInMsIncludeOffset() {
        return startMonthInMs - headOffset * DAY_IN_MS;
    }

    public long getEndMonthInMsIncludeOffset() {
        return endMonthInMs + trailOffset * DAY_IN_MS;
    }

    public long setMonthDays(JewCalendar monthCalendar) {
        long shift = (monthCalendar.getJewishDayOfMonth() - 1 + headOffset) * DAY_IN_MS;
        JewCalendar calendar = new JewCalendar(new Date(monthCalendar.getTime().getTime() - shift));
        long beginOfMonth = 0;
        while (dayList.size() < 42){
            long startDayInMs = dayList.size() == 0 ? calendar.getBeginOfDay() : dayList.get(dayList.size() - 1).getEndDayInMillis();
            int jewishDayOfMonth = calendar.getJewishDayOfMonth();
            Day day = new Day(jewishDayOfMonth, hebrewHebDateFormatter.formatHebrewNumber(jewishDayOfMonth)
                    , startDayInMs, calendar.getGregorianDayOfMonth());
            dayList.add(day);
            calendar.forward();
            if (jewishDayOfMonth == 1){
                beginOfMonth = startDayInMs;
            }  if (beginOfMonth == 0 || dayList.size() > headOffset + daysInMonth){ // headset
                day.setOutOfMonthRange(true);
            }

        }
         return beginOfMonth;
    }
}
