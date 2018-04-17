package com.thedroidboy.jalendar.model

import android.preference.PreferenceManager
import com.thedroidboy.jalendar.MyApplication
import com.thedroidboy.jalendar.utils.LocationHelper
import net.sourceforge.zmanim.ZmanimCalendar
import net.sourceforge.zmanim.hebrewcalendar.HebrewDateFormatter
import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar
import java.util.*
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
                    val shabbat: Shabbat?,
                    val dafYomi: String) {

    companion object {

        private val hebrewDateFormatter = HebrewDateFormatter()

        init {
            hebrewDateFormatter.isUseGershGershayim = true
            hebrewDateFormatter.isUseLongHebrewYears = true
            hebrewDateFormatter.isHebrewFormat = true
        }

        fun create(time: Long): DayTimes {
            val geoLocation = LocationHelper.getChosenGeoLocation(PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance()))

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = time
            val zmanimCalendar = ZmanimCalendar(geoLocation)
            zmanimCalendar.calendar = calendar
            val jewishCalendar = JewishCalendar(calendar)
            val month = hebrewDateFormatter.formatMonth(jewishCalendar)
            val dayInMonth = hebrewDateFormatter.formatHebrewNumber(jewishCalendar.jewishDayOfMonth)
            val dayInWeek = hebrewDateFormatter.formatHebrewNumber(jewishCalendar.dayOfWeek)
            val year = hebrewDateFormatter.formatHebrewNumber(jewishCalendar.jewishYear)
            val date = "$dayInWeek, $dayInMonth $month, $year"
            var shabbat: Shabbat? = null
            try {
                if (jewishCalendar.dayOfWeek == 6) {
                    val knisa = zmanimCalendar.candleLighting.time
                    val tzet = (zmanimCalendar.tzais.time)
                    val tzet72 = (zmanimCalendar.tzais72.time)

                    val shift24 = Date(jewishCalendar.time.time + TimeUnit.DAYS.toMillis(1))
                    val shiftCalendar = JewishCalendar()
                    shiftCalendar.setDate(shift24)
                    val parahsh = hebrewDateFormatter.formatParsha(shiftCalendar)

                    shabbat = Shabbat(parahsh, knisa, tzet, tzet72)
                } else if (jewishCalendar.dayOfWeek == 7) {
                    val parahsh = hebrewDateFormatter.formatParsha(jewishCalendar)

                    val now = jewishCalendar.time.time
                    val shift24 = Date(now - TimeUnit.DAYS.toMillis(1))
                    val shift = Calendar.getInstance()
                    shift.time = shift24
                    zmanimCalendar.calendar = shift
                    val knisa = zmanimCalendar.candleLighting.time
                    val tzet = (zmanimCalendar.tzais.time)
                    val tzet72 = (zmanimCalendar.tzais72.time)
                    shabbat = Shabbat(parahsh, knisa, tzet, tzet72)
                    shift.timeInMillis = now
                    zmanimCalendar.calendar = shift
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return DayTimes(geoLocation.locationName, date
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
                    , shabbat
                    , hebrewDateFormatter.formatDafYomiBavli(jewishCalendar.dafYomiBavli))
        }
    }
}

data class Shabbat(val parasha: String, val knisatShabbbat: Long, val tzetShabbat: Long, val tzetShabbat72: Long)

