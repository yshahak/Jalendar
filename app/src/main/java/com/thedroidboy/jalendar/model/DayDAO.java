package com.thedroidboy.jalendar.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Yaakov Shahak on 12/7/2017.
 */

@Dao
public interface DayDAO {

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insertMonthes(Month... months);
//
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDay(Day month);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMonthDays(List<Day> monthDays);

    @Query("SELECT * FROM Day")
    List<Day> getAllDays();

//    @Query("SELECT * FROM Month WHERE monthHashCode >= :monthCodeStart ORDER BY monthHashCode ASC LIMIT :sum")
//    LiveData<List<Month>> getMonthSegment(int monthCodeStart, int sum);
//
//    @Query("SELECT * FROM Month WHERE monthHashCode LIKE :monthCode")
//    LiveData<Month> getMonth(int monthCode);

}
