package com.thedroidboy.jalendar.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.thedroidboy.jalendar.fragments.FragmentDay;

/**
 * Created by Yaakov Shahak
 * on 20/11/2017.
 */

public class PagerAdapterDay extends PagerAdapterBase {


    private static final String TAG = "PagerAdapterDay";


    public PagerAdapterDay(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem: " + position);
        FragmentDay fragmentDay = FragmentDay.newInstance(position - INITIAL_OFFSET);
        return fragmentDay;
    }
}
