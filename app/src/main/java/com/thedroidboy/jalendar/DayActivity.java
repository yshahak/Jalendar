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
    }

}
