package com.thedroidboy.jalendar;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

import com.thedroidboy.jalendar.model.Converters;
import com.thedroidboy.jalendar.model.Day;
import com.thedroidboy.jalendar.model.DayDAO;
import com.thedroidboy.jalendar.model.Month;
import com.thedroidboy.jalendar.model.MonthDAO;

/**
 * Created by Yaakov Shahak on 12/7/2017.
 */

@Database(entities = {Month.class, Day.class}, version = 2)
@TypeConverters({Converters.class})

public abstract class AppDatabase extends RoomDatabase {

    public abstract MonthDAO monthDAO();

    public abstract DayDAO dayDAO();

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };
}
