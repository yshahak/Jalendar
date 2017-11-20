package com.thedroidboy.jalendar.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.thedroidboy.jalendar.fragments.FragmentMonth;

/**
 * Created by Yaakov Shahak
 * on 20/11/2017.
 */

public class PagerAdapterMonth extends FragmentStatePagerAdapter {


    public static final int INITIAL_OFFSET = 1000;

    public PagerAdapterMonth(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return FragmentMonth.newInstance(position - INITIAL_OFFSET);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }
}
