package com.thedroidboy.jalendar.model

import android.os.Parcelable
import biweekly.ICalVersion
import biweekly.io.ParseContext
import biweekly.io.scribe.property.RecurrenceRuleScribe
import biweekly.parameter.ICalParameters
import biweekly.property.RecurrenceRule
import biweekly.util.Frequency
import com.thedroidboy.jalendar.MyApplication
import com.thedroidboy.jalendar.R
import com.thedroidboy.jalendar.calendars.jewish.JewCalendar
import com.thedroidboy.jalendar.calendars.jewish.JewCalendar.hebrewHebDateFormatter
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
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
                       var rrule: String? = null,
                       val allDayEvent: Boolean = false) : Parcelable, Comparable<GoogleEvent> {

    @IgnoredOnParcel
    var repeatValue: Int = -1
    @IgnoredOnParcel
    var frequency: Frequency? = null
    @IgnoredOnParcel
    var rule: RecurrenceRule? = null

    companion object {
        private val simpleLoazyDateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
        private val simpleEventFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        private val scribe = RecurrenceRuleScribe()
        private val parseContext = ParseContext().also {
            it.version = ICalVersion.V2_0;
        }

        fun newInstance(eventId: Long,
                        calendarId: Long,
                        eventTitle: String,
                        begin: Long,
                        end: Long,
                        displayColor: Int,
                        dayOfMonth: Int = 0,
                        rrule: String): GoogleEvent {
            return GoogleEvent(eventId, calendarId, eventTitle, begin, end, displayColor, dayOfMonth, rrule)
        }
    }

    fun convertRruleToFrequencyAndRepeatValue() {
        if (rrule == null) {
            return
        }
        val rrule = scribe.parseText(rrule, null, ICalParameters(), parseContext)
        this.frequency = rrule.value.frequency
        this.repeatValue = if (rrule.value.count != null) rrule.value.count else -1
        if (frequency == Frequency.WEEKLY && rrule.value != null) {
            val days = rrule.value.byDay
            for (day in days) {

            }
        }
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

    fun getRepeatTitle(): String {

        val ctx = MyApplication.getInstance()
        if (frequency == null) {
            return ctx.getString(R.string.instance_single)
        }
        when (frequency) {
            Frequency.DAILY -> return ctx.getString(R.string.instance_daily)
            Frequency.WEEKLY -> return ctx.getString(R.string.instance_weekly)
            Frequency.MONTHLY -> return ctx.getString(R.string.instance_monthly)
            Frequency.YEARLY -> return ctx.getString(R.string.instance_yearly)
        }
        return ctx.getString(R.string.instance_single)
    }

    fun getRepeatVisibility(): Boolean {
        return frequency != null
    }


    override fun compareTo(other: GoogleEvent): Int {
        return (this.begin - other.begin).toInt()
    }

}