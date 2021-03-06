package com.thedroidboy.jalendar.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thedroidboy.jalendar.R;
import com.thedroidboy.jalendar.adapters.PagerAdapterBase;
import com.thedroidboy.jalendar.adapters.PagerAdapterMonthDay;

import static com.thedroidboy.jalendar.adapters.PagerAdapterBase.INITIAL_OFFSET;


/**
 * Created by B.E.L on 24/11/2016.
 */

public class HebrewPickerDialog extends DialogFragment implements ViewPager.OnPageChangeListener {

    private TextView monthLabel;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_month_picker, container, false);
        monthLabel = root.findViewById(R.id.month_label);
        viewPager = root.findViewById(R.id.view_pager);
        View btnCancel = root.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(v -> dismiss());
        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(new PagerAdapterMonthDay(getChildFragmentManager(), false));
        viewPager.setCurrentItem(INITIAL_OFFSET);
        viewPager.postDelayed(() -> onPageSelected(PagerAdapterMonthDay.INITIAL_OFFSET), 200);
        return root;
    }
//
//    @Override
//    public void onDismiss(DialogInterface dialog) {
//        super.onDismiss(dialog);
//        onDatePickerDismiss = null;
//    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        PagerAdapterBase adapter = (PagerAdapterBase) viewPager.getAdapter();
        monthLabel.setText(adapter.getPageTitle(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
