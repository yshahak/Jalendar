package com.thedroidboy.jalendar.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thedroidboy.jalendar.R;
import com.thedroidboy.jalendar.adapters.PagerAdapterBase;
import com.thedroidboy.jalendar.adapters.PagerAdapterMonthDay;

/**
 * Created by Yaakov Shahak
 * on 13/02/2018.
 */
public class PagerFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private ViewPager viewPager;

    public static PagerFragment newInstance(PagerAdapterMonthDay.DISPLAY display) {
        Bundle args = new Bundle();
        args.putString("STATE", display.name());
        PagerFragment fragment = new PagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pager_holder, container, false);
        viewPager = rootView.findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(this);
        PagerAdapterMonthDay.DISPLAY display = PagerAdapterMonthDay.DISPLAY.valueOf(getArguments().getString("STATE", "MONTH"));
        initViewPager(display);
        return rootView;
    }

    private void initViewPager(PagerAdapterMonthDay.DISPLAY display) {
        viewPager.setAdapter(new PagerAdapterMonthDay(getChildFragmentManager(), true, display));
        viewPager.setCurrentItem(PagerAdapterMonthDay.INITIAL_OFFSET);
        viewPager.post(() -> onPageSelected(PagerAdapterMonthDay.INITIAL_OFFSET));
    }

    public void shiftToPosition(int position) {
        if (position != 0) {
            new Handler().post(() -> {
                int newPosition = viewPager.getCurrentItem() + position;
                viewPager.setCurrentItem(newPosition);
                viewPager.post(() -> onPageSelected(newPosition));
            });
        }
    }

    public void movePagerToPosition(int position) {
        int current = viewPager.getCurrentItem();
        int sign = current > position ? -1 : 1;
        while (viewPager.getCurrentItem() != position) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + sign, true);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        PagerAdapterBase adapter = (PagerAdapterBase) viewPager.getAdapter();
        getActivity().setTitle(adapter.getPageTitle(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
