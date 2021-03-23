package com.Aaronatomy.Quiz.Model;

import com.Aaronatomy.Quiz.Database.StaticResource;
import com.Aaronatomy.Quiz.Database.User;
import com.Aaronatomy.Quiz.Utility.QApplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.crud.DataSupport;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by AaronAtomy on 2018/4/5.
 * PeekTable
 */

public class PeekTable {
    private OkHttpClient client;

    public PeekTable() {
        client = QApplication.getOkHttpClient();
    }

    public String startPeek(String semester, String term) {
        String htmlHolder = "";
        FormBody formBody = new FormBody.Builder()
                .add("__EVENTTARGET", "xnd")
                .add("__EVENTARGUMENT", "")
                .add("__VIEWSTATE", StaticResource.State)
                .add("xnd", semester) // 学年
                .add("xqd", term) // 学期
                .build();
        Request request = new Request.Builder()
                .url(DataSupport.findFirst(User.class).getUrlTable())
                .header("Referer", DataSupport.findFirst(User.class).getUrlRef())
                .header("Cookie", CookieProvider.get(StaticResource.Url_VerifyCode))
                .post(formBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            htmlHolder = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return htmlHolder;
    }

    public String[] getSemester() {
        String htmlHolder = startPeek("", "");
        Document document = Jsoup.parse(htmlHolder);
        Element semesterGroupBox = document.getElementById("xnd");
        Elements semesters = semesterGroupBox.getElementsByTag("option");
        String[] semesterHolder = new String[semesters.size()];
        int index = 0;
        for (Element semester : semesters) {
            semesterHolder[index] = semester.attr("value");
            index++;
        }

        return semesterHolder;
    }
}
