package com.Aaronatomy.Quiz.Utility;

import android.app.Application;

import com.Aaronatomy.Quiz.Model.QCookieJar;
import com.facebook.drawee.backends.pipeline.Fresco;

import org.litepal.LitePal;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by AaronAtomy on 2018/3/27.
 * QApplication
 */

public class QApplication extends Application {
    private static final int Time_Out = 16;
    private static OkHttpClient client;
    private static String[] semester;

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this); // 初始化LitePal
        Fresco.initialize(this); // 初始化Fresco
    }

    public static OkHttpClient getOkHttpClient() {
        if (client == null)
            client = new OkHttpClient.Builder().
                    connectTimeout(Time_Out, TimeUnit.SECONDS).
                    readTimeout(Time_Out, TimeUnit.SECONDS).
                    writeTimeout(Time_Out, TimeUnit.SECONDS).
                    cookieJar(new QCookieJar()).
                    build();

        return client;
    }

    public static String[] getSemester() {
        return semester;
    }

    public static void setSemester(String[] temp) {
        semester = temp;
    }
}
