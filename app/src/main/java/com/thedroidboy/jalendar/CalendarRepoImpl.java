package com.thedroidboy.jalendar;

import android.annotation.SuppressLint;
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

import java.util.HashMap;
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
    private HashMap<Day, Integer> dayToPositionMap;
    private boolean threadIsRunning = true;

    public CalendarRepoImpl(MonthDAO monthDAO, DayDAO dayDAO, JewCalendar jewCalendar) {
        this.monthDAO = monthDAO;
        this.dayDAO = dayDAO;
        this.jewCalendar = jewCalendar;
        init();
    }

    @SuppressLint("UseSparseArrays")
    private void init(){
        new Thread(() -> {
            dayMap = new SparseArray<>();
            dayToPositionMap = new HashMap<>();
            monthMap = new SparseArray<>();
            initMonthMap(0);
            initDayMap();
            threadIsRunning = false;
        }).start();
    }

    private void initMonthMap(int position) {
        int i = position;
        int startMonthCode = JewCalendarPool.obtain(position).monthHashCode();
        List<Month> list = monthDAO.getMonthSegmentForward(startMonthCode, 10);
        for (Month month : list) {
            monthMap.put(month.getMonthHashCode(), month);
            i++;
        }
        List<Month> monthList = monthDAO.getMonthSegmentBackward(startMonthCode, 10);
        i = position - 1;
        for (Month month : monthList) {
            monthMap.put(month.getMonthHashCode(), month);
            i--;
        }
    }

    private void initDayMap() {
        int i = 0;
        int currentDayHasCode = jewCalendar.dayHashCode();
        List<Day> list = dayDAO.getDaySegmentForward(currentDayHasCode, 100);
        for (Day day : list) {
            dayMap.put(i, day);
            dayToPositionMap.put(day, i);
            i++;
        }
        List<Day> dayList = dayDAO.getDaySegmentBackward(currentDayHasCode, 100);
        i = -1;
        for (Day day : dayList) {
            dayMap.put(i, day);
            dayToPositionMap.put(day, i);
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
            int monthHashCode = JewCalendarPool.obtain(position).monthHashCode();
            Month month = monthMap.get(monthHashCode);
            if (month == null) {
                Log.d(TAG, "getMonth: month is null: " + monthHashCode);
                initMonthMap(position);
                month = monthMap.get(monthHashCode);
                if (month == null) {
                    JewCalendar jewCalendar = JewCalendarPool.obtain(position);
                    pullMonth(jewCalendar);
                    month = monthMap.get(monthHashCode);
                }
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
            if (day == null) {
                //todo complete this
            }
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
//        Log.d(TAG, "getMonthByPosition: " + position);
        MutableLiveData<Month> liveData = new MutableLiveData<>();
        getMonth(position, liveData);
        return liveData;
    }

    @Override
    public LiveData<Day> getDayByPosition(int position) {
//        Log.d(TAG, "getDayByPosition: " + position);
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
        monthMap.put(month.getMonthHashCode(), month);
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

    @Override
    public Integer getPositionForDay(Day day) {
        return dayToPositionMap.get(day);
    }
}
