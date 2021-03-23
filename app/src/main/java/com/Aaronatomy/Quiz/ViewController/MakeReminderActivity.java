package com.Aaronatomy.Quiz.ViewController;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.Aaronatomy.Quiz.Database.Reminder;
import com.Aaronatomy.Quiz.Database.StaticResource;
import com.Aaronatomy.Quiz.Protocol.QDialogActivity;
import com.Aaronatomy.Quiz.R;

import org.litepal.crud.DataSupport;

import java.util.Calendar;
import java.util.Random;

public class MakeReminderActivity extends QDialogActivity {
    private EditText msg;
    private DatePicker datePicker;
    private TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_reminder);

        setView();
        if (getIntent().getExtras() != null) {
            String defaultMsg = getIntent().getExtras().getString(StaticResource.Msg);
            if (!TextUtils.isEmpty(defaultMsg))
                msg.setText(defaultMsg);
        }
    }

    View.OnClickListener make = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String iMsg = msg.getText().toString();
            if (TextUtils.isEmpty(iMsg)) {
                Toast.makeText(MakeReminderActivity.this,
                        "请设置提醒的内容", Toast.LENGTH_SHORT).show();
            } else {
                Calendar calendar = Calendar.getInstance();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    calendar.set(datePicker.getYear(),
                            datePicker.getMonth(),
                            datePicker.getDayOfMonth(),
                            timePicker.getHour(),
                            timePicker.getMinute());
                else
                    calendar.set(datePicker.getYear(),
                            datePicker.getMonth(),
                            datePicker.getDayOfMonth(),
                            timePicker.getCurrentHour(),
                            timePicker.getCurrentMinute());

                long when = calendar.getTimeInMillis();
                Reminder reminder = new Reminder();
                reminder.setMsg(msg.getText().toString());
                reminder.setWhen(when);
                reminder.save();

                Intent intent = new Intent(MakeReminderActivity.this, ReminderPostedActivity.class);
                intent.putExtra("id", DataSupport.findLast(Reminder.class).getID());
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        MakeReminderActivity.this,  (new Random().nextInt(100)), intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, when, pendingIntent);
                Toast.makeText(MakeReminderActivity.this,
                        "提醒设置成功", Toast.LENGTH_SHORT).show();

                Intent broadcast = new Intent(StaticResource.BroadCast_DataSetChanged);
                sendBroadcast(broadcast);
                MakeReminderActivity.this.finish();
            }
        }
    };

    private void setView() {
        msg = findViewById(R.id.makeReminder_msg);
        datePicker = findViewById(R.id.makeReminder_datePicker);
        timePicker = findViewById(R.id.makeReminder_timePicker);
        Button buttonMake = findViewById(R.id.makeReminder_buttonMake);
        Button buttonCancel = findViewById(R.id.makeReminder_buttonCancel);

        timePicker.setIs24HourView(true);
        buttonMake.setOnClickListener(make);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MakeReminderActivity.this.finish();
            }
        });
    }
}
