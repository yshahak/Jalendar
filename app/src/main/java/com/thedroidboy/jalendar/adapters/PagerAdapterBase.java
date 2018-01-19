package com.thedroidboy.jalendar.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.thedroidboy.jalendar.R;
import com.thedroidboy.jalendar.fragments.FragmentDay;
import com.thedroidboy.jalendar.fragments.FragmentMonth;

/**
 * Created by Yaakov Shahak
 * on 20/11/2017.
 */

public class PagerAdapterBase extends FragmentPagerAdapter {

    private static final String TAG = "PagerAdapterBase";

    public static final int INITIAL_OFFSET = 1000;
    private boolean dropPages;
    private DISPLAY displayState = DISPLAY.MONTH;
    private final FragmentManager mFragmentManager;

    public PagerAdapterBase(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem: " + position);
        return displayState == DISPLAY.MONTH ? FragmentMonth.newInstance(position - INITIAL_OFFSET) : FragmentDay.newInstance(position - INITIAL_OFFSET);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE / 2;
    }

    @Override
    public String getPageTitle(int position) {
        // Do we already have this fragment?
        String name = makeFragmentName(R.id.view_pager, position);
        FragmentTitle fragmentByTag = (FragmentTitle) mFragmentManager.findFragmentByTag(name);
        if (fragmentByTag != null) {
            return fragmentByTag.getFragmentTitle();
        }
        return "fragment not known";
    }

    @Override
    public int getItemPosition(Object object) {
        int i = dropPages ? POSITION_NONE : super.getItemPosition(object);
//        if (dropPages){
//            mFragmentManager.beginTransaction().detach((Fragment)object).commit();
//        }
        return i;
    }

    public void setDisplayState(DISPLAY displayState) {
        this.displayState = displayState;
        dropPages = true;
        notifyDataSetChanged();
        dropPages = false;
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    public interface FragmentTitle {
        String getFragmentTitle();
    }

    public enum DISPLAY {
        MONTH,
        DAY
    }

}
