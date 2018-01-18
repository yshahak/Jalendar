package com.thedroidboy.jalendar;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import android.util.SparseArray;

import com.thedroidboy.jalendar.calendars.google.GoogleManager;
import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;
import com.thedroidboy.jalendar.calendars.jewish.JewCalendarPool;
import com.thedroidboy.jalendar.model.Day;
import com.thedroidboy.jalendar.model.DayDAO;
import com.thedroidboy.jalendar.model.Month;
import com.thedroidboy.jalendar.model.MonthDAO;

import java.util.List;

import static com.thedroidboy.jalendar.calendars.google.Contract.INSTANCE_PROJECTION;

/**
 * Created by $Yaakov Shahak on 12/7/2017.
 */

public class CalendarRepoImpl implements CalendarRepo {

    public static final String TAG = CalendarRepoImpl.class.getSimpleName();
    private final MonthDAO monthDAO;
    private final DayDAO dayDAO;
    private final JewCalendar jewCalendar;
    private SparseArray<Month> monthMap;
    private SparseArray<Day> dayMap;
    private boolean threadIsRunning = true;

    public CalendarRepoImpl(MonthDAO monthDAO, DayDAO dayDAO, JewCalendar jewCalendar) {
        this.monthDAO = monthDAO;
        this.dayDAO = dayDAO;
        this.jewCalendar = jewCalendar;
        init();
    }

    private void init(){
        new Thread(() -> {
            initMonthMap();
            initDayMap();
            threadIsRunning = false;
        }).start();
    }

    private void initMonthMap() {
        monthMap = new SparseArray<>();
        int i = 0;
        int currentMonthHasCode = jewCalendar.monthHashCode();
        List<Month> list = monthDAO.getMonthSegmentForward(currentMonthHasCode, 10);
        for (Month month : list) {
            monthMap.put(i, month);
            i++;
        }
        List<Month> monthList = monthDAO.getMonthSegmentBackward(currentMonthHasCode, 10);
        i = -1;
        for (Month month : monthList) {
            monthMap.put(i, month);
            i--;
        }
    }

    private void initDayMap() {
        dayMap = new SparseArray<>();
        int i = 0;
        int currentDayHasCode = jewCalendar.dayHashCode();
        List<Day> list = dayDAO.getDaySegmentForward(currentDayHasCode, 10);
        for (Day day : list) {
            dayMap.put(i, day);
            i++;
        }
        List<Day> dayList = dayDAO.getDaySegmentBackward(currentDayHasCode, 10);
        i = -1;
        for (Day day : dayList) {
            dayMap.put(i, day);
            i--;
        }
    }

    private void getMonth(int position, MutableLiveData<Month> liveData){
        new Thread(() -> {
            while (threadIsRunning){
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Month month = monthMap.get(position);
            if (month == null) {
                JewCalendar jewCalendar = JewCalendarPool.obtain(position);
                jewCalendar.shiftMonth(position);
                pullMonth(jewCalendar);
                month = monthMap.get(position);
            }
            liveData.postValue(month);
        }).start();
    }

    private void getDay(int position, MutableLiveData<Day> liveData){
        new Thread(() -> {
            while (threadIsRunning){
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Day day = dayMap.get(position);
            liveData.postValue(day);
        }).start();
    }

    @Override
    public void insertMonth(Month month) {
        monthDAO.insertMonth(month);
    }

    @Override
    public void insertMonthDays(List<Day> monthDays) {
        dayDAO.insertMonthDays(monthDays);
    }

    @Override
    public List<Month> getMonthes(int monthHashCode, int sum) {
        return monthDAO.getMonthSegmentForward(monthHashCode, sum);
    }

    @Override
    public LiveData<Month> getMonthByPosition(int position) {
        Log.d(TAG, "getMonthByPosition: " + position);
        MutableLiveData<Month> liveData = new MutableLiveData<>();
        getMonth(position, liveData);
        return liveData;
    }

    @Override
    public LiveData<Day> getDayByPosition(int position) {
        Log.d(TAG, "getDayByPosition: " + position);
        MutableLiveData<Day> liveData = new MutableLiveData<>();
        getDay(position, liveData);
        return liveData;
    }

    @Override
    public LiveData<Month> getMonthByCalendar(JewCalendar jewCalendar) {
        int monthCode = jewCalendar.monthHashCode();
        LiveData<Month> monthLiveData = monthDAO.getMonth(monthCode);
        Month month = monthLiveData.getValue();
        if (month != null){
            addDaysToMonth(month);
        }
        return monthLiveData;
    }

    @Override
    public void pullMonth(JewCalendar jewCalendar) {
        Log.d(TAG, "getMonthByCalendar: didn't found one in db");
        Month month = new Month(jewCalendar);
        monthMap.put(jewCalendar.getCurrentPosition(), month);
        new Thread(() -> {
            insertMonth(month);
            insertMonthDays(month.getDayList());
        }).start();
    }

    private void addDaysToMonth(Month month) {
        long start = month.getStartMonthInMsIncludeOffset();
        long end = month.getEndMonthInMsIncludeOffset();
        List<Day> monthDays = dayDAO.getDaysInSegmant(start, end);
        for (Day monthDay : monthDays) {
            boolean outOfMonthRange = monthDay.getStartDayInMillis() < month.getStartMonthInMs() || monthDay.getEndDayInMillis() > month.getEndMonthInMs();
            monthDay.setOutOfMonthRange(outOfMonthRange);
        }
        month.setDayList(monthDays);
    }

    @Override
    public Cursor getMonthEventsCursor(Context context, long start, long end) {
        Uri uri = GoogleManager.getInstanceUriForInterval(start, end);
        String WHERE_CALENDARS_SELECTED = CalendarContract.Calendars.VISIBLE + " = ? "; //AND " +
        String[] WHERE_CALENDARS_ARGS = {"1"};//
        ContentResolver cr = context.getContentResolver();
        return cr.query(uri, INSTANCE_PROJECTION, WHERE_CALENDARS_SELECTED, WHERE_CALENDARS_ARGS,
                CalendarContract.Events.DTSTART + " ASC");
    }
}
