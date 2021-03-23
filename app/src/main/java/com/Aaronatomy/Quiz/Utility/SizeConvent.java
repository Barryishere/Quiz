package com.Aaronatomy.Quiz.Utility;

import android.content.Context;

/**
 * Created by AaronAtomy on 2018/2/15.
 * SizeConvent
 */

public class SizeConvent {
    //根据手机的分辨率从 dp 的单位 转成为 px(像素)
    public static int dp2PX(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale+0.5f);
    }

    //根据手机的分辨率从 px(像素) 的单位 转成为 dp
    public static int px2DP(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
