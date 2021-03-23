package com.Aaronatomy.Quiz.Protocol;

import android.support.v7.widget.RecyclerView;

/**
 * Created by AaronAtomy on 2018/4/24.
 * Updatable
 */

public interface Updatable {
    public void refreshDataSource();

    public RecyclerView.Adapter getAdapter();
}
