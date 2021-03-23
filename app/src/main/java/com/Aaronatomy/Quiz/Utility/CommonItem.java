package com.Aaronatomy.Quiz.Utility;

/**
 * Created by AaronAtomy on 2018/2/14.
 * 通知列表项
 */

public class CommonItem {
    private long id;
    private String title;
    private String content;
    private String message;

    public CommonItem(long id, String title, String content, String message) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.message = message;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getMessage() {
        return message;
    }

}
