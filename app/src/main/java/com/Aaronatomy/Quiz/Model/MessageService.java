package com.Aaronatomy.Quiz.Model;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.Aaronatomy.Quiz.Database.Message;
import com.Aaronatomy.Quiz.Database.StaticResource;
import com.Aaronatomy.Quiz.Database.User;
import com.Aaronatomy.Quiz.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;

/**
 * Created by AaronAtomy on 2018/2/28.
 * MessageService
 */

public class MessageService extends Service {
    public static final int Mqtt_Qos = 0;
    private static final int defaultValue = 5;
    private static final int notify_Msg = 0;
    private static final int notify_Notify = 1;
    private static final int notify_Check = 2;
    private boolean subscribeInitializedFlag = false;
    private MqttAndroidClient androidClient;
    private Intent actionIntent;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startCommand(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startCommand(Intent intent) {
        actionIntent = intent;
        if (androidClient == null) {
            androidClient = new MqttAndroidClient(this.getApplicationContext(),
                    StaticResource.MqttHost, User.getUser().getAccount());

            androidClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Toast.makeText(MessageService.this,
                            getString(R.string.MqttConnectionLost),
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    QMessage quizMsg = QMessage.parse(message.getPayload());
                    Message msg = new Message();
                    msg.setTopic(quizMsg.getTopic());
                    msg.setMessage(quizMsg.getMessage());
                    msg.setTimeStamp(quizMsg.getTimeStamp());
                    msg.save();

                    Intent broadcast = new Intent(StaticResource.BroadCast_MsgArrived);
                    sendBroadcast(broadcast);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    try {
                        QMessage quizMsg = QMessage.parse(token.getMessage().getPayload());
                        if (!quizMsg.getTopic().equals(User.getUser().getMajor()))
                            Toast.makeText(MessageService.this,
                                    getString(R.string.MqttMsgDeliverSucceed),
                                    Toast.LENGTH_SHORT).show();
                    } catch (MqttException | IOException | ClassNotFoundException e) {
                        Log.e(e.getMessage(), "Error Caused By: " + e.getCause());
                    }
                }
            });
        }

        try {
            androidClient.connect(null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    try {
                        // 服务在启动时自动订阅已经订阅的主题
                        if (!subscribeInitializedFlag) {
                            androidClient.subscribe(StaticResource.Institution, Mqtt_Qos);
                            androidClient.subscribe(User.getUser().getMajor(), Mqtt_Qos);
                            androidClient.subscribe(User.getUser().getMajor() + "Quiz.CheckIn", Mqtt_Qos);
                            subscribeInitializedFlag = true;
                        }

                        int action = actionIntent.getIntExtra(QAction.Action, defaultValue);
                        switch (action) {
                            case QAction.MqttAction_Publish:
                                QMessage message = (QMessage) actionIntent.getExtras().get(QAction.Msg);
                                if (message != null) {
                                    androidClient.publish(message.getTopic(),
                                            QMessage.serialize(message),
                                            Mqtt_Qos, false);
                                    Toast.makeText(MessageService.this,
                                            "消息已发送", Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case QAction.MqttAction_ClassCheckIn:
                                break;
                        }
                    } catch (MqttException | NullPointerException | IOException e) {
                        Log.e(e.getMessage(), "Unknown Error Caused By: " + e.getCause());
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MessageService.this,
                            exception.getCause().toString(),
                            Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            Log.e(e.getMessage(), "Error Caused By: " + e.getCause());
        }
    }
}
