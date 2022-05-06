@file:Suppress("unused")

package com.elseboot3909.GCRClient

import android.app.Application

import com.google.android.material.color.DynamicColors

class PreLoader: Application() {

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}