package com.wmx.newspushmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by wangmingxing on 18-1-31.
 */

public class PushMonitorBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(GlobalConfig.ACTION_WAKEUP_APPS)) {
            NewsPushMonitorApplication.startAppWakeupAlarm(context);
            if (GlobalConfig.isAutoWakeUpApps()) {
                Intent wakeupServiceIntent = new Intent(context, AppWakeupService.class);
                context.startService(wakeupServiceIntent);
                return;
            }
        }

        if (GlobalConfig.mIsSettingMode) {
            return;
        }

        Intent it = new Intent();
        it.setClass(context, MainActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(it);
    }
}
