package com.thedroidboy.jalendar.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thedroidboy.jalendar.R;
import com.thedroidboy.jalendar.activities.CreteIvriEventActivity;
import com.thedroidboy.jalendar.activities.DayDetailsActivity;
import com.thedroidboy.jalendar.adapters.PagerAdapterBase;
import com.thedroidboy.jalendar.adapters.PagerAdapterMonthDay;
import com.thedroidboy.jalendar.utils.Constants;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pager_holder, container, false);
        viewPager = rootView.findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(this);
        PagerAdapterMonthDay.DISPLAY display = PagerAdapterMonthDay.DISPLAY.valueOf(getArguments().getString("STATE", "MONTH"));
        initViewPager(display);
        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            PagerAdapterBase adapter = (PagerAdapterBase) viewPager.getAdapter();
            if (adapter != null) {
                long startDay = adapter.getStartDayInMs(viewPager.getCurrentItem());
                if (startDay != -1) {
                    Calendar instance = Calendar.getInstance();
                    int hourNow = instance.get(Calendar.HOUR_OF_DAY);
                    int minuteNow = (instance.get(Calendar.MINUTE) / 15) * 15;
                    Intent intent = new Intent(getContext(), CreteIvriEventActivity.class)
                            .putExtra(CalendarContract.Events.TITLE, "test")
                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDay + TimeUnit.HOURS.toMillis(hourNow) + TimeUnit.MINUTES.toMillis(minuteNow));
                    startActivity(intent);
                    //Intent chooser = Intent.createChooser(intent, "Create an new event");
//                    return new Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI)
//                            .putExtra(CalendarContract.Events.TITLE, "test")
//                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDay + TimeUnit.HOURS.toMillis(hourNow) + TimeUnit.MINUTES.toMillis(minuteNow));
                }
            }
        });
        return rootView;
    }

    private void initViewPager(PagerAdapterMonthDay.DISPLAY display) {
        viewPager.setAdapter(new PagerAdapterMonthDay(getChildFragmentManager(), true, display));
        viewPager.setCurrentItem(PagerAdapterMonthDay.INITIAL_OFFSET);
        viewPager.post(() -> onPageSelected(PagerAdapterMonthDay.INITIAL_OFFSET));
    }

    public void refresh() {
        viewPager.getAdapter().notifyDataSetChanged();
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

    public void startDayDetailsActivity() {
        final PagerAdapterMonthDay adapter = (PagerAdapterMonthDay) viewPager.getAdapter();
        if (adapter != null) {
            long startDay = adapter.getStartDayInMs(viewPager.getCurrentItem());
            if (startDay != -1) {
                Calendar instance = Calendar.getInstance();
                int hourNow = instance.get(Calendar.HOUR_OF_DAY);
                Intent intent = new Intent(getContext(), DayDetailsActivity.class)
                        .putExtra(Constants.KEY_TIME, startDay + TimeUnit.HOURS.toMillis(hourNow));
                startActivity(intent);
            } else {
                startActivity(new Intent(getActivity(), DayDetailsActivity.class));
            }
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        PagerAdapterBase adapter = (PagerAdapterBase) viewPager.getAdapter();
        if (getActivity() != null && adapter != null) {
            viewPager.post(() -> getActivity().setTitle(adapter.getPageTitle(position)));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
