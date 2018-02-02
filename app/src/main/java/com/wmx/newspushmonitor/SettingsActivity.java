package com.wmx.newspushmonitor;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;


public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.preferences_fragment, new SettingsFragment());
        fragmentTransaction.commit();
    }

    public void performBack(View v) {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GlobalConfig.mIsSettingMode = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        GlobalConfig.mIsSettingMode = false;
    }
}
