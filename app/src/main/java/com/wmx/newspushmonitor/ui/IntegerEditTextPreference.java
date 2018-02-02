package com.wmx.newspushmonitor.ui;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

/**
 * Created by wangmingxing on 18-2-2.
 */

public class IntegerEditTextPreference extends EditTextPreference {

    public IntegerEditTextPreference(Context context) {
        super(context);
    }

    public IntegerEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IntegerEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected String getPersistedString(String defaultReturnValue) {
        return String.valueOf(getPersistedInt(0));
    }

    @Override
    protected boolean persistString(String value) {
        try {
            return persistInt(Integer.valueOf(value));
        } catch (Exception e) {
            return false;
        }
    }
}
