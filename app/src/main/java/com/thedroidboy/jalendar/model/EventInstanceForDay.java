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

import biweekly.ICalVersion;
import biweekly.io.ParseContext;
import biweekly.io.scribe.property.RecurrenceRuleScribe;
import biweekly.parameter.ICalParameters;
import biweekly.property.RecurrenceRule;
import biweekly.util.Frequency;

import static com.thedroidboy.jalendar.calendars.jewish.JewCalendar.hebrewHebDateFormatter;

/**
 * Created by yshahak on 14/10/2016.
 */

public class EventInstanceForDay implements Comparable<EventInstanceForDay>, Parcelable, Cloneable {

    protected long eventId;
    protected long calendarId;
    protected String eventTitle;
    protected long begin, end;
    protected int displayColor;
    protected String calendarDisplayName;
    protected int dayOfMonth;
    protected int repeatValue = -1;
    protected Frequency frequency = null;

    public EventInstanceForDay(long eventId, String eventTitle, long begin, long end, int displayColor, String calendarDisplayName, int dayOfMonth) {
        this.eventId = eventId;
        this.eventTitle = (eventTitle != null && eventTitle.length() > 0) ? eventTitle : "(ללא כותרת)";
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

    public void setCalendarId(long calendarId) {
        this.calendarId = calendarId;
    }

    public long getCalendarId() {
        return calendarId;
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

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public String getRepeatTitle() {

        Context ctx = MyApplication.getInstance();
        if (frequency == null) {
            return ctx.getString(R.string.instance_single);
        }
        switch (frequency) {
            case DAILY:
                return ctx.getString(R.string.instance_daily);
            case WEEKLY:
                return ctx.getString(R.string.instance_weekly);
            case MONTHLY:
                return ctx.getString(R.string.instance_monthly);
            case YEARLY:
                return ctx.getString(R.string.instance_yearly);
        }
        return ctx.getString(R.string.instance_single);
    }

    public void setRepeatValue(int repeatValue) {
        this.repeatValue = repeatValue;
    }

    public int getRepeatValue() {
        if (repeatValue != -1){
            return repeatValue;
        }
        //default values
        switch (frequency) {
            case DAILY:
                return 365;
            case WEEKLY:
                return 36;
            case MONTHLY:
                return 12;
            case YEARLY:
                return 10;
        }
        return 1;
    }

    public boolean getRepeatVisibility(){
        return frequency != null;
    }

    public Frequency getFrequency() {
        return frequency;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.eventId);
        dest.writeLong(this.calendarId);
        dest.writeString(this.eventTitle);
//        dest.writeByte(this.allDayEvent ? (byte) 1 : (byte) 0);
        dest.writeLong(this.begin);
        dest.writeLong(this.end);
        dest.writeInt(this.displayColor);
        dest.writeString(this.calendarDisplayName);
        dest.writeInt(this.dayOfMonth);
        dest.writeInt(this.repeatValue);
        dest.writeString(this.frequency.name());
    }

    protected EventInstanceForDay(Parcel in) {
        this.eventId = in.readLong();
        this.calendarId = in.readLong();
        this.eventTitle = in.readString();
//        this.allDayEvent = in.readByte() != 0;
        this.begin = in.readLong();
        this.end = in.readLong();
        this.displayColor = in.readInt();
        this.calendarDisplayName = in.readString();
        this.dayOfMonth = in.readInt();
        this.repeatValue = in.readInt();
        this.frequency = Frequency.valueOf(in.readString());
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

    private static RecurrenceRuleScribe scribe = new RecurrenceRuleScribe();
    private static ParseContext parseContext = new ParseContext();
    static {
        parseContext.setVersion(ICalVersion.V2_0);
    }

    public void convertRruletoFrequencyAndRepeatValue(String rule){
        if (rule == null) {
            return;
        }
        RecurrenceRule rrule = scribe.parseText(rule, null, new ICalParameters(), parseContext);
        this.frequency = rrule.getValue().getFrequency();
        this.repeatValue = rrule.getValue().getCount() != null ? rrule.getValue().getCount() : -1;
    }
}
