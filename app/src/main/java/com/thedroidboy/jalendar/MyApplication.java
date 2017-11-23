package com.thedroidboy.jalendar;

import android.app.Application;

/**
 * Created by Yaakov Shahak
 * on 23/11/2017.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JewCalendarPool.init();
    }
}
