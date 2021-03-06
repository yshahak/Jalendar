package com.thedroidboy.jalendar.activities;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TimePicker;

import com.thedroidboy.jalendar.CalendarRepo;
import com.thedroidboy.jalendar.R;
import com.thedroidboy.jalendar.calendars.google.GoogleManager;
import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;
import com.thedroidboy.jalendar.databinding.ActivityCreateIvriEventBinding;
import com.thedroidboy.jalendar.fragments.HebrewPickerDialog;
import com.thedroidboy.jalendar.fragments.TimePickerFragment;
import com.thedroidboy.jalendar.model.Day;
import com.thedroidboy.jalendar.model.GoogleEvent;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import biweekly.util.Frequency;
import dagger.android.AndroidInjection;

import static com.thedroidboy.jalendar.calendars.google.Contract.KEY_HEBREW_ID;

/**
 * Created by B.E.L on 31/10/2016.
 */

@SuppressWarnings("unused")
public class CreteIvriEventActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener , KeyboardVisibilityEventListener
        , PopupMenu.OnMenuItemClickListener, View.OnClickListener, /*OnValueChangeListener, */TextWatcher {

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
        KeyboardVisibilityEvent.setEventListener(this, this);
        calendar = Calendar.getInstance();
        createEventInstance();
        binding.headerEditTextEventTitle.addTextChangedListener(this);
//        binding.countPicker.setListener(this);
    }

    private void createEventInstance(){
        Intent eventIntent = getIntent();
        GoogleEvent event = eventIntent.getParcelableExtra(EXTRA_EVENT);
        long calID = prefs.getLong(KEY_HEBREW_ID, -1L);
        if (event == null) {
            Calendar calendar = Calendar.getInstance();
            long startEvent = eventIntent.getLongExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, 0L);
            long endEvent = eventIntent.getLongExtra(CalendarContract.EXTRA_EVENT_END_TIME, 0L);
            if (endEvent == 0){
                endEvent = startEvent + TimeUnit.HOURS.toMillis(1);
            }
            long eventId = eventIntent.getLongExtra(CalendarContract.Instances.EVENT_ID, -1L);
            String title = eventIntent.getStringExtra(CalendarContract.Events.TITLE);
            String desc = eventIntent.getStringExtra(CalendarContract.Events.DESCRIPTION);
            String location = eventIntent.getStringExtra(CalendarContract.Events.EVENT_LOCATION);
            int available = eventIntent.getIntExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
            String email = eventIntent.getStringExtra(Intent.EXTRA_EMAIL);
            event = GoogleEvent.Companion.newInstance(eventId, calID, title, startEvent, endEvent, Color.BLUE, -1,"");
            String rrule = eventIntent.getStringExtra(CalendarContract.Events.RRULE);
        }
        event.convertRruleToFrequencyAndRepeatValue();
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

    public void editRepeat(View v){
        onValueChanged(0, 10);
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_event_instances, popup.getMenu());
        int position = 0;
        if (binding.getEvent().getRecurrenceRule() != null) {
            Frequency frequency = binding.getEvent().getRecurrenceRule().getValue().getFrequency();
            switch (frequency) {
                case DAILY:
                    position = 1;
                    break;
                case WEEKLY:
                    position = 2;
                    break;
                case MONTHLY:
                    position = 3;
                    break;
                case YEARLY:
                    position = 4;
                    break;
            }
        }
        popup.getMenu().getItem(position).setChecked(true);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        GoogleEvent event = binding.getEvent();
        event.clearFrequency();
        switch (item.getItemId()) {
            case R.id.repeat_single:
                break;
            case R.id.repeat_daily:
                event.setFrequency(Frequency.DAILY);
                break;
            case R.id.repeat_weekly:
                event.setFrequency(Frequency.WEEKLY);
                break;
            case R.id.repeat_monthly:
                event.setFrequency(Frequency.MONTHLY);
                break;
            case R.id.repeat_yearly:
                event.setFrequency(Frequency.YEARLY);
                break;
            case R.id.repeat_custom:
                //todo custom dialog
                break;
        }
        binding.setEvent(event);
        return true;
    }

    public void clickX(View view) {
        finish();
    }

    public void deleteEvent(View view) {
        if (binding.getEvent().isRecuuringEvent()){
            deleteRecurringEvent();
        } else {
            new AlertDialog.Builder(this, R.style.AlertDialogCustom)
                    .setTitle("האם למחוק אירוע זה?")
                    .setPositiveButton("כן", (dialogInterface, i) -> {
                        GoogleManager.deleteEvent(getApplicationContext(), binding.getEvent());
                        finish();
                    })
                    .setNegativeButton("לא", null)
                    .show();
        }

    }

    public void deleteRecurringEvent() {
        /*AlertDialog alertDialog = */new AlertDialog.Builder(this, R.style.AlertDialogCustom)
                .setTitle("מחיקת אירועים")
                .setPositiveButton("אירוע זה בלבד", (dialogInterface, i) -> {
                    GoogleManager.deleteEvent(getApplicationContext(), binding.getEvent());
                    finish();
                })
                .setNeutralButton("כל האירועים", ((dialog, which) -> {
                    GoogleManager.deleteSingleEventFromRecurring(getApplicationContext(), binding.getEvent());
                    finish();
                }))
                .setNegativeButton("בטל", null)
                .show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        GoogleEvent event = binding.getEvent();
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
            GoogleEvent event = binding.getEvent();
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

//    @Override
    public void onValueChanged(int oldValue, int newValue) {
        GoogleEvent event = binding.getEvent();
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