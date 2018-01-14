package com.thedroidboy.jalendar.dagger;

import com.thedroidboy.jalendar.MainActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by $Yaakov Shahak on 1/14/2018.
 */
@Subcomponent()
public interface MainActivitySubComponent  extends AndroidInjector<MainActivity> {
    @Subcomponent.Builder
    public abstract class Builder extends AndroidInjector.Builder<MainActivity> {}
}