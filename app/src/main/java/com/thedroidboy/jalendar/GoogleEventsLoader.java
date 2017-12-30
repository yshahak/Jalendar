package com.thedroidboy.jalendar;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.thedroidboy.jalendar.model.Month;

/**
 * Created by $Yaakov Shahak on 12/30/2017.
 */

public class GoogleEventsLoader extends AsyncTaskLoader<Boolean> {

    private final MonthRepo monthRepo;
    private final Month month;

    public GoogleEventsLoader(Context context, MonthRepo monthRepo, Month month) {
        super(context);
        this.monthRepo = monthRepo;
        this.month = month;
    }

    @Override
    public Boolean loadInBackground() {
        monthRepo.addMonthEvents(getContext(), month);
        return true;
    }
}
