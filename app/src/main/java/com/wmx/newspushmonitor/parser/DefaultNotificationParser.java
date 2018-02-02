package com.wmx.newspushmonitor.parser;

import android.content.Intent;
import android.text.TextUtils;

import com.wmx.newspushmonitor.GlobalConfig;

/**
 * Created by wangmingxing on 18-1-29.
 */

public class DefaultNotificationParser extends BaseNotificationParser {

    public DefaultNotificationParser(String pkg) {
        super(pkg);
    }

    @Override
    protected void onParseTextView(int viewId, String text, NewsInfo newsInfo) {
        int titleViewId = GlobalConfig.getTitleViewId(newsInfo.packageName);
        if (titleViewId == viewId && TextUtils.isEmpty(newsInfo.contentTitle)) {
            newsInfo.contentTitle = text;
            return;
        }

        int contentViewId = GlobalConfig.getContentViewId(newsInfo.packageName);
        if (contentViewId == viewId && TextUtils.isEmpty(newsInfo.contentText)) {
            newsInfo.contentText = text;
        }
    }

    @Override
    protected void onParseIntent(Intent intent, NewsInfo newsInfo) {

    }
}


