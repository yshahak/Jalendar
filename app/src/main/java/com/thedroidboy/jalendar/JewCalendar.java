package com.thedroidboy.jalendar;

import android.graphics.Color;
import android.os.Parcel;

import com.thedroidboy.jalendar.model.Day;

import net.sourceforge.zmanim.hebrewcalendar.HebrewDateFormatter;
import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by yshahak
 * on 12/28/2016.
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class JewCalendar extends JewishCalendar {

    public static HebrewDateFormatter hebrewDateFormatter = new HebrewDateFormatter();
    private static JewCalendar calculatorCalendar = new JewCalendar();
    static {
        hebrewDateFormatter.setHebrewFormat(true);
    }
    private List<Day> dayList = new ArrayList<>();

    private int headOffset, trailOffset;

    private int oldPosition;
    private boolean isRecycled;
    public boolean flagCurrentMonth;

    public JewCalendar(int jewishYear, int jewishMonth, int day) {
        super(jewishYear, jewishMonth, day);
    }

    public JewCalendar(Calendar calendar) {
        super(calendar);
    }

    public boolean isRecycled() {
        return isRecycled;
    }

    public JewCalendar(int offset) {
        shiftMonth(offset);
    }

    public JewCalendar(Date date) {
        super(date);
        setOffsets();
    }

    public JewCalendar() {
        setOffsets();
    }

    public JewCalendar shiftMonth(int position) {
        int offset = position - oldPosition;
        if (offset > 0) {
            for (int i = 0; i < offset; i++) {
                shiftMonthForward();
            }
        } else if (offset < 0) {
            for (int i = offset * (-1); i > 0; i--) {
                shiftMonthBackword();
            }
        } else {
            flagCurrentMonth = true;
        }
//        setOffsets();
//        setMonthDays();
        oldPosition = position;
        return this;
    }
    public void shiftDay(int offset) {
        if (offset > 0) {
            for (int i = 0; i < offset; i++) {
                shiftDayForward();
            }
        } else if (offset < 0) {
            for (int i = offset * (-1); i > 0; i--) {
                shiftDayBackword();
            }
        }
    }

    private void shiftMonthForward(int offset) {
        int currentMonth = getJewishMonth();
        int next = getJewishMonth() + 1;
        if (next == 7) {
            setJewishYear(getJewishYear() + 1);
        } else if (next == 14 || (next == 13 && !isJewishLeapYear())) {
            next = 1;
        }
        setJewishMonth(next);
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

    private void shiftDayForward() {
        int next = getJewishDayOfMonth() + 1;
        if (next == 31 || (next == 30 && !isFullMonth())) {
            next = 1;
            shiftMonthForward();
        }
        setJewishDayOfMonth(next);
    }

    private void shiftDayBackword() {
        int previous = getJewishDayOfMonth() - 1;
        if (previous == 0) {
            shiftMonthBackword();
            previous = isFullMonth() ? 30 : 29;
        }
        setJewishDayOfMonth(previous);
    }


    public void setOffsets() {
        { //calculate head
            int dayInMonth = getJewishDayOfMonth() % 7;
            Date date = getTime();
            date.setTime(date.getTime() - 1000 * 60 * 60 * 24 * (--dayInMonth));
            JewishCalendar mockCalendar = new JewishCalendar(date);
            int dayInWeek = mockCalendar.getDayOfWeek();
            headOffset = --dayInWeek;
        }
        {//calculate trail
            JewishCalendar mock = new JewishCalendar(getTime());
            mock.setJewishDayOfMonth(isFullMonth() ? 30 : 29);
            int dayOfWeek = mock.getDayOfWeek();
            trailOffset = 7 - dayOfWeek;
        }
//        Log.d("TAG", getMonthName() +  ", headOffset:" + headOffset + ", trailOffset:" + trailOffset);
    }

    public void setHour(int hour) {
        if (hour >= 0 && hour < 24) {
            setJewishDate(getJewishYear(), getJewishMonth(), getJewishDayOfMonth(), hour, getTime().getMinutes(), 0);
        }
    }

    public String getYearName() {
        return hebrewDateFormatter.formatHebrewNumber(getJewishYear());
    }

    public String getMonthName() {
        return hebrewDateFormatter.formatMonth(this);
    }

    public boolean isFullMonth() {
        return getDaysInJewishMonth() == 30;
    }

    public int getHeadOffset() {
        return headOffset;
    }

    public int getTrailOffset() {
        return trailOffset;
    }

    public String getDayLabel() {
        return hebrewDateFormatter.formatHebrewNumber(getJewishDayOfMonth());
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

    public List<Day> getMonthDays() {
        return dayList;
    }

    public void setMonthDays() {
        dayList.clear();
        JewCalendar copy = this;
        copy.setJewishDayOfMonth(1);
        copy.shiftDay(copy.getHeadOffset()*(-1));
        for (int i = 0; i < copy.getHeadOffset(); i++) {
            Day day = new Day(copy);
            day.setOutOfMonthRange(true);
            day.setBackgroundColor(Color.GRAY);
            copy.shiftDay(1);
            if (dayList.size() ==0 ){
                day.setBeginAndEnd(copy);
            } else {
                day.setBeginAndEnd(dayList.get(dayList.size() - 1));
            }
            dayList.add(day);
        }
        int daysSum = copy.getDaysInJewishMonth();
        for (int i = 1; i <= daysSum; i++) {
            copy.setJewishDayOfMonth(i);
            Day day = new Day(copy);
            if (dayList.size() ==0 ){
                day.setBeginAndEnd(copy);
            } else {
                day.setBeginAndEnd(dayList.get(dayList.size() - 1));
            }
            dayList.add(day);
        }
        copy.shiftDay(1);
        for (int i = 0; i < copy.getTrailOffset(); i++){
            Day day = new Day(copy);
            day.setOutOfMonthRange(true);
            day.setBeginAndEnd(dayList.get(dayList.size() - 1));
            copy.shiftDay(1);
            dayList.add(day);
            day.setBackgroundColor(Color.GRAY);
        }
        copy.shiftMonthBackword();
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

    public Object clone() {
        JewCalendar clone;
        clone = (JewCalendar) super.clone();
        clone.headOffset = this.headOffset;
        clone.trailOffset = this.trailOffset;
        return clone;
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
//        System.out.println(hebrewDateFormatter.format(copy));
        Date date = copy.getTime(true);
//        System.out.println(simpleDateFormat.format(date));
        return date.getTime();
    }

    public long getEndOfMonth() {
        JewCalendar copy = (JewCalendar) clone();
        copy.shiftMonthForward(1);
        copy.setJewishDate(copy.getJewishYear(), copy.getJewishMonth(), 1);
//        System.out.println(hebrewDateFormatter.format(copy));
        Date date = copy.getTime(true);
//        System.out.println(simpleDateFormat.format(date));
        return date.getTime();
    }


    public int monthHashCode() {
        return (getJewishYear() - 5700) * 1000 + getJewishMonth() * 100;
    }
}