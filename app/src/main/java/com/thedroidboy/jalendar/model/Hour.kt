package com.thedroidboy.jalendar.model

import android.os.Parcel
import android.os.Parcelable
import com.thedroidboy.jalendar.calendars.google.EventInstance

/**
 * Created by Yaakov Shahak
 * on 04/01/2018.
 */
data class Hour(val label: String, val hourEvents: List<EventInstance>) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            ArrayList<EventInstance>().apply { source.readList(this, EventInstance::class.java.classLoader) }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(label)
        writeList(hourEvents)
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Hour> = object : Parcelable.Creator<Hour> {
            override fun createFromParcel(source: Parcel): Hour = Hour(source)
            override fun newArray(size: Int): Array<Hour?> = arrayOfNulls(size)
        }
    }
}