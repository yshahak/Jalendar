package com.thedroidboy.jalendar.fragments;

import android.Manifest;
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
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thedroidboy.jalendar.CalendarRepo;
import com.thedroidboy.jalendar.GoogleEventsLoader;
import com.thedroidboy.jalendar.R;
import com.thedroidboy.jalendar.adapters.PagerAdapterBase;
import com.thedroidboy.jalendar.adapters.RecyclerAdapterDay;
import com.thedroidboy.jalendar.databinding.FragmentDayItemBinding;
import com.thedroidboy.jalendar.model.Day;
import com.thedroidboy.jalendar.model.DayVM;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Created by Yaakov Shahak
 * on 20/11/2017.
 */

public class FragmentDay extends Fragment implements PagerAdapterBase.FragmentData, LoaderManager.LoaderCallbacks<List<Day>> {

    private static final String KEY_POSITION = "keyPosition";
    private static final String TAG = FragmentDay.class.getSimpleName();
    private DayVM dayVM;
    @Inject
    CalendarRepo calendarRepo;
    @Inject
    SharedPreferences prefs;
    private FragmentDayItemBinding dayBinding;
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
        dayVM = ViewModelProviders.of(this).get(DayVM.class);
        dayVM.init(position, calendarRepo);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dayBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_day_item, container, false);
        LiveData<Day> dayLiveData = dayVM.getDayLiveData();
        dayLiveData.observe(this, day -> {
            if (day != null) {
                dayBinding.setSingleDay(day);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                dayBinding.dayRecyclerView.setLayoutManager(layoutManager);
                dayBinding.dayRecyclerView.setAdapter(new RecyclerAdapterDay(day));
                getLoaderManager().initLoader(100, null, this);
            }
        });
        dayBinding.fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", v -> {
                    if (dayLiveData.getValue() != null) {
                        long startDay = dayLiveData.getValue().getStartDayInMillis();
                        Calendar instance = Calendar.getInstance();
                        int hourNow = instance.get(Calendar.HOUR_OF_DAY);
                        int minuteNow = (instance.get(Calendar.MINUTE) / 15) * 15;
                        Intent intent = new Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI)
                                .putExtra(CalendarContract.Events.TITLE, "test")
                                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDay + TimeUnit.HOURS.toMillis(hourNow) + TimeUnit.MINUTES.toMillis(minuteNow));
//                        Intent chooser = Intent.createChooser(intent, "Create an new event");
                        startActivity(intent);
                    }

                }).show());
        return dayBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dayVM.getDayLiveData().getValue() != null && EasyPermissions.hasPermissions(getContext(), Manifest.permission.READ_CALENDAR)) {
            getLoaderManager().initLoader(100, null, this);
        }
    }

    @Override
    public Loader<List<Day>> onCreateLoader(int id, Bundle args) {
        return new GoogleEventsLoader(getContext(), calendarRepo, Collections.singletonList(dayVM.getDayLiveData().getValue()));
    }

    @Override
    public void onLoadFinished(Loader<List<Day>> loader, List<Day> data) {
        if (dayVM.getDayLiveData().getValue() != null) {
            Log.d(TAG, "onLoadFinished day: " + dayVM.getDayLiveData().getValue());
            dayBinding.dayRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Day>> loader) {

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
