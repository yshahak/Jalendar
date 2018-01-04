package com.thedroidboy.jalendar.model;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.thedroidboy.jalendar.calendars.google.EventInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by yshahak on 09/10/2016.
 */
@Entity
public class Day implements Parcelable {

    public static final long DAY_IN_MS = TimeUnit.DAYS.toMillis(1);

    @Ignore
    private transient List<EventInstance> googleEventInstances = new ArrayList<>();

    private final String label;
    @PrimaryKey
    private final long startDayInMillis;
    private final int dayInMonth;
    private final int loazyDayOfMonth;

    @Ignore
    private boolean isOutOfMonthRange;
    @Ignore
    private float cellHeight;
    @Ignore
    private transient SparseArray<Hour> hoursEventsMap;

//    @Ignore
//    private int backgroundColor = Color.TRANSPARENT;

    public Day(int dayInMonth, String label, long startDayInMillis,int loazyDayOfMonth) {
        this.label = label;
        this.dayInMonth = dayInMonth;
        this.startDayInMillis = startDayInMillis;
        this.loazyDayOfMonth = loazyDayOfMonth;
    }

    public int getLoazyDayOfMonth() {
        return loazyDayOfMonth;
    }

    public String getLabel() {
        return label;
    }

    public String getLoaziLabel() {
        return Integer.toString(loazyDayOfMonth);
    }

    public int getLabelColor(){
        return isOutOfMonthRange ? Color.LTGRAY : Color.BLACK;
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
        if (googleEventInstances == null) {
            googleEventInstances = new ArrayList<>();
        }
        return googleEventInstances;
    }

    public void setCellHeight(float cellHeight) {
        this.cellHeight = cellHeight;
    }

    public int getCellHeight() {
        return (int)cellHeight;
    }

    public void setHoursEventsMap(SparseArray<Hour> hoursEventsMap) {
        this.hoursEventsMap = hoursEventsMap;
    }

    public SparseArray<Hour> getHoursEventsMap() {
        return hoursEventsMap;
    }

    @BindingAdapter("android:minHeight")
    public static void setMinHeight(View view, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
    }

    @Override
    public String toString() {
        return "Day{" +
                "\t label='" + label + '\'' +
                "\t loazyDay='" + loazyDayOfMonth + '\'' +
                "\t startDayInMillis=" + startDayInMillis +
                "\t dayInMonth=" + dayInMonth +
                "\t isOutOfMonthRange=" + isOutOfMonthRange +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.label);
        dest.writeLong(this.startDayInMillis);
        dest.writeInt(this.dayInMonth);
        dest.writeInt(this.loazyDayOfMonth);
        dest.writeByte(this.isOutOfMonthRange ? (byte) 1 : (byte) 0);
        dest.writeFloat(this.cellHeight);
        dest.writeSparseArray((SparseArray) this.hoursEventsMap);
    }

    protected Day(Parcel in) {
        this.label = in.readString();
        this.startDayInMillis = in.readLong();
        this.dayInMonth = in.readInt();
        this.loazyDayOfMonth = in.readInt();
        this.isOutOfMonthRange = in.readByte() != 0;
        this.cellHeight = in.readFloat();
        this.hoursEventsMap = in.readSparseArray(Hour.class.getClassLoader());
    }

    public static final Parcelable.Creator<Day> CREATOR = new Parcelable.Creator<Day>() {
        @Override
        public Day createFromParcel(Parcel source) {
            return new Day(source);
        }

        @Override
        public Day[] newArray(int size) {
            return new Day[size];
        }
    };
}