package com.thedroidboy.jalendar.fragments;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.thedroidboy.jalendar.calendars.google.GoogleManager;
import com.thedroidboy.jalendar.databinding.FragmentDayItemBinding;
import com.thedroidboy.jalendar.model.Day;
import com.thedroidboy.jalendar.model.DayVM;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Created by Yaakov Shahak
 * on 20/11/2017.
 */

public class FragmentDay extends Fragment implements PagerAdapterBase.FragmentData, LoaderManager.LoaderCallbacks<List<Day>>, DataChanged {

    private static final String KEY_POSITION = "keyPosition";
    private static final String TAG = FragmentDay.class.getSimpleName();
    private DayVM dayVM;
    private final DataObserver dataObserver;
    @Inject
    CalendarRepo calendarRepo;
    @Inject
    SharedPreferences prefs;
    private FragmentDayItemBinding dayBinding;
    private static SimpleDateFormat simpleFormatter = new SimpleDateFormat("d/M");

    public FragmentDay() {
        dataObserver = new DataObserver(new Handler(), this);
    }
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
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dayBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_day_item, container, false);
        LiveData<Day> dayLiveData = dayVM.getDayLiveData();
        dayLiveData.observe(this, day -> {
            if (day != null) {
                dayBinding.setSingleDay(day);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                dayBinding.dayRecyclerView.setLayoutManager(layoutManager);
                Log.d(TAG, "onCreateView: new day=" + day.toString());
                dayBinding.dayRecyclerView.setAdapter(new RecyclerAdapterDay(day));
                long first = day.getStartDayInMillis();
                long last = day.getEndDayInMillis();
                getActivity().getContentResolver().
                        registerContentObserver(
                                GoogleManager.getInstanceUriForInterval(first, last),
                                true,
                                dataObserver);
                getLoaderManager().initLoader(100, null, this);
            }
        });
        return dayBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dayVM.getDayLiveData().getValue() != null && EasyPermissions.hasPermissions(getContext(), Manifest.permission.READ_CALENDAR)) {
            getLoaderManager().restartLoader(100, null, this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getContentResolver().
                unregisterContentObserver(dataObserver);
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
        Log.d(TAG, "onLoaderReset: ");
    }

    @Override
    public String getFragmentTitle() {
        LiveData<Day> dayLiveData = dayVM.getDayLiveData();
        Day dataValue = dayLiveData.getValue();
        if (dataValue != null) {
            return dataValue.getLabelDayAndMonth() + " " + simpleFormatter.format(dataValue.getStartDayInMillis());
        } else {
            dayLiveData.observe(this, day -> (getActivity()).setTitle(day != null ? day.getLabelDayAndMonth() + " " + simpleFormatter.format(day.getStartDayInMillis()): "..."));
        }
        return "...";
    }

    @Override
    public long getStartDayInMs() {
        LiveData<Day> dayLiveData = dayVM.getDayLiveData();
        if (dayLiveData.getValue() != null) {
            return dayLiveData.getValue().getStartDayInMillis();
        }
        return -1;
    }

    @Override
    public void onDataChanged() {
        getLoaderManager().restartLoader(100, null, this);
    }
}


