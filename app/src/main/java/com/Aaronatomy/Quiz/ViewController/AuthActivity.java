package com.Aaronatomy.Quiz.ViewController;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.Aaronatomy.Quiz.Database.StaticResource;
import com.Aaronatomy.Quiz.Database.User;
import com.Aaronatomy.Quiz.Model.CookieProvider;
import com.Aaronatomy.Quiz.Model.PeekTable;
import com.Aaronatomy.Quiz.Protocol.QNormalActivity;
import com.Aaronatomy.Quiz.R;
import com.Aaronatomy.Quiz.Utility.QApplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by AaronAtomy on 2018/2/20.
 * AuthActivity
 */

public class AuthActivity extends QNormalActivity {
    private EditText inputAccount;
    private EditText inputPassword;
    private EditText inputVerifyCode;
    private ImageView verifyCodeView;
    private OnLoginHandler loginHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        setView();

        loginHandler = new OnLoginHandler(this);
        new GetVerifyCode(verifyCodeView).execute();
    }

    private void setView() {
        verifyCodeView = findViewById(R.id.auth_verifyCode);
        inputAccount = findViewById(R.id.auth_inputAccount);
        inputPassword = findViewById(R.id.auth_inputPassword);
        inputVerifyCode = findViewById(R.id.auth_inputVerifyCode);
        final Button loginButton = findViewById(R.id.auth_loginButton);

        verifyCodeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetVerifyCode(verifyCodeView).execute();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account = inputAccount.getText().toString();
                String password = inputPassword.getText().toString();
                String verifyCode = inputVerifyCode.getText().toString();
                if (verifyLoginInfor(account, password, verifyCode))
                    new Login(loginHandler, account, password, verifyCode).execute();
            }
        });

        // 如果查找到保存的用户信息则自动填充
        User user = DataSupport.findFirst(User.class);
        if (user != null) {
            inputAccount.setText(user.getAccount());
            inputPassword.setText(user.getPassword());
        }
    }

    private boolean verifyLoginInfor(String account, String password, String verifyCode) {
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password) || TextUtils.isEmpty(verifyCode)) {
            if (TextUtils.isEmpty(account))
                inputAccount.setError("请输入账号");
            else if (TextUtils.isEmpty(password))
                inputPassword.setError("请输入密码");
            else if (TextUtils.isEmpty(verifyCode))
                inputVerifyCode.setError("请输入验证码");
            return false;
        } else
            return true;
    }

    // 处理用户登陆后的界面刷新
    static class OnLoginHandler extends Handler {
        private WeakReference<AuthActivity> authActivity;
        static final int Login_Succeed = 1;
        static final int Login_Failed = 0;

        OnLoginHandler(AuthActivity authActivity) {
            this.authActivity = new WeakReference<>(authActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AuthActivity activity = authActivity.get();
            if (msg.what == Login_Succeed) {
                activity.startActivity(new Intent(activity, MainActivity.class));
                activity.finish();
            } else {
                new GetVerifyCode(activity.verifyCodeView).execute();
                activity.inputVerifyCode.setText("");
                Toast.makeText(activity, "密码或验证码错误，请重试", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 处理用户登陆
    static class Login extends AsyncTask<String, Boolean, Boolean> {
        private String account;
        private String password;
        private String verifyCode;
        private User user;

        private OkHttpClient client;
        private AuthActivity.OnLoginHandler loginHandler;

        Login(AuthActivity.OnLoginHandler loginHandler,
              String account, String password, String verifyCode) {
            this.client = QApplication.getOkHttpClient();
            this.loginHandler = loginHandler;
            this.account = account;
            this.password = password;
            this.verifyCode = verifyCode;

            // 由于校园网的会话只保存一个Session
            // 上次的信息可能无效，每次启动APP都需要重新登陆
            DataSupport.deleteAll(User.class);
            user = new User();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            FormBody formBody = new FormBody.Builder()
                    .add("__VIEWSTATE", StaticResource.ViewState)
                    .add("txtUserName", account)
                    .add("Textbox1", "")
                    .add("TextBox2", password)
                    .add("txtSecretCode", verifyCode)
                    .add("RadioButtonList1", StaticResource.Login_As_Student)
                    .add("Button1", "")
                    .add("lbLanguage", "")
                    .add("hidPdrs", "")
                    .add("hidsc", "")
                    .build();
            Request request = new Request.Builder()
                    .url(StaticResource.Url_Login)
                    .header("Cookie", CookieProvider.get(StaticResource.Url_VerifyCode))
                    .post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.priorResponse() != null && response.priorResponse().message().equals("Found")) {
                    String href, htmlHolder, urlRef, urlTable = null, urlGrade = null;
                    urlRef = StaticResource.Campus_Home + account;
                    htmlHolder = response.body().string();
                    Document document = Jsoup.parse(htmlHolder);
                    Elements links = document.getElementsByTag("a");
                    for (Element link : links) {
                        href = link.attr("href");
                        if (href.contains("xskbcx"))
                            urlTable = StaticResource.Campus_Host + href; // 个人课表
                        if (href.contains("xscj"))
                            urlGrade = StaticResource.Campus_Host + href; // 成绩查询
                    }

                    if (!TextUtils.isEmpty(urlTable) && !TextUtils.isEmpty(urlGrade)) {
                        setUserPre(urlRef, urlTable, urlGrade);
                        PeekTable peek = new PeekTable();
                        QApplication.setSemester(peek.getSemester());
                        htmlHolder = peek.startPeek("", "");
                        Document document1 = Jsoup.parse(htmlHolder);
                        Element rawName = document1.getElementById("Label6");
                        Element rawMajor = document1.getElementById("Label9");

                        String name = rawName.text().replace("姓名：", "");
                        String major = rawMajor.text().replace("行政班：", "");
                        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(major))
                            setUserFinal(name, major);
                    } else return false;

                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Message msg = new Message();
            if (aBoolean)
                msg.what = AuthActivity.OnLoginHandler.Login_Succeed;
            else
                msg.what = AuthActivity.OnLoginHandler.Login_Failed;

            loginHandler.sendMessage(msg);
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            super.onProgressUpdate(values);
        }

        private void setUserPre(String ref, String table, String grade) {
            user.setAccount(account);
            user.setPassword(password);
            user.setUrlRef(ref);
            user.setUrlTable(table);
            user.setUrlGrade(grade);
            user.save();
        }

        private void setUserFinal(String name, String major) {
            User user = DataSupport.findFirst(User.class);
            user.setName(name);
            user.setMajor(major);
            if (account.equals("160614055"))
                user.setAdmin(true);
            else
                user.setAdmin(false);
            user.save();
        }
    }

    // 获取验证码
    static class GetVerifyCode extends AsyncTask<Void, Void, Bitmap> {
        private final WeakReference<ImageView> imageView;
        private OkHttpClient client;

        GetVerifyCode(ImageView imageView) {
            this.client = QApplication.getOkHttpClient();
            this.imageView = new WeakReference<>(imageView);
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Request request = new Request.Builder()
                    .url(StaticResource.Url_VerifyCode)
                    .build();
            Bitmap bitmap = null;

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    Headers headers = response.headers();
                    HttpUrl url = request.url();
                    List<Cookie> cookies = Cookie.parseAll(url, headers);
                    if (cookies.size() > 0)
                        client.cookieJar().saveFromResponse(url, cookies);

                    byte[] pic = response.body().bytes();
                    bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView view = imageView.get();
            view.setImageBitmap(bitmap); // 更新验证码
        }
    }
}
