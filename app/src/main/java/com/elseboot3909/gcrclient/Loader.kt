package com.elseboot3909.gcrclient

import android.app.Application
import android.util.Log
import com.elseboot3909.gcrclient.utils.Constants
import com.elseboot3909.gcrclient.utils.ServerDataManager

import com.google.android.material.color.DynamicColors

class Loader : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.e("${Constants.LOG_TAG} (${this.javaClass.name})", "Launching!")
        DynamicColors.applyToActivitiesIfAvailable(this)
        ServerDataManager.loadServerDataList(applicationContext)
        ServerDataManager.loadSavedPosition(applicationContext)
        if (ServerDataManager.selectedPos == -1 && ServerDataManager.serverDataList.isEmpty()) {
            ServerDataManager.writeNewPosition(applicationContext, 0)
        }
    }
}