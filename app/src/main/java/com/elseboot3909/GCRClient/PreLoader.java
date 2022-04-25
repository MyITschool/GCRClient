package com.elseboot3909.GCRClient;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

public class PreLoader extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}