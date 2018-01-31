package com.thedroidboy.jalendar.adapters;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thedroidboy.jalendar.CreteIvriEventActivity;
import com.thedroidboy.jalendar.R;
import com.thedroidboy.jalendar.databinding.EventItemForDayBinding;
import com.thedroidboy.jalendar.model.Day;
import com.thedroidboy.jalendar.model.EventInstanceForDay;


/**
 * Created by Yaakov Shahak
 * on 05/11/2017.
 */

public class RecyclerAdapterDay extends RecyclerView.Adapter<RecyclerAdapterDay.DayViewHolder> {

    private static final String TAG = RecyclerAdapterDay.class.getSimpleName();
    private final Day day;

    public RecyclerAdapterDay(Day day) {
        this.day = day;
        if (day.getGoogleEventInstanceForDays().size() == 0){
            day.getGoogleEventInstanceForDays().add(new EventInstanceForDay(-1, "אין אירועים ליום זה",
                     -1, -1, Color.TRANSPARENT, "", -1));
        }
    }

    @Override
    public DayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        EventItemForDayBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.event_item_for_day, parent, false);
        return new DayViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(DayViewHolder holder, int position) {
//        Log.d(TAG, "onBindViewHolder: " + position + "\t" + day.getGoogleEventInstanceForDays().get(position));
        holder.bindTo(day.getGoogleEventInstanceForDays().get(position));
    }

    @Override
    public int getItemCount() {
        return day.getGoogleEventInstanceForDays().size();
    }

    class DayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final EventItemForDayBinding binding;

        DayViewHolder(EventItemForDayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        void bindTo(EventInstanceForDay event) {
            binding.setEvent(event);
        }

        @Override
        public void onClick(View v) {
            EventInstanceForDay event = day.getGoogleEventInstanceForDays().get(getAdapterPosition());
            if (event.getEventTitle().equals("אין אירועים ליום זה")){
                return;
            }
            Intent intent = new Intent(itemView.getContext(), CreteIvriEventActivity.class);
            intent.putExtra(CreteIvriEventActivity.EXTRA_EVENT, event);
            itemView.getContext().startActivity(intent);
        }
    }

}

