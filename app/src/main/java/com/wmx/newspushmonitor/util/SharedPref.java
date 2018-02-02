package com.wmx.newspushmonitor.util;

import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by wangmingxing on 18-1-31.
 */

public class SharedPref {

    public static int getInt(SharedPreferences sharedPreferences, String key, int def) {
        return sharedPreferences.getInt(key, def);
    }

    public static void putInt(SharedPreferences sharedPreferences, String key, int value) {;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void putStringSet(SharedPreferences sharedPreferences, String key, Set<String> values) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(key, values);
        editor.apply();
    }

    public static Set<String> getStringSet(SharedPreferences sharedPreferences, String key, Set<String> defValues) {
        return sharedPreferences.getStringSet(key, defValues);
    }
}
