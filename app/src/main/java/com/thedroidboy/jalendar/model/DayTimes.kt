package com.thedroidboy.jalendar.model

import net.sourceforge.zmanim.ZmanimCalendar
import net.sourceforge.zmanim.hebrewcalendar.HebrewDateFormatter
import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar
import java.util.concurrent.TimeUnit


/**
 * Created by Yaakov Shahak
 * on 25/03/2018.
 */
data class DayTimes(val location: String,
                    val dayTitle: String,
                    val alos: Long,
                    val sunrise: Long,
                    val sofShmaMGA: Long,
                    val sofShmaGRA: Long,
                    val sofTfilaMGA: Long,
                    val sofTfilaGRA: Long,
                    val chatzos: Long,
                    val minchGdola: Long,
                    val minchKtana: Long,
                    val sunset: Long,
                    val dusk: Long,
                    val shabbat: Shabbat?) {

    companion object {

        private val hebrewDateFormatter = HebrewDateFormatter()

        init {
            hebrewDateFormatter.isUseGershGershayim = true
            hebrewDateFormatter.isUseLongHebrewYears = true
            hebrewDateFormatter.isHebrewFormat = true
        }

        fun create(): DayTimes {
            val zmanimCalendar = ZmanimCalendar()
            val jewishCalendar = JewishCalendar()
            val month = hebrewDateFormatter.formatMonth(jewishCalendar)
            val dayInMonth = hebrewDateFormatter.formatHebrewNumber(jewishCalendar.jewishDayOfMonth)
            val dayInWeek = hebrewDateFormatter.formatHebrewNumber(jewishCalendar.dayOfWeek)
            val year = hebrewDateFormatter.formatHebrewNumber(jewishCalendar.jewishYear)
            val date = "$dayInWeek, $dayInMonth $month, $year"

            return DayTimes("Jerusalem", date
                    , zmanimCalendar.alosHashachar.time
                    , zmanimCalendar.sunrise.time
                    , zmanimCalendar.sofZmanShmaMGA.time
                    , zmanimCalendar.sofZmanShmaGRA.time
                    , zmanimCalendar.sofZmanTfilaMGA.time
                    , zmanimCalendar.sofZmanTfilaGRA.time
                    , zmanimCalendar.chatzos.time
                    , zmanimCalendar.minchaGedola.time
                    , zmanimCalendar.minchaKetana.time
                    , zmanimCalendar.sunset.time
                    , zmanimCalendar.sunset.time + TimeUnit.MINUTES.toMillis(20)
                    , null)
        }
    }
}

data class Shabbat(val parasha: String, val knisatShabbbat: Long, val tzetShabbat: Long, val tzetShabbat72: Long)

