package com.thedroidboy.jalendar.calendars.google;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.text.format.Time;
import android.util.Log;

import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.thedroidboy.jalendar.calendars.google.Contract.Calendar_PROJECTION;
import static com.thedroidboy.jalendar.calendars.google.Contract.HEBREW_CALENDAR_SUMMERY_TITLE;
import static com.thedroidboy.jalendar.calendars.google.Contract.KEY_HEBREW_ID;
import static com.thedroidboy.jalendar.calendars.google.Contract.PROJECTION_ACCOUNTNAME_INDEX;
import static com.thedroidboy.jalendar.calendars.google.Contract.PROJECTION_COLOR_INDEX;
import static com.thedroidboy.jalendar.calendars.google.Contract.PROJECTION_DISPLAY_NAME_INDEX;
import static com.thedroidboy.jalendar.calendars.google.Contract.PROJECTION_ID_INDEX;
import static com.thedroidboy.jalendar.calendars.google.Contract.PROJECTION_OWNER_ACCOUNT_INDEX;
import static com.thedroidboy.jalendar.calendars.google.Contract.PROJECTION_VISIBLE_INDEX;
import static com.thedroidboy.jalendar.calendars.google.Contract.REQUEST_READ_CALENDAR;

/**
 * Created by B.E.L on 01/11/2016.
 */

public class GoogleManager {

    public static final int REQUEST_CODE_EDIT_EVENT = 100;

    public static HashMap<String, List<CalendarAccount>> accountListNames = new HashMap<>();

    public static void getCalendars(Activity activity) {

        // Submit the query and get a Cursor object back.
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            syncCalendars(activity);
            // Run query
            Cursor cur;
            ContentResolver cr = activity.getContentResolver();
            Uri uri = CalendarContract.Calendars.CONTENT_URI;
            String selection = "(" + CalendarContract.Calendars.ACCOUNT_TYPE + " = ? " + ")";
            String[] selectionArgs = new String[]{"com.google"};

            cur = cr.query(uri, Calendar_PROJECTION, selection, selectionArgs, null);
            if (cur != null) {
                accountListNames.clear();
                while (cur.moveToNext()) {
                    int calID , color;
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
                    if (accountListNames.get(accountName) == null){
                        List<CalendarAccount> accountList = new ArrayList<>();
                        accountList.add(calendarAccount);
                        accountListNames.put(accountName, accountList);
                    } else {
                        accountListNames.get(accountName).add(calendarAccount);
                    }
                    Log.d("TAG", "calID: " + calID + " , displayName: " + displayName + ", accountName: " + accountName
                            + " , ownerName: " + ownerName);

                    if (displayName.equals(HEBREW_CALENDAR_SUMMERY_TITLE)){
                        PreferenceManager.getDefaultSharedPreferences(activity).edit()
                                .putLong(KEY_HEBREW_ID, calID).apply();
                    }
                }
                cur.close();
            }

        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CALENDAR}, REQUEST_READ_CALENDAR);
        }
    }


    @SuppressWarnings("MissingPermission")
    private static void syncCalendars(Context context){
        Account[] accounts = AccountManager.get(context).getAccounts();
        String authority = CalendarContract.Calendars.CONTENT_URI.getAuthority();
        for (Account account : accounts) {
            Bundle extras = new Bundle();
            extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            if (account.type.equals("com.google")) {
                ContentResolver.requestSync(account, authority, extras);
            }
        }
    }

    public static Uri asSyncAdapter(long begin, long end) {
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "com.google");
        ContentUris.appendId(builder, begin);
        ContentUris.appendId(builder, end);
        return builder.build();
    }



    public static Uri asSyncAdapterDay(JewCalendar jewishCalendar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.US);
        Time time = new Time();
        time.set(jewishCalendar.getTime().getTime());
//        time.monthDay = time.monthDay - 1;

        time.allDay = true;
        time.hour = 0;
        time.minute = 0;
        time.second = 0;
        long begin = Time.getJulianDay(time.toMillis(true), 0);
        Log.d("TAG", "start: " + simpleDateFormat.format(time.toMillis(true)));
        time.monthDay = time.monthDay + 1;
        long end = Time.getJulianDay(time.toMillis(true), 0);
        Log.d("TAG", "end: " + simpleDateFormat.format(time.toMillis(true)));

        Uri.Builder builder = CalendarContract.Instances.CONTENT_BY_DAY_URI.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "com.google");
        ContentUris.appendId(builder, begin);
        ContentUris.appendId(builder, end);
        return builder.build();
    }

    public static Uri asSyncAdapterMonth(JewCalendar jewishCalendar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.US);
        jewishCalendar.setJewishDayOfMonth(2);
        Time time = new Time();
        time.set(jewishCalendar.getTime().getTime());
        time.allDay = true;
        time.hour = 0;
        time.minute = 0;
        time.second = 0;
        long begin = Time.getJulianDay(time.toMillis(true), 0);
        Log.d("TAG", "start: " + simpleDateFormat.format(time.toMillis(true)));
        if (jewishCalendar.getDaysInJewishMonth() == 29){
            time.monthDay += 29;
        } else {
            time.monthDay += 30;
        }
        long end = Time.getJulianDay(time.toMillis(true), 0);
        Log.d("TAG", "end: " + simpleDateFormat.format(time.toMillis(true)));

        Uri.Builder builder = CalendarContract.Instances.CONTENT_BY_DAY_URI.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "com.google");
        ContentUris.appendId(builder, begin);
        ContentUris.appendId(builder, end);
        return builder.build();
    }

    public static void updateCalendarVisibility(ContentResolver contentResolver, CalendarAccount calendarAccount, boolean visibility) {
        Uri.Builder builder = CalendarContract.Calendars.CONTENT_URI.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER,"true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, calendarAccount.getCalendarName())
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "com.google");
        ContentUris.appendId(builder, calendarAccount.getAccountId());
        ContentValues contentValues = new ContentValues();
        contentValues.put(CalendarContract.Calendars.VISIBLE, visibility);
        contentResolver.update(builder.build(), contentValues, null, null);
    }

    public static Uri getInstanceUriForInterval(long start, long end) {
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "com.google");
        ContentUris.appendId(builder, start);
        ContentUris.appendId(builder, end);
        return builder.build();
    }
}