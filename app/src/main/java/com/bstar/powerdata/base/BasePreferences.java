package com.bstar.powerdata.base;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public abstract class BasePreferences {

    public static final String APPLICATION_PREFERENCES = BasePreferences.class.getSimpleName();
    protected final SharedPreferences mPreferences;

    protected BasePreferences(Context context) {
        mPreferences = context.getSharedPreferences(APPLICATION_PREFERENCES, Activity.MODE_PRIVATE);
    }

    public abstract void clear();
}
