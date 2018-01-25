package com.thedroidboy.jalendar;

import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;
import com.thedroidboy.jalendar.databinding.ActivityCreateIvriEventBinding;
import com.thedroidboy.jalendar.model.Day;
import com.thedroidboy.jalendar.model.EventInstanceForDay;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import me.angrybyte.numberpicker.view.ActualNumberPicker;

import static com.thedroidboy.jalendar.calendars.google.Contract.KEY_HEBREW_ID;

/**
 * Created by B.E.L on 31/10/2016.
 */

public class CreteIvriEventActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener , KeyboardVisibilityEventListener
        , PopupMenu.OnMenuItemClickListener, View.OnClickListener {

    public static final String EXTRA_USE_CURRENT_DAY = "EXTRA_USE_CURRENT_DAY" ;
    public static JewCalendar currentCalendar;
    private ActivityCreateIvriEventBinding binding;
    ActualNumberPicker eventCountPicker;
    @Inject
    CalendarRepo calendarRepo;
    @Inject
    SharedPreferences prefs;
    private PICKER_STATE pickerState;
    private SimpleDateFormat sdf;
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
        cretaeEventInstance();
//        binding.eventInstances.setTag(R.id.repeat_single);

    }

    private void cretaeEventInstance(){
        Calendar calendar = Calendar.getInstance();
        long startEvent = getIntent().getLongExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, 0L);
        long endEvent = getIntent().getLongExtra(CalendarContract.EXTRA_EVENT_END_TIME, 0L);
        if (endEvent == 0){
            endEvent = startEvent + TimeUnit.HOURS.toMillis(1);
        }
        long id = startEvent;
        String title = getIntent().getStringExtra(CalendarContract.Events.TITLE);
        String desc = getIntent().getStringExtra(CalendarContract.Events.DESCRIPTION);
        String location = getIntent().getStringExtra(CalendarContract.Events.EVENT_LOCATION);
        int available = getIntent().getIntExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        String email = getIntent().getStringExtra(Intent.EXTRA_EMAIL);
        EventInstanceForDay event = new EventInstanceForDay(id, title, startEvent, endEvent, -1, "", 1);
        binding.setEvent(event);
    }

//    @OnClick({R.id.event_start_day, R.id.event_end_day})
    public void openDayDialog(View view) {
        hebrewPickerDialog = new HebrewPickerDialog();
//        HebrewPickerDialog.onDatePickerDismiss = onDatePickerDismiss;
        hebrewPickerDialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }
//
//    @OnClick({R.id.event_start_time, R.id.event_end_time})
    void openTimeDialog(TextView text) {
//        DialogFragment newFragment = new TimePickerFragment();
//        newFragment.show(getSupportFragmentManager(), "timePicker");
//        pickerState = (text.equals(eventStartTime)) ? PICKER_STATE.STATE_START_TIME : PICKER_STATE.STATE_END_TIME;
    }
//
//
//    @OnClick(R.id.header_btn_save)
//    void click() {
//        boolean save = headerBtnSave.getText().equals("שמור");
//        if (save) {
//            saveEvent();
//        } else {
//            InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            keyboard.hideSoftInputFromWindow(headerTitleEditText.getWindowToken(), 0);
//            headerBtnSave.setText("שמור");
//
//        }
//    }
//
//
////    @OnClick(R.id.event_instances)
//    void showPopup(View v) {
//        PopupMenu popup = new PopupMenu(this, v);
//        popup.setOnMenuItemClickListener(this);
//        MenuInflater inflater = popup.getMenuInflater();
//        inflater.inflate(R.menu.menu_event_instances, popup.getMenu());
//        int repeatId = (int) textViewRepeat.getTag();
//        int position = 0;
//        switch (repeatId) {
//            case R.id.repeat_single:
//                break;
//            case R.id.repeat_daily:
//                position = 1;
//                break;
//            case R.id.repeat_weekly:
//                position = 2;
//                break;
//            case R.id.repeat_monthly:
//                position = 3;
//                break;
//            case R.id.repeat_yearly:
//                position = 4;
//                break;
//        }
//        popup.getMenu().getItem(position).setChecked(true);
//        popup.show();
//    }
//
    @Override
    public boolean onMenuItemClick(MenuItem item) {
//        textViewRepeat.setText(item.getTitle());
//        textViewRepeat.setTag(item.getItemId());
//        if (item.getItemId() != R.id.repeat_single) {
//            eventCountPicker.setVisibility(View.VISIBLE);
//            eventCountTitle.setVisibility(View.VISIBLE);
//        }
//        switch (item.getItemId()) {
//            case R.id.repeat_single:
//                eventCountPicker.setVisibility(View.GONE);
//                eventCountTitle.setVisibility(View.GONE);
//                break;
//            case R.id.repeat_daily:
//                eventCountPicker.setValue(365);
//                break;
//            case R.id.repeat_weekly:
//                eventCountPicker.setValue(36);
//                break;
//            case R.id.repeat_monthly:
//                eventCountPicker.setValue(12);
//                break;
//            case R.id.repeat_yearly:
//                eventCountPicker.setValue(10);
//                break;
//        }
        return false;
    }
//
//    @OnClick(R.id.header_btn_x)
//    void clickX() {
//        finish();
//    }
//
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

//        switch (pickerState) {
//            case STATE_START_TIME:
//                calendar.set(Calendar.MINUTE, minute);
//                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
//                eventStartTime.setText(sdf.format(calendar.getTime()));
//                calendarEndTime.set(Calendar.MINUTE, minute);
//                calendarEndTime.set(Calendar.HOUR_OF_DAY, ++hourOfDay);
//                eventEndTime.setText(sdf.format(calendarEndTime.getTime()));
//                break;
//            case STATE_END_TIME:
//                calendarEndTime.set(Calendar.MINUTE, minute);
//                calendarEndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
//                eventEndTime.setText(sdf.format(calendarEndTime.getTime()));
//        }
        pickerState = null;
    }


    private void saveEvent() {
        long calID = prefs.getLong(KEY_HEBREW_ID, -1L);
        if (calID == -1L){
            return;
        }
//        String title = headerTitleEditText.getText().toString();
//        int repeatId = (int) textViewRepeat.getTag();
        ContentResolver contentResolver = getContentResolver();
//        EventInstance.Repeat repeat = null;
//        switch (repeatId){
//            case R.id.repeat_single:
//                repeat = EventInstance.Repeat.SINGLE;
//                break;
//            case R.id.repeat_daily:
//                repeat = EventInstance.Repeat.DAY;
//                break;
//            case R.id.repeat_weekly:
//                repeat = EventInstance.Repeat.WEEK;
//                break;
//            case R.id.repeat_monthly:
//                repeat = EventInstance.Repeat.MONTH;
//                break;
//            case R.id.repeat_yearly:
//                repeat = EventInstance.Repeat.YEAR;
//                break;
//        }
//        if (repeat == null) {
//            return;
//        }
//        long start = calendar.getTimeInMillis();
//        eventsProvider.addEvent(contentResolver, title, calID, repeat, start, calendarEndTime.getTimeInMillis() - start, eventCountPicker.getValue());
//        GoogleManager.addHebrewEventToGoogleServer(this, title, repeatId, calendar, calendarEndTime, String.valueOf(eventCountPicker.getValue()));
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


    private enum PICKER_STATE {
        STATE_START_TIME,
        STATE_END_TIME,
        STATE_START_DATE,
        STATE_END_DATE
    }


}