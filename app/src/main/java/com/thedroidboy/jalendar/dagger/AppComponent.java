package com.thedroidboy.jalendar.dagger;

import com.thedroidboy.jalendar.MyApplication;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

/**
 * Created by Yaakov Shahak
 * on 10/12/2017.
 */

@Component(modules = { AndroidInjectionModule.class,  AppModule.class,  BuilderModules.class})
public interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(MyApplication application);
        AppComponent build();
    }

    void inject(MyApplication app);
}