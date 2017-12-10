package com.thedroidboy.jalendar;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.GridLayout;

import com.thedroidboy.jalendar.databinding.DayItemBinding;
import com.thedroidboy.jalendar.model.Day;
import com.thedroidboy.jalendar.model.Month;

/**
 * Created by Yaakov Shahak
 * on 06/11/2017.
 */

public class MonthGridLayout extends GridLayout {


    private Month month;

    public MonthGridLayout(Context context) {
        super(context);
    }

    public MonthGridLayout(Context context, Month month) {
        super(context);
        this.month = month;
    }

    public MonthGridLayout(Context context, AttributeSet attrs, Month month) {
        super(context, attrs);
        this.month = month;
    }

    public MonthGridLayout(Context context, AttributeSet attrs, int defStyleAttr, Month month) {
        super(context, attrs, defStyleAttr);
        this.month = month;
    }

    public void setMonth(Month month) {
        this.month = month;
        setDays();
    }

    private void setDays(){
        LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
        for (Day day : month.getDayList()){
            DayItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.day_item, this, false);
            binding.setDay(day);
        }
    }
}
