package com.thedroidboy.jalendar.views;

import android.content.Context;
import android.util.AttributeSet;

import me.angrybyte.numberpicker.view.ActualNumberPicker;

/**
 * Created by Yaakov Shahak
 * on 29/01/2018.
 */

public class CustomNumberPicker extends ActualNumberPicker {
    public CustomNumberPicker(Context context) {
        super(context);
    }

    public CustomNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setValue(int newValue) {
        super.setValue(newValue);
        post(this::invalidate); //this fix the bug: https://github.com/milosmns/actual-number-picker/issues/16
    }
}
