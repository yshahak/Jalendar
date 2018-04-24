package com.thedroidboy.jalendar.calendars.google;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;

import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;
import com.thedroidboy.jalendar.model.GoogleEvent;

import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import biweekly.util.Frequency;

/**
 * Created by B.E.L on 01/11/2016.
 */

public class GoogleManager {

    public static final int REQUEST_CODE_EDIT_EVENT = 100;
    private static final String TAG = GoogleManager.class.getSimpleName();
    //1088145040884-uudgqtifedrnk0sadmss2mlj0mhfh132.apps.googleusercontent.com

    @SuppressWarnings("MissingPermission")
    public static void syncCalendars(Context context) {
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

    public static void updateCalendarVisibility(Context context, CalendarAccount calendarAccount, boolean visibility) {
        Uri.Builder builder = CalendarContract.Calendars.CONTENT_URI.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, calendarAccount.getAccountName())
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "com.google");
        ContentUris.appendId(builder, calendarAccount.getAccountId());
        ContentValues contentValues = new ContentValues();
        contentValues.put(CalendarContract.Calendars.VISIBLE, visibility);
        ContentResolver cr =  context.getContentResolver();
        cr.update(builder.build(), contentValues, null, null);
        syncCalendars(context);
    }

    public static Uri getInstanceUriForInterval(long start, long end) {
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "com.google");
        ContentUris.appendId(builder, start);
        ContentUris.appendId(builder, end);
        return builder.build();
    }
    @SuppressWarnings("MissingPermission")
    public static void deleteEvent(Context context, GoogleEvent event) {
        ContentResolver cr = context.getContentResolver();
        Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.getEventId());
        int rows = cr.delete(deleteUri, null, null);
        Log.d(TAG, "deleteEvent: " + rows);
        syncCalendars(context);
    }

    @SuppressWarnings("MissingPermission")
    public static void addHebrewEventToGoogleServer(Context context, GoogleEvent event) {
        ContentValues values;
        ContentResolver cr = context.getContentResolver();
        Frequency frequency = event.getFrequency();
        values = getContentValuesForEvent(event);
        if (event.getEventId() == -1L) {
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            if (uri != null) {
                long eventId = Long.parseLong(uri.getLastPathSegment());
                event.setEventId(eventId);
                if (Frequency.MONTHLY.equals(frequency) || Frequency.YEARLY.equals(frequency)){
                    new Thread(() -> {
                        editAllEventInstances(context, event);
                    }).start();
                }
            }
        } else {
            Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.getEventId());
            cr.update(updateUri, values, null, null);
        }
        syncCalendars(context);
    }

    @SuppressLint("DefaultLocale")
    @SuppressWarnings("MissingPermission")
    private static ContentValues getContentValuesForEvent(GoogleEvent event) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, event.getBegin());
        Frequency frequency = event.getFrequency();
        if (frequency == null) {
            values.put(CalendarContract.Events.DTEND, event.getEnd());
        } else {
            switch (frequency) {
                case DAILY:
                    long duration = event.getEnd() - event.getBegin();
                    long hours = TimeUnit.MILLISECONDS.toHours(duration);
                    values.put(CalendarContract.Events.DURATION, String.format("PT%dH%dM", hours, TimeUnit.MILLISECONDS.toMinutes(duration - TimeUnit.HOURS.toMillis(hours))));
                    values.put(CalendarContract.Events.RRULE, "FREQ=DAILY;COUNT=" + event.getRepeatValue());//;BYDAY=TU   "FREQ=WEEKLY;BYDAY=TU;UNTIL=20150428"
                    break;
                case WEEKLY:
                    values.put(CalendarContract.Events.DURATION, "PT1H0M");
                    values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;COUNT=" + event.getRepeatValue());
                    break;
                case MONTHLY:
                    values.put(CalendarContract.Events.DURATION, "PT1H0M");
                    values.put(CalendarContract.Events.RRULE, "FREQ=MONTHLY;COUNT=" + event.getRepeatValue());
//                    values.put(CalendarContract.Events._SYNC_ID, System.currentTimeMillis() + "");
                    break;
                case YEARLY:
                    values.put(CalendarContract.Events.DURATION, "PT1H0M");
                    values.put(CalendarContract.Events.RRULE, "FREQ=YEARLY;COUNT=" + event.getRepeatValue());
//                    values.put(CalendarContract.Events._SYNC_ID, System.currentTimeMillis() + "");
                    break;
            }
        }
        values.put(CalendarContract.Events.TITLE, event.getEventTitle());
        values.put(CalendarContract.Events.CALENDAR_ID, event.getCalendarId());
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        return values;
    }

    @SuppressLint("MissingPermission")
    private static void editAllEventInstances(Context context, GoogleEvent event) {
        syncCalendars(context);
        ContentResolver cr = context.getContentResolver();
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "com.google");
        ContentUris.appendId(builder, event.getBegin());
        long endInstances = event.getFrequency().equals(Frequency.MONTHLY) ? TimeUnit.DAYS.toMillis(365) :  TimeUnit.DAYS.toMillis(365 * 10);
        ContentUris.appendId(builder, event.getBegin() + endInstances);
        String WHERE_CALENDARS_SELECTED = CalendarContract.Calendars.VISIBLE + " = ? AND " +CalendarContract.Instances.CALENDAR_ID + " = ? AND " + CalendarContract.Instances.EVENT_ID + " = ?";
        String[] WHERE_CALENDARS_ARGS = {"1", Long.toString(event.getCalendarId()), Long.toString(event.getEventId())};//
        String[] mProjection = {
                CalendarContract.Events.DTSTART, //0
                CalendarContract.Events.DTEND,  //1
                CalendarContract.Instances.BEGIN,  //2
                CalendarContract.Instances.END,   //3
//                CalendarContract.Events._SYNC_ID,  //4
//                CalendarContract.Events.ORIGINAL_SYNC_ID
        }; //5

        Cursor cur = cr.query(builder.build(), mProjection, WHERE_CALENDARS_SELECTED, WHERE_CALENDARS_ARGS,
                CalendarContract.Events.DTSTART + " ASC");

        if (cur != null) {
            if (cur.moveToFirst()) {
                JewCalendar calStart = new JewCalendar(new Date(event.getBegin()));
                JewCalendar calEnd = new JewCalendar(new Date(event.getEnd()));
                int i = 0;
                long eventStart = calStart.getTime().getTime();
                long eventEnd = calEnd.getTime().getTime();
                do {
//                    long syncId = cur.getLong(4);
                    long start = cur.getLong((2));
                    long end = cur.getLong((3));
                    calStart.shiftMonth(i);
                    calEnd.shiftMonth(i);
                    event.setBegin(calStart.getTime().getTime());
                    event.setEnd(calEnd.getTime().getTime());
                    ContentValues values = new ContentValues();
//                    values.put(CalendarContract.Events.CALENDAR_ID, event.getCalendarId());
//                    values.put(CalendarContract.Events.TITLE, event.getEventTitle());
                    values.put(CalendarContract.Events.DTSTART, event.getBegin());
//                    values.put(CalendarContract.Events.DTEND, event.getEnd());
                    values.put(CalendarContract.Events.ORIGINAL_INSTANCE_TIME, start);
//                    values.put(CalendarContract.Events.ORIGINAL_SYNC_ID, syncId);
//                    values.put(CalendarContract.Events.ORIGINAL_ID, event.getEventId());
                    // Create the exception
                    Uri uri = Uri.withAppendedPath(CalendarContract.Events.CONTENT_EXCEPTION_URI,  String.valueOf(event.getEventId()));
//                    uri = asSyncAdapter(uri, PreferenceManager.getDefaultSharedPreferences(context).getString("user_email", "yshahak@gmail.com"));
//                    int update = cr.update(ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.getEventId()), values, null, null);
                    Uri result = cr.insert(uri, values);
                    Log.d(TAG, "editAllEventInstances: update row= " + result);
                    i++;
                } while (cur.moveToNext());
            }
            cur.close();
            syncCalendars(context);
        }
    }

    /**
     * Creates an updated URI that includes query parameters that identify the source as a
     * sync adapter.
     */
    static Uri asSyncAdapter(Uri uri, String account) {
        return uri.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "com.google").build();
    }
//    public static void openEvent(Context context, EventInstance eventInstance){
//        Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventInstance.getEventId());
//        Intent intent = new Intent(Intent.ACTION_VIEW)
//                .setData(uri)
//                .putExtra(CalendarContract.Events.TITLE, eventInstance.getEventTitle());
//        ((Activity)context).startActivityForResult(intent, REQUEST_CODE_EDIT_EVENT);
//    }


}
