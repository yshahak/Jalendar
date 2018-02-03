package com.thedroidboy.jalendar.calendars.google;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
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

import com.google.api.services.calendar.model.Events;
import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;
import com.thedroidboy.jalendar.model.EventInstanceForDay;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

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
//1088145040884-uudgqtifedrnk0sadmss2mlj0mhfh132.apps.googleusercontent.com
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
                    Log.d("TAG", "calID: " + calID + " , displayName: " + displayName + ", accountName: " + accountName
                            + " , ownerName: " + ownerName);

                    if (displayName.equals(HEBREW_CALENDAR_SUMMERY_TITLE)) {
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
    private static void syncCalendars(Context context) {
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
        if (jewishCalendar.getDaysInJewishMonth() == 29) {
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
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
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

    @SuppressWarnings("MissingPermission")
    public static void addHebrewEventToGoogleServer(Context context, EventInstanceForDay event) {
        ContentValues values;
        ContentResolver cr = context.getContentResolver();
        switch (event.getRepeatState()) {
            case SINGLE:
            case DAY:
            case WEEK:
                values = getContentValueForSingleEvent(event);
                if (event.getEventId() == -1L) {
                    Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                    if (uri != null) {
                        long eventId = Long.parseLong(uri.getLastPathSegment());
                        event.setEventId(eventId);
                    }
                } else {
                    Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.getEventId());
                    cr.update(updateUri, values, null, null);
                }
                break;
            case MONTH:
                ContentValues[] contentValues = new ContentValues[event.getRepeatValue()];

                values = getContentValueForSingleEvent(event);
                if (event.getEventId() == -1L) {
                    Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                    if (uri != null) {
                        long eventId = Long.parseLong(uri.getLastPathSegment());
                        event.setEventId(eventId);
                        editAllEventInstances(event);
                    }
                }
                JewCalendar calStart = new JewCalendar(new Date(event.getBegin()));
                JewCalendar calEnd = new JewCalendar(new Date(event.getEnd()));
                event.setRepeatState(EventInstanceForDay.Repeat.SINGLE);
                for (int i = 0 ; i < contentValues.length ; i++) {
                    calStart.shiftMonth(i);
                    calEnd.shiftMonth(i);
                    event.setBegin(calStart.getTime().getTime());
                    event.setEnd(calEnd.getTime().getTime());
                    contentValues[i] = getContentValueForSingleEvent(event);
                }
                cr.bulkInsert(CalendarContract.Events.CONTENT_URI, contentValues);
                break;
            case YEAR:
                contentValues = new ContentValues[event.getRepeatValue()];
                JewCalendar jewishCalendarStart = new JewCalendar(new Date(event.getBegin()));
                JewCalendar jewishCalendarEnd = new JewCalendar(new Date(event.getEnd()));
                event.setRepeatState(EventInstanceForDay.Repeat.SINGLE);
                for (int i = 0; i < contentValues.length; i++) {
//                    Log.d("TAG",  hebrewDateFormatter.formatHebrewNumber(jewishCalendarStart.getJewishYear()) + " , "
//                            + hebrewDateFormatter.formatMonth(jewishCalendarStart) + " , "
//                            + hebrewDateFormatter.formatHebrewNumber(jewishCalendarStart.getJewishDayOfMonth()));
                    contentValues[i] = getContentValueForSingleEvent(event);
                    jewishCalendarStart.setJewishYear(jewishCalendarStart.getJewishYear() + 1);
                    jewishCalendarEnd.setJewishYear(jewishCalendarEnd.getJewishYear() + 1);
                }
                cr.bulkInsert(CalendarContract.Events.CONTENT_URI, contentValues);
                break;
        }
        syncCalendars(context);
    }

    private static void editAllEventInstances(Context context, EventInstanceForDay event) {

        com.google.api.services.calendar.Calendar service = GoogleApiHelper.getCalendarService(context);
        // First retrieve the instances from the API.
        Events instances = null;
        try {
            instances = service.events().instances(Long.toString(event.getCalendarId()), Long.toString(event.getEventId()), ).execute();
//            Event instance = instances.getItems().get(0);
//            instance.setStatus("cancelled");
//            instance.setStart(new EventDateTime());
//            Event updatedInstance = service.events().update("primary", instance.getId(), instance).execute();
//            System.out.println(updatedInstance.getUpdated());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @SuppressLint("DefaultLocale")
    @SuppressWarnings("MissingPermission")
    private static ContentValues getContentValueForSingleEvent(EventInstanceForDay event) {

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, event.getBegin());

        switch (event.getRepeatState()) {
            case SINGLE:
                values.put(CalendarContract.Events.DTEND, event.getEnd());
                break;
            case DAY:
                long duration = event.getEnd() - event.getBegin();
                long hours = TimeUnit.MILLISECONDS.toHours(duration);
                values.put(CalendarContract.Events.DURATION, String.format("PT%dH%dM", hours, TimeUnit.MILLISECONDS.toMinutes(duration - TimeUnit.HOURS.toMillis(hours))));
                values.put(CalendarContract.Events.RRULE, "FREQ=DAILY;COUNT=" + event.getRepeatValue());//;BYDAY=TU   "FREQ=WEEKLY;BYDAY=TU;UNTIL=20150428"
                break;
            case WEEK:
                values.put(CalendarContract.Events.DURATION, "PT1H0M");
                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;COUNT=" + event.getRepeatValue());
                break;
            case MONTH:
                values.put(CalendarContract.Events.DURATION, "PT1H0M");
                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;COUNT=" + event.getRepeatValue());
                break;
        }
        values.put(CalendarContract.Events.TITLE, event.getEventTitle());
        values.put(CalendarContract.Events.CALENDAR_ID, event.getCalendarId());
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        return values;
    }

//    public static void openEvent(Context context, EventInstance eventInstance){
//        Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventInstance.getEventId());
//        Intent intent = new Intent(Intent.ACTION_VIEW)
//                .setData(uri)
//                .putExtra(CalendarContract.Events.TITLE, eventInstance.getEventTitle());
//        ((Activity)context).startActivityForResult(intent, REQUEST_CODE_EDIT_EVENT);
//    }

    /**
     * Fetch a list of the next 10 events from the primary calendar.
     *
     * @return List of Strings describing returned events.
     * @throws IOException
     */
    private List<String> getDataFromApi() throws IOException {
        // List the next 10 events from the primary calendar.
        List<String> eventStrings = new ArrayList<String>();
//        DateTime now = new DateTime(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(365));
//        com.google.api.services.calendar.Calendar mService = null;
//        CalendarContract.Events events = mService.events().list("primary")
//                .setMaxResults(10)
//                .setTimeMin(now)
//                .setOrderBy("startTime")
//                .setSingleEvents(true)
//                .execute();
//        List<com.google.api.services.calendar.model.Event> items = events.getItems();
//
//        for (com.google.api.services.calendar.model.Event event : items) {
//            DateTime start = event.getStart().getDateTime();
//            if (start == null) {
//                 All-day events don't have start times, so just use
//                 the start date.
//                start = event.getStart().getDate();
//            }
//            eventStrings.add(
//                    String.format("%s (%s)", event.getSummary(), start));
//        }
        return eventStrings;
    }
}
