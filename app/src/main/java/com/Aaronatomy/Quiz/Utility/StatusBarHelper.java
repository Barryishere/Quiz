package com.Aaronatomy.Quiz.Utility;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by AaronAtomy on 2018/2/11.
 * StatusBarHelper
 */

public class StatusBarHelper {

    // 隐藏AppCompatActivity的标题栏
    public static void hideActionBar(ActionBar actionBar) {
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    // 设置状态栏透明（仅对MIUI生效）
    public static void setTranslucentStatus(boolean on, Window win) {
        // Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    // 设置状态栏字体为黑色（仅对MIUI生效）
    public static void setStatusBarDarkMode(boolean darkMode, Activity activity) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkMode ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
