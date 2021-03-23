package com.Aaronatomy.Quiz.ViewController;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.Aaronatomy.Quiz.Database.StaticResource;
import com.Aaronatomy.Quiz.Database.User;
import com.Aaronatomy.Quiz.Protocol.QDialogActivity;
import com.Aaronatomy.Quiz.R;
import com.Aaronatomy.Quiz.Model.QAction;

public class PostMsgActivity extends QDialogActivity {
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_post_msg);
        setView();
    }

    private void setView() {
        editText = findViewById(R.id.activityPost_Input);
        Button button = findViewById(R.id.activityPost_post);
        Button buttonIns = findViewById(R.id.activityPost_postAsIns);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String says = editText.getText().toString();
                if (!TextUtils.isEmpty(says)) {
                    String topic = User.getUser().getMajor();
                    QAction.makeAction(PostMsgActivity.this, says, topic);
                    finish();
                } else
                    Toast.makeText(PostMsgActivity.this,
                            "请输入通知内容", Toast.LENGTH_SHORT).show();
            }
        });

        buttonIns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String says = editText.getText().toString();
                if (!TextUtils.isEmpty(says)) {
                    QAction.makeAction(PostMsgActivity.this, says, StaticResource.Institution);
                    finish();
                } else
                    Toast.makeText(PostMsgActivity.this,
                            "请输入通知内容", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
