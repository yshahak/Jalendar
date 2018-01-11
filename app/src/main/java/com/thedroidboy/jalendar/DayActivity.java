package com.thedroidboy.jalendar;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.thedroidboy.jalendar.adapters.RecyclerAdapterDay;
import com.thedroidboy.jalendar.databinding.ActivityDayBinding;
import com.thedroidboy.jalendar.model.Day;

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
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        dayBinding.dayRecyclerView.setLayoutManager(layoutManager);
        dayBinding.dayRecyclerView.setAdapter(new RecyclerAdapterDay(day));

//        LinearLayout dayContainer = findViewById(R.id.hourContainer);
//        LayoutInflater inflater = LayoutInflater.from(this);
//        for(int i = 0; i < 23; i++) {
//            HourItemForDayBinding binding = DataBindingUtil.inflate(inflater, R.layout.hour_item_for_day, dayContainer, true);
//            Hour hour = day.getHoursEventsMap().get(i);
//            binding.setHour(hour);
//            List<EventForHour> eventInstanceForDays = hour.getHourEventForDays();
//            if (eventInstanceForDays.size() == 0) {
//                continue;
//            }
//            for (EventForHour eventInstanceForHour : eventInstanceForDays){
//                TextViewEventForHourBinding eventBinding = DataBindingUtil.inflate(inflater, R.layout.text_view_event_for_hour, binding.hourEventContainer, true);
//                if(eventInstanceForHour.event.weight > binding.hourEventContainer.getWeightSum()){
//                    binding.hourEventContainer.setWeightSum(eventInstanceForHour.event.weight);
//                }
//                eventBinding.setEvent(eventInstanceForHour);
//                eventBinding.hourLabel.setTag(eventInstanceForHour);
//            }
//        }
    }

}
