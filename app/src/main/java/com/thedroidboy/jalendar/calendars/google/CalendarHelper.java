package com.thedroidboy.jalendar.calendars.google;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thedroidboy.jalendar.MyApplication;
import com.thedroidboy.jalendar.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.thedroidboy.jalendar.calendars.google.Contract.PROJECTION_ACCOUNTNAME_INDEX;
import static com.thedroidboy.jalendar.calendars.google.Contract.PROJECTION_COLOR_INDEX;
import static com.thedroidboy.jalendar.calendars.google.Contract.PROJECTION_DISPLAY_NAME_INDEX;
import static com.thedroidboy.jalendar.calendars.google.Contract.PROJECTION_ID_INDEX;
import static com.thedroidboy.jalendar.calendars.google.Contract.PROJECTION_OWNER_ACCOUNT_INDEX;
import static com.thedroidboy.jalendar.calendars.google.Contract.PROJECTION_VISIBLE_INDEX;

/**
 * Created by Yaakov Shahak
 * on 22/02/2018.
 */

public class CalendarHelper {

    public static final String HOLIDAYS = "Holidays";
    public static final String TAG = "CalendarHelper";

    /**
     * setting the drawer category menu of the store
     */
    @SuppressLint("RestrictedApi")
    public static void setCalendarsListInDrawer(Context context, Cursor cursor, LinearLayout calendarsList) {
        HashMap<String, List<CalendarAccount>> accountListNames = getCalendarInfo(cursor);
        calendarsList.removeAllViews();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        for (String calendarAccountNmae : accountListNames.keySet()) {
            TextView header = (TextView) layoutInflater.inflate(R.layout.calendar_accont_header, calendarsList, false);
            header.setText(calendarAccountNmae);
            calendarsList.addView(header);
            int counter = 0;
            for (final CalendarAccount calendarAccount : accountListNames.get(calendarAccountNmae)) {
                if (calendarAccountNmae.equals(HOLIDAYS) && counter == 1) {
                    GoogleManager.updateCalendarVisibility(context, calendarAccount, false);
                    continue;
                }
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
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    GoogleManager.updateCalendarVisibility(context, calendarAccount, isChecked);
                });
                counter++;
            }
        }

    }

    private static HashMap<String, List<CalendarAccount>> getCalendarInfo(@NonNull Cursor cur) {
        HashMap<String, List<CalendarAccount>> accountListNames = new HashMap<>();
        accountListNames.clear();
        while (cur.moveToNext()) {
            int calID, color;
            String displayName, accountName, ownerName;
            boolean visible;
            calID = cur.getInt(PROJECTION_ID_INDEX);
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNTNAME_INDEX);
            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
            String copyName = accountName; //for updating visibilty
            if (ownerName.contains("#holiday@group.v.calendar.google.com")) {
                accountName = HOLIDAYS;
            }
            color = cur.getInt(PROJECTION_COLOR_INDEX);
            visible = cur.getInt(PROJECTION_VISIBLE_INDEX) == 1;
            CalendarAccount calendarAccount = new CalendarAccount(calID, color, displayName, accountName, ownerName, visible);
            if (accountListNames.get(accountName) == null) {
                List<CalendarAccount> accountList = new ArrayList<>();
                accountList.add(calendarAccount);
                accountListNames.put(accountName, accountList);
            } else {
                if (accountName.equals(HOLIDAYS)){
                    calendarAccount.setAccountName(copyName);
                    new Handler().post(() -> {
                        GoogleManager.updateCalendarVisibility(MyApplication.getInstance(), calendarAccount, false); //we don't
                    });
                } else {
                    accountListNames.get(accountName).add(calendarAccount);
                }
            }
            Log.d(TAG, "calID: " + calID + " , displayName: " + displayName + ", accountName: " + accountName + " , ownerName: " + ownerName);
        }
        cur.close();
        return accountListNames;
    }
}
