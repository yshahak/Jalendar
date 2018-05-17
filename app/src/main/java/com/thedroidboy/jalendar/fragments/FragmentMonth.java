package com.thedroidboy.jalendar.fragments;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.thedroidboy.jalendar.CalendarRepo;
import com.thedroidboy.jalendar.GoogleEventsLoader;
import com.thedroidboy.jalendar.R;
import com.thedroidboy.jalendar.adapters.PagerAdapterBase;
import com.thedroidboy.jalendar.calendars.google.GoogleManager;
import com.thedroidboy.jalendar.calendars.jewish.JewCalendarPool;
import com.thedroidboy.jalendar.databinding.FragmentMonthItemBinding;
import com.thedroidboy.jalendar.model.Day;
import com.thedroidboy.jalendar.model.Month;
import com.thedroidboy.jalendar.model.MonthVM;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Created by Yaakov Shahak
 * on 20/11/2017.
 */

public class FragmentMonth extends Fragment implements LoaderManager.LoaderCallbacks<List<Day>>, PagerAdapterBase.FragmentData, DataChanged {

    private static final String KEY_POSITION = "keyPosition";
    private static final String KEY_SHOW_EVENTS = "keyShowEvents";
    private static final String TAG = FragmentMonth.class.getSimpleName();
    private MonthVM monthVM;
    private FragmentMonthItemBinding binding;
    private final DataObserver dataObserver;
    @Inject
    CalendarRepo calendarRepo;
    @Inject
    SharedPreferences prefs;
    private int currentDayOfMonth = -1;
    private float cellHeight;
    private boolean shouldShowEvents;

    public FragmentMonth() {
        this.dataObserver = new DataObserver(new Handler(), this);
    }


    public static FragmentMonth newInstance(int position, boolean shouldShowEvents) {
        FragmentMonth fragmentMonth = new FragmentMonth();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_POSITION, position);
        bundle.putBoolean(KEY_SHOW_EVENTS, shouldShowEvents);
        fragmentMonth.setArguments(bundle);
        return fragmentMonth;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);
        super.onCreate(savedInstanceState);
        int position = getArguments().getInt(KEY_POSITION);
        Log.d(TAG, "onCreate: " + position);
        shouldShowEvents = getArguments().getBoolean(KEY_SHOW_EVENTS);
        if (position == 0) {
            currentDayOfMonth = JewCalendarPool.obtain(position).dayHashCode();
        }
        monthVM = ViewModelProviders.of(this).get(MonthVM.class);
        monthVM.init(position, calendarRepo);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_month_item, container, false);
        LiveData<Month> monthLiveData = monthVM.getMonth();
        monthLiveData.observe(this, month -> {
            if (month != null) {
                binding.setMonth(month);
                bindMonth(binding);
                if (shouldShowEvents && EasyPermissions.hasPermissions(getContext(), Manifest.permission.READ_CALENDAR)) {
                    List<Day> dayList = month.getDayList();
                    long first = dayList.get(0).getStartDayInMillis();
                    long last = dayList.get(dayList.size() - 1).getEndDayInMillis();
                    getActivity().getContentResolver().
                            registerContentObserver(
                                    GoogleManager.getInstanceUriForInterval(first, last),
                                    true,
                                    dataObserver);
                    getLoaderManager().initLoader(100, null, this);
                }
            }
        });
        getCellHeight();
        Log.d(TAG, "onCreateView: " + getArguments().getInt(KEY_POSITION));
        return binding.getRoot();
    }

    private float getCellHeight() {
//        float cellHeight = prefs.getFloat("cellHeight", 0);
        if (cellHeight == 0) {
            binding.monthContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    binding.monthContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int height = binding.monthContainer.getMeasuredHeight();
                    cellHeight = height / 6f;
//                    prefs.edit().putFloat("cellHeight", cellHeight).apply();
                    bindMonth(binding);
                }
            });
        }
        return cellHeight;
    }

    private void bindMonth(FragmentMonthItemBinding binding) {
        int position = 0;
        List<Day> dayList;
        float cellHeight = getCellHeight();
        if (monthVM.getMonth().getValue() != null) {
            dayList = monthVM.getMonth().getValue().getDayList();
            binding.week1.bindDays(dayList.subList(position, position += 7), cellHeight, currentDayOfMonth);
            binding.week2.bindDays(dayList.subList(position, position += 7), cellHeight, currentDayOfMonth);
            binding.week3.bindDays(dayList.subList(position, position += 7), cellHeight, currentDayOfMonth);
            binding.week4.bindDays(dayList.subList(position, position += 7), cellHeight, currentDayOfMonth);
            binding.week5.bindDays(dayList.subList(position, position += 7), cellHeight, currentDayOfMonth);
            binding.week6.bindDays(dayList.subList(position, position += 7), cellHeight, currentDayOfMonth);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (monthVM.getMonth().getValue() != null && EasyPermissions.hasPermissions(getContext(), Manifest.permission.READ_CALENDAR)) {
            getLoaderManager().initLoader(100, null, this);
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
        return new GoogleEventsLoader(getContext(), calendarRepo, monthVM.getMonth().getValue().getDayList());
    }

    @Override
    public void onLoadFinished(Loader<List<Day>> loader, List<Day> data) {
//        Log.d(TAG, "onLoadFinished: ");
        if (monthVM.getMonth().getValue() != null) {
            //trying to avoid CurrentModification exception when looping over the data
            binding.getRoot().post(() -> {
                monthVM.getMonth().getValue().setDayList(data);
                bindMonth(binding);
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Day>> loader) {
//        Log.d(TAG, "onLoaderReset:");
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public String getFragmentTitle() {
        LiveData<Month> monthLiveData = monthVM.getMonth();
        if (monthLiveData.getValue() != null) {
            return monthLiveData.getValue().getMonthHebLabel();
        } else {
            monthLiveData.observe(this, month -> {
                try {
                    (getActivity()).setTitle(month.getMonthHebLabel());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return "...";
    }

    @Override
    public long getStartDayInMs() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime();
    }

    @Override
    public void onDataChanged() {
        getLoaderManager().restartLoader(100, null, this);
    }
}

interface DataChanged {
    void onDataChanged();
}

class DataObserver extends ContentObserver {

    private final DataChanged dataChanged;

    public DataObserver(Handler handler, DataChanged dataChanged) {
        super(handler);
        this.dataChanged = dataChanged;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        if (!selfChange){
            dataChanged.onDataChanged();
        }
    }
}