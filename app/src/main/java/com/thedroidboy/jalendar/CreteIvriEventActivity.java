package com.thedroidboy.jalendar;

import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.TimePicker;

import com.thedroidboy.jalendar.calendars.jewish.JewCalendar;
import com.thedroidboy.jalendar.databinding.ActivityCreateIvriEventBinding;
import com.thedroidboy.jalendar.model.EventInstanceForDay;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import me.angrybyte.numberpicker.view.ActualNumberPicker;

import static com.thedroidboy.jalendar.calendars.google.Contract.KEY_HEBREW_ID;
import static com.thedroidboy.jalendar.calendars.jewish.JewCalendar.hebrewHebDateFormatter;

/**
 * Created by B.E.L on 31/10/2016.
 */

public class CreteIvriEventActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener , KeyboardVisibilityEventListener, PopupMenu.OnMenuItemClickListener {

    public static final String EXTRA_USE_CURRENT_DAY = "EXTRA_USE_CURRENT_DAY" ;
    public static JewCalendar currentCalendar;
//    @BindView(R.id.header_btn_save)
//    TextView headerBtnSave;
//    @BindView(R.id.header_btn_x)
//    ImageView headerBtnX;
//    @BindView(R.id.header_edit_text_event_title)
//    EditText headerTitleEditText;
//    @BindView(R.id.checkbox_all_day_event)
    SwitchCompat switchCompatAllDay;
//    @BindView(R.id.event_start_day)
//    TextView eventStartDay;
//    @BindView(R.id.event_end_day)
//    TextView eventEndDay;
//    @BindView(R.id.event_start_time)
//    TextView eventStartTime;
//    @BindView(R.id.event_end_time)
//    TextView eventEndTime;
//    @BindView(R.id.event_instances)
//    TextView textViewRepeat;
//    @BindView(R.id.event_count_title)
    TextView eventCountTitle;
    //    @BindView(R.id.progress_bar)ProgressBar progressBar;
//    @BindView(R.id.countPicker)
    private ActivityCreateIvriEventBinding binding;
    ActualNumberPicker eventCountPicker;
    @Inject
    CalendarRepo calendarRepo;
    @Inject
    SharedPreferences prefs;

//    EventsProvider eventsProvider;

    private PICKER_STATE pickerState;
    private SimpleDateFormat sdf;
    private Calendar calendarStartTime, calendarEndTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_ivri_event);

        setSupportActionBar(binding.myToolbar);
        sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        KeyboardVisibilityEvent.setEventListener(this, this);

//        binding.eventInstances.setTag(R.id.repeat_single);

    }

    private void cretaeEventInstance(){
        Calendar calendar = Calendar.getInstance();
        long startEvent = getIntent().getLongExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, 0L);
        long endEvent = getIntent().getLongExtra(CalendarContract.EXTRA_EVENT_END_TIME, 0L);
        long id = calendar.getTimeInMillis();
        String title = getIntent().getStringExtra(CalendarContract.Events.TITLE);
        String desc = getIntent().getStringExtra(CalendarContract.Events.DESCRIPTION);
        String location = getIntent().getStringExtra(CalendarContract.Events.EVENT_LOCATION);
        int available = getIntent().getIntExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        String email = getIntent().getStringExtra(Intent.EXTRA_EMAIL);
        EventInstanceForDay event = new EventInstanceForDay(id, title, startEvent, endEvent, -1, "", 1);
        binding.setEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        setDates();
    }
//
    private void setDates() {
        boolean useCurrentDay = getIntent().getBooleanExtra(EXTRA_USE_CURRENT_DAY, false);
        if (currentCalendar == null) {
            currentCalendar = new JewCalendar();
        }
        String date = hebrewHebDateFormatter.formatHebrewNumber(currentCalendar.getJewishDayOfMonth()) + " " +
                hebrewHebDateFormatter.formatMonth(currentCalendar);
        hebrewHebDateFormatter.setLongWeekFormat(true);
        String day = hebrewHebDateFormatter.formatDayOfWeek(currentCalendar) + " , " + date;
        binding.eventStartDay.setText(day);
        binding.eventEndDay.setText(day);
        calendarStartTime = Calendar.getInstance();
        calendarStartTime.setTime(currentCalendar.getTime());
        calendarStartTime.set(Calendar.MINUTE, 0);
        binding.eventStartTime.setText(sdf.format(calendarStartTime.getTime()));
        calendarEndTime = Calendar.getInstance();
        calendarEndTime.setTime(currentCalendar.getTime());
        calendarEndTime.set(Calendar.MINUTE, 0);
        calendarEndTime.set(Calendar.HOUR_OF_DAY, calendarEndTime.get(Calendar.HOUR_OF_DAY) + 1);
        binding.eventEndTime.setText(sdf.format(calendarEndTime.getTime()));
    }
//
//    @OnClick({R.id.event_start_day, R.id.event_end_day})
//    void openDayDialog() {
//        DialogFragment dialog = new HebrewPickerDialog();
//        HebrewPickerDialog.onDatePickerDismiss = onDatePickerDismiss;
//
//        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
//
//    }
//
//    @OnClick({R.id.event_start_time, R.id.event_end_time})
//    void openTimeDialog(TextView text) {
//        DialogFragment newFragment = new TimePickerFragment();
//        newFragment.show(getSupportFragmentManager(), "timePicker");
//        pickerState = (text.equals(eventStartTime)) ? PICKER_STATE.STATE_START_TIME : PICKER_STATE.STATE_END_TIME;
//    }
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
//                calendarStartTime.set(Calendar.MINUTE, minute);
//                calendarStartTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
//                eventStartTime.setText(sdf.format(calendarStartTime.getTime()));
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
//        long start = calendarStartTime.getTimeInMillis();
//        eventsProvider.addEvent(contentResolver, title, calID, repeat, start, calendarEndTime.getTimeInMillis() - start, eventCountPicker.getValue());
//        GoogleManager.addHebrewEventToGoogleServer(this, title, repeatId, calendarStartTime, calendarEndTime, String.valueOf(eventCountPicker.getValue()));
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

    OnDatePickerDialog onDatePickerDismiss = new OnDatePickerDialog() {
        @Override
        public void onBtnOkPressed() {
            setDates();
        }

        @Override
        public void onAttached() {
//            progressBar.setVisibility(View.GONE);
        }
    };


    private enum PICKER_STATE {
        STATE_START_TIME,
        STATE_END_TIME,
        STATE_START_DATE,
        STATE_END_DATE
    }

    public interface OnDatePickerDialog {
        void onBtnOkPressed();

        void onAttached();
    }

}