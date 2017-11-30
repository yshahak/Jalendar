package com.thedroidboy.jalendar.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thedroidboy.jalendar.JewCalendar;
import com.thedroidboy.jalendar.JewCalendarPool;
import com.thedroidboy.jalendar.R;
import com.thedroidboy.jalendar.databinding.MonthItemBinding;
import com.thedroidboy.jalendar.model.MonthFactory;
import com.thedroidboy.jalendar.model.MonthVM;

/**
 * Created by Yaakov Shahak
 * on 20/11/2017.
 */

public class FragmentMonth extends Fragment {

    private static final String KEY_POSITION = "keyPosition";
    private MonthFactory monthFactory;

    public static FragmentMonth newInstance(int position){
        FragmentMonth fragmentMonth = new FragmentMonth();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_POSITION, position);
        fragmentMonth.setArguments(bundle);
        return fragmentMonth;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int position = getArguments().getInt(KEY_POSITION);
        JewCalendar jewCalendar = JewCalendarPool.obtain(position);
        this.monthFactory = new MonthFactory(jewCalendar);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MonthVM monthVM = ViewModelProviders.of(this, monthFactory).get(MonthVM.class);
        MonthItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.month_item, container, false);
//        binding.recyclerView.setHasFixedSize(true);
        binding.setMonth(monthVM);
        int position = 0;
        binding.week1.bindDays(monthVM.getDayList().subList(position,  position += 7));
        binding.week2.bindDays(monthVM.getDayList().subList(position,  position += 7));
        binding.week3.bindDays(monthVM.getDayList().subList(position,  position += 7));
        binding.week4.bindDays(monthVM.getDayList().subList(position,  position += 7));
        binding.week5.bindDays(monthVM.getDayList().subList(position,  position += 7));
        binding.week6.bindDays(monthVM.getDayList().subList(position,  position += 7));
//        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7, LinearLayoutManager.VERTICAL, false));
//        binding.recyclerView.setAdapter(new DayRecyclerAdapter(monthVM));
//        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        binding.recyclerView.setAdapter(new RecyclerAdapterMonth(monthVM));
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        JewCalendarPool.release(position);
    }
}
