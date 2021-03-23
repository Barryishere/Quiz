package com.Aaronatomy.Quiz.Database;

import org.litepal.crud.DataSupport;

/**
 * Created by AaronAtomy on 2018/4/20.
 * Reminder
 */

public class Reminder extends DataSupport {
    private String msg;
    private long when;

    public long getID() {
        return getBaseObjId();
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getWhen() {
        return when;
    }

    public void setWhen(long when) {
        this.when = when;
    }
}
