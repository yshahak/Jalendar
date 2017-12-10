package com.thedroidboy.jalendar.dagger;

import android.app.Activity;

import com.thedroidboy.jalendar.MainActivity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

/**
 * Created by Yaakov Shahak
 * on 10/12/2017.
 */

@Module
public abstract class BuilderModules {

    @Binds
    @IntoMap
    @ActivityKey(MainActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindYourActivityInjectorFactory(MainActivitySubComponent.Builder builder);
}
