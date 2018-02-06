package com.thedroidboy.jalendar.calendars.google;

import android.content.Context;
import android.preference.PreferenceManager;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;

import java.util.Arrays;

/**
 * Created by $Yaakov Shahak on 2/2/2018.
 */

public class GoogleApiHelper {

    private static final String[] SCOPES = { CalendarScopes.CALENDAR};

    public static Calendar getCalendarService(Context context){

        GoogleAccountCredential mCredential = GoogleAccountCredential.usingOAuth2(context, Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        mCredential.setSelectedAccountName(PreferenceManager.getDefaultSharedPreferences(context).getString("user_email", null));
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        // authorize
//        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");


        return new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, mCredential)
                .build();
    }



}