package com.bstar.powerdata;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ArrayRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.WindowManager;

import com.example.shadow.appkit.AndroidApplication;
import com.example.shadow.appkit.eventbus.EventBusController;

import java.util.Locale;

public class PowerDataApplication extends AndroidApplication {

    private static PowerDataApplication sInstance;

    private static boolean isMainActivityExist;
    private static boolean isVoiceActivityVisible;

    @Override
    protected EventBusController onSetEventBusController() {
        return new MainEventBusController();
    }

    public static PowerDataApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        sInstance = this;
        super.onCreate();
    }

    public static String getStringResource(@StringRes int resId) {
        return sInstance.getResources().getString(resId);
    }

    public static Drawable getDrawableResource(@DrawableRes int resId) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getDrawable(sInstance, resId);
        } else {
            return sInstance.getResources().getDrawable(resId);
        }
    }

    public static String getStringResource(@StringRes int resId, Object... formatArgs) {
        return sInstance.getResources().getString(resId, formatArgs);
    }

    public static String[] getStringArrayResource(@ArrayRes int resId) {
        return sInstance.getResources().getStringArray(resId);
    }

    public static Locale getUserLocale() {
        return getInstance().getResources().getConfiguration().locale;
    }

    public static int getIntColor(int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(sInstance, id);
        } else {
            return sInstance.getResources().getColor(id);
        }
    }

    public static String getBuildVersion() {
        try {
            PackageInfo pInfo = sInstance.getPackageManager().getPackageInfo(sInstance.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Point getDeviceSize() {
        WindowManager wm = (WindowManager) sInstance.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static boolean isMainActivityExist() {
        return isMainActivityExist;
    }

    public static void onMainActivityResumed() {
        isMainActivityExist = true;
    }

    public static void onMainActivityDestroy() {
        isMainActivityExist = false;
    }

    public static boolean isVoiceActivityVisible() {
        return isVoiceActivityVisible;
    }

    public static void onVoiceActivityResumed() {
        isVoiceActivityVisible = true;
    }

    public static void onVoiceActivityPaused() {
        isVoiceActivityVisible = false;
    }
}
