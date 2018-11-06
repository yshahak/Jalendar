package com.thedroidboy.jalendar.model;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by yshahak on 09/10/2016.
 */
@Entity
public class Day implements Parcelable {

    public static final long DAY_IN_MS = TimeUnit.DAYS.toMillis(1);

//    @Ignore
//    private List<EventInstanceForDay> googleEventInstanceForDays = new ArrayList<>();

    @Ignore
    private List<GoogleEvent> googleEventsForDay = new ArrayList<>();

    private final String labelDay;
    private final String labelDayAndMonth;
    @PrimaryKey
    private final int dayHashCode;
    private final long startDayInMillis;
    private final int dayInMonth;
    private final int loazyDayOfMonth;

    @Ignore
    private boolean isOutOfMonthRange;
    @Ignore
    private float cellHeight;

    @Ignore
    private boolean isCurrentDay;

    public Day(int dayHashCode, int dayInMonth, String labelDay, String labelDayAndMonth, long startDayInMillis, int loazyDayOfMonth) {
        this.dayHashCode = dayHashCode;
        this.labelDay = labelDay;
        this.labelDayAndMonth = labelDayAndMonth;
        this.dayInMonth = dayInMonth;
        this.startDayInMillis = startDayInMillis;
        this.loazyDayOfMonth = loazyDayOfMonth;
    }

    public List<GoogleEvent> getGoogleEventsForDay() {
        if (googleEventsForDay == null) {
            googleEventsForDay = new ArrayList<>();
        }
        return googleEventsForDay;
    }

    public int getLoazyDayOfMonth() {
        return loazyDayOfMonth;
    }

    public String getLabelDay() {
        return labelDay;
    }

    public String   getLoaziLabel() {
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

    public String getLabelDayAndMonth() {
        return labelDayAndMonth;
    }

    public boolean isOutOfMonthRange() {
        return isOutOfMonthRange;
    }

    public void setCurrentDay(boolean currentDay) {
        isCurrentDay = currentDay;
    }

    public int getDayHashCode() {
        return dayHashCode;
    }

    public boolean isCurrentDay() {
        return isCurrentDay;
    }

//    public List<EventInstanceForDay> getGoogleEventInstanceForDays() {
//        if (googleEventInstanceForDays == null) {
//            googleEventInstanceForDays = new ArrayList<>();
//        }
//        return googleEventInstanceForDays;
//    }

    public void setCellHeight(float cellHeight) {
        this.cellHeight = cellHeight;
    }

    public int getCellHeight() {
        return (int)cellHeight;
    }

//    public void setHoursEventsMap(SparseArray<Hour> hoursEventsMap) {
//        this.hoursEventsMap = hoursEventsMap;
//    }
//
//    public SparseArray<Hour> getHoursEventsMap() {
//        return hoursEventsMap;
//    }

    @BindingAdapter("android:minHeight")
    public static void setMinHeight(View view, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
    }

    @Override
    public String toString() {
        return "Day{" +
                "\t dayHashCode='" + dayHashCode + '\'' +
                "\t labelDay='" + labelDay + '\'' +
                "\t loazyDay='" + loazyDayOfMonth + '\'' +
                "\t startDayInMillis=" + startDayInMillis +
                "\t dayInMonth=" + dayInMonth +
                "\t isOutOfMonthRange=" + isOutOfMonthRange +
                "\t googleEventInstanceForDays=" + googleEventsForDay +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.googleEventsForDay);
        dest.writeInt(this.dayHashCode);
        dest.writeString(this.labelDay);
        dest.writeString(this.labelDayAndMonth);
        dest.writeLong(this.startDayInMillis);
        dest.writeInt(this.dayInMonth);
        dest.writeInt(this.loazyDayOfMonth);
        dest.writeByte(this.isOutOfMonthRange ? (byte) 1 : (byte) 0);
        dest.writeFloat(this.cellHeight);
    }

    protected Day(Parcel in) {
        this.googleEventsForDay = in.createTypedArrayList(GoogleEvent.CREATOR);
        this.dayHashCode = in.readInt();
        this.labelDay = in.readString();
        this.labelDayAndMonth = in.readString();
        this.startDayInMillis = in.readLong();
        this.dayInMonth = in.readInt();
        this.loazyDayOfMonth = in.readInt();
        this.isOutOfMonthRange = in.readByte() != 0;
        this.cellHeight = in.readFloat();
    }

    @Override
    public int hashCode() {
        return dayHashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Day && dayHashCode == ((Day) obj).dayHashCode;
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