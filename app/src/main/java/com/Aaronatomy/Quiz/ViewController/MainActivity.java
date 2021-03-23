package com.Aaronatomy.Quiz.ViewController;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.Aaronatomy.Quiz.Database.Reminder;
import com.Aaronatomy.Quiz.Database.StaticResource;
import com.Aaronatomy.Quiz.Database.User;
import com.Aaronatomy.Quiz.Model.DataSourceChangedHandler;
import com.Aaronatomy.Quiz.Model.MessageService;
import com.Aaronatomy.Quiz.Protocol.OnItemClickListener;
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
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by AaronAtomy on 2018/2/20.
 * MainActivity
 */

public class MainActivity extends QNormalActivity implements Updatable {
    private DataSourceChangedHandler handler = new DataSourceChangedHandler(this);
    private ArrayList<CommonItem> commonItems;
    private CommonAdapter adapter;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setReceiver();
        setView();

        Intent intent = new Intent(MainActivity.this, MessageService.class);
        startService(intent);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this);
        normalDialog.setIcon(R.drawable.ic_error);
        normalDialog.setTitle("真的要退出吗");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        normalDialog.show();
    }

    @Override
    public void refreshDataSource() {
        Reminder reminder = DataSupport.findLast(Reminder.class);
        commonItems.add(0, parseReminder(reminder));
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    private void setReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(StaticResource.BroadCast_DataSetChanged))
                    MainActivity.this.handler.sendEmptyMessage(0x00);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(StaticResource.BroadCast_DataSetChanged);
        registerReceiver(receiver, filter);
    }

    // 初始化UI
    private void setView() {
        Button buttonSyllabus = findViewById(R.id.main_buttonSyllabus);
        Button buttonGrade = findViewById(R.id.main_buttonGrade);
        Button buttonNotification = findViewById(R.id.main_buttonNotification);
        Button buttonCard = findViewById(R.id.main_buttonCard);
        Button buttonBook = findViewById(R.id.main_buttonBook);
        Button buttonAbout = findViewById(R.id.main_buttonAbout);
        Button buttonMake = findViewById(R.id.activityMain_make);
        RecyclerView recyclerView = findViewById(R.id.activityMain_list);

        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (adapter == null) {
            adapter = new CommonAdapter(getReminders());
            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onClick(CommonItem item, final int position) {
                    final long id = item.getId();
                    final String msg = item.getContent();
                    AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(MainActivity.this);
                    normalDialog.setTitle("确认删除提醒吗？");
                    normalDialog.setIcon(R.drawable.ic_error);
                    normalDialog.setPositiveButton("取消", null);
                    normalDialog.setNegativeButton("删除",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    adapter.deleteItem(position, id, Reminder.class);
                                }
                            });
                    AlertDialog alertDialog = normalDialog.create();
                    alertDialog.show();
                    Button button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    button.setTextColor(getResources().getColor(R.color.colorAlert));
                }
            });
        }
        recyclerView.setAdapter(adapter);

        buttonSyllabus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SyllabusActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        buttonGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GradeActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        buttonNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        buttonCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CardActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        buttonBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputPasswordDialog();
            }
        });

        buttonAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo
            }
        });

        buttonMake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,
                        MakeReminderActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fade_in, 0);
            }
        });
    }

    private List<CommonItem> getReminders() {
        if (commonItems == null)
            commonItems = new ArrayList<>();

        List<Reminder> messages = DataSupport.findAll(Reminder.class);
        if (messages.size() > 0)
            for (Reminder msg : messages)
                commonItems.add(parseReminder(msg));

        Collections.reverse(commonItems);
        return commonItems;
    }

    private CommonItem parseReminder(Reminder msg) {
        String result, duration;
        Calendar calendar = Calendar.getInstance();
        long currentTime = System.currentTimeMillis();
        calendar.setTimeInMillis(msg.getWhen());
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH-mm-ss", Locale.getDefault());
        String reminderTime = dateFormat.format(date);

        if (currentTime > msg.getWhen()) {
            result = "提醒已完成";
            duration = "提醒于：" + reminderTime;
        } else {
            result = "提醒正在进行";
            duration = "将在" + reminderTime + "提醒";
        }

        return new CommonItem(msg.getID(), msg.getMsg(), result, duration);
    }

    private void showInputPasswordDialog() {
        final EditText editText = new EditText(MainActivity.this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this);
        normalDialog.setIcon(R.drawable.ic_error);
        normalDialog.setTitle("请输入校园卡密码");
        normalDialog.setView(editText);
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String string = editText.getText().toString();
                        if (!TextUtils.isEmpty(string))
                            showBorrowInformationDialog(string);
                    }
                });
        normalDialog.setNegativeButton("取消", null);
        normalDialog.show();
    }

    private void showBorrowInformationDialog(final String password) {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this);
        normalDialog.setIcon(R.drawable.ic_error);
        normalDialog.setTitle("借阅信息");
        normalDialog.setMessage("查询中，请稍后");
        normalDialog.setPositiveButton("确定", null);
        AlertDialog alertDialog = normalDialog.create();
        alertDialog.show();
        new GetBorrowInformation(password, alertDialog).execute();
    }

    static class GetBorrowInformation extends AsyncTask<String, Void, Void> {
        private OkHttpClient client;
        private String password;
        private String total, arrears, remain, msg;
        private WeakReference<AlertDialog> alertDialogWeakReference;

        GetBorrowInformation(String password, AlertDialog alertDialog) {
            client = QApplication.getOkHttpClient();
            this.password = password;
            alertDialogWeakReference = new WeakReference<>(alertDialog);
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
                        Elements cells = document.getElementsByTag("td");
                        for (Element cell : cells) {
                            if (cell.text().contains("共借图书")) {
                                Element href = cell.child(0).child(0);
                                total = href.ownText();
                            }
                            if (cell.text().contains("欠费金额")) {
                                Element href = cell.child(0).child(0);
                                arrears = href.ownText();
                            }
                            if (cell.text().contains("未还图书")) {
                                Element href = cell.child(0).child(0);
                                remain = href.ownText();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!TextUtils.isEmpty(total) && !TextUtils.isEmpty(arrears) && !TextUtils.isEmpty(remain))
                msg = "共借图书：" + total + "本\n"
                        + "欠款金额：" + arrears + "元\n"
                        + "未还图书：" + remain + "本";
            else
                msg = "查询失败，请在核对账号密码后重试";

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            AlertDialog alertDialog = alertDialogWeakReference.get();
            alertDialog.setMessage(msg);
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
