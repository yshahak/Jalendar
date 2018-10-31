package com.thedroidboy.jalendar.adapters

import com.thedroidboy.jalendar.calendars.google.CalendarHelper
import java.util.*


data class SpinnerCalIdAdapter(val entries: Array<String>, val selectedItem: Int = 0, val shouldShowSpinner: Boolean = false) {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SpinnerCalIdAdapter

        if (!Arrays.equals(entries, other.entries)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(entries)
    }
}