package com.Aaronatomy.Quiz.Model;

import com.Aaronatomy.Quiz.Utility.QApplication;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created by AaronAtomy on 2018/4/5.
 * CookieProvider
 */

public class CookieProvider {
    public static String get(String uri) {
        HttpUrl url = HttpUrl.parse(uri);
        StringBuilder cookieStr = new StringBuilder(); // 获取需要提交的CookieStr
        List<Cookie> cookies = QApplication.getOkHttpClient().cookieJar().loadForRequest(url); // 从缓存中获取Cookie
        for (Cookie cookie : cookies)
            cookieStr.append(cookie.name()).append("=").append(cookie.value()).append(";");

        return cookieStr.toString();
    }

    public static String get(HttpUrl url) {
        StringBuilder cookieStr = new StringBuilder(); // 获取需要提交的CookieStr
        List<Cookie> cookies = QApplication.getOkHttpClient().cookieJar().loadForRequest(url); // 从缓存中获取Cookie
        for (Cookie cookie : cookies)
            cookieStr.append(cookie.name()).append("=").append(cookie.value()).append(";");

        return cookieStr.toString();
    }
}
