package com.Aaronatomy.Quiz.Model;

import android.os.Handler;
import android.os.Message;

import com.Aaronatomy.Quiz.Protocol.Updatable;

import java.lang.ref.WeakReference;

/**
 * Created by AaronAtomy on 2018/4/24.
 * DataSourceChangedHandler
 */

public class DataSourceChangedHandler extends Handler {
    private WeakReference<Updatable> weakReference;

    public DataSourceChangedHandler(Updatable gradeActivity) {
        weakReference = new WeakReference<>(gradeActivity);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Updatable activity = weakReference.get();
        activity.refreshDataSource();
        activity.getAdapter().notifyDataSetChanged();
    }
}
