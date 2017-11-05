package com.thedroidboy.jalendar;

import android.arch.paging.PagedListAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Yaakov Shahak
 * on 05/11/2017.
 */

public class CalendarRecyclerAdapter extends PagedListAdapter<MonthVM, CalendarRecyclerAdapter.CalendarViewHolder> {

    private static final String TAG = CalendarRecyclerAdapter.class.getSimpleName();

    protected CalendarRecyclerAdapter() {
        super(MonthVM.DIFF_CALLBACK);
    }

    @Override
    public CalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new CalendarViewHolder(layoutInflater.inflate(R.layout.month_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CalendarViewHolder holder, int position) {
        MonthVM monthVM = getItem(position);
        if (monthVM != null) {
            holder.bindTo(monthVM);
        }
    }

    static class CalendarViewHolder extends RecyclerView.ViewHolder{
        private TextView monthLabel;

        CalendarViewHolder(View itemView) {
            super(itemView);
            monthLabel = itemView.findViewById(R.id.month_label);
        }

        public void bindTo(MonthVM monthVM){
            Log.d(TAG, "binding:" + monthVM.getMonthHebName());
            monthLabel.setText(monthVM.getMonthHebName());
        }
    }

}

