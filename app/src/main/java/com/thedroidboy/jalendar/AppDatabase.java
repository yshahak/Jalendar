package com.thedroidboy.jalendar;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.thedroidboy.jalendar.model.MonthDAO;
import com.thedroidboy.jalendar.model.Month;

/**
 * Created by $Yaakov Shahak on 12/7/2017.
 */

@Database(entities = {Month.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract MonthDAO monthDAO();
}
