package com.wmx.newspushmonitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.wmx.newspushmonitor.util.SharedPref;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by wangmingxing on 17-12-25.
 */

public class GlobalConfig {
    public static Context mAppContext;
    public static final String ACTION_WAKEUP_APPS = "com.wmx.newpushmonitor.ACTION_WAKEUP_APPS";
    private static final int DEFAULT_APP_WAKEUP_INTERVAL_MINS = 30;
    public static boolean mIsSettingMode = false;

    public final static String PKG_UCBROWER = "com.UCMobile";
    public final static String PKG_UCTOUTIAO = "com.uc.infoflow";
    public final static String PKG_JINRITOUTIAO = "com.ss.android.article.news";
    public final static String PKG_TENCENTNEWS = "com.tencent.news";
    public final static String PKG_FENGHUANGNEWS = "com.ifeng.news2";
    public final static String PKG_NETEASENEWS = "com.netease.newsreader.activity";
    public final static String PKG_SOUHUNEWS = "com.sohu.newsclient";
    public final static String PKG_SINANEWS = "com.sina.news";
    public final static String PKG_YIDIANZIXUN = "com.hipu.yidian";
    public final static String PKG_TIANTIANKUAIBAO = "com.tencent.reading";
    public final static String PKG_QQBROWER = "com.tencent.mtt";
    public final static String PKG_ZAKER = "com.myzaker.ZAKER_Phone";
    public final static String PKG_PENGPAI = "com.wondertek.paper";
    public final static String PKG_FLIPBOARD = "flipboard.cn";
    public final static String PKG_CCTV = "cn.cntvnews";
    public final static String PKG_LANJING = "com.app.lanjing";
    public static final String PKG_BAIDU = "com.baidu.searchbox";
    public static final String PKG_SOGOU = "com.sogou.activity.src";
    public static final String PKG_360BROWSER = "com.qihoo.browser";

    private static final String KEY_PREF_RECORD_LOG = "record_log_pref";
    private static final String KEY_PREF_WAKEUP_APPS = "wakeup_apps_pref";
    private static final String KEY_PREF_WAKEUP_INTERVAL = "wakeup_interval_pref";
    private static final String KEY_PREF_APPS_TO_MONITOR = "monitorapps";

    private static String[] mMonitorApps;
    private static final String[] sDefaultMonitorApps = new String[] {
            PKG_UCBROWER, PKG_UCTOUTIAO, PKG_JINRITOUTIAO, PKG_TENCENTNEWS, PKG_FENGHUANGNEWS,
            PKG_FENGHUANGNEWS, PKG_NETEASENEWS, PKG_SOUHUNEWS, PKG_SINANEWS, PKG_YIDIANZIXUN,
            PKG_TIANTIANKUAIBAO, PKG_QQBROWER, PKG_ZAKER, PKG_PENGPAI, PKG_FLIPBOARD, PKG_CCTV,
            PKG_LANJING, PKG_BAIDU, PKG_SOGOU, PKG_360BROWSER,
    };

    public static void init(Context context) {
        mAppContext = context;
        setDefaultNewsViewId(PKG_CCTV, 0x7f0d0306, 0x7f0d0301);
        setDefaultNewsViewId(PKG_QQBROWER, 0x7f0d0193, 0);
    }

    public static boolean isRecordLog() {
        SharedPreferences sharedPref = getSharedPref();
        return sharedPref.getBoolean(KEY_PREF_RECORD_LOG, true);
    }

    public static boolean isAutoWakeUpApps() {
        SharedPreferences sharedPref = getSharedPref();
        return sharedPref.getBoolean(KEY_PREF_WAKEUP_APPS, true);
    }

    public static int getAppsWakeupInterval() {
        SharedPreferences sharedPref = getSharedPref();
        String interval = sharedPref.getString(KEY_PREF_WAKEUP_INTERVAL,
                String.valueOf(DEFAULT_APP_WAKEUP_INTERVAL_MINS));
        return Integer.valueOf(interval) * 60 * 1000;
    }

    public static String[] getMonitorApps() {
        if (mMonitorApps != null) {
            return mMonitorApps;
        }

        SharedPreferences sharedPref = getSharedPref();
        Set<String> set = sharedPref.getStringSet(KEY_PREF_APPS_TO_MONITOR, null);
        if (set == null) {
            mMonitorApps = sDefaultMonitorApps;
            HashSet<String> apps = new HashSet<>();
            for (String app : sDefaultMonitorApps) {
                apps.add(app);
            }
            putStringSet(sharedPref, KEY_PREF_APPS_TO_MONITOR, apps);
        } else {
            mMonitorApps = set.toArray(new String[set.size()]);
        }
        return mMonitorApps;
    }

    public static void addMonitorApp(String pkg) {
        SharedPreferences sharedPreferences = getSharedPref();
        Set<String> set = sharedPreferences.getStringSet(KEY_PREF_APPS_TO_MONITOR, null);
        set.add(pkg);
        putStringSet(sharedPreferences, KEY_PREF_APPS_TO_MONITOR, set);
        mMonitorApps = null;
    }

    public static String getSharedPrefKeyForTitleId(String pkg) {
        return pkg + ".title.viewid";
    }

    public static String getSharedPrefKeyForContentId(String pkg) {
        return pkg + ".content.viewid";
    }

    public static int getTitleViewId(String pkg) {
        return SharedPref.getInt(getSharedPref(), getSharedPrefKeyForTitleId(pkg), 0);
    }

    public static int getContentViewId(String pkg) {
        return SharedPref.getInt(getSharedPref(), getSharedPrefKeyForContentId(pkg), 0);
    }

    public static void setTitleViewId(String pkg, int id) {
        SharedPref.putInt(getSharedPref(), getSharedPrefKeyForTitleId(pkg), id);
    }

    public static void setContentViewId(String pkg, int id) {
        SharedPref.putInt(getSharedPref(), getSharedPrefKeyForContentId(pkg), id);
    }

    public static void setNewsViewId(String pkg, int titleId, int contentId) {
        setTitleViewId(pkg, titleId);
        setContentViewId(pkg, contentId);
    }

    public static void setDefaultNewsViewId(String pkg, int titleId, int contentId) {
        if (getTitleViewId(pkg) == 0) {
            setTitleViewId(pkg, titleId);
        }

        if (getContentViewId(pkg) == 0) {
            setContentViewId(pkg, contentId);
        }
    }

    private static SharedPreferences getSharedPref() {
        return PreferenceManager.getDefaultSharedPreferences(mAppContext);
    }

    private static void putStringSet(SharedPreferences sharedPreferences, String key, Set<String> values) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(key, values);
        editor.apply();
    }
}
