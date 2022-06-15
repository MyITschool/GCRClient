package com.elseboot3909.gcrclient

import android.app.Application
import android.util.Log
import com.elseboot3909.gcrclient.utils.Constants
import com.google.android.material.color.DynamicColors

class Loader : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.e("${Constants.LOG_TAG} (${this.javaClass.name})", "Launching!")
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}