package com.thedroidboy.jalendar;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import com.thedroidboy.jalendar.databinding.HourItemForDayBinding;
import com.thedroidboy.jalendar.model.Day;

public class DayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        this.setContentView(R.layout.activity_day);
        this.setSupportActionBar(findViewById(R.id.toolbar));
        Day day = this.getIntent().getParcelableExtra("day");
        LinearLayout dayContainer = findViewById(R.id.hourContainer);

        for(int i = 0; i < 23; ++i) {
//            LinearLayout hourContainer = new LinearLayout(this);
//            hourContainer.setOrientation(LinearLayout.VERTICAL);
//            dayContainer.addView(hourContainer, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            HourItemForDayBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.hour_item_for_day, dayContainer, false);
            binding.setHour((day.getHoursEventsMap().get(i)));
        }
    }

}
