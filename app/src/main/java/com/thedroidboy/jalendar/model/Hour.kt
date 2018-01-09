package com.thedroidboy.jalendar.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Yaakov Shahak
 * on 04/01/2018.
 */
data class Hour(val label: String, val hourEventForDays: List<EventForHour>) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            ArrayList<EventForHour>().apply { source.readList(this, EventForHour::class.java.classLoader) }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(label)
        writeList(hourEventForDays)
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Hour> = object : Parcelable.Creator<Hour> {
            override fun createFromParcel(source: Parcel): Hour = Hour(source)
            override fun newArray(size: Int): Array<Hour?> = arrayOfNulls(size)
        }
    }
}