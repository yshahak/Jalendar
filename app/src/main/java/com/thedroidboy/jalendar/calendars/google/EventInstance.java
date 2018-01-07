package com.thedroidboy.jalendar.calendars.google;

import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.thedroidboy.jalendar.MyApplication;
import com.thedroidboy.jalendar.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by yshahak on 14/10/2016.
 */

public class EventInstance implements Comparable<EventInstance>,Parcelable {

    private long eventId;
    private String eventTitle;
    private boolean allDayEvent;
    private long begin, end;
    private int displayColor;
    private String calendarDisplayName;
    private int dayOfMonth;
    private Date beginDate, endDate;
    private int parallelEventsCount;


    public EventInstance(long eventId, String eventTitle, boolean allDayEvent, long begin, long end, int displayColor, String calendarDisplayName) {
        this.eventId = eventId;
        this.eventTitle = (eventTitle != null && eventTitle.length() > 0) ? eventTitle : "(ללא כותרת)";
        this.allDayEvent = allDayEvent;
        this.begin = begin;
        this.end = end;
        this.displayColor = displayColor;
        this.calendarDisplayName = calendarDisplayName;
    }

    public EventInstance(long eventId, String eventTitle, boolean allDayEvent, long begin, long end, int displayColor, String calendarDisplayName, int dayOfMonth) {
        this.eventId = eventId;
        this.eventTitle = (eventTitle != null && eventTitle.length() > 0) ? eventTitle : "(ללא כותרת)";
        this.allDayEvent = allDayEvent;
        this.begin = begin;
        this.end = end;
        this.displayColor = displayColor;
        this.calendarDisplayName = calendarDisplayName;
        this.dayOfMonth = dayOfMonth;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public long getEventId() {
        return eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public boolean isAllDayEvent() {
        return allDayEvent;
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

    public Date getBeginDate() {
        return beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setParallelEventsCount(int parallelEventsCount) {
        this.parallelEventsCount = parallelEventsCount;
    }

    public int getParallelEventsCount() {
        return parallelEventsCount;
    }
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault());

    public float getCellHeight() {
        int cell = MyApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.event_in_day);
        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(end - begin);
        if (minutes >= 60){
            return cell;
        }
        return (cell * (minutes / 60f));
    }

    @BindingAdapter("android:minHeight")
    public static void setMinHeight(View view, float height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height =(int)height;
        view.setLayoutParams(layoutParams);
    }

    public float getEventTopMargin() {

        int cell = MyApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.event_in_day);
        int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(begin) % 60);
        return (cell * (minutes / 60f));
    }

    @BindingAdapter("android:layout_marginTop")
    public static void setLayoutMarginTop(View view, float margin) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        layoutParams.topMargin = (int) margin;
        view.setLayoutParams(layoutParams);
    }

    @Override
    public String toString() {
        String result = String.format(Locale.US, "\neventId=%d" +
                "\neventTitle=%s" +
                "\nbegin=%s" +
                "\nend=%s",
                eventId,eventTitle, simpleDateFormat.format(begin), simpleDateFormat.format(end));
        return result;
    }

    @Override
    public int compareTo(@NonNull EventInstance instance) {
        return (int) (this.begin - instance.begin);
    }

    public enum Repeat{
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
        dest.writeByte(this.allDayEvent ? (byte) 1 : (byte) 0);
        dest.writeLong(this.begin);
        dest.writeLong(this.end);
        dest.writeInt(this.displayColor);
        dest.writeString(this.calendarDisplayName);
        dest.writeInt(this.dayOfMonth);
        dest.writeLong(this.beginDate != null ? this.beginDate.getTime() : -1);
        dest.writeLong(this.endDate != null ? this.endDate.getTime() : -1);
        dest.writeInt(this.parallelEventsCount);
    }

    protected EventInstance(Parcel in) {
        this.eventId = in.readLong();
        this.eventTitle = in.readString();
        this.allDayEvent = in.readByte() != 0;
        this.begin = in.readLong();
        this.end = in.readLong();
        this.displayColor = in.readInt();
        this.calendarDisplayName = in.readString();
        this.dayOfMonth = in.readInt();
        long tmpBeginDate = in.readLong();
        this.beginDate = tmpBeginDate == -1 ? null : new Date(tmpBeginDate);
        long tmpEndDate = in.readLong();
        this.endDate = tmpEndDate == -1 ? null : new Date(tmpEndDate);
        this.parallelEventsCount = in.readInt();
    }

    public static final Parcelable.Creator<EventInstance> CREATOR = new Parcelable.Creator<EventInstance>() {
        @Override
        public EventInstance createFromParcel(Parcel source) {
            return new EventInstance(source);
        }

        @Override
        public EventInstance[] newArray(int size) {
            return new EventInstance[size];
        }
    };
}
