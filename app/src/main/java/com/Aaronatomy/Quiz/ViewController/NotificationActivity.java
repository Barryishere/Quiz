package com.Aaronatomy.Quiz.ViewController;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.Aaronatomy.Quiz.Database.Message;
import com.Aaronatomy.Quiz.Database.StaticResource;
import com.Aaronatomy.Quiz.Database.User;
import com.Aaronatomy.Quiz.Model.DataSourceChangedHandler;
import com.Aaronatomy.Quiz.Protocol.OnItemClickListener;
import com.Aaronatomy.Quiz.Protocol.QNormalActivity;
import com.Aaronatomy.Quiz.Protocol.Updatable;
import com.Aaronatomy.Quiz.R;
import com.Aaronatomy.Quiz.Utility.CommonAdapter;
import com.Aaronatomy.Quiz.Utility.CommonItem;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationActivity extends QNormalActivity implements Updatable {
    private DataSourceChangedHandler handler = new DataSourceChangedHandler(this);
    private CoordinatorLayout coordinatorLayout;
    private ArrayList<CommonItem> commonItems;
    private RecyclerView recyclerView;
    private CommonAdapter adapter;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        setReceiver(); // 注册广播接收器
        setView();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver); // 注销广播接收器
        super.onDestroy();
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    @Override
    public void refreshDataSource() {
        Message msg = DataSupport.findLast(Message.class);
        commonItems.add(0, new CommonItem(
                msg.getID(),
                msg.getTopic(),
                msg.getMessage(),
                msg.getTimeStamp()));
    }

    // 初始化广播接收器
    // 接收 Aaronatomy.Quiz.Broadcast.MsgArrived
    // 需要在Fragment生命周期结束时注销
    // 否则可能造成内存泄漏
    private void setReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(StaticResource.BroadCast_MsgArrived))
                    NotificationActivity.this.handler.sendEmptyMessage(0x00);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(StaticResource.BroadCast_MsgArrived);
        registerReceiver(receiver, filter);
    }

    // 初始化UI
    private void setView() {
        ImageButton post = findViewById(R.id.notification_buttonPost);
        coordinatorLayout = findViewById(R.id.notification_coordinatorLayout);
        recyclerView = findViewById(R.id.notification_notifyList);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (User.getUser().isAdmin()) {
                    Intent intent = new Intent(NotificationActivity.this, PostMsgActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_fade_in, 0);
                } else if (StaticResource.isDemoMode) {
                    Toast.makeText(NotificationActivity.this,
                            "现在是演示模式",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NotificationActivity.this, PostMsgActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_fade_in, 0);
                } else {
                    Toast.makeText(NotificationActivity.this,
                            "只有拥有管理员权限才能发送消息",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bindAdapter();
    }

    // 初始化adapter并与RecyclerView绑定
    private void bindAdapter() {
        // fragment在切换的时候会call onCreateView() -> bindAdapter()
        // 需要检查adapter是否为null，避免重复初始化，造成数据重复
        if (adapter == null) {
            adapter = new CommonAdapter(getNotifications());
            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onClick(CommonItem item, final int position) {
                    final long id = item.getId();
                    final String msg = item.getContent();
                    AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(NotificationActivity.this);
                    normalDialog.setTitle(item.getTitle());
                    normalDialog.setMessage(item.getContent());
                    normalDialog.setPositiveButton("提醒",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(NotificationActivity.this,
                                            MakeReminderActivity.class);
                                    intent.putExtra(StaticResource.Msg, msg);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.anim_fade_in, 0);
                                }
                            });
                    normalDialog.setNegativeButton("删除",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showConfirmSnack(position, id);
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
    }

    // 为RecyclerView提供数据源
    private List<CommonItem> getNotifications() {
        if (commonItems == null)
            commonItems = new ArrayList<>();

        List<Message> messages = DataSupport.findAll(Message.class);
        if (messages.size() > 0)
            for (Message msg : messages) // 遍历TableMessage并取得所有非聊天消息
                commonItems.add(new CommonItem(msg.getID(),
                        msg.getTopic(), msg.getMessage(), msg.getTimeStamp()));

        Collections.reverse(commonItems); // 按消息时间倒序排列
        return commonItems;
    }

    // 显示确认删除的Snack
    private void showConfirmSnack(final int position, final long id) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout,
                NotificationActivity.this.getText(R.string.alertDeleteCanNotRecall),
                Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(NotificationActivity.this.getResources().getColor(R.color.colorAlert));
        snackbar.setAction(NotificationActivity.this.getText(R.string.delete), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.deleteItem(position, id, Message.class);
            }
        }).show();
    }
}
