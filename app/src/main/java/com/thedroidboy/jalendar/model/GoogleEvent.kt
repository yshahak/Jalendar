package com.thedroidboy.jalendar.model

import android.os.Parcel
import android.os.Parcelable
import biweekly.util.Frequency
import com.thedroidboy.jalendar.calendars.jewish.JewCalendar
import com.thedroidboy.jalendar.calendars.jewish.JewCalendar.hebrewHebDateFormatter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Yaakov Shahak
 * on 23/04/2018.
 */
data class GoogleEvent(var eventId: Long,
                       var calendarId: Long,
                       var eventTitle: String,
                       var begin: Long,
                       var end: Long,
                       var displayColor: Int,
                       var calendarDisplayName: String,
                       var dayOfMonth: Int = 0,
                       var repeatValue: Int = -1,
                       var frequency: Frequency? = null,
                       var rrule: String = "") : Parcelable,  Comparable<GoogleEvent> {

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

    constructor(source: Parcel) : this(
            source.readLong(),
            source.readLong(),
            source.readString(),
            source.readLong(),
            source.readLong(),
            source.readInt(),
            source.readString(),
            source.readInt(),
            source.readInt(),
            kotlin.run {
                val name = source.readString()
                if (name == "null") null else Frequency.valueOf(name)
            },
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(eventId)
        writeLong(calendarId)
        writeString(eventTitle)
        writeLong(begin)
        writeLong(end)
        writeInt(displayColor)
        writeString(calendarDisplayName)
        writeInt(dayOfMonth)
        writeInt(repeatValue)
        if (frequency != null) {
            dest.writeString(frequency!!.name)
        } else {
            dest.writeString("null")
        }
        writeString(rrule)
    }

    override fun compareTo(other: GoogleEvent): Int {
        return (this.begin - other.begin).toInt()
    }

    companion object {
        private val simpleLoazyDateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())

        private val simpleDateFormat = SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault())

        private val simpleEventFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        @JvmField
        val CREATOR: Parcelable.Creator<GoogleEvent> = object : Parcelable.Creator<GoogleEvent> {
            override fun createFromParcel(source: Parcel): GoogleEvent = GoogleEvent(source)
            override fun newArray(size: Int): Array<GoogleEvent?> = arrayOfNulls(size)
        }
    }
}