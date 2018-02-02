package com.wmx.newspushmonitor;

import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.wmx.newspushmonitor.util.LogWriter;
import com.wmx.newspushmonitor.util.RefUtil;

import java.io.File;

/**
 * Created by wangmingxing on 18-2-1.
 */

public class SettingsFragment extends PreferenceFragment {
    private static final String TAG = "SettingsFragment";
    private static final String KEY_PREF_CLEAR_LOG = "clear_log_pref";
    private static final String KEY_PREF_ADD_NEW_APP = "add_new_app_pref";
    private static final String KEY_PREF_EXTRA = "packageName";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs_settings);
        initPreference();
    }

    private Preference.OnPreferenceChangeListener mOnPreferenceChangeListener =
            new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            if (preference.getKey().equals(KEY_PREF_ADD_NEW_APP)) {
                String pkg = ((EditTextPreference) preference).getEditText().getText().toString();
                if (TextUtils.isEmpty(pkg)) {
                    return false;
                }
                addNewApp(pkg);
                return true;
            }

            String pkg = preference.getExtras().getString(KEY_PREF_EXTRA);
            Preference appPref = findPreference(pkg);
            appPref.setSummary(getMonitorAppSummary(pkg, preference));
            return true;
        }
    };

    private Preference.OnPreferenceClickListener mOnPreferenceClickListener =
            new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference.getKey().equals(KEY_PREF_CLEAR_LOG)) {
                clearLog();
                return true;
            }
            return false;
        }
    };

    private void clearLog() {
        File logDir = new File(LogWriter.getLogSaveDir());
        if (logDir.exists()) {
            File[] logs = logDir.listFiles();
            for(File log : logs) {
                log.delete();
            }
        }

        Toast toast = Toast.makeText(getActivity(),
                getString(R.string.toast_log_clear), Toast.LENGTH_SHORT);
        toast.show();
    }

    private void addNewApp(String pkg) {
        GlobalConfig.addMonitorApp(pkg);
        addMonitorApp(pkg);
    }

    private void initPreference() {
        String[] monitorApps = GlobalConfig.getMonitorApps();
        for (String app : monitorApps) {
            addMonitorApp(app);
        }

        Preference clearLogPrefs = findPreference(KEY_PREF_CLEAR_LOG);
        clearLogPrefs.setOnPreferenceClickListener(mOnPreferenceClickListener);

        Preference addNewAppPrefs = findPreference(KEY_PREF_ADD_NEW_APP);
        addNewAppPrefs.setOnPreferenceChangeListener(mOnPreferenceChangeListener);
    }

    private String getMonitorAppSummary(String pkg, Preference preference) {
        String titleId = String.valueOf(GlobalConfig.getTitleViewId(pkg));
        String contentId = String.valueOf(GlobalConfig.getContentViewId(pkg));;

        if (preference != null) {
            EditTextPreference editTextPreference = (EditTextPreference) preference;
            String key = editTextPreference.getKey();
            if (key.equals(GlobalConfig.getSharedPrefKeyForTitleId(pkg))) {
                titleId = editTextPreference.getEditText().getText().toString();
            } else if (key.equals(GlobalConfig.getSharedPrefKeyForContentId(pkg))) {
                contentId = editTextPreference.getEditText().getText().toString();
            }
        }
        return "titleViewID:" + titleId + ",contentViewID:" + contentId;
    }

    private void addMonitorApp(String pkg) {
        if (TextUtils.isEmpty(pkg)) {
            return;
        }

        PreferenceManager manager = getPreferenceManager();
        try {
            PreferenceScreen screen = (PreferenceScreen) RefUtil.callDeclaredMethod(manager,
                    "inflateFromResource",
                    new Class[]{Context.class, int.class, PreferenceScreen.class},
                    getActivity(), R.xml.prefs_monitor_apps, null);
            screen.setKey(pkg);
            screen.setTitle(pkg);
            screen.setSummary(getMonitorAppSummary(pkg, null));

            PreferenceGroup appConfigPref = (PreferenceGroup) screen.findPreference("monitor_app_config_pref");
            appConfigPref.setTitle(pkg);

            Preference titleViewIdPref = appConfigPref.findPreference("title_view_id_pref");
            titleViewIdPref.setKey(GlobalConfig.getSharedPrefKeyForTitleId(pkg));
            titleViewIdPref.getExtras().putString(KEY_PREF_EXTRA, pkg);
            titleViewIdPref.setOnPreferenceChangeListener(mOnPreferenceChangeListener);

            Preference contentViewIdPref = appConfigPref.findPreference("content_view_id_pref");
            contentViewIdPref.setKey(GlobalConfig.getSharedPrefKeyForContentId(pkg));
            contentViewIdPref.getExtras().putString(KEY_PREF_EXTRA, pkg);
            contentViewIdPref.setOnPreferenceChangeListener(mOnPreferenceChangeListener);

            PreferenceCategory container = (PreferenceCategory)
                    getPreferenceManager().findPreference("monitor_apps_pref");
            container.addPreference(screen);
        } catch (Exception e) {
            LogWriter.e(TAG, "Add monitor ppp to preference error!", e);
        }
    }
}
