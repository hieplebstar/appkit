package com.bstar.powerdata.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public abstract class CrashlyticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
    }
}
