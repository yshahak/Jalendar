package com.thedroidboy.jalendar.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thedroidboy.jalendar.CalendarRepo;
import com.thedroidboy.jalendar.R;
import com.thedroidboy.jalendar.adapters.PagerAdapterBase;
import com.thedroidboy.jalendar.adapters.RecyclerAdapterDay;
import com.thedroidboy.jalendar.databinding.FragmentDayItemBinding;
import com.thedroidboy.jalendar.model.Day;
import com.thedroidboy.jalendar.model.DayVM;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;


/**
 * Created by Yaakov Shahak
 * on 20/11/2017.
 */

public class FragmentDay extends Fragment implements PagerAdapterBase.FragmentTitle {

    private static final String KEY_POSITION = "keyPosition";
    private static final String TAG = FragmentDay.class.getSimpleName();
    private DayVM dayVM;
    @Inject
    CalendarRepo calendarRepo;
    @Inject
    SharedPreferences prefs;
//    private int currentDayOfMonth = -1;

    public static FragmentDay newInstance(int position) {
        FragmentDay fragmentDay = new FragmentDay();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_POSITION, position);
        fragmentDay.setArguments(bundle);
        return fragmentDay;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);
        super.onCreate(savedInstanceState);
        int position = getArguments().getInt(KEY_POSITION);
//        if (position == 0){
//            JewCalendar jewCalendar = JewCalendarPool.obtain(0);
//            currentDayOfMonth = jewCalendar.dayHashCode();
//        }
        dayVM = ViewModelProviders.of(this).get(DayVM.class);
        dayVM.init(position, calendarRepo);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentDayItemBinding dayBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_day_item, container, false);
        LiveData<Day> dayLiveData = dayVM.getDayLiveData();
        dayLiveData.observe(this, day -> {
            if (day != null) {
                dayBinding.setSingleDay(day);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                dayBinding.dayRecyclerView.setLayoutManager(layoutManager);
                dayBinding.dayRecyclerView.setAdapter(new RecyclerAdapterDay(day));
            }
        });
        dayBinding.fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", v -> {
                    if (dayLiveData.getValue() != null) {
                        long startDay = dayLiveData.getValue().getStartDayInMillis();
                        int hourNow = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                        Intent intent = new Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI)
                                .putExtra(CalendarContract.Events.TITLE, "")
                                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDay + TimeUnit.HOURS.toMillis(hourNow));
//                        Intent chooser = Intent.createChooser(intent, "Create an new event");
                        startActivity(intent);
                    }

                }).show());
        return dayBinding.getRoot();
    }

    @Override
    public String getFragmentTitle() {
        LiveData<Day> dayLiveData = dayVM.getDayLiveData();
        if (dayLiveData.getValue() != null) {
            return dayLiveData.getValue().getLabelDayAndMonth();
        } else {
            dayLiveData.observe(this, day -> (getActivity()).setTitle(day != null ? day.getLabelDayAndMonth() : "day is null"));
        }
        return "day not known";
    }
}