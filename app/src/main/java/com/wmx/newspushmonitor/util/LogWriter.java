package com.wmx.newspushmonitor.util;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.wmx.newspushmonitor.GlobalConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wangmingxing on 17-12-25.
 */

public class LogWriter {
    private static final String TAG = "LogWriter";
    private static Handler mLogWriterHandler;
    private static final String LOG_SAVEDIR = "/sdcard/pushmonitor/";

    public static final int PRI_DEBUG = 0;
    public static final int PRI_INFO = 1;
    public static final int PRI_WARN = 2;
    public static final int PRI_ERRPR = 3;

    private static final String[] sPriority = new String[] {
            "D/", "I/", "W/", "E/",
    };

    public static String getLogSaveDir() {
        return LOG_SAVEDIR;
    }

    private static void writeLog(final String msg) {
        if (!GlobalConfig.isRecordLog()) {
            return;
        }

        if (ContextCompat.checkSelfPermission(GlobalConfig.mAppContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (mLogWriterHandler == null) {
            HandlerThread logThread = new HandlerThread("log_thread");
            logThread.start();
            mLogWriterHandler = new Handler(logThread.getLooper());
        }

        mLogWriterHandler.post(new Runnable() {
            @Override
            public void run() {
                File dir = new File(LOG_SAVEDIR);
                if (!dir.exists()) {
                    dir.mkdir();
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String logFile = "log-" + sdf.format(new Date());
                FileWriter fw = null;
                try {
                    fw = new FileWriter(new File(dir, logFile), true);
                    fw.write(msg);
                    fw.write("\n");
                } catch (Exception e) {
                    Log.e(TAG, "Save log error!", e);
                } finally {
                    if (fw != null) {
                        try {
                            fw.close();
                        } catch (IOException e) {
                            Log.e(TAG, "Save log error", e);
                        }
                    }
                }
            }
        });
    }

    public static void writeLog(int priority, String tag, String msg) {
        SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        StringBuilder sb = new StringBuilder();
        sb.append(sdf.format(new Date()))
            .append(" ");

        if (priority > PRI_ERRPR) {
            priority = PRI_ERRPR;
        } else if (priority < PRI_DEBUG) {
            priority = PRI_DEBUG;
        }
        sb.append(sPriority[priority])
            .append(tag)
            .append(": ")
            .append(msg);
        writeLog(sb.toString());
    }

    public static void d(String tag, String msg) {
        Log.d(tag, msg);
        writeLog(PRI_DEBUG, tag, msg);
    }

    public static void i(String tag, String msg) {
        Log.i(tag, msg);
        writeLog(PRI_INFO, tag, msg);
    }

    public static void w(String tag, String msg) {
        Log.w(tag, msg);
        writeLog(PRI_WARN, tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
        writeLog(PRI_ERRPR, tag, msg);
    }

    public static void e(String tag, Throwable t) {
        e(tag, "", t);
    }

    public static void e(String tag, String msg, Throwable t) {
        Log.e(tag, msg, t);
        e(tag, msg + "\n" + Log.getStackTraceString(t));
    }
}
