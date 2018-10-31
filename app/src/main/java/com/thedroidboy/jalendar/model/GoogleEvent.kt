@file:Suppress("NON_EXHAUSTIVE_WHEN")

package com.thedroidboy.jalendar.model

import android.os.Parcelable
import android.util.Log
import biweekly.ICalVersion
import biweekly.io.ParseContext
import biweekly.io.scribe.property.RecurrenceRuleScribe
import biweekly.parameter.ICalParameters
import biweekly.property.RecurrenceRule
import biweekly.util.Frequency
import biweekly.util.Recurrence
import com.thedroidboy.jalendar.MyApplication
import com.thedroidboy.jalendar.R
import com.thedroidboy.jalendar.calendars.byDayToDayOfWeekList
import com.thedroidboy.jalendar.calendars.convertDayToHebrew
import com.thedroidboy.jalendar.calendars.google.CalendarHelper
import com.thedroidboy.jalendar.calendars.jewish.JewCalendar
import com.thedroidboy.jalendar.calendars.jewish.JewCalendar.hebrewHebDateFormatter
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import net.sourceforge.zmanim.hebrewcalendar.JewishDate
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Yaakov Shahak
 * on 23/04/2018.
 */
@Parcelize
data class GoogleEvent(var eventId: Long,
                       var calendarId: Long,
                       var eventTitle: String,
                       var begin: Long,
                       var end: Long,
                       val displayColor: Int,
                       var dayOfMonth: Int = 0,
                       var stringOccurenceRule: String? = null,
                       val allDayEvent: Boolean = false) : Parcelable, Comparable<GoogleEvent> {

    @IgnoredOnParcel
    var recurrenceRule: RecurrenceRule? = null

    companion object {
        private val simpleLoazyDateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
        private val simpleEventFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        private val scribe = RecurrenceRuleScribe()
        private val parseContext = ParseContext().also {
            it.version = ICalVersion.V2_0
        }

        fun newInstance(eventId: Long,
                        calendarId: Long,
                        eventTitle: String,
                        begin: Long,
                        end: Long,
                        displayColor: Int,
                        dayOfMonth: Int = 0,
                        rrule: String?): GoogleEvent {
            return GoogleEvent(eventId, calendarId, eventTitle, begin, end, displayColor, dayOfMonth, rrule)
        }
    }

    fun convertRruleToFrequencyAndRepeatValue() {
        stringOccurenceRule?.let {
            recurrenceRule = scribe.parseText(stringOccurenceRule, null, ICalParameters(), parseContext)
        }
    }

    fun getRepeatTitle(): String {
        val ctx = MyApplication.getInstance()
        recurrenceRule?.let { rule ->
            val builder = StringBuilder()
            rule.value.frequency?.also { frequency ->
                when (frequency) {
                    Frequency.DAILY -> builder.append(ctx.getString(R.string.instance_daily))
                    Frequency.WEEKLY -> builder.append(ctx.getString(R.string.instance_weekly))
                    Frequency.MONTHLY -> builder.append(ctx.getString(R.string.instance_monthly))
                    Frequency.YEARLY -> builder.append(ctx.getString(R.string.instance_yearly))
                }
            }
            rule.value.byDay?.takeIf { it.size > 0 }?.let {
                builder.append(" בימי ")
                for (day in it) {
                    day?.let { builder.append(it.convertDayToHebrew(ctx)).append(" ") }
                }
            }
            rule.value.count?.takeIf { it > 0 }?.let { builder.append(" ").append(it).append(" פעמים") }
            rule.value.until?.let {
                builder.append(" עד ").append(simpleLoazyDateFormat.format(it)).append(" ").append(hebrewHebDateFormatter.format(JewishDate(it))) }
            return builder.toString()
        }
        return ctx.getString(R.string.instance_single)
    }

    fun getRepeatHeader(): String {
        return if (getRepeatValue() > 0) {
            "חזרות:"
        } else {
            "חזרות: ללא הגבלה"
        }
    }

    fun getRepeatValue(): Int {
        return recurrenceRule?.value?.count ?: 0
    }

    fun setRepeatValue(value: Int) {
        if (value != getRepeatValue()) {
            recurrenceRule?.let { rule ->
                val recurrence = Recurrence.Builder(rule.value.frequency).byDay(rule.byDayToDayOfWeekList()).count(value).build()
                recurrenceRule = RecurrenceRule(recurrence)
            }
        }
    }

    fun setFrequency(value: Frequency) {
        //todo need to complete on which day to start when switch from daily to other forms
        recurrenceRule = RecurrenceRule(Recurrence.Builder(value)./*count(getRepeatValue()).*/build())
    }

    fun clearFrequency() {
        recurrenceRule = null
    }


    fun getEventTime(): String {
        return if (begin == -1L) {
            ""
        } else getStartEventHour() + " - " + getEndEventHour()
    }

    fun getStartEventDate(): String {
        val calendar = JewCalendar(Date(begin))
        val date = hebrewHebDateFormatter.formatHebrewNumber(calendar.jewishDayOfMonth) + " " +
                hebrewHebDateFormatter.formatMonth(calendar)
        return hebrewHebDateFormatter.formatDayOfWeek(calendar) + " , " + date
    }

    fun getStartEventDateLoazy(): String {
        return simpleLoazyDateFormat.format(Date(begin))
    }

    fun getEndEventDate(): String {
        val calendar = JewCalendar(Date(end))
        val date = hebrewHebDateFormatter.formatHebrewNumber(calendar.getJewishDayOfMonth()) + " " +
                hebrewHebDateFormatter.formatMonth(calendar)
        return hebrewHebDateFormatter.formatDayOfWeek(calendar) + " , " + date
    }

    fun getEndEventDateLoazy(): String {
        return simpleLoazyDateFormat.format(Date(end))
    }

    fun getStartEventHour(): String {
        return simpleEventFormat.format(begin)
    }

    fun getEndEventHour(): String {
        return simpleEventFormat.format(end)
    }

    fun getRepeatVisibility(): Boolean {
        return recurrenceRule?.value?.frequency != null
    }

    fun isRecuuringEvent() = recurrenceRule != null

    override fun compareTo(other: GoogleEvent): Int {
        return (this.begin - other.begin).toInt()
    }

    fun onCalIdSelected(selected: Int) {
        Log.d("TAG", "selected=${CalendarHelper.accountToIdsMap.keys.toTypedArray()[selected]}")
        calendarId = CalendarHelper.accountToIdsMap.get(CalendarHelper.accountToIdsMap.keys.toTypedArray()[selected])!!.toLong()
    }

}