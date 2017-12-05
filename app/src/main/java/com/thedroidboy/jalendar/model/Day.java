package com.thedroidboy.jalendar.model;


import android.graphics.Color;

import com.thedroidboy.jalendar.calendars.google.EventInstance;

import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.thedroidboy.jalendar.calendars.jewish.JewCalendar.hebrewDateFormatter;


/**
 * Created by yshahak on 09/10/2016.
 */
public class Day {

    public static final String ID = "id";
    public static final String LABEL = "label";
    public static final String START_DAT_IN_MILLIS = "startDatInMillis";
    public static final String END_DAY_IN_MILLIS = "endDayInMillis";
    public static final String DAY_IN_MONTH = "dayInMonth";
    public static final String IS_OUT_OF_MONTH_RANGE = "isOutOfMonthRange";
    private static Calendar calendar = Calendar.getInstance();
    private List<EventInstance> googleEventInstances = new ArrayList<>();

    int id;
    private String label;
    private long startDayInMillis;
    private long endDayInMillis;
    private int dayInMonth;
    private int loazyDayOfMonth;
    private boolean isOutOfMonthRange;
    private int backgroundColor = Color.TRANSPARENT;

    public Day() {
    }

    public Day(int dayInMonth) {
        this.dayInMonth = dayInMonth;
        this.label = hebrewDateFormatter.formatHebrewNumber(dayInMonth);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setDayInMonth(int dayInMonth) {
        this.dayInMonth = dayInMonth;
    }

    public int getId() {
        return id;
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
        return endDayInMillis;
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

    public void setBeginAndEnd(JewishCalendar jewishCalendar) {
        calendar.set(jewishCalendar.getGregorianYear(), jewishCalendar.getGregorianMonth(), jewishCalendar.getGregorianDayOfMonth());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        this.startDayInMillis = calendar.getTimeInMillis();
        this.endDayInMillis = startDayInMillis + TimeUnit.DAYS.toMillis(1);
    }

    public void setBeginAndEnd(JewishCalendar jewishCalendar, int offset) {
        calendar.set(jewishCalendar.getGregorianYear(), jewishCalendar.getGregorianMonth(), jewishCalendar.getGregorianDayOfMonth());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        this.startDayInMillis = calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(offset);
        this.loazyDayOfMonth = new Date(this.startDayInMillis).getDay();
        this.endDayInMillis = startDayInMillis + TimeUnit.DAYS.toMillis(1);
    }

    public void setLoazyDayOfMonth(int day) {
        this.loazyDayOfMonth = day;
    }

    public void setBeginAndEnd(Day day) {
        this.startDayInMillis = day.endDayInMillis;
        this.endDayInMillis = startDayInMillis + TimeUnit.DAYS.toMillis(1);
    }

    @Override
    public String toString() {
        return "Day{" +
                "\tid=" + id +
                "\t label='" + label + '\'' +
                "\t startDayInMillis=" + startDayInMillis +
                "\t endDayInMillis=" + endDayInMillis +
                "\t dayInMonth=" + dayInMonth +
                "\t isOutOfMonthRange=" + isOutOfMonthRange +
                "\t backgroundColor=" + backgroundColor +
                '}';
    }
}