package com.thedroidboy.jalendar.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Yaakov Shahak on 12/7/2017.
 */

@Dao
public interface MonthDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMonthes(Month... months);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMonth(Month month);

    @Query("SELECT * FROM Month")
    List<Month> getAllMonthes();

    @Query("SELECT * FROM Month WHERE monthHashCode >= :monthCodeStart ORDER BY monthHashCode ASC LIMIT :sum")
    List<Month> getMonthSegmentForward(int monthCodeStart, int sum);

    @Query("SELECT * FROM Month WHERE monthHashCode < :monthCodeStart ORDER BY monthHashCode DESC LIMIT :sum")
    List<Month> getMonthSegmentBackward(int monthCodeStart, int sum);

    @Query("SELECT * FROM Month WHERE monthHashCode = :monthCode LIMIT 1")
    LiveData<Month> getMonth(int monthCode);

}
