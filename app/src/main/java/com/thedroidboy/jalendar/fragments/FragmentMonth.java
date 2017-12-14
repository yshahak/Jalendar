package com.thedroidboy.jalendar.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.database.Cursor;
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

import com.thedroidboy.jalendar.R;
import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;
import com.thedroidboy.jalendar.calendars.jewish.JewCalendarPool;
import com.thedroidboy.jalendar.databinding.MonthItemBinding;
import com.thedroidboy.jalendar.model.MonthFactory;
import com.thedroidboy.jalendar.model.MonthVM;

import dagger.android.support.AndroidSupportInjection;


/**
 * Created by Yaakov Shahak
 * on 20/11/2017.
 */

public class FragmentMonth extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String KEY_POSITION = "keyPosition";
    private static final String TAG = FragmentMonth.class.getSimpleName();
    private MonthFactory monthFactory;
    private MonthVM monthVM;
    private MonthItemBinding binding;

    public static FragmentMonth newInstance(int position){
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
        monthVM = ViewModelProviders.of(this).get(MonthVM.class);
//        monthVM.init(jewCalendar, monthRepo);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.month_item, container, false);
        monthVM.getMonth().observe(this, month -> {
            if (month != null) {
                Log.d(TAG, "onCreateView: " + month.getMonthHebLabel());
                binding.setMonth(month);
                bindMonth(binding);
            }
        });
//        binding.recyclerView.setHasFixedSize(true);

//        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7, LinearLayoutManager.VERTICAL, false));
//        binding.recyclerView.setAdapter(new DayRecyclerAdapter(monthVM));
//        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        binding.recyclerView.setAdapter(new RecyclerAdapterMonth(monthVM));
        getLoaderManager().initLoader(0, null, this);
        return binding.getRoot();
    }

    private void bindMonth(MonthItemBinding binding) {
        int position = 0;
//        binding.week1.bindDays(monthVM.getDayList().subList(position,  position += 7));
//        binding.week2.bindDays(monthVM.getDayList().subList(position,  position += 7));
//        binding.week3.bindDays(monthVM.getDayList().subList(position,  position += 7));
//        binding.week4.bindDays(monthVM.getDayList().subList(position,  position += 7));
//        binding.week5.bindDays(monthVM.getDayList().subList(position,  position += 7));
//        binding.week6.bindDays(monthVM.getDayList().subList(position,  position += 7));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        JewCalendarPool.release(position);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        Day first = monthVM.getDayList().get(0);
//        Day last = monthVM.getDayList().get(monthVM.getDayList().size() - 1);
//        Uri uri = GoogleManager.getInstanceUriForInterval(first.getStartDayInMillis(), last.getEndDayInMillis());
//        String WHERE_CALENDARS_SELECTED = CalendarContract.Calendars.VISIBLE + " = ? "; //AND " +
//        String[] WHERE_CALENDARS_ARGS = {"1"};//
//        return new CursorLoader(getActivity(),
//                uri,
//                INSTANCE_PROJECTION,
//                WHERE_CALENDARS_SELECTED,
//                WHERE_CALENDARS_ARGS,
//                CalendarContract.Events.DTSTART + " ASC");
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//        EventsHelper.bindCursorToDayList(monthVM.getDayList(), cursor);
        bindMonth(binding);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
