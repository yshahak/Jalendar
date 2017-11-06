package com.thedroidboy.jalendar;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.GridLayout;

import com.thedroidboy.jalendar.databinding.DayItemBinding;
import com.thedroidboy.jalendar.model.Day;
import com.thedroidboy.jalendar.model.MonthVM;

/**
 * Created by Yaakov Shahak
 * on 06/11/2017.
 */

public class MonthGridLayout extends GridLayout {


    private MonthVM monthVM;

    public MonthGridLayout(Context context) {
        super(context);
    }

    public MonthGridLayout(Context context, MonthVM monthVM) {
        super(context);
        this.monthVM = monthVM;
    }

    public MonthGridLayout(Context context, AttributeSet attrs, MonthVM monthVM) {
        super(context, attrs);
        this.monthVM = monthVM;
    }

    public MonthGridLayout(Context context, AttributeSet attrs, int defStyleAttr, MonthVM monthVM) {
        super(context, attrs, defStyleAttr);
        this.monthVM = monthVM;
    }

    public void setMonthVM(MonthVM monthVM) {
        this.monthVM = monthVM;
        setDays();
    }

    private void setDays(){
        LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
        for (Day day : monthVM.getDayList()){
            DayItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.day_item, this, false);
            binding.setDay(day);
        }
    }
}
