package com.thedroidboy.jalendar.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.thedroidboy.jalendar.GoogleEventsLoader;
import com.thedroidboy.jalendar.MonthRepo;
import com.thedroidboy.jalendar.R;
import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;
import com.thedroidboy.jalendar.calendars.jewish.JewCalendarPool;
import com.thedroidboy.jalendar.databinding.MonthItemBinding;
import com.thedroidboy.jalendar.model.Day;
import com.thedroidboy.jalendar.model.Month;
import com.thedroidboy.jalendar.model.MonthVM;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;


/**
 * Created by Yaakov Shahak
 * on 20/11/2017.
 */

public class FragmentMonth extends Fragment implements LoaderManager.LoaderCallbacks<List<Day>> {

    private static final String KEY_POSITION = "keyPosition";
    private static final String TAG = FragmentMonth.class.getSimpleName();
    private MonthVM monthVM;
    private MonthItemBinding binding;
    @Inject
    MonthRepo monthRepo;
    @Inject
    SharedPreferences prefs;

    public static FragmentMonth newInstance(int position) {
        FragmentMonth fragmentMonth = new FragmentMonth();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_POSITION, position);
        fragmentMonth.setArguments(bundle);
        return fragmentMonth;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);
        super.onCreate(savedInstanceState);
        int position = getArguments().getInt(KEY_POSITION);
        JewCalendar jewCalendar = JewCalendarPool.obtain(position);
        Log.d(TAG, "onCreate: pos=" + position + " | calendar=" + jewCalendar.getJewishYear());
        monthVM = ViewModelProviders.of(this).get(MonthVM.class);
        monthVM.init(jewCalendar, monthRepo);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.month_item, container, false);
        LiveData<Month> monthLiveData = monthVM.getMonth();
        monthLiveData.observe(this, month -> {
            if (month != null) {
                Log.d(TAG, "onCreateView: " + month.getMonthHebLabel());
                binding.setMonth(month);
                bindMonth(binding);
                getLoaderManager().initLoader(100, null, this);
            } else {
                monthVM.pull();
            }
        });
        getCellHeight();

        return binding.getRoot();
    }

    private float getCellHeight() {
        float cellHeight = prefs.getFloat("cellHeight", 0);
        if (cellHeight == 0) {
            binding.monthContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    binding.monthContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int height = binding.monthContainer.getMeasuredHeight();
                    float cellHeight = height / 6f;
                    prefs.edit().putFloat("cellHeight", cellHeight).apply();
                    bindMonth(binding);
                }
            });
        }
        return cellHeight;
    }

    private void bindMonth(MonthItemBinding binding) {
        int position = 0;
        List<Day> dayList;
        float cellHeight = getCellHeight();
        if (monthVM.getMonth().getValue() != null) {
            dayList = monthVM.getMonth().getValue().getDayList();
            binding.week1.bindDays(dayList.subList(position, position += 7), cellHeight);
            binding.week2.bindDays(dayList.subList(position, position += 7), cellHeight);
            binding.week3.bindDays(dayList.subList(position, position += 7), cellHeight);
            binding.week4.bindDays(dayList.subList(position, position += 7), cellHeight);
            binding.week5.bindDays(dayList.subList(position, position += 7), cellHeight);
            binding.week6.bindDays(dayList.subList(position, position += 7), cellHeight);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Loader<List<Day>> onCreateLoader(int id, Bundle args) {
        return new GoogleEventsLoader(getContext(), monthRepo, monthVM.getMonth().getValue().getDayList());
    }

    @Override
    public void onLoadFinished(Loader<List<Day>> loader, List<Day> data) {
        if (monthVM.getMonth().getValue() != null) {
            monthVM.getMonth().getValue().setDayList(data);
            bindMonth(binding);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Day>> loader) {

    }
}
