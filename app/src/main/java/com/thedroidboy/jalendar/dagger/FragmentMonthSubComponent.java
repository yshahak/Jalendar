package com.thedroidboy.jalendar.dagger;

import com.thedroidboy.jalendar.fragments.FragmentMonth;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by Yaakov Shahak
 * on 10/12/2017.
 */
@Subcomponent()
public interface FragmentMonthSubComponent extends AndroidInjector<FragmentMonth> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<FragmentMonth> {}
}
