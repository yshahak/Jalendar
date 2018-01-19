package com.thedroidboy.jalendar.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.thedroidboy.jalendar.fragments.FragmentMonth;

/**
 * Created by Yaakov Shahak
 * on 20/11/2017.
 */

public class PagerAdapterMonth extends PagerAdapterBase {


    private static final String TAG = "PagerAdapterMonth";

    public PagerAdapterMonth(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem: " + position);
        FragmentMonth fragmentMonth = FragmentMonth.newInstance(position - INITIAL_OFFSET);
        return fragmentMonth;
    }
}