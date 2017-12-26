package com.thedroidboy.jalendar.model;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Color;

import com.thedroidboy.jalendar.calendars.google.EventInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by yshahak on 09/10/2016.
 */
@Entity
public class Day {

    public static final long DAY_IN_MS = TimeUnit.DAYS.toMillis(1);


    public static final String ID = "id";
    public static final String LABEL = "label";
    public static final String START_DAT_IN_MILLIS = "startDatInMillis";
    public static final String END_DAY_IN_MILLIS = "endDayInMillis";
    public static final String DAY_IN_MONTH = "dayInMonth";
    public static final String IS_OUT_OF_MONTH_RANGE = "isOutOfMonthRange";

    @Ignore
    private List<EventInstance> googleEventInstances = new ArrayList<>();

//    private final long id;
    private final String label;
    @PrimaryKey
    private final long startDayInMillis;
//    private final long endDayInMillis;
    private final int dayInMonth;
    private final int loazyDayOfMonth;
    @Ignore
    private boolean isOutOfMonthRange;
    @Ignore
    private int backgroundColor = Color.TRANSPARENT;

    public Day(int dayInMonth, String label, long startDayInMillis,int loazyDayOfMonth) {
//        this.id = startDayInMillis;
        this.label = label;
        this.dayInMonth = dayInMonth;
        this.startDayInMillis = startDayInMillis;
//        this.endDayInMillis = endDayInMillis;
        this.loazyDayOfMonth = loazyDayOfMonth;
    }

//    public Day(int dayInMonth) {
//        this.dayInMonth = dayInMonth;
//        this.label = hebrewHebDateFormatter.formatHebrewNumber(dayInMonth);
//    }


//    public long getId() {
//        return id;
//    }


    public int getLoazyDayOfMonth() {
        return loazyDayOfMonth;
    }

    public String getLabel() {
        return label;
    }

    public String getLoaziLabel() {
        return Integer.toString(loazyDayOfMonth);
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getLabelColor(){
        return isOutOfMonthRange ? Color.LTGRAY : Color.BLACK;
    }

    public float getDayAlphe() {
        return isOutOfMonthRange ? 0.5f : 1f;
    }

    public long getStartDayInMillis() {
        return startDayInMillis;
    }

    public int getDayInMonth() {
        return dayInMonth;
    }

    public long getEndDayInMillis() {
        return startDayInMillis + DAY_IN_MS;
    }

    public void setOutOfMonthRange(boolean outOfMonthRange) {
        isOutOfMonthRange = outOfMonthRange;
    }

    public boolean isOutOfMonthRange() {
        return isOutOfMonthRange;
    }

    public List<EventInstance> getGoogleEventInstances() {
        return googleEventInstances;
    }

//    public void setBeginAndEnd(JewishCalendar jewishCalendar) {
//        calendar.set(jewishCalendar.getGregorianYear(), jewishCalendar.getGregorianMonth(), jewishCalendar.getGregorianDayOfMonth());
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
//        this.startDayInMillis = calendar.getTimeInMillis();
//        this.endDayInMillis = startDayInMillis + TimeUnit.DAYS.toMillis(1);
//    }
//
//    public void setBeginAndEnd(JewishCalendar jewishCalendar, int offset) {
//        calendar.set(jewishCalendar.getGregorianYear(), jewishCalendar.getGregorianMonth(), jewishCalendar.getGregorianDayOfMonth());
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
//        this.startDayInMillis = calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(offset);
//        this.loazyDayOfMonth = new Date(this.startDayInMillis).getDay();
//        this.endDayInMillis = startDayInMillis + TimeUnit.DAYS.toMillis(1);
//    }

//    public void setLoazyDayOfMonth(int day) {
//        this.loazyDayOfMonth = day;
//    }
//
//    public void setBeginAndEnd(Day day) {
//        this.startDayInMillis = day.endDayInMillis;
//        this.endDayInMillis = startDayInMillis + TimeUnit.DAYS.toMillis(1);
//    }

    @Override
    public String toString() {
        return "Day{" +
                "\t label='" + label + '\'' +
                "\t loazyDay='" + loazyDayOfMonth + '\'' +
                "\t startDayInMillis=" + startDayInMillis +
                "\t dayInMonth=" + dayInMonth +
                "\t isOutOfMonthRange=" + isOutOfMonthRange +
                "\t backgroundColor=" + backgroundColor +
                '}';
    }
}