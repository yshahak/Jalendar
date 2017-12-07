package com.thedroidboy.jalendar;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.thedroidboy.jalendar.adapters.PagerAdapterMonth;
import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;
import com.thedroidboy.jalendar.calendars.jewish.JewCalendarPool;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.view_pager);
        validateCalendarPermission();
        setMonthTitle(0);
    }

    private void initViewPager() {
        viewPager.setAdapter(new PagerAdapterMonth(getSupportFragmentManager()));
        viewPager.setCurrentItem(PagerAdapterMonth.INITIAL_OFFSET);
//        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(this);
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
        setTitle(calendar.getHebMonthName() + " " +  calendar.getYearHebName());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(100)
    private void validateCalendarPermission() {
        String[] perms = {Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR};
        if (EasyPermissions.hasPermissions(this, perms)) {
            initViewPager();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.calendar_ask_premission),100, perms);
        }
    }
}
