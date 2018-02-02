package com.wmx.newspushmonitor.ui;

/**
 * Created by wangmingxing on 18-1-31.
 */

public class NewsInfoItem {
    public String packageName;
    public String newsInfo;

    public NewsInfoItem(String packageName, String newsInfo) {
        this.packageName = packageName;
        this.newsInfo = newsInfo;
    }
}
