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

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem: " + position);
        return displayState == DISPLAY.MONTH ? FragmentMonth.newInstance(position - INITIAL_OFFSET, shouldShowEvents) : FragmentDay.newInstance(position - INITIAL_OFFSET);
    }


    @Override
    public int getItemPosition(Object object) {
        return dropPages ? POSITION_NONE : super.getItemPosition(object);
    }

    public void setDisplayState(DISPLAY displayState) {
        this.displayState = displayState;
        dropPages = true;
        notifyDataSetChanged();
        dropPages = false;
    }


    public enum DISPLAY {
        MONTH,
        DAY
    }

}