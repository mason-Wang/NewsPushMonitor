package com.wmx.newspushmonitor;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;

import com.wmx.newspushmonitor.ui.NewsInfoAdapter;
import com.wmx.newspushmonitor.ui.NewsInfoItem;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements AccessibilityManager.AccessibilityStateChangeListener {
    private static final String TAG = "MainActivity";

    private AccessibilityManager accessibilityManager;
    private Button mServiceControlBtn;
    private RecyclerView mNewsInfoRecycleView;
    private NewsInfoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initAccessibilityService();
        checkPermission();
        handleIntent(getIntent());
    }

    @Override
    public void onAccessibilityStateChanged(boolean enabled) {
        updateMonitorStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMonitorStatus();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.setting) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.service_ctl_btn:
                    Intent accessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(accessibleIntent);
                    break;

                default:
                    break;
            }
        }
    };

    private void initView() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        mNewsInfoRecycleView = (RecyclerView) findViewById(R.id.newsinfo_recycler_view);
        mNewsInfoRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mNewsInfoRecycleView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter = new NewsInfoAdapter();
        mNewsInfoRecycleView.setAdapter(mAdapter);

        mServiceControlBtn = (Button) findViewById(R.id.service_ctl_btn);
        mServiceControlBtn.setOnClickListener(mOnClickListener);
    }

    private void initAccessibilityService() {
        accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        accessibilityManager.addAccessibilityStateChangeListener(this);
    }

    private void handleIntent(Intent intent) {
        String newsInfo = intent.getStringExtra("newsInfo");
        String packageName = intent.getStringExtra("packageName");
        if (!TextUtils.isEmpty(packageName)){
            mAdapter.addNewsInfoItem(new NewsInfoItem(packageName, newsInfo));
        }
    }

    private void updateMonitorStatus() {
        String statusStr;
        if (isServiceEnabled()) {
            statusStr = getString(R.string.close_service);
        } else {
            statusStr = getString(R.string.start_service);
        }
        mServiceControlBtn.setText(statusStr);
    }

    private boolean isServiceEnabled() {
        List<AccessibilityServiceInfo> accessibilityServices =
                accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(getPackageName() + "/.PushMonitorAccessibilityService")) {
                return true;
            }
        }
        return false;
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            "android.permission.GET_INTENT_SENDER_INTENT"}, 0);
        }
    }
}
