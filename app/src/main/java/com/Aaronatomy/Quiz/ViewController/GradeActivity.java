package com.Aaronatomy.Quiz.ViewController;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;

import com.Aaronatomy.Quiz.Database.StaticResource;
import com.Aaronatomy.Quiz.Database.User;
import com.Aaronatomy.Quiz.Model.CookieProvider;
import com.Aaronatomy.Quiz.Model.DataSourceChangedHandler;
import com.Aaronatomy.Quiz.Protocol.QNormalActivity;
import com.Aaronatomy.Quiz.Protocol.Updatable;
import com.Aaronatomy.Quiz.R;
import com.Aaronatomy.Quiz.Utility.CommonAdapter;
import com.Aaronatomy.Quiz.Utility.CommonItem;
import com.Aaronatomy.Quiz.Utility.QApplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GradeActivity extends QNormalActivity implements Updatable {
    private DataSourceChangedHandler handler = new DataSourceChangedHandler(this);
    private ArrayList<CommonItem> gradeItems;
    private RecyclerView recyclerView;
    private CommonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_grade);
        setView();
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    @Override
    public void refreshDataSource() {
        // Required
    }

    private void setView() {
        recyclerView = findViewById(R.id.grade_list);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bindAdapter();
    }

    private void bindAdapter() {
        if (adapter == null) {
            getGrades();
            adapter = new CommonAdapter(gradeItems);
        }

        recyclerView.setAdapter(adapter);
    }

    private void getGrades() {
        if (gradeItems == null)
            gradeItems = new ArrayList<>();
        new GetGrade(gradeItems, handler).execute(); // 更新新闻
    }

    static class GetGrade extends AsyncTask<Void, Integer, Void> {
        private OkHttpClient client;
        private ArrayList<CommonItem> gradeItems;
        private DataSourceChangedHandler handler;

        GetGrade(ArrayList<CommonItem> gradeItems, DataSourceChangedHandler handler) {
            this.gradeItems = gradeItems;
            this.handler = handler;
            client = QApplication.getOkHttpClient();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String url = DataSupport.findFirst(User.class).getUrlGrade();
            ArrayList<String> infor;
            FormBody formBody = new FormBody.Builder()
                    .add("__VIEWSTATE", StaticResource.ViewStateGrade)
                    .add("ddlXN", "")
                    .add("ddlXQ", "")
                    .add("txtQSCJ", "0")
                    .add("txtZZCJ", "100")
                    .add("Button2", "%D4%DA%D0%A3%D1%A7%CF%B0%B3%C9%BC%A8%B2%E9%D1%AF")
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .header("Cookie", CookieProvider.get(StaticResource.Url_VerifyCode))
                    .header("Referer", DataSupport.findFirst(User.class).getUrlRef())
                    .post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String htmlHolder = response.body().string();
                    Document document = Jsoup.parse(htmlHolder);
                    Element dataList = document.getElementById("DataGrid1");
                    Elements items = dataList.getElementsByTag("tr");
                    for (Element item : items) {
                        if (item.attr("class").equals("datelisthead"))
                            continue;
                        Elements details = item.getElementsByTag("td");
                        infor = new ArrayList<>();
                        for (Element detail : details)
                            infor.add(detail.text());
                        if (infor.size() >= 3) {
                            infor.remove(0);
                            CommonItem gradeItem = new CommonItem(
                                    0, infor.get(0),
                                    "期末总成绩为：" + infor.get(3),
                                    "其中卷面成绩为：" + infor.get(2)
                            );
                            gradeItems.add(gradeItem);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            handler.sendEmptyMessage(0);
            return null;
        }
    }
}
