package com.thedroidboy.jalendar.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.thedroidboy.jalendar.CalendarRepo;
import com.thedroidboy.jalendar.R;
import com.thedroidboy.jalendar.adapters.PagerAdapterMonthDay;
import com.thedroidboy.jalendar.calendars.google.CalendarHelper;
import com.thedroidboy.jalendar.fragments.PagerFragment;
import com.thedroidboy.jalendar.model.Day;
import com.thedroidboy.jalendar.utils.Constants;
import com.thedroidboy.jalendar.utils.LocationHelper;
import com.thedroidboy.jalendar.utils.Utils;

import net.sourceforge.zmanim.util.GeoLocation;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.thedroidboy.jalendar.calendars.google.Contract.Calendar_PROJECTION;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 1000;
    private static final int PLACE_PICKER_REQUEST = 2000;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawertToggle;
    private LinearLayout calendarsList;
    private TextView locationLabel;
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
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        calendarsList = findViewById(R.id.calender_list);
        radioButtonDay = findViewById(R.id.display_day);
        radioButtonMonth = findViewById(R.id.display_month);
        radioButtonDay.setOnCheckedChangeListener(this);
        radioButtonMonth.setOnCheckedChangeListener(this);
        locationLabel = findViewById(R.id.label_location);
        locationLabel.setOnClickListener(v -> chooseLocationOnMap(locationLabel));
        setLocationValue();
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
            case R.id.action_current_day_details:
                startDayDetailsActivity();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startDayDetailsActivity(){
        PagerFragment fragment = getPagerFragment();
        if (fragment != null) {
            fragment.startDayDetailsActivity();
        }
    }

    private void movePagerToPosition(int position) {
        PagerFragment fragment = getPagerFragment();
        if (fragment != null) {
            fragment.movePagerToPosition(position);
        }
    }

    private PagerFragment getPagerFragment() {
        String tag = display.name();
        return (PagerFragment) getSupportFragmentManager().findFragmentByTag(tag);
    }

    @SuppressWarnings("MissingPermission")
    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PLACE_PICKER_REQUEST:
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(this, data);
                    if (place != null) {
                        final boolean hebrew = Utils.isLocaleHebrew(prefs);
                        GeoLocation geoLocation = LocationHelper.getGeoLocationFromPlace(getApplicationContext(), place, hebrew);
                        LocationHelper.saveLocation(prefs, geoLocation);
                    }
                    setLocationValue();
                }
                break;
            case RC_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    String[] perms = {Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR, Manifest.permission.GET_ACCOUNTS};
                    EasyPermissions.requestPermissions(this, getString(R.string.calendar_ask_premission), 100, perms);
                }

        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (display.equals(PagerAdapterMonthDay.DISPLAY.DAY)) {
            radioButtonMonth.setChecked(true);
        } else {
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
            getSupportLoaderManager().initLoader(101, null, this);
        } else {
            startActivityForResult(new Intent(this, GoogleSignInActivity.class), RC_SIGN_IN);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "(" + CalendarContract.Calendars.ACCOUNT_TYPE + " = ? " + "AND " + CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " != ? )";
        String[] selectionArgs = new String[]{"com.google", "Contacts"};
        return new CursorLoader(this,  // Context
                uri, // URI
                Calendar_PROJECTION,                // Projection
                selection,                          // Selection
                selectionArgs,                      // Selection args
                null); // Sort
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cur) {
        if (cur != null) {
            CalendarHelper.setCalendarsListInDrawer(this, cur, calendarsList);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

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

    private void setLocationValue() {
        String location = LocationHelper.getLocation(prefs);
        if (location != null) {
            locationLabel.setText(location);
        }
    }

    public void chooseLocationOnMap(View view) {
        if (prefs.getBoolean(Constants.KEY_DISPLAY_PLACES_HELP_DIALOG, true)) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getString(R.string.pick_place_title))
                    .setMessage(getString(R.string.place_picker_help))
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        try {
                            startActivityForResult(new PlacePicker.IntentBuilder().build(MainActivity.this), PLACE_PICKER_REQUEST);
                        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                            e.printStackTrace();
                        }
                    })
                    .show();
            prefs.edit().putBoolean(Constants.KEY_DISPLAY_PLACES_HELP_DIALOG, false).apply();
        } else {
            checkGooglePlayServices();
            try {
                startActivityForResult(new PlacePicker.IntentBuilder().build(MainActivity.this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkGooglePlayServices(){
        switch (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)){
            case ConnectionResult.SERVICE_MISSING:
                GoogleApiAvailability.getInstance().getErrorDialog(this,ConnectionResult.SERVICE_MISSING,0).show();
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                GoogleApiAvailability.getInstance().getErrorDialog(this,ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED,0).show();
                break;
            case ConnectionResult.SERVICE_DISABLED:
                GoogleApiAvailability.getInstance().getErrorDialog(this,ConnectionResult.SERVICE_DISABLED,0).show();
                break;
        }
    }
}
