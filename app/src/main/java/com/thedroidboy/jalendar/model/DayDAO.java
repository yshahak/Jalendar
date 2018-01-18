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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDay(Day month);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMonthDays(List<Day> monthDays);

    @Query("SELECT * FROM Day")
    List<Day> getAllDays();

    @Query("SELECT * FROM DAY WHERE startDayInMillis >= :start AND startDayInMillis <= :end ORDER BY dayHashCode ASC")
    List<Day> getDaysInSegmant(long start, long end);

    @Query("SELECT * FROM Day WHERE dayHashCode >= :dayCodeStart ORDER BY dayHashCode ASC LIMIT :sum")
    List<Day> getDaySegmentForward(int dayCodeStart, int sum);

    @Query("SELECT * FROM Day WHERE dayHashCode < :dayCodeStart ORDER BY dayHashCode DESC LIMIT :sum")
    List<Day> getDaySegmentBackward(int dayCodeStart, int sum);


}
