package com.thedroidboy.jalendar.dagger;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.thedroidboy.jalendar.MainActivity;
import com.thedroidboy.jalendar.fragments.FragmentMonth;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

/**
 * Created by Yaakov Shahak
 * on 10/12/2017.
 */

@Module (subcomponents = {FragmentMonthSubComponent.class, MainActivitySubComponent.class})
public abstract class BuildersModule {

    @Binds
    @IntoMap
    @ActivityKey(MainActivity.class)
    abstract AndroidInjector.Factory<? extends Activity>
    bindMainActivityInjectorFactory(MainActivitySubComponent.Builder builder);

    @Binds
    @IntoMap
    @dagger.android.support.FragmentKey(FragmentMonth.class)
    abstract AndroidInjector.Factory<? extends Fragment> bindFragmentMonthInjectorFactory(FragmentMonthSubComponent.Builder builder);
}
