package com.Aaronatomy.Quiz.Model;

import android.content.Context;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by AaronAtomy on 2018/3/15.
 * QAction
 */

public class QAction {
    static final int MqttAction_Publish = 0;
    static final int MqttAction_ClassCheckIn = 1;
    static final String Msg = "msg";
    static final String Topic = "topic";
    static final String Action = "action";

    // 向主题发送消息
    public static void makeAction(Context context, String msg, String topic) {
        Intent actionIntent = new Intent(context, MessageService.class);
        QMessage message = new QMessage();
        message.setTopic(topic);
        message.setMessage(msg);
        message.setTimeStamp(getTimeStamp());

        actionIntent.putExtra(Msg, message);
        actionIntent.putExtra(Action, MqttAction_Publish);
        context.startService(actionIntent);
    }

    // 获取时间戳
    private static String getTimeStamp() {
        SimpleDateFormat stamp = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.getDefault());
        return stamp.format(new Date());
    }
}
