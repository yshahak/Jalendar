package com.thedroidboy.jalendar.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thedroidboy.jalendar.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * Created by Yaakov Shahak
 * on 26/03/2018.
 */

public class RowDayDetails extends LinearLayout {
    private String mHeader, rowValue;
    private TextView headerTextView, valueTextView;

    public RowDayDetails(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RowDayDetails(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        setOrientation(VERTICAL);
        inflate(getContext(), R.layout.row_day_details, this);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RowDayDetails,
                0, 0);

        try {
            mHeader = a.getString(R.styleable.RowDayDetails_header);
            rowValue = a.getString(R.styleable.RowDayDetails_row_value);
        } finally {
            a.recycle();
        }
        headerTextView = findViewById(R.id.row_text_header);
        headerTextView.setText(mHeader);
        valueTextView = findViewById(R.id.row_text_value);
        valueTextView.setText(rowValue);
    }

    @BindingAdapter("bind:row_value")
    public static void bindRowValue(RowDayDetails view, Long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        view.setRowValue(String.format(Locale.getDefault(), "%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
    }

    @BindingAdapter("bind:row_value")
    public static void bindRowValue(RowDayDetails view, String value) {
        view.setRowValue(value);
    }

    public void setRowValue(String rowValue) {
        this.rowValue = rowValue;
        valueTextView.setText(rowValue);
    }
}
