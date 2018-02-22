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
        var isCalendarIsVisible: Boolean
){
    companion object {
        fun fromCursor(cur : Cursor) : CalendarAccount {
            val calID: Int = cur.getInt(PROJECTION_ID_INDEX)
            val color: Int = cur.getInt(PROJECTION_COLOR_INDEX)
            val displayName: String = cur.getString(PROJECTION_DISPLAY_NAME_INDEX)
            val accountName: String = cur.getString(PROJECTION_ACCOUNTNAME_INDEX)
            val ownerName: String = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX)
            val visible: Boolean = cur.getInt(PROJECTION_VISIBLE_INDEX) == 1
            return CalendarAccount(calID, color, displayName, accountName, ownerName, visible)
        }
    }
}