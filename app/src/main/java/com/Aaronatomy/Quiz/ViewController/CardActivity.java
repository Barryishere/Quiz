package com.Aaronatomy.Quiz.ViewController;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CardActivity extends QNormalActivity implements Updatable {
    private DataSourceChangedHandler handler = new DataSourceChangedHandler(this);
    private ArrayList<CommonItem> bills;
    private CommonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        bills = new ArrayList<>();
        setView();
        inputPasswordDialog();
    }

    private void inputPasswordDialog() {
        final EditText editText = new EditText(CardActivity.this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(CardActivity.this);
        normalDialog.setIcon(R.drawable.ic_error);
        normalDialog.setTitle("请输入校园卡密码");
        normalDialog.setView(editText);
        normalDialog.setCancelable(false);
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String string = editText.getText().toString();
                        if (!TextUtils.isEmpty(string))
                            new GetBill(string, bills, handler).execute();
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CardActivity.this.finish();
                    }
                });
        normalDialog.show();
    }

    @Override
    public void refreshDataSource() {

    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    private void setView() {
        RecyclerView recyclerView = findViewById(R.id.card_list);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (adapter == null)
            adapter = new CommonAdapter(bills);

        recyclerView.setAdapter(adapter);
    }

    static class GetBill extends AsyncTask<String, Void, Void> {
        private OkHttpClient client;
        private String password;
        private String total;
        private ArrayList<CommonItem> commonItems;
        private DataSourceChangedHandler handler;

        GetBill(String password, ArrayList<CommonItem> commonItems, DataSourceChangedHandler handler) {
            client = QApplication.getOkHttpClient();
            total = "?";
            this.password = password;
            this.commonItems = commonItems;
            this.handler = handler;
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                String[] extras = getExtras();
                if (!TextUtils.isEmpty(extras[0]) && !TextUtils.isEmpty(extras[1])) {
                    FormBody formBody = new FormBody.Builder()
                            .add("authType", "0")
                            .add("username", User.getUser().getAccount())
                            .add("password", password)
                            .add("lt", extras[0])
                            .add("execution", extras[1])
                            .add("_eventId", "submit")
                            .build();
                    Request request = new Request.Builder()
                            .url(StaticResource.Campus_Extras)
                            .post(formBody)
                            .build();
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        Headers headers = response.headers();
                        HttpUrl url = request.url();
                        List<Cookie> cookies = Cookie.parseAll(url, headers);
                        if (cookies.size() > 0)
                            client.cookieJar().saveFromResponse(url, cookies);

                        String htmlHolder = response.body().string();
                        Document document = Jsoup.parse(htmlHolder);
                        Element form = document.getElementById("queryForm");
                        Elements cells = form.getElementsByTag("td");
                        for (Element cell : cells) {
                            if (cell.toString().contains("【一卡通】余额")) {
                                Element totalHolder = cell.child(0);
                                total = totalHolder.ownText();
                            }
                        }

                        Elements scripts = document.getElementsByTag("script");
                        for (Element script : scripts) {
                            if (script.toString().contains("renderYktInfo")) {
                                String str = script.toString();
                                String[] temp = str.split("cs=");
                                String rawUrl = temp[1].split("',")[0];
                                String urlBacked = "http://portal.aqnu.edu.cn/?.cs=" + rawUrl;
                                Request requestDetail = new Request.Builder()
                                        .url(urlBacked)
                                        .header("Cookie", CookieProvider.get(url))
                                        .build();
                                Response responseDetail = client.newCall(requestDetail).execute();
                                String billHolder = responseDetail.body().string();
                                String[] bills = billHolder.split(",\\{");
                                for (String bill : bills) {
                                    String[] tempDetail = bill
                                            .replace("{\"aqsfyktInfo\":[{\"XFSJ\":\"", "")
                                            .replace("\"XFSJ\":\"", "")
                                            .replace("\",\"JYJE\":", "+")
                                            .replace(",\"JYCS\":\"", "+")
                                            .replace("\"}", "")
                                            .split("\\+");
                                    if (tempDetail.length >= 3) {
                                        CommonItem item = new CommonItem(0,
                                                tempDetail[2],
                                                "消费金额：" + tempDetail[1],
                                                "消费时间：" + tempDetail[0]);
                                        commonItems.add(item);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            CommonItem totalMsg = new CommonItem(
                    0, "目前卡内总余额为：", "￥ " + total, "以上信息来自网络");
            commonItems.add(0, totalMsg);
            handler.sendEmptyMessage(0);
            return null;
        }

        private String[] getExtras() throws IOException {
            String[] extras = new String[2];
            Request request = new Request.Builder().url(StaticResource.Campus_Extras).build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String htmlHolder = response.body().string();
                Document document = Jsoup.parse(htmlHolder);
                Elements elements = document.getElementsByTag("input");
                for (Element element : elements) {
                    if (element.attr("name").equals("lt"))
                        extras[0] = element.attr("value");
                    if (element.attr("name").equals("execution"))
                        extras[1] = element.attr("value");
                }
            }
            return extras;
        }
    }
}
