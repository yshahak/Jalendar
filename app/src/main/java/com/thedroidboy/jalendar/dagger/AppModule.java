package com.thedroidboy.jalendar.dagger;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.thedroidboy.jalendar.AppDatabase;
import com.thedroidboy.jalendar.CalendarRepo;
import com.thedroidboy.jalendar.CalendarRepoImpl;
import com.thedroidboy.jalendar.model.DayDAO;
import com.thedroidboy.jalendar.model.MonthDAO;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Yaakov Shahak
 * on 10/12/2017.
 */

@Module
public class AppModule {


    @Provides @Singleton
    Context provideContext(Application application){
        return application;
    }

    @Provides @Singleton
    AppDatabase provideAppDataBase(Context context){
        return Room.databaseBuilder(context, AppDatabase.class, "jalendar").build();
    }

    @Provides
    SharedPreferences provideSharedPrefs(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    MonthDAO provideMonthDAO(AppDatabase appDatabase){
        return appDatabase.monthDAO();
    }

    @Provides
    DayDAO provideDayDAO(AppDatabase appDatabase){
        return appDatabase.dayDAO();
    }

    @Provides @Singleton
    CalendarRepo provideMonthRepo(MonthDAO monthDAO, DayDAO dayDAO){
        return new CalendarRepoImpl(monthDAO, dayDAO);
    }
}
