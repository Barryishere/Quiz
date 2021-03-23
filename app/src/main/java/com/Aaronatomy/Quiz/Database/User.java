package com.Aaronatomy.Quiz.Database;

import android.support.annotation.Nullable;

import org.litepal.crud.DataSupport;

/**
 * Created by AaronAtomy on 2018/3/2.
 * 用于存储用户基本信息的数据库
 */

public class User extends DataSupport {
    // 基本信息
    private String account;
    private String password;
    private String major;
    private String name;
    private boolean isAdmin;

    // 扩展信息
    private String urlRef;
    private String urlTable;
    private String urlGrade;
    private String urlTest;
    private String viewStateTable;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getUrlRef() {
        return urlRef;
    }

    public void setUrlRef(String urlRef) {
        this.urlRef = urlRef;
    }

    public String getUrlTable() {
        return urlTable;
    }

    public void setUrlTable(String urlTable) {
        this.urlTable = urlTable;
    }

    public String getUrlGrade() {
        return urlGrade;
    }

    public void setUrlGrade(String urlGrade) {
        this.urlGrade = urlGrade;
    }

    public String getViewStateTable() {
        return viewStateTable;
    }

    public void setViewStateTable(String viewStateTable) {
        this.viewStateTable = viewStateTable;
    }

    @Nullable
    public static User getUser() {
        return DataSupport.findFirst(User.class);
    }
}
