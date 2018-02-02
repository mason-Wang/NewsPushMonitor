package com.wmx.newspushmonitor.parser;

/**
 * Created by wangmingxing on 18-1-29.
 */

public class NotificationParserFactory {

    public static BaseNotificationParser getNotificationParser(String pkg) {
        return new DefaultNotificationParser(pkg);
    }
}
