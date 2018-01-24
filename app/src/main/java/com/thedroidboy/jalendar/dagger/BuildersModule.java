package com.thedroidboy.jalendar.dagger;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.thedroidboy.jalendar.CreteIvriEventActivity;
import com.thedroidboy.jalendar.MainActivity;
import com.thedroidboy.jalendar.fragments.FragmentDay;
import com.thedroidboy.jalendar.fragments.FragmentMonth;

import dagger.Binds;
import dagger.Module;
import dagger.Subcomponent;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

/**
 * Created by Yaakov Shahak
 * on 10/12/2017.
 */

@Module (subcomponents = {FragmentMonthSubComponent.class, FragmentDaySubComponent.class, MainActivitySubComponent.class, CreateEventActivitySubComponent.class})
public abstract class BuildersModule {

    @Binds
    @IntoMap
    @ActivityKey(MainActivity.class)
    abstract AndroidInjector.Factory<? extends Activity>
    bindMainActivityInjectorFactory(MainActivitySubComponent.Builder builder);

    @Binds
    @IntoMap
    @ActivityKey(CreteIvriEventActivity.class)
    abstract AndroidInjector.Factory<? extends Activity>
    bindCreateEventActivityInjectorFactory(CreateEventActivitySubComponent.Builder builder);

    @Binds
    @IntoMap
    @dagger.android.support.FragmentKey(FragmentMonth.class)
    abstract AndroidInjector.Factory<? extends Fragment> bindFragmentMonthInjectorFactory(FragmentMonthSubComponent.Builder builder);

    @Binds
    @IntoMap
    @dagger.android.support.FragmentKey(FragmentDay.class)
    abstract AndroidInjector.Factory<? extends Fragment> bindFragmentDayInjectorFactory(FragmentDaySubComponent.Builder builder);
}

@Subcomponent()
interface MainActivitySubComponent  extends AndroidInjector<MainActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<MainActivity> {}
}

@Subcomponent()
interface CreateEventActivitySubComponent  extends AndroidInjector<CreteIvriEventActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<CreteIvriEventActivity> {}
}

@Subcomponent()
interface FragmentMonthSubComponent extends AndroidInjector<FragmentMonth> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<FragmentMonth> {}
}
@Subcomponent()
interface FragmentDaySubComponent extends AndroidInjector<FragmentDay> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<FragmentDay> {}
}