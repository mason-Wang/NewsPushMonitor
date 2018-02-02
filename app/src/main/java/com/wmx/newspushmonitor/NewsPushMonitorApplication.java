package com.wmx.newspushmonitor;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by wangmingxing on 18-1-31.
 */

public class NewsPushMonitorApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        GlobalConfig.init(this);
        startAppWakeupAlarm(this, 60 * 1000);
    }

    public static void startAppWakeupAlarm(Context context, int timeout) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, PushMonitorBroadcastReceiver.class);
        intent.setAction(GlobalConfig.ACTION_WAKEUP_APPS);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + timeout, pi);
    }

    public static void startAppWakeupAlarm(Context context) {
        startAppWakeupAlarm(context, GlobalConfig.getAppsWakeupInterval());
    }
}
