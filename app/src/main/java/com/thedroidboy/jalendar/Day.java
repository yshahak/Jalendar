package com.thedroidboy.jalendar;


import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static com.thedroidboy.jalendar.JewCalendar.hebrewDateFormatter;


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
//    private List<EventInstance> googleEventInstances = new ArrayList<>();
    int id;
    private String label;
    private long startDayInMillis;
    private long endDayInMillis;
    private int dayInMonth;
    private boolean isOutOfMonthRange;

    public Day() {
    }

    public Day(JewCalendar jewishCalendar) {
        this.dayInMonth = jewishCalendar.getJewishDayOfMonth();
        id = jewishCalendar.monthHashCode() + dayInMonth;
        this.label = hebrewDateFormatter.formatHebrewNumber(dayInMonth);
        setBeginAndEnd(jewishCalendar);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setStartDayInMillis(long startDayInMillis) {
        this.startDayInMillis = startDayInMillis;
    }

    public void setEndDayInMillis(long endDayInMillis) {
        this.endDayInMillis = endDayInMillis;
    }

    public void setDayInMonth(int dayInMonth) {
        this.dayInMonth = dayInMonth;
    }

    public int getId() {
        return id;
    }

//    public List<EventInstance> getGoogleEventInstances() {
//        return googleEventInstances;
//    }
//
//    public void setGoogleEventInstances(List<EventInstance> googleEventInstances) {
//        this.googleEventInstances = googleEventInstances;
//    }

    public String getLabel() {
        return label;
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

    public void setBeginAndEnd(JewishCalendar jewishCalendar) {
        calendar.set(jewishCalendar.getGregorianYear(), jewishCalendar.getGregorianMonth(), jewishCalendar.getGregorianDayOfMonth());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        this.startDayInMillis = calendar.getTimeInMillis();
        this.endDayInMillis = startDayInMillis + TimeUnit.DAYS.toMillis(1);
    }

    public void setBeginAndEnd(Day day) {
        this.startDayInMillis = day.endDayInMillis;
        this.endDayInMillis = startDayInMillis + TimeUnit.DAYS.toMillis(1);
    }
}