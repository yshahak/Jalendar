package com.thedroidboy.jalendar;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.thedroidboy.jalendar.databinding.DayItemBinding;
import com.thedroidboy.jalendar.model.Day;

import java.util.List;

/**
 * Created by Yaakov Shahak
 * on 22/11/2017.
 */

public class SimpleWeekView extends LinearLayout {

    public SimpleWeekView(Context context) {
        super(context);
        init();
    }

    public SimpleWeekView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        setOrientation(HORIZONTAL);
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        setWeightSum(7);
        for (int i = 0; i < 7; i++){
            DayItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.day_item, this, false);
            binding.getRoot().setTag(binding);
            addView(binding.getRoot());
        }
    }

    public void bindDays(List<Day> days){
        for (int i = 0; i < getChildCount(); i++){
            View view = getChildAt(i);
            DayItemBinding binding = (DayItemBinding) view.getTag();
            if (binding != null) {
                binding.setDay(days.get(i));
            }
        }
    }




}
