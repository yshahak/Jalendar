package com.thedroidboy.jalendar;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.thedroidboy.jalendar.adapters.PagerAdapterMonth;
import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;
import com.thedroidboy.jalendar.calendars.jewish.JewCalendarPool;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ViewPager viewPager;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawertToggle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(2);
        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        validateCalendarPermission();
        setMonthTitle(0);
        drawerLayout = findViewById(R.id.drawer_layout);
        setDrawerMenu();
    }

    private void initViewPager() {
        viewPager.setAdapter(new PagerAdapterMonth(getSupportFragmentManager()));
        viewPager.setCurrentItem(PagerAdapterMonth.INITIAL_OFFSET);
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawertToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
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

    /**
     * setting the drawer category menu of the store
     */
    private void setDrawerMenu() {
        mDrawertToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                0,
                0) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                syncState();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                syncState();
            }
        };
        drawerLayout.setDrawerListener(mDrawertToggle);
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
