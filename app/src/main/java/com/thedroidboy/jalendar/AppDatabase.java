package com.thedroidboy.jalendar;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.thedroidboy.jalendar.model.Converters;
import com.thedroidboy.jalendar.model.Day;
import com.thedroidboy.jalendar.model.DayDAO;
import com.thedroidboy.jalendar.model.Month;
import com.thedroidboy.jalendar.model.MonthDAO;

/**
 * Created by Yaakov Shahak on 12/7/2017.
 */

@Database(entities = {Month.class, Day.class}, version = 1)
@TypeConverters({Converters.class})

public abstract class AppDatabase extends RoomDatabase {

    public abstract MonthDAO monthDAO();

    public abstract DayDAO dayDAO();
}
