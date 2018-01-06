package com.wmx.newspushmonitor;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.util.List;

/**
 * Created by wangmingxing on 17-12-25.
 */

public class PushMonitorAccessibilityService extends AccessibilityService {
    private static final String TAG = "PushMonitorService";

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.packageNames = GlobalConfig.sMonitorApps;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
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

        Log.i(TAG, "onAccessibilityEvent "
                + "package=" + pkg
                + ",time=" + time
                + ",text=" + sb.toString());
    }

    @Override
    public void onInterrupt() {

    }
}
