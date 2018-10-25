package com.thedroidboy.jalendar.calendars.google

import android.database.Cursor
import com.thedroidboy.jalendar.calendars.google.Contract.*

/**
 * Created by yshahak
 * on 11/21/2016.
 */

data class CalendarAccount(
        var accountId: Int,
        var calendarColor: Int,
        val calendarDisplayName: String,
        var accountName: String,
        var calendarOwnerName: String,
        var isCalendarVisible: Boolean
)