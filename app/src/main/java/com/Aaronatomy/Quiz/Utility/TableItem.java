package com.Aaronatomy.Quiz.Utility;

import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by AaronAtomy on 2018/4/6.
 * TableItem
 */

public class TableItem {
    private String course; // 课程名
    private WeekDay day; // 周几的课
    private int[] period; // 一天中的哪几节上课
    private String teacher; // 教师
    private String room; // 教室
    private String extra;
    private String extras;

    public TableItem(String course, String extras, String teacher, String room) {
        this.course = course;
        this.teacher = teacher;
        this.room = room;
        this.extras = extras;
        extrasParser(extras);
    }

    private void extrasParser(String time) {
        // time是形如”周五第3,4节{第4-17周}“的一个字符串，从中分裂出需要的信息
        String[] baked = time.replace("第", "=")
                .replace("节", "=")
                .replace("{", "=")
                .replace("}", "=")
                .split("=");

        ArrayList<String> params = new ArrayList<>();
        for (String aBaked : baked)
            if (!TextUtils.isEmpty(aBaked))
                params.add(aBaked);

        if (params.size() == 3) {
            switch (params.get(0)) {
                case "周一":
                    day = WeekDay.DAY1;
                    break;
                case "周二":
                    day = WeekDay.DAY2;
                    break;
                case "周三":
                    day = WeekDay.DAY3;
                    break;
                case "周四":
                    day = WeekDay.DAY4;
                    break;
                case "周五":
                    day = WeekDay.DAY5;
                    break;
                case "周六":
                    day = WeekDay.DAY6;
                    break;
                case "周日":
                    day = WeekDay.DAY7;
                    break;
            }

            String[] tempStr = params.get(1).split(",");
            period = new int[tempStr.length];
            for (int i = 0; i < tempStr.length; i++)
                period[i] = Integer.valueOf(tempStr[i]);

            extra = params.get(2);
        }
    }

    public String getCourse() {
        return course;
    }

    public WeekDay getDay() {
        return day;
    }

    public int[] getPeriod() {
        return period;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getRoom() {
        return room;
    }

    public String getExtra() {
        return extra;
    }

    @Override
    public String toString() {
        return String.valueOf(new StringBuilder(course + "@" + room +"\n"+ extra));
    }
}
