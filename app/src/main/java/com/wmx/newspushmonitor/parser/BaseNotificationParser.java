package com.wmx.newspushmonitor.parser;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.wmx.newspushmonitor.util.LogWriter;
import com.wmx.newspushmonitor.util.RefUtil;

import java.util.Iterator;
import java.util.List;

/**
 * Created by wangmingxing on 18-1-24.
 */

public abstract class BaseNotificationParser {
    protected static final String TAG = "NotificationParser";
    private String packageName;

    public BaseNotificationParser(String pkg) {
        packageName = pkg;
    }

    public static class NewsInfo {
        public String packageName;  // The package of the news come from
        public String contentTitle;  // News notification title
        public String contentText;  // News notification content
        public String url;  // The url of the news
        public long when;   // The time when news received
        public String extras;
    }

    protected abstract void onParseTextView(int viewId, String text, NewsInfo newsInfo);

    protected abstract void onParseIntent(Intent intent, NewsInfo newsInfo);

    public NewsInfo parse(Notification notification) {
        NewsInfo newsInfo = new NewsInfo();
        if (notification == null) {
            return newsInfo;
        }

        newsInfo.when = notification.when;
        newsInfo.packageName = packageName;
        CharSequence title = notification.extras.getCharSequence(Notification.EXTRA_TITLE);
        if (TextUtils.isEmpty(title)) {
            title = notification.extras.getCharSequence(Notification.EXTRA_TITLE_BIG);
        }
        if (!TextUtils.isEmpty(title)) {
            newsInfo.contentTitle = title.toString();
        }

        CharSequence text = notification.extras.getCharSequence(Notification.EXTRA_TEXT);
        if (TextUtils.isEmpty(text)) {
            text = notification.extras.getCharSequence(Notification.EXTRA_BIG_TEXT);
        }
        if (!TextUtils.isEmpty(text)) {
            newsInfo.contentText = text.toString();
        }

        LogWriter.i(TAG, "contentTitle:" + newsInfo.contentTitle);
        LogWriter.i(TAG, "contentText:" + newsInfo.contentText);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            PendingIntent pendingIntent = notification.contentIntent;
            if (pendingIntent != null) {
                try {
                    Intent intent = (Intent) RefUtil.callDeclaredMethod(
                            pendingIntent, "getIntent", new Class[]{});
                    StringBuilder sb = new StringBuilder(1024);
                    intentToString(sb, intent);

                    LogWriter.i(TAG, "intent:" + sb.toString());
                    onParseIntent(intent, newsInfo);
                } catch (Exception e) {
                    LogWriter.e(TAG, e);
                }
            }
        }

        RemoteViews rv = notification.contentView;
        if (rv == null) {
            return newsInfo;
        }

        List mActions = (List) RefUtil.getDeclaredFieldValue(rv,"mActions");
        if (mActions != null) {
            StringBuilder extrasb = new StringBuilder();
            for (Object action : mActions) {
                try {
                    String actionName = (String) RefUtil.callDeclaredMethod(action,
                            "getActionName", new Class[]{});
                    Log.i(TAG, "Action Name:" + actionName);
                    if (actionName.startsWith("ReflectionAction")) {
                        String methodName = (String) RefUtil.getDeclaredFieldValue(action, "methodName");
                        if ("setText".equals(methodName)) {
                            CharSequence value = (CharSequence)
                                    RefUtil.getDeclaredFieldValue(action, "value");
                            int viewId = (int) RefUtil.getDeclaredFieldValue(action, "viewId");
                            LogWriter.i(TAG, "TextView ViewId:Text = 0x"
                                    + Integer.toHexString(viewId) + ":" + value);

                            extrasb.append("Viewid:Text=")
                                    .append(viewId)
                                    .append(":")
                                    .append(text).append("\n");

                            onParseTextView(viewId, value == null ? null : value.toString(), newsInfo);
                        }
                    } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M
                            && actionName.equals("SetOnClickPendingIntent")) {
                        int viewId = (int) RefUtil.getDeclaredFieldValue(action, "viewId");
                        PendingIntent pi = (PendingIntent)
                                RefUtil.getDeclaredFieldValue(action, "pendingIntent");
                        Intent intent = (Intent) RefUtil.callDeclaredMethod(
                                pi, "getIntent", new Class[] {});

                        StringBuilder sb = new StringBuilder(1024);
                        intentToString(sb, intent);
                        LogWriter.i(TAG, "ViewId:Intent = 0x" + Integer.toHexString(viewId) + ":" + sb.toString());
                        onParseIntent(intent, newsInfo);
                    }
                } catch (Exception e) {
                    LogWriter.e(TAG, e);
                }
            }

            newsInfo.extras = extrasb.toString();
        }

        return newsInfo;
    }

    private void intentToString(StringBuilder b, Intent intent) {
        boolean first = true;

        b.append("Intent detials { ");
        String data = intent.getDataString();
        if (data != null) {
            b.append("dat=").append(data);
            first = false;
        }

        try {
            Bundle extras = intent.getExtras();
            LogWriter.i(TAG, "Intent extras=" + extras);
            if (extras != null) {
                RefUtil.callDeclaredMethod(extras, "unparcel", new Class[]{});

                if (!first) {
                    b.append(' ');
                }
                b.append("extras=").append(extras.toString());
            }

            if (extras != null) {
                Iterator<String> keyIter = extras.keySet().iterator();
                while (keyIter.hasNext()) {
                    String key = keyIter.next();
                    Object value = extras.get(key);
                    if (value instanceof Intent) {
                        intentToString(b, (Intent) value);
                    }
                }
            }
        } catch (Exception e) {
            LogWriter.e(TAG, e);
        } finally {
            b.append(" }");
        }
    }
}
