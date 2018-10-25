package com.thedroidboy.jalendar.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.thedroidboy.jalendar.fragments.FragmentDay;
import com.thedroidboy.jalendar.fragments.FragmentMonth;

/**
 * Created by Yaakov Shahak
 * on 20/11/2017.
 */

public class PagerAdapterMonthDay extends PagerAdapterBase {


    private static final String TAG = "PagerAdapterMonthDay";
    private DISPLAY displayState = DISPLAY.MONTH;
    private boolean dropPages;
    private final boolean shouldShowEvents;

    public PagerAdapterMonthDay(FragmentManager fm, boolean shouldShowEvents) {
        super(fm);
        this.shouldShowEvents = shouldShowEvents;
    }

    public PagerAdapterMonthDay(FragmentManager fm, boolean shouldShowEvents, DISPLAY display) {
        super(fm);
        this.shouldShowEvents = shouldShowEvents;
        this.displayState = display;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem: " + position);
        if (Math.abs(position) < 3){
            position += INITIAL_OFFSET;
        }
        return displayState == DISPLAY.MONTH ? FragmentMonth.newInstance(position - INITIAL_OFFSET, shouldShowEvents) : FragmentDay.newInstance(position - INITIAL_OFFSET);
    }


    @Override
    public int getItemPosition(Object object) {
        return dropPages ? POSITION_NONE : super.getItemPosition(object);
    }

    @Override
    public void notifyDataSetChanged() {
        dropPages = true;
        super.notifyDataSetChanged();
        dropPages = false;
    }

    public void setDisplayState(DISPLAY displayState) {
        if(displayState != this.displayState) {
            this.displayState = displayState;
            dropPages = true;
            notifyDataSetChanged();
            dropPages = false;
        }
    }

    public DISPLAY getDisplayState() {
        return displayState;
    }

    @Override
    public long onDayDetailsClicked() {
        return 0;
    }

    public enum DISPLAY {
        MONTH,
        DAY
    }

}