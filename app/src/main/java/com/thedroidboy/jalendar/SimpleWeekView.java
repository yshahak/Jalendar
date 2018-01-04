package com.thedroidboy.jalendar;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thedroidboy.jalendar.calendars.google.EventInstance;
import com.thedroidboy.jalendar.databinding.DayItemBinding;
import com.thedroidboy.jalendar.model.Day;

import java.util.List;

/**
 * Created by Yaakov Shahak
 * on 22/11/2017.
 */

public class SimpleWeekView extends LinearLayout implements View.OnClickListener {

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

    public void bindDays(List<Day> days, float cellHeight){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (int i = 0; i < getChildCount(); i++){
            View view = getChildAt(i);
            DayItemBinding binding = (DayItemBinding) view.getTag();
            if (binding != null) {
                Day day = days.get(i);
                view.setTag(R.string.app_name, day);
                view.setOnClickListener(this);
                day.setCellHeight(cellHeight);
                binding.setDay(day);
                ViewGroup container = view.findViewById(R.id.day_events_container);
                container.removeAllViews();
                List<EventInstance> eventInstances = day.getGoogleEventInstances();
                if (eventInstances == null || eventInstances.size() == 0) {
                    continue;
                }
                for (EventInstance eventInstance : eventInstances){
                    TextView textView = (TextView) inflater.inflate(R.layout.text_view_event_for_month, container, false);
                    textView.setText(eventInstance.getEventTitle());
                    textView.setBackgroundColor(eventInstance.getDisplayColor());
                    container.addView(textView);
                    textView.setTag(eventInstance);
                }
            }

        }
    }

    @Override
    public void onClick(View view) {
        Day day = (Day) view.getTag(R.string.app_name);
        if (day != null) {
            Intent intent = new Intent(getContext(), DayActivity.class);
            intent.putExtra("day", day);
            getContext().startActivity(intent);
        }
    }


}
