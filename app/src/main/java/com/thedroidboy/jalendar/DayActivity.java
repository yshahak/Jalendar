package com.thedroidboy.jalendar;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.thedroidboy.jalendar.calendars.google.EventInstance;
import com.thedroidboy.jalendar.databinding.ActivityDayBinding;
import com.thedroidboy.jalendar.databinding.HourItemForDayBinding;
import com.thedroidboy.jalendar.databinding.TextViewEventForHourBinding;
import com.thedroidboy.jalendar.model.Day;
import com.thedroidboy.jalendar.model.Hour;

import java.util.List;

public class DayActivity extends AppCompatActivity {

    private static final String TAG = DayActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDayBinding dayBinding = DataBindingUtil.setContentView(this, R.layout.activity_day);
        setSupportActionBar(dayBinding.toolbar);

        dayBinding.fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Day day = this.getIntent().getParcelableExtra("day");
        dayBinding.setSingleDay(day);
        LinearLayout dayContainer = findViewById(R.id.hourContainer);
        LayoutInflater inflater = LayoutInflater.from(this);
        for(int i = 0; i < 23; i++) {
            HourItemForDayBinding binding = DataBindingUtil.inflate(inflater, R.layout.hour_item_for_day, dayContainer, true);
            Hour hour = day.getHoursEventsMap().get(i);
            binding.setHour(hour);
            List<EventInstance> eventInstances = hour.getHourEvents();
            if (eventInstances.size() == 0) {
                continue;
            }
            for (EventInstance eventInstance : eventInstances){
                TextViewEventForHourBinding eventBinding = DataBindingUtil.inflate(inflater, R.layout.text_view_event_for_hour, binding.hourEventContainer, true);
                eventBinding.setEvent(eventInstance);
                eventBinding.hourLabel.setText(eventInstance.getEventTitle());
                eventBinding.hourLabel.setBackgroundColor(eventInstance.getDisplayColor());
                eventBinding.hourLabel.setTag(eventInstance);
            }
        }
    }

}
