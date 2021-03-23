package com.Aaronatomy.Quiz.ViewController;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.Aaronatomy.Quiz.Protocol.QNormalActivity;

import org.litepal.tablemanager.Connector;

/**
 * Created by AaronAtomy on 2018/2/20.
 * SplashActivity
 */

public class SplashActivity extends QNormalActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Connector.getDatabase();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, AuthActivity.class));
                finish();
                overridePendingTransition(0, 0);
            }
        }, 600);
    }
}
