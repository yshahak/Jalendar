package com.thedroidboy.jalendar.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thedroidboy.jalendar.DayRecyclerAdapter;
import com.thedroidboy.jalendar.JewCalendar;
import com.thedroidboy.jalendar.R;
import com.thedroidboy.jalendar.databinding.MonthItemBinding;
import com.thedroidboy.jalendar.model.MonthVM;

/**
 * Created by Yaakov Shahak
 * on 20/11/2017.
 */

public class FragmentMonth extends Fragment {

    private static final String KEY_POSITION = "keyPosition";
    private int position;

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
        this.position = getArguments().getInt(KEY_POSITION);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        JewCalendar jewCalendar = new JewCalendar(position);
        MonthVM monthVM = new MonthVM();
        monthVM.setMonthHebName(jewCalendar.getMonthName());
        monthVM.setDaysInMonth(jewCalendar.getDaysInJewishMonth());
        monthVM.setHeadOffset(jewCalendar.getMonthHeadOffset());
        monthVM.setTrailOffset(jewCalendar.getMonthTrailOffset());
        monthVM.setMonthDays(jewCalendar);

        MonthItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.month_item, container, false);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7, LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setHasFixedSize(true);
        binding.setMonth(monthVM);
        binding.recyclerView.setAdapter(new DayRecyclerAdapter(monthVM));
        return binding.getRoot();
    }
}
