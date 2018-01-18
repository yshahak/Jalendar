package com.thedroidboy.jalendar.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.thedroidboy.jalendar.fragments.FragmentDay;

/**
 * Created by Yaakov Shahak
 * on 20/11/2017.
 */

public class PagerAdapterDay extends FragmentPagerAdapter {


    public static final int INITIAL_OFFSET = 1000;
    private static final String TAG = "PagerAdapterMonth";

    public PagerAdapterDay(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem: " + position);
        return FragmentDay.newInstance(position - INITIAL_OFFSET);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE / 2;
    }
}
