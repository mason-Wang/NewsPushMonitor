package com.wmx.newspushmonitor;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Intent;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;

import com.wmx.newspushmonitor.parser.BaseNotificationParser;
import com.wmx.newspushmonitor.parser.NotificationParserFactory;
import com.wmx.newspushmonitor.util.LogWriter;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by wangmingxing on 17-12-25.
 */

public class PushMonitorAccessibilityService extends AccessibilityService {
    private static final String TAG = "PushMonitorService";

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = getServiceInfo();
        info.packageNames = GlobalConfig.getMonitorApps();
        setServiceInfo(info);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        int type = accessibilityEvent.getEventType();
        if (type != AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            return;
        }

        String pkg = accessibilityEvent.getPackageName().toString();
        long time = accessibilityEvent.getEventTime();
        Notification notification = (Notification) accessibilityEvent.getParcelableData();
        List<CharSequence> texts = accessibilityEvent.getText();

        StringBuilder sb = new StringBuilder();
        for (CharSequence text : texts) {
            sb.append(text).append("###");
        }

        LogWriter.i(TAG, "onAccessibilityEvent "
                + "package=" + pkg
                + ",time=" + time
                + ",text=" + sb.toString());

        LogWriter.i(TAG, "Parse notification for " + pkg + " begin!");
        BaseNotificationParser notificationParser = NotificationParserFactory.getNotificationParser(pkg);
        BaseNotificationParser.NewsInfo newsInfo = notificationParser.parse(notification);
        LogWriter.i(TAG, "when:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(newsInfo.when));
        LogWriter.i(TAG, "ContentTitle:" + newsInfo.contentTitle);
        LogWriter.i(TAG, "ContentText:" + newsInfo.contentText);
        LogWriter.i(TAG, "Url:" + newsInfo.url);
        LogWriter.i(TAG, "Parse notification for " + pkg + " end!");
        LogWriter.i(TAG, "##################################################################");

        commitNewsInfoToServer(newsInfo);
        showNewsInfo(newsInfo);
    }

    @Override
    public void onInterrupt() {

    }

    private void commitNewsInfoToServer(final BaseNotificationParser.NewsInfo newsInfo) {
        // TODO uploads notification infos to server
    }

    private void showNewsInfo(BaseNotificationParser.NewsInfo newsInfo) {
        if (GlobalConfig.mIsSettingMode) {
            return;
        }

        StringBuilder sb = new StringBuilder(512);
        if (!TextUtils.isEmpty(newsInfo.extras)) {
            sb.append(newsInfo.extras);
        }

        sb.append("PackageName:").append(newsInfo.packageName)
            .append("\nTime:").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(newsInfo.when))
            .append("\nTitle:").append(newsInfo.contentTitle)
            .append("\nText:").append(newsInfo.contentText);

        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.putExtra("newsInfo", sb.toString());
        intent.putExtra("packageName", newsInfo.packageName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
