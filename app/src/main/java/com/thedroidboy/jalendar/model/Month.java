package com.thedroidboy.jalendar.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;

import java.util.ArrayList;
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
    @Ignore
    private String monthHebName;
    @Ignore
    private String monthYearName;
    @Ignore
    private List<Day> dayList = new ArrayList<>();

    public Month(int monthHashCode, String monthHebLabel, String monthEnLabel, int daysInMonth, int headOffset, int trailOffset) {
        this.monthHashCode = monthHashCode;
        this.monthHebLabel = monthHebLabel;
        this.monthEnLabel = monthEnLabel;
        this.daysInMonth = daysInMonth;
        this.headOffset = headOffset;
        this.trailOffset = trailOffset;
    }

    public Month(JewCalendar jewCalendar) {
        this.monthHashCode = jewCalendar.monthHashCode();
        this.monthHebLabel = jewCalendar.getHebMonthName() + " " + jewCalendar.getYearHebName();
        this.monthEnLabel = jewCalendar.getEnMonthName() + " " + jewCalendar.getYearEnName();
        this.daysInMonth = jewCalendar.getDaysInJewishMonth();
        this.headOffset = jewCalendar.getMonthHeadOffset();
        this.trailOffset = jewCalendar.getMonthTrailOffset();

        setMonthHebName(jewCalendar.getHebMonthName());
        setYearHebName(jewCalendar.getYearHebName());
//        setMonthDays(jewCalendar);
        setMonthOnlyDays(jewCalendar);
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

    public int getHeadOffset() {
        return headOffset;
    }

    public int getTrailOffset() {
        return trailOffset;
    }

    public void setMonthOnlyDays(JewCalendar monthCalendar) {
        int currentDay = monthCalendar.getJewishDayOfMonth();
        monthCalendar.setJewishDayOfMonth(1);
        int loazyDay = monthCalendar.getGregorianDayOfMonth();
        int loazyDaysInMonth = monthCalendar.getLastDayOfGregorianMonth();
        for (int i = 0; i < daysInMonth; i++) {
            long startDayInMs = dayList.size() == 0 ? monthCalendar.getBeginOfDay() : dayList.get(dayList.size() - 1).getEndDayInMillis();
            Day day = new Day(startDayInMs, i + 1, hebrewHebDateFormatter.formatHebrewNumber(i + 1), startDayInMs, startDayInMs + DAY_IN_MS, loazyDay++);
            dayList.add(day);
            if (loazyDay > loazyDaysInMonth) {
                loazyDay = 1;
            }
        }
        monthCalendar.setJewishDayOfMonth(currentDay);
    }

//    public void setMonthDays(JewCalendar monthCalendar) {
//        int currentDay = monthCalendar.getJewishDayOfMonth();
//        monthCalendar.setJewishDayOfMonth(1);
//        int loazyDay = monthCalendar.getGregorianDayOfMonth();
//        int daysInLoaziMonth = monthCalendar.getLastDayOfGregorianMonth();
//        int headOffset = getHeadOffset();
//        int daysInPrevMonth = monthCalendar.getDaysInPreviousMonth();
//        for (int i = daysInPrevMonth - headOffset; i < daysInPrevMonth; i++) {
//            Day day = new Day(i + 1);
//            day.setOutOfMonthRange(true);
//            day.setBackgroundColor(Color.GRAY);
//            if (dayList.size() == 0) {
//                day.setBeginAndEnd(monthCalendar, headOffset);
//            } else {
//                day.setBeginAndEnd(dayList.get(dayList.size() - 1));
//            }
//            day.setLoazyDayOfMonth(loazyDay++);
//            if (loazyDay == daysInLoaziMonth){
//                loazyDay = 1;
//            }
//            dayList.add(day);
//        }
//        int daysSum = monthCalendar.getDaysInJewishMonth();
//        for (int i = 1; i <= daysSum; i++) {
//            Day day = new Day(i);
//            if (dayList.size() == 0) {
//                day.setBeginAndEnd(monthCalendar);
//            } else {
//                day.setBeginAndEnd(dayList.get(dayList.size() - 1));
//            }
//            day.setLoazyDayOfMonth(loazyDay++);
//            if (loazyDay == daysInLoaziMonth){
//                loazyDay = 1;
//            }
//            dayList.add(day);
//        }
//        int monthTrailOffset = monthCalendar.getMonthTrailOffset();
//        int i = 1;
//        for (; i <= monthTrailOffset; i++) {
//            Day day = new Day(i);
//            day.setOutOfMonthRange(true);
//            day.setBeginAndEnd(dayList.get(dayList.size() - 1));
//            day.setLoazyDayOfMonth(loazyDay++);
//            if (loazyDay == daysInLoaziMonth){
//                loazyDay = 1;
//            }
//            dayList.add(day);
//            day.setBackgroundColor(Color.GRAY);
//        }
//        while (dayList.size() < 42){
//            Day day = new Day(i);
//            day.setOutOfMonthRange(true);
//            day.setBeginAndEnd(dayList.get(dayList.size() - 1));
//            day.setLoazyDayOfMonth(loazyDay++);
//            if (loazyDay == daysInLoaziMonth){
//                loazyDay = 1;
//            }
//            dayList.add(day);
//            day.setBackgroundColor(Color.GRAY);
//            i++;
//        }
//        monthCalendar.setJewishDayOfMonth(currentDay);
////        Log.d(TAG, "setMonthDays: took " + (System.currentTimeMillis() - start) + " ms");
//    }


}
