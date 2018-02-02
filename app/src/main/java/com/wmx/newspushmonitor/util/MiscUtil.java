package com.wmx.newspushmonitor.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.security.MessageDigest;

/**
 * Created by wangmingxing on 18-1-29.
 */

public class MiscUtil {

    public static String getApplicationName(Context context, String packageName) {
        PackageManager packageManager;
        ApplicationInfo applicationInfo;
        try {
            packageManager = context.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }

        String applicationName =
                (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }

    public static String md5(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(string.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuilder buf = new StringBuilder();
            for (byte element : b) {
                i = element;
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static void startApp(Context context, String packageName){
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if(intent != null){
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
            context.startActivity(intent);
        }
    }
}
