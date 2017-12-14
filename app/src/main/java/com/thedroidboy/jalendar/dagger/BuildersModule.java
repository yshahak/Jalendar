package com.thedroidboy.jalendar.dagger;

import android.support.v4.app.Fragment;

import com.thedroidboy.jalendar.fragments.FragmentMonth;

import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

/**
 * Created by Yaakov Shahak
 * on 10/12/2017.
 */

@Module (subcomponents = FragmentMonthSubComponent.class)
public abstract class BuildersModule {

    @Binds
    @IntoMap
    @dagger.android.support.FragmentKey(FragmentMonth.class)
    abstract AndroidInjector.Factory<? extends Fragment> bindFragmentMonthInjectorFactory(FragmentMonthSubComponent.Builder builder);
}
