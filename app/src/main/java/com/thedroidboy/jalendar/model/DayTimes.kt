package com.thedroidboy.jalendar.model

import net.sourceforge.zmanim.ZmanimCalendar
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

    //    private val iconId: Int = 0
//    private val dayTitle: String? = null
//    private val dayOfWeek: String? = null
//    private val dayOfMonth: String? = null
//    private val month: String? = null
//    private val year: String? = null
//    private val location: String? = null
//    private val candleOffsetInMinutes: Int = 0
//    private val parash: String? = null
//    private val knisatShabbbat:  
//    private val tzetShabbat:  
//    private val tzetShabbat72:  
//    private val isShabbat: Boolean = false
    companion object {
        fun create () : DayTimes {
            val zmanimCalendar = ZmanimCalendar()
            val title = "title"
            return DayTimes("Jerusalem", title
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

data class Shabbat (val parasha: String, val knisatShabbbat: Long, val tzetShabbat:Long, val tzetShabbat72: Long)

