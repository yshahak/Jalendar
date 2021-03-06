package com.thedroidboy.jalendar.calendars.jewish;

import android.os.Parcel;

import net.sourceforge.zmanim.hebrewcalendar.HebrewDateFormatter;
import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yshahak
 * on 12/28/2016.
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class JewCalendar extends JewishCalendar {

    private static final String TAG = JewCalendar.class.getSimpleName();
    public static HebrewDateFormatter hebrewHebDateFormatter = new HebrewDateFormatter();
    public static HebrewDateFormatter hebrewEnDateFormatter = new HebrewDateFormatter();
    private int currentPosition;

    static {
        hebrewHebDateFormatter.setHebrewFormat(true);
        hebrewEnDateFormatter.setHebrewFormat(false);
    }

    public JewCalendar(int jewishYear, int jewishMonth, int day) {
        super(jewishYear, jewishMonth, day);
    }

    public JewCalendar(Calendar calendar) {
        super(calendar);
    }

    public JewCalendar() {
        super();
    }

    public JewCalendar(int offset) {
        shiftMonth(offset);
    }

    public JewCalendar(Date date) {
        super(date);
    }

    public JewCalendar shiftMonth(int offset) {
        int diff = offset - currentPosition;
        if (diff > 0) {
            for (int i = 0; i < diff; i++) {
                shiftMonthForward();
            }
        } else if (diff < 0) {
            for (int i = diff * (-1); i > 0; i--) {
                shiftMonthBackword();
            }
        }
        this.currentPosition = offset;
        return this;
    }

    public void shiftDay(int offset) {
        if (offset > 0) {
            for (int i = 0; i < offset; i++) {
                forward();
            }
        } else if (offset < 0) {
            for (int i = offset * (-1); i > 0; i--) {
                back();
            }
        }
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void shiftMonthForward() {
        int next = getJewishMonth() + 1;
        if (next == 7) {
            setJewishYear(getJewishYear() + 1);
        } else if (next == 14 || (next == 13 && !isJewishLeapYear())) {
            next = 1;
        }
        setJewishMonth(next);
    }

    public void shiftMonthBackword() {
        int previous = getJewishMonth() - 1;
        if (previous == 0) {
            previous = isJewishLeapYear() ? 13 : 12;
        } else if (previous == 6) {
            setJewishYear(getJewishYear() - 1);
        }
        setJewishMonth(previous);
    }

    public int getMonthHeadOffset() {
        //calculate head
        int currentDayOfMonth = getJewishDayOfMonth();
        int currentDayOfWeek = getDayOfWeek();
        int moveToFirst = currentDayOfWeek - currentDayOfMonth + 1;
        while (moveToFirst < 1) {
            moveToFirst += 7;
        }
        return moveToFirst - 1;
    }

    public int getMonthTrailOffset() {
        //calculate trail
        int totalDays = getDaysInJewishMonth();
        int currentDayOfMonth = getJewishDayOfMonth();
        int currentDayOfWeek = getDayOfWeek();
        int remain = (totalDays - currentDayOfMonth + currentDayOfWeek) % 7;
        return remain == 0 ? remain : (7 - remain);
    }

    public void setHour(int hour) {
        if (hour >= 0 && hour < 24) {
            setJewishDate(getJewishYear(), getJewishMonth(), getJewishDayOfMonth(), hour, getTime().getMinutes(), 0);
        }
    }

    public String getYearHebName() {
        return hebrewHebDateFormatter.formatHebrewNumber(getJewishYear());
    }

    public String getYearEnName() {
        return hebrewEnDateFormatter.formatHebrewNumber(getJewishYear());
    }

    public String getHebMonthName() {
        return hebrewHebDateFormatter.formatMonth(this);
    }

    public String getEnMonthName(){
        return hebrewEnDateFormatter.formatMonth(this);
    }

    public boolean isFullMonth() {
        return getDaysInJewishMonth() == 30;
    }

    public int getDaysInPreviousMonth() {
        int year = getJewishYear();
        int month = getJewishMonth() - 1;
        if (month == 6) {
            year--;
        }
        return month != 2 && month != 4 && month != 6 && (month != 8 || isCheshvanLong(year)) && (month != 9 || !isKislevShort(year)) && month != 10 && (month != 12 || isJewishLeapYear(year)) && month != 13 ? 30 : 29;
    }

    private static boolean isCheshvanLong(int year) {
        return getDaysInJewishYear(year) % 10 == 5;
    }

    private static boolean isKislevShort(int year) {
        return getDaysInJewishYear(year) % 10 == 3;
    }

    private static boolean isJewishLeapYear(int year) {
        return (7 * year + 1) % 19 < 7;
    }

    public String getDayLabel() {
        return hebrewHebDateFormatter.formatHebrewNumber(getJewishDayOfMonth());
    }

    public long getBeginOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public long getEndOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }



    protected JewCalendar(Parcel in) {
    }

    public static int getDaysDifference(JewCalendar baseCalendar, JewCalendar compareCalendar) {
        int shift = 0, sign;
        if (baseCalendar.getJewishYear() != compareCalendar.getJewishYear()) {
            sign = baseCalendar.getJewishYear() > compareCalendar.getJewishYear() ? 1 : -1;
            while (baseCalendar.getJewishYear() != compareCalendar.getJewishYear()) {
                compareCalendar.shiftDay(sign);
                shift += sign;
            }
        }
        if (baseCalendar.getJewishMonth() != compareCalendar.getJewishMonth()) {
            sign = baseCalendar.getJewishMonth() > compareCalendar.getJewishMonth() ? 1 : -1;
            while (baseCalendar.getJewishMonth() != compareCalendar.getJewishMonth()) {
                compareCalendar.shiftDay(sign);
                shift += sign;
            }

        }
        if (baseCalendar.getJewishDayOfMonth() != compareCalendar.getJewishDayOfMonth()) {
            sign = baseCalendar.getJewishDayOfMonth() > compareCalendar.getJewishDayOfMonth() ? 1 : -1;
            while (baseCalendar.getJewishDayOfMonth() != compareCalendar.getJewishDayOfMonth()) {
                compareCalendar.shiftDay(sign);
                shift += sign;
            }
        }
        return shift;
    }

    public static int getMonthDifference(JewCalendar baseCalendar, JewCalendar compareCalendar) {
        int shift = 0, sign;
        if (baseCalendar.getJewishYear() != compareCalendar.getJewishYear()) {
            sign = baseCalendar.getJewishYear() > compareCalendar.getJewishYear() ? 1 : -1;
            while (baseCalendar.getJewishYear() != compareCalendar.getJewishYear()) {
                compareCalendar.shiftMonth(sign);
                shift += sign;
            }
        }
        if (baseCalendar.getJewishMonth() != compareCalendar.getJewishMonth()) {
            sign = baseCalendar.getJewishMonth() > compareCalendar.getJewishMonth() ? 1 : -1;
            while (baseCalendar.getJewishMonth() != compareCalendar.getJewishMonth()) {
                compareCalendar.shiftMonth(sign);
                shift += sign;
            }

        }
        return shift;
    }

    public static int getDayOfMonth(long date) {
        JewCalendar jewCalendar = new JewCalendar(new Date(date));
        return jewCalendar.getJewishDayOfMonth();
    }


    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.US);

    public Date getTime(boolean reset) {
        if (reset) {
            Calendar cal = Calendar.getInstance();
            cal.set(this.getGregorianYear(), this.getGregorianMonth(), getGregorianDayOfMonth(), 0, 0, 0);
            return cal.getTime();
        } else {
            return super.getTime();
        }
    }

    public long getBeginOfMonth() {
        JewCalendar copy = (JewCalendar) clone();
        copy.setJewishDate(getJewishYear(), getJewishMonth(), 1);
//        System.out.println(hebrewHebDateFormatter.format(copy));
        Date date = copy.getTime(true);
//        System.out.println(simpleDateFormat.format(date));
        return date.getTime();
    }

    public long getEndOfMonth() {
        JewCalendar copy = (JewCalendar) clone();
        copy.shiftMonthForward();
        copy.setJewishDate(copy.getJewishYear(), copy.getJewishMonth(), 1);
//        System.out.println(hebrewHebDateFormatter.format(copy));
        Date date = copy.getTime(true);
//        System.out.println(simpleDateFormat.format(date));
        return date.getTime();
    }

    public int getLastDayOfGregorianMonth() {
        int year = getGregorianYear();
        switch (getGregorianMonth()) {
            case 2:
                return (year % 4 != 0 || year % 100 == 0) && year % 400 != 0 ? 28 : 29;
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            default:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
        }
    }

    public int getLastDayOfPrevGregorianMonth() {
        int year = getGregorianYear();
        int gregorianMonth = getGregorianMonth() - 1;
        if (gregorianMonth == 0){
            gregorianMonth = 12;
        }
        switch (gregorianMonth) {
            case 2:
                return (year % 4 != 0 || year % 100 == 0) && year % 400 != 0 ? 28 : 29;
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            default:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
        }
    }

    public int monthHashCode() {

        int jewishMonth = getJewishMonth();
        if (jewishMonth <= 6){
            jewishMonth += isJewishLeapYear() ? 7 : 6;
        } else {
            jewishMonth -= 6;
        }
        return (getJewishYear() - 5000) * 100 + jewishMonth;
    }

    public int dayHashCode() {
        return monthHashCode() * 100 + getJewishDayOfMonth();
    }
}