package com.Aaronatomy.Quiz.ViewController;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.Aaronatomy.Quiz.Database.Reminder;
import com.Aaronatomy.Quiz.R;

import org.litepal.crud.DataSupport;

public class ReminderPostedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_posted);
        long id = getIntent().getLongExtra("id", 0);
        if (isAvailable(id)) {
            showDetailDialog(id);
        } else {
            Toast.makeText(ReminderPostedActivity.this,
                    "未查询到相关记录", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isAvailable(long id) {
        Reminder reminder = DataSupport.find(Reminder.class, id);
        return reminder != null;
    }

    private void showDetailDialog(long id) {
        Reminder reminder = DataSupport.find(Reminder.class, id);
        String msg = reminder.getMsg();
        AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ReminderPostedActivity.this);
        normalDialog.setTitle("新提醒");
        normalDialog.setMessage(msg);
        normalDialog.setIcon(R.drawable.ic_error);
        normalDialog.setPositiveButton("好的", null);
        normalDialog.show();
    }
}
