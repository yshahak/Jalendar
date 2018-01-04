package com.thedroidboy.jalendar.model

import com.thedroidboy.jalendar.calendars.google.EventInstance

/**
 * Created by Yaakov Shahak
 * on 04/01/2018.
 */
data class Hour(val label: String, val hourEvents : List<EventInstance>)