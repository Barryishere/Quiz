package com.Aaronatomy.Quiz.Model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by AaronAtomy on 2018/3/17.
 * Mqtt消息
 */

public class QMessage implements Serializable {
    public static final long serialVersionUID = 1L;

    private String topic;
    private String message;
    private String timeStamp;

    static byte[] serialize(QMessage message) throws IOException {
        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutStream = new ObjectOutputStream(byteOutStream);
        objectOutStream.writeObject(message);
        objectOutStream.flush();
        objectOutStream.close();

        byte[] serialized = byteOutStream.toByteArray();
        byteOutStream.close();
        return serialized;
    }

    static QMessage parse(byte[] stream) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteInStream = new ByteArrayInputStream(stream);
        ObjectInputStream objectInStream = new ObjectInputStream(byteInStream);
        QMessage parsed = (QMessage) objectInStream.readObject();

        byteInStream.close();
        objectInStream.close();
        return parsed;
    }

    String getTopic() {
        return topic;
    }

    void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    String getTimeStamp() {
        return timeStamp;
    }

    void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
