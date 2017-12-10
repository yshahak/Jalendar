package com.thedroidboy.jalendar.dagger;

import com.thedroidboy.jalendar.MainActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by Yaakov Shahak
 * on 10/12/2017.
 */
@Subcomponent()
public interface MainActivitySubComponent extends AndroidInjector<MainActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<MainActivity> {}
}
