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
    LiveData<List<Month>> getMonthSegment(int monthCodeStart, int sum);

    @Query("SELECT * FROM Month WHERE monthHashCode LIKE :monthCode")
    LiveData<Month> getMonth(int monthCode);

}
