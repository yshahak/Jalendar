package com.thedroidboy.jalendar;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        MonthVM monthVM = ViewModelProviders.of(this).get(MonthVM.class);
        final CalendarRecyclerAdapter recyclerAdapter = new CalendarRecyclerAdapter();
        monthVM.init();
        monthVM.getMonthList().observe(this, pagedList -> {
            Log.d(TAG, "onCreate: pagingList" + pagedList.size());
            recyclerAdapter.setList(pagedList);
        });
        recyclerView.setAdapter(recyclerAdapter);
    }
}
