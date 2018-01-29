package com.thedroidboy.jalendar.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.thedroidboy.jalendar.MyApplication;
import com.thedroidboy.jalendar.R;
import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.thedroidboy.jalendar.calendars.jewish.JewCalendar.hebrewHebDateFormatter;

/**
 * Created by yshahak on 14/10/2016.
 */

public class EventInstanceForDay implements Comparable<EventInstanceForDay>, Parcelable, Cloneable {

    protected long eventId;
    protected String eventTitle;
    //    protected boolean allDayEvent;
    protected long begin, end;
    protected int displayColor;
    protected String calendarDisplayName;
    protected int dayOfMonth;
    protected Repeat repeatState = Repeat.SINGLE;

    public EventInstanceForDay(long eventId, String eventTitle, long begin, long end, int displayColor, String calendarDisplayName, int dayOfMonth) {
        this.eventId = eventId;
        this.eventTitle = (eventTitle != null && eventTitle.length() > 0) ? eventTitle : "(ללא כותרת)";
//        this.allDayEvent = allDayEvent;
        this.begin = begin;
        this.end = end;
        this.displayColor = displayColor;
        this.calendarDisplayName = calendarDisplayName;
        this.dayOfMonth = dayOfMonth;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public void setBegin(long begin) {
        this.begin = begin;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public void setDisplayColor(int displayColor) {
        this.displayColor = displayColor;
    }

    public void setCalendarDisplayName(String calendarDisplayName) {
        this.calendarDisplayName = calendarDisplayName;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public long getEventId() {
        return eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public boolean isAllDayEvent() {
        return (end - begin) == TimeUnit.DAYS.toMillis(1);
    }

    public long getBegin() {
        return begin;
    }

    public long getEnd() {
        return end;
    }

    public int getDisplayColor() {
        return displayColor;
    }

    public String getCalendarDisplayName() {
        return calendarDisplayName;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public String getEventTime() {
        if (begin == -1) {
            return "";
        }
        return getStartEventHour() + " - " + getEndEventHour();
    }

    public String getStartEventDate() {
        JewCalendar calendar = new JewCalendar(new Date(begin));
        String date = hebrewHebDateFormatter.formatHebrewNumber(calendar.getJewishDayOfMonth()) + " " +
                hebrewHebDateFormatter.formatMonth(calendar);
        return hebrewHebDateFormatter.formatDayOfWeek(calendar) + " , " + date;
    }

    public String getStartEventDateLoazy() {
        return simpleLoazyDateFormat.format(new Date(begin));
    }

    public String getEndEventDate() {
        JewCalendar calendar = new JewCalendar(new Date(end));
        String date = hebrewHebDateFormatter.formatHebrewNumber(calendar.getJewishDayOfMonth()) + " " +
                hebrewHebDateFormatter.formatMonth(calendar);
        return hebrewHebDateFormatter.formatDayOfWeek(calendar) + " , " + date;
    }

    public String getEndEventDateLoazy() {
        return simpleLoazyDateFormat.format(new Date(end));
    }

    public String getStartEventHour() {
        return simpleEventFormat.format(begin);
    }

    public String getEndEventHour() {
        return simpleEventFormat.format(end);
    }

    public void setRepeatState(Repeat repeatState) {
        this.repeatState = repeatState;
    }

    public String getRepeatTitle() {
        Context ctx = MyApplication.getInstance();
        switch (repeatState) {
            case SINGLE:
                return ctx.getString(R.string.instance_single);
            case DAY:
                return ctx.getString(R.string.instance_daily);
            case WEEK:
                return ctx.getString(R.string.instance_weekly);
            case MONTH:
                return ctx.getString(R.string.instance_monthly);
            case YEAR:
                return ctx.getString(R.string.instance_yearly);
        }
        return ctx.getString(R.string.instance_single);
    }

    public int getRepeatValue() {
        switch (repeatState) {
            case SINGLE:
                break;
            case DAY:
                return 365;
            case WEEK:
                return 36;
            case MONTH:
                return 12;
            case YEAR:
                return 10;
        }
        return 1;
    }

    public boolean getRepeatVisibility(){
        return !repeatState.equals(Repeat.SINGLE);
    }

    public Repeat getRepeatState() {
        return repeatState;
    }

    private static SimpleDateFormat simpleLoazyDateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault());
    private static SimpleDateFormat simpleEventFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());


    @Override
    public String toString() {
        return String.format(Locale.US, "\neventId=%d" +
                        "\neventTitle=%s" +
                        "\nbegin=%s" +
                        "\nend=%s",
                eventId, eventTitle, simpleDateFormat.format(begin), simpleDateFormat.format(end));
    }

    @Override
    public int compareTo(@NonNull EventInstanceForDay instance) {
        return (int) (this.begin - instance.begin);
    }

    public enum Repeat {
        SINGLE,
        DAY,
        WEEK,
        MONTH,
        YEAR
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.eventId);
        dest.writeString(this.eventTitle);
//        dest.writeByte(this.allDayEvent ? (byte) 1 : (byte) 0);
        dest.writeLong(this.begin);
        dest.writeLong(this.end);
        dest.writeInt(this.displayColor);
        dest.writeString(this.calendarDisplayName);
        dest.writeInt(this.dayOfMonth);
        dest.writeString(this.repeatState.name());
    }

    protected EventInstanceForDay(Parcel in) {
        this.eventId = in.readLong();
        this.eventTitle = in.readString();
//        this.allDayEvent = in.readByte() != 0;
        this.begin = in.readLong();
        this.end = in.readLong();
        this.displayColor = in.readInt();
        this.calendarDisplayName = in.readString();
        this.dayOfMonth = in.readInt();
        this.repeatState = Repeat.valueOf(in.readString());
    }

    public static final Parcelable.Creator<EventInstanceForDay> CREATOR = new Parcelable.Creator<EventInstanceForDay>() {
        @Override
        public EventInstanceForDay createFromParcel(Parcel source) {
            return new EventInstanceForDay(source);
        }

        @Override
        public EventInstanceForDay[] newArray(int size) {
            return new EventInstanceForDay[size];
        }
    };

    @Override
    public EventInstanceForDay clone() throws CloneNotSupportedException {
        return (EventInstanceForDay) super.clone();
    }
}
