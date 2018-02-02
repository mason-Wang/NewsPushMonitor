package com.wmx.newspushmonitor;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.wmx.newspushmonitor.util.MiscUtil;

/**
 * Created by wangmingxing on 18-2-1.
 */

public class AppWakeupService extends IntentService {

    public AppWakeupService() {
        super("app_wakeup_service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String[] apps = GlobalConfig.getMonitorApps();
        for(String app : apps) {
            MiscUtil.startApp(this, app);
            sleep(10000);
            MiscUtil.startApp(this, getPackageName());
            sleep(10000);

            if (!GlobalConfig.isAutoWakeUpApps() || GlobalConfig.mIsSettingMode) {
                break;
            }
        }
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
