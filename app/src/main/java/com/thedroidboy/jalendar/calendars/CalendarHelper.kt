package com.thedroidboy.jalendar.calendars

import android.content.Context
import biweekly.property.RecurrenceRule
import biweekly.util.ByDay
import biweekly.util.DayOfWeek
import com.thedroidboy.jalendar.R

/**
 * Created by Yaakov Shahak
 * on 01/05/2018.
 */
fun ByDay.convertDayToHebrew(context: Context): String {
    return when (day) {
        DayOfWeek.SUNDAY -> {
            context.resources.getString(R.string.sunday)
        }
        DayOfWeek.MONDAY -> {
            context.resources.getString(R.string.monday)
        }
        DayOfWeek.TUESDAY -> {
            context.resources.getString(R.string.tuesday)
        }
        DayOfWeek.WEDNESDAY -> {
            context.resources.getString(R.string.wednsday)
        }
        DayOfWeek.THURSDAY -> {
            context.resources.getString(R.string.thursday)
        }
        DayOfWeek.FRIDAY -> {
            context.resources.getString(R.string.friday)
        }
        DayOfWeek.SATURDAY -> {
            context.resources.getString(R.string.saturday)
        }
    }
}

inline fun RecurrenceRule.byDayToDayOfWeekList(): List<DayOfWeek> {
    val list = mutableListOf<DayOfWeek>()
    for (day in value.byDay){
        list.add(day.day)
    }
    return list
}