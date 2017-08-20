package com.suraj.dailyexpenses;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddReminderActivity extends AppCompatActivity {
    private EditText etReminderName;
    private EditText etReminderAmount;
    private EditText etReminderTime;
    private Button btnAddReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        etReminderName = (EditText) findViewById(R.id.etReminderName);
        etReminderAmount = (EditText) findViewById(R.id.etReminderAmount);
        etReminderTime = (EditText) findViewById(R.id.etReminderTime);

        btnAddReminder = (Button) findViewById(R.id.btnAddReminder);

        etReminderTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);

                    TimePickerDialog mTimePicker;

                    mTimePicker = new TimePickerDialog(AddReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            etReminderTime.setText(selectedHour + ":" + selectedMinute);
                        }
                    }, hour, minute, false);

                    mTimePicker.show();
                }
            }
        });
        etReminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;

                mTimePicker = new TimePickerDialog(AddReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etReminderTime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, false);

                mTimePicker.show();
            }
        });

        final int defaultColor = etReminderTime.getCurrentTextColor();

        etReminderTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();

                Pattern pattern  = Pattern.compile("[0-9]{1,2}:[0-9]{1,2}");

                Matcher matcher  = pattern.matcher(s);
                if(matcher.matches()){
                    etReminderTime.setTextColor(defaultColor);
                }else{
                    etReminderTime.setTextColor(Color.RED);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReminder();
                etReminderName.setText("");
                etReminderAmount.setText("");
                etReminderTime.setText("");
            }
        });

    }

    public void addReminder() {
        String[] timeString = etReminderTime.getText().toString().split(":");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeString[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeString[1]));
        calendar.set(Calendar.SECOND, 0);

        long time = calendar.getTimeInMillis();

        System.out.println(calendar.get(Calendar.HOUR_OF_DAY));

        Intent intent = new Intent(AddReminderActivity.this, ReminderReceiver.class);
        intent.putExtra("name", etReminderName.getText().toString());
        intent.putExtra("rs", Integer.parseInt(etReminderAmount.getText().toString()));

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                time,
                AlarmManager.INTERVAL_DAY, PendingIntent.getBroadcast(AddReminderActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));


        Set<String> reminderSet = Utils.getStringSetFromSharedPreferences(Utils.REMINDERS_SHARED_PREFERENCE_STRING, false);

        String reminderSettingString = etReminderName.getText() + ";" + etReminderAmount.getText() + ";" + etReminderTime.getText();

        reminderSet.add(reminderSettingString);

        Utils.putStringSetInSharedPreferences(Utils.REMINDERS_SHARED_PREFERENCE_STRING, reminderSet);

    }
}
