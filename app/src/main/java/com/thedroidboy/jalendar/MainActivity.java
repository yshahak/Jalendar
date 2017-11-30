package com.thedroidboy.jalendar;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.thedroidboy.jalendar.adapters.PagerAdapterMonth;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new PagerAdapterMonth(getSupportFragmentManager()));
        viewPager.setCurrentItem(PagerAdapterMonth.INITIAL_OFFSET);
//        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(this);
        setMonthTitle(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_current_day:
                movePagerToPosition(PagerAdapterMonth.INITIAL_OFFSET);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void movePagerToPosition(int position) {
        int current = viewPager.getCurrentItem();
        int sign = current > position ? -1: 1;
        while (viewPager.getCurrentItem() != position){
            viewPager.setCurrentItem(viewPager.getCurrentItem() + sign, true);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int realPosition = position - PagerAdapterMonth.INITIAL_OFFSET;
        setMonthTitle(realPosition);
    }

    private void setMonthTitle(int realPosition) {
        JewCalendar calendar = JewCalendarPool.obtain(realPosition);
        setTitle(calendar.getYearName() + " " + calendar.getMonthName());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
