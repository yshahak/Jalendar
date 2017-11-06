package com.thedroidboy.jalendar;

import android.arch.paging.PagedListAdapter;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.thedroidboy.jalendar.databinding.MonthItemBinding;
import com.thedroidboy.jalendar.model.MonthVM;

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
        MonthItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.month_item, parent, false);
        return new CalendarViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(CalendarViewHolder holder, int position) {
        MonthVM monthVM = getItem(position);
        if (monthVM != null) {
            holder.bindTo(monthVM);
        }
    }

    static class CalendarViewHolder extends RecyclerView.ViewHolder {
        final MonthItemBinding binding;

        CalendarViewHolder(MonthItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.recyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 7, LinearLayoutManager.VERTICAL, false));
            binding.recyclerView.setHasFixedSize(true);

        }

        void bindTo(MonthVM monthVM) {
            binding.setMonth(monthVM);
            binding.recyclerView.setAdapter(new DayRecyclerAdapter(monthVM));
        }
    }

}

