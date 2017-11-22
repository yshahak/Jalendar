package com.thedroidboy.jalendar.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.thedroidboy.jalendar.SimpleWeekView;
import com.thedroidboy.jalendar.model.MonthVM;

/**
 * Created by Yaakov Shahak
 * on 05/11/2017.
 */

public class RecyclerAdapterMonth extends RecyclerView.Adapter<RecyclerAdapterMonth.WeekViewHolder> {

    private static final String TAG = RecyclerAdapterMonth.class.getSimpleName();
    private final MonthVM monthVM;

    public RecyclerAdapterMonth(MonthVM monthVM) {
        this.monthVM = monthVM;
    }

    @Override
    public WeekViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SimpleWeekView simpleWeekView = new SimpleWeekView(parent.getContext());
        parent.addView(simpleWeekView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new WeekViewHolder(simpleWeekView);
    }

    @Override
    public void onBindViewHolder(WeekViewHolder holder, int position) {
        int normalPosition = position * 7;
        ((SimpleWeekView)holder.itemView).bindDays(monthVM.getDayList().subList(normalPosition, normalPosition + 7));
    }

    @Override
    public int getItemCount() {
        return monthVM.getDayList().size() / 7;
    }

    static class WeekViewHolder extends RecyclerView.ViewHolder{

        WeekViewHolder(View view) {
            super(view);
        }
    }

}

