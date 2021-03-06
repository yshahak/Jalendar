package com.thedroidboy.jalendar.dagger;

import android.app.Application;

import com.thedroidboy.jalendar.MyApplication;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by Yaakov Shahak
 * on 10/12/2017.
 */
@Singleton
@Component(modules = {AppModule.class, BuildersModule.class, AndroidInjectionModule.class, AndroidSupportInjectionModule.class})//
public interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        AppComponent build();
    }

    void inject(MyApplication app);
}