package com.thedroidboy.jalendar.dagger;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.thedroidboy.jalendar.AppDatabase;
import com.thedroidboy.jalendar.MonthRepo;
import com.thedroidboy.jalendar.MonthRepoImpl;
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
    MonthDAO provideMonthDAO(AppDatabase appDatabase){
        return appDatabase.monthDAO();
    }

    @Provides
    DayDAO provideDayDAO(AppDatabase appDatabase){
        return appDatabase.dayDAO();
    }

    @Provides @Singleton
    MonthRepo provideMonthRepo(MonthDAO monthDAO, DayDAO dayDAO){
        return new MonthRepoImpl(monthDAO, dayDAO);
    }
}
