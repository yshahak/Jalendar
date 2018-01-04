package com.thedroidboy.jalendar;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.thedroidboy.jalendar.databinding.DayItemBinding;
import com.thedroidboy.jalendar.model.Day;
import com.thedroidboy.jalendar.model.Month;


/**
 * Created by Yaakov Shahak
 * on 05/11/2017.
 */

public class DayRecyclerAdapter extends RecyclerView.Adapter<DayRecyclerAdapter.DayViewHolder> {

    private static final String TAG = DayRecyclerAdapter.class.getSimpleName();
    private final Month month;

    public DayRecyclerAdapter(Month month) {
        this.month = month;
    }

    @Override
    public DayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        DayItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.day_item_for_month, parent, false);
        return new DayViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(DayViewHolder holder, int position) {
        holder.bindTo(month.getDayList().get(position));
    }

    @Override
    public int getItemCount() {
        return month.getDayList().size();
    }

    static class DayViewHolder extends RecyclerView.ViewHolder{
        final DayItemBinding binding;

        DayViewHolder( DayItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindTo(Day day){
//            Log.d(TAG, "bindTo: day" + day.toString());
            binding.setDay(day);
        }
    }

}

