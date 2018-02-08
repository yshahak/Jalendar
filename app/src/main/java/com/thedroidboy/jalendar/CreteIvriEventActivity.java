package com.thedroidboy.jalendar;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TimePicker;

import com.thedroidboy.jalendar.calendars.google.GoogleManager;
import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;
import com.thedroidboy.jalendar.databinding.ActivityCreateIvriEventBinding;
import com.thedroidboy.jalendar.fragments.HebrewPickerDialog;
import com.thedroidboy.jalendar.fragments.TimePickerFragment;
import com.thedroidboy.jalendar.model.Day;
import com.thedroidboy.jalendar.model.EventInstanceForDay;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import me.angrybyte.numberpicker.listener.OnValueChangeListener;

import static com.thedroidboy.jalendar.calendars.google.Contract.KEY_HEBREW_ID;

/**
 * Created by B.E.L on 31/10/2016.
 */

@SuppressWarnings("unused")
public class CreteIvriEventActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener , KeyboardVisibilityEventListener
        , PopupMenu.OnMenuItemClickListener, View.OnClickListener, OnValueChangeListener, TextWatcher {

    public static final String EXTRA_EVENT = "EXTRA_EVENT" ;
    public static final String EXTRA_USE_CURRENT_DAY = "EXTRA_USE_CURRENT_DAY" ;
    public static JewCalendar currentCalendar;
    private ActivityCreateIvriEventBinding binding;
    @Inject
    CalendarRepo calendarRepo;
    @Inject
    SharedPreferences prefs;
    private PICKER_STATE pickerState;
    private Calendar calendar;
    private DialogFragment hebrewPickerDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_ivri_event);
        setSupportActionBar(binding.myToolbar);
        binding.countPicker.setListener(this);
        KeyboardVisibilityEvent.setEventListener(this, this);
        calendar = Calendar.getInstance();
        createEventInstance();
        binding.headerEditTextEventTitle.addTextChangedListener(this);
    }

    private void createEventInstance(){
        EventInstanceForDay event = getIntent().getParcelableExtra(EXTRA_EVENT);
        if (event == null) {
            Calendar calendar = Calendar.getInstance();
            long startEvent = getIntent().getLongExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, 0L);
            long endEvent = getIntent().getLongExtra(CalendarContract.EXTRA_EVENT_END_TIME, 0L);
            if (endEvent == 0){
                endEvent = startEvent + TimeUnit.HOURS.toMillis(1);
            }
            long id = getIntent().getLongExtra(CalendarContract.Instances.EVENT_ID, -1L);
            String title = getIntent().getStringExtra(CalendarContract.Events.TITLE);
            String desc = getIntent().getStringExtra(CalendarContract.Events.DESCRIPTION);
            String location = getIntent().getStringExtra(CalendarContract.Events.EVENT_LOCATION);
            int available = getIntent().getIntExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
            String email = getIntent().getStringExtra(Intent.EXTRA_EMAIL);
            event = new EventInstanceForDay(id, title, startEvent, endEvent, -1, "", 1);
        }
        long calID = prefs.getLong(KEY_HEBREW_ID, -1L);
        event.setCalendarId(calID);
        binding.setEvent(event);
    }

    public void openDayDialog(View view) {
        hebrewPickerDialog = new HebrewPickerDialog();
        hebrewPickerDialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }
//
    public void openTimeDialog(View text) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
        pickerState = (text.equals(binding.eventStartTime)) ? PICKER_STATE.STATE_START_TIME : PICKER_STATE.STATE_END_TIME;
    }


    public void saveClicked(View view) {
        boolean save = binding.headerBtnSave.getText().equals("שמור");
        if (save) {
            saveEvent();
        } else {
            InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (keyboard != null) {
                keyboard.hideSoftInputFromWindow(binding.headerEditTextEventTitle.getWindowToken(), 0);
            }
            binding.headerBtnSave.setText("שמור");

        }
    }


    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_event_instances, popup.getMenu());
        int position = 0;
        switch (binding.getEvent().getRepeatState()) {
            case SINGLE:
                break;
            case DAY:
                position = 1;
                break;
            case WEEK:
                position = 2;
                break;
            case MONTH:
                position = 3;
                break;
            case YEAR:
                position = 4;
                break;
        }
        popup.getMenu().getItem(position).setChecked(true);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        EventInstanceForDay event = binding.getEvent();
        switch (item.getItemId()) {
            case R.id.repeat_single:
                event.setRepeatState(EventInstanceForDay.Repeat.SINGLE);
                break;
            case R.id.repeat_daily:
                event.setRepeatState(EventInstanceForDay.Repeat.DAY);
                break;
            case R.id.repeat_weekly:
                event.setRepeatState(EventInstanceForDay.Repeat.WEEK);
                break;
            case R.id.repeat_monthly:
                event.setRepeatState(EventInstanceForDay.Repeat.MONTH);
                break;
            case R.id.repeat_yearly:
                event.setRepeatState(EventInstanceForDay.Repeat.YEAR);
                break;
        }
        binding.setEvent(event);
        return true;
    }

    public void clickX(View view) {
        finish();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        EventInstanceForDay event = binding.getEvent();
        Calendar calendar = Calendar.getInstance();
        long timeDiff = event.getEnd() - event.getBegin();
        switch (pickerState) {
            case STATE_START_TIME:
                calendar.setTimeInMillis(event.getBegin());
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                event.setBegin(calendar.getTimeInMillis());
                if(event.getBegin() > event.getEnd()){
                    event.setEnd(event.getBegin() + timeDiff);
                }
                break;
            case STATE_END_TIME:
                calendar.setTimeInMillis(event.getEnd());
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                event.setEnd(calendar.getTimeInMillis());
                if(event.getBegin() > event.getEnd()){
                    event.setBegin(event.getEnd() - timeDiff);
                }
        }
        binding.setEvent(event);
        pickerState = null;
    }


    private void saveEvent() {
        GoogleManager.addHebrewEventToGoogleServer(this, binding.getEvent());
//        MainActivity.recreateFlag = true;
        finish();
    }

    @Override
    public void onBackPressed() {
        binding.headerBtnSave.setText("שמור");
        if (pickerState != null) {
            pickerState = null;
        }
        super.onBackPressed();
    }

    //KeyBoard visibilty listener
    @Override
    public void onVisibilityChanged(boolean isOpen) {
        binding.headerBtnSave.setText(isOpen ? "בוצע" : "שמור");
        binding.headerEditTextEventTitle.setCursorVisible(isOpen);
    }

    @Override
    public void onClick(View view) {
        Day day = (Day) view.getTag(R.string.app_name);
        if (day != null) {
            EventInstanceForDay event = binding.getEvent();
            calendar.setTimeInMillis(event.getBegin());
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            event.setBegin(day.getStartDayInMillis() + TimeUnit.HOURS.toMillis(hour) + TimeUnit.MINUTES.toMillis(minute));
            calendar.setTimeInMillis(event.getEnd());
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            event.setEnd(day.getStartDayInMillis() + TimeUnit.HOURS.toMillis(hour) + TimeUnit.MINUTES.toMillis(minute));
            binding.setEvent(event);
        }
        if (hebrewPickerDialog != null) {
            hebrewPickerDialog.dismiss();
        }
    }

    @Override
    public void onValueChanged(int oldValue, int newValue) {
        EventInstanceForDay event = binding.getEvent();
        event.setRepeatValue(newValue);
        binding.setEvent(event);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        binding.getEvent().setEventTitle(binding.headerEditTextEventTitle.getText().toString());
    }


    private enum PICKER_STATE {
        STATE_START_TIME,
        STATE_END_TIME,
        STATE_START_DATE,
        STATE_END_DATE
    }


}