package com.thedroidboy.jalendar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.thedroidboy.jalendar.adapters.PagerAdapterMonthDay;
import com.thedroidboy.jalendar.calendars.google.CalendarAccount;
import com.thedroidboy.jalendar.calendars.google.GoogleManager;
import com.thedroidboy.jalendar.fragments.PagerFragment;
import com.thedroidboy.jalendar.model.Day;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.thedroidboy.jalendar.calendars.google.Contract.Calendar_PROJECTION;
import static com.thedroidboy.jalendar.calendars.google.Contract.KEY_HEBREW_ID;
import static com.thedroidboy.jalendar.calendars.google.Contract.PROJECTION_ACCOUNTNAME_INDEX;
import static com.thedroidboy.jalendar.calendars.google.Contract.PROJECTION_COLOR_INDEX;
import static com.thedroidboy.jalendar.calendars.google.Contract.PROJECTION_DISPLAY_NAME_INDEX;
import static com.thedroidboy.jalendar.calendars.google.Contract.PROJECTION_ID_INDEX;
import static com.thedroidboy.jalendar.calendars.google.Contract.PROJECTION_OWNER_ACCOUNT_INDEX;
import static com.thedroidboy.jalendar.calendars.google.Contract.PROJECTION_VISIBLE_INDEX;

public class MainActivity extends AppCompatActivity implements  LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 1000;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawertToggle;
    private LinearLayout calendarsList;
    private Toolbar toolbar;
    private RadioButton radioButtonDay, radioButtonMonth;
    @Inject
    SharedPreferences prefs;
    @Inject
    CalendarRepo calendarRepo;
    private PagerAdapterMonthDay.DISPLAY display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.my_toolbar);
        calendarsList = findViewById(R.id.calender_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        radioButtonDay = findViewById(R.id.display_day);
        radioButtonMonth = findViewById(R.id.display_month);
        radioButtonDay.setOnCheckedChangeListener(this);
        radioButtonMonth.setOnCheckedChangeListener(this);
        validateCalendarPermission();
        drawerLayout = findViewById(R.id.drawer_layout);
        setDrawerMenu();
        initScreen(PagerAdapterMonthDay.DISPLAY.MONTH, 0);
    }

    private void initScreen(PagerAdapterMonthDay.DISPLAY display, int position) {
        if (display.equals(this.display)) {
            return;
        }
        this.display = display;
        final FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = display.name();
        PagerFragment fragment = (PagerFragment) fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = PagerFragment.newInstance(display);
        }
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment, tag)
                .commit();
        fragment.shiftToPosition(position);
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
                movePagerToPosition(PagerAdapterMonthDay.INITIAL_OFFSET);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void movePagerToPosition(int position) {
        String tag = display.name();
        PagerFragment fragment = (PagerFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            fragment.movePagerToPosition(position);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (display.equals(PagerAdapterMonthDay.DISPLAY.DAY) ) {
            radioButtonMonth.setChecked(true);
        }
        else {
            super.onBackPressed();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(100)
    private void validateCalendarPermission() {
        String[] perms = {Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR, Manifest.permission.GET_ACCOUNTS};
        if (EasyPermissions.hasPermissions(this, perms)) {
            if (prefs.getString("user_email", null) == null) {
                startActivityForResult(new Intent(this, GoogleSignInActivity.class), RC_SIGN_IN);
            }
            getSupportLoaderManager().initLoader(101, null, this);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.calendar_ask_premission), 100, perms);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "(" + CalendarContract.Calendars.ACCOUNT_TYPE + " = ? " + ")";
        String[] selectionArgs = new String[]{"com.google"};
        return new CursorLoader(this,  // Context
                uri, // URI
                Calendar_PROJECTION,                // Projection
                selection,                           // Selection
                selectionArgs,                           // Selection args
                null); // Sort
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cur) {
        if (cur != null) {
            accountListNames.clear();
            while (cur.moveToNext()) {
                int calID, color;
                String displayName;
                String accountName;
                String ownerName;
                boolean visible;
                // Get the field values
                calID = cur.getInt(PROJECTION_ID_INDEX);
                displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
                accountName = cur.getString(PROJECTION_ACCOUNTNAME_INDEX);

                ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
                color = cur.getInt(PROJECTION_COLOR_INDEX);
                visible = cur.getInt(PROJECTION_VISIBLE_INDEX) == 1;
                CalendarAccount calendarAccount = new CalendarAccount();
                calendarAccount.setAccountId(calID);
                calendarAccount.setCalendarName(accountName);
                calendarAccount.setCalendarDisplayName(displayName);
                calendarAccount.setCalendarOwnerName(ownerName);
                calendarAccount.setCalendarColor(color);
                calendarAccount.setCalendarIsVisible(visible);
                if (accountListNames.get(accountName) == null) {
                    List<CalendarAccount> accountList = new ArrayList<>();
                    accountList.add(calendarAccount);
                    accountListNames.put(accountName, accountList);
                } else {
                    accountListNames.get(accountName).add(calendarAccount);
                }
                Log.d(TAG, "calID: " + calID + " , displayName: " + displayName + ", accountName: " + accountName
                        + " , ownerName: " + ownerName);

//                if (displayName.equals(HEBREW_CALENDAR_SUMMERY_TITLE)) {
//                    prefs.edit()
//                            .putLong(KEY_HEBREW_ID, calID).apply();
//                }
                if (prefs.getLong(KEY_HEBREW_ID, -1L) == -1L) {
                    prefs.edit()
                            .putLong(KEY_HEBREW_ID, calID).apply();
                }
            }
            cur.close();
            setCalendarsListInDrawer();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * setting the drawer category menu of the store
     */
    @SuppressLint("RestrictedApi")
    public void setCalendarsListInDrawer() {

        calendarsList.removeAllViews();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        for (String calendarAccountNmae : accountListNames.keySet()) {
            TextView header = (TextView) layoutInflater.inflate(R.layout.calendar_accont_header, calendarsList, false);
            header.setText(calendarAccountNmae);
            calendarsList.addView(header);
            for (final CalendarAccount calendarAccount : accountListNames.get(calendarAccountNmae)) {
                final AppCompatCheckBox checkBox = (AppCompatCheckBox) layoutInflater.inflate(R.layout.calendar_visibility_row, calendarsList, false);
                calendarsList.addView(checkBox);
                ColorStateList colorStateList = new ColorStateList(
                        new int[][]{
                                new int[]{-android.R.attr.state_enabled}, //disabled
                                new int[]{android.R.attr.state_enabled} //enabled
                        },
                        new int[]{
                                calendarAccount.getCalendarColor() //disabled
                                , calendarAccount.getCalendarColor() //enabled

                        }
                );
                checkBox.setSupportButtonTintList(colorStateList);

                checkBox.setChecked(calendarAccount.isCalendarIsVisible());
                checkBox.setText(calendarAccount.getCalendarDisplayName());
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> GoogleManager.updateCalendarVisibility(getContentResolver(), calendarAccount, isChecked));
            }
        }

    }

    public HashMap<String, List<CalendarAccount>> accountListNames = new HashMap<>();

    @Override
    public void onClick(View view) {
        Day day = (Day) view.getTag(R.string.app_name);
        if (day != null) {
            Integer positionForDay = calendarRepo.getPositionForDay(day);
            if (positionForDay == null) {
                return;
            }
            radioButtonDay.setTag(R.string.tag_month_position, positionForDay);
            radioButtonDay.setChecked(true);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Log.d(TAG, "onCheckedChanged: " + isChecked);
        if (isChecked) {
            drawerLayout.closeDrawers();
            Integer position = (Integer) radioButtonDay.getTag(R.string.tag_month_position);
            if (position == null) {
                position = 0;
            } else {
                radioButtonDay.setTag(R.string.tag_month_position, null);
            }
            PagerAdapterMonthDay.DISPLAY display = compoundButton.getId() == R.id.display_month ? PagerAdapterMonthDay.DISPLAY.MONTH : PagerAdapterMonthDay.DISPLAY.DAY;
            initScreen(display, position);
        }
    }
}
