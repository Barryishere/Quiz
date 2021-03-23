package com.Aaronatomy.Quiz.Database;

import org.litepal.crud.DataSupport;

/**
 * Created by AaronAtomy on 2018/3/2.
 * Message
 */

public class Message extends DataSupport {
    private boolean isRead;
    private String topic;
    private String message;
    private String timeStamp;

    public Message() {
        isRead = false;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public long getID() {
        return getBaseObjId();
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
