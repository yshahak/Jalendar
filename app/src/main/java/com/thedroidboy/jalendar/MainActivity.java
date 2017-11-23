package com.thedroidboy.jalendar;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.thedroidboy.jalendar.adapters.PagerAdapterMonth;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new PagerAdapterMonth(getSupportFragmentManager()));
        viewPager.setCurrentItem(PagerAdapterMonth.INITIAL_OFFSET);
        viewPager.setOffscreenPageLimit(2);
    }

}
