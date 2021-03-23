package com.Aaronatomy.Quiz.Model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.Aaronatomy.Quiz.Database.Reminder;

import org.litepal.crud.DataSupport;

/**
 * Created by AaronAtomy on 2018/4/20.
 * ReminderReceiver
 */

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getLongExtra("id", 0);
        Reminder reminder = DataSupport.find(Reminder.class, id);
        if (reminder != null)
            Toast.makeText(context, reminder.getMsg(), Toast.LENGTH_SHORT).show();
    }
}
