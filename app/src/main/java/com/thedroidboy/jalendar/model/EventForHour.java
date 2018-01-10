package com.thedroidboy.jalendar.model;

import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.thedroidboy.jalendar.MyApplication;
import com.thedroidboy.jalendar.R;

/**
 * Created by Yaakov Shahak
 * on 09/01/2018.
 */

public class EventForHour implements Parcelable {


    private static int cellHeight = MyApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.event_in_day);
    public final int startHour, endHour, hour, startMinute, endMinute;
    public final EventInstanceForDay event;
    public int weight;

    public EventForHour(EventInstanceForDay event, int startHour, int endHour, int hour, int startMinute, int endMinute) {
        this.event = event;
        this.hour = hour;
        this.startHour = startHour;
        this.endHour = endHour;
        this.startMinute = startMinute;
        this.endMinute = endMinute;
    }

    public float getCellHeight() {
        if (startHour != endHour && (hour == startHour || hour != endHour)) { //event extends to more than hour and this is first hour or middle hour
            return cellHeight;
        }
        if (hour == startHour) { // just one hour extension of event
            return (cellHeight * ((endMinute - startMinute) / 60f));
        }
        return (cellHeight * (endMinute / 60f)); // more than one hour extension and this is the last hour
    }


    public float getEventTopMargin() {
        if (hour != startHour) {
            return 0;
        }
        return (cellHeight * (startMinute / 60f));
    }

    public String getLabel(){
        return hour == startHour ? event.eventTitle : "";
    }

    public int getBackground(){
        return event.getDisplayColor();
    }

    @BindingAdapter("android:minHeight")
    public static void setMinHeight(View view, float height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) height;
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("android:layout_marginTop")
    public static void setLayoutMarginTop(View view, float margin) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        layoutParams.topMargin = (int) margin;
        view.setLayoutParams(layoutParams);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.startHour);
        dest.writeInt(this.endHour);
        dest.writeInt(this.hour);
        dest.writeInt(this.startMinute);
        dest.writeInt(this.endMinute);
        dest.writeInt(this.weight);
        dest.writeParcelable(this.event, flags);
    }

    protected EventForHour(Parcel in) {
        this.startHour = in.readInt();
        this.endHour = in.readInt();
        this.hour = in.readInt();
        this.startMinute = in.readInt();
        this.endMinute = in.readInt();
        this.weight = in.readInt();
        this.event = in.readParcelable(EventInstanceForDay.class.getClassLoader());
    }

    public static final Parcelable.Creator<EventForHour> CREATOR = new Parcelable.Creator<EventForHour>() {
        @Override
        public EventForHour createFromParcel(Parcel source) {
            return new EventForHour(source);
        }

        @Override
        public EventForHour[] newArray(int size) {
            return new EventForHour[size];
        }
    };
}
