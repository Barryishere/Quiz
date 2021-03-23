package com.Aaronatomy.Quiz.Utility;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.Aaronatomy.Quiz.Protocol.OnItemClickListener;
import com.Aaronatomy.Quiz.R;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by AaronAtomy on 2018/3/18.
 * CommonAdapter
 */

public class CommonAdapter extends RecyclerView.Adapter<CommonAdapter.ViewHolder> {
    private OnItemClickListener itemClickListener;
    private List<CommonItem> commonItems;

    // 默认构造方法，接收一个NotifyItem泛型数组
    public CommonAdapter(List<CommonItem> commonItems) {
        this.commonItems = commonItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext()).inflate(R.layout.listitem_notify, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CommonItem item = commonItems.get(position);
        holder.title.setText(item.getTitle());
        holder.content.setText(item.getContent());
        holder.message.setText(item.getMessage());

        if (itemClickListener != null) {
            View view = ((ViewGroup) holder.itemView).getChildAt(0);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int iPosition = holder.getAdapterPosition();
                    itemClickListener.onClick(item, iPosition);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return commonItems.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        itemClickListener = onItemClickListener;
    }

    public void deleteItem(int position, long id, Class type) {
        commonItems.remove(position); // 删除项目
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, commonItems.size() - 1); // 更新列表
        if (DataSupport.find(type, id) != null)
            DataSupport.delete(type, id); // 从数据库中移除项目
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView content;
        TextView message;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.fragment1_tagTitle);
            content = itemView.findViewById(R.id.fragment1_tagContent);
            message = itemView.findViewById(R.id.fragment1_tagTimeStamp);
        }
    }
}
