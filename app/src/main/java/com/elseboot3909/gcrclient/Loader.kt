package com.elseboot3909.gcrclient

import android.app.Application
import android.util.Log
import com.elseboot3909.gcrclient.credentials.dataStores
import com.elseboot3909.gcrclient.remote.client
import com.elseboot3909.gcrclient.repository.repos
import com.elseboot3909.gcrclient.utils.Constants
import com.elseboot3909.gcrclient.viewmodel.viewModels
import com.google.android.material.color.DynamicColors
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module

class Loader : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.e("${Constants.LOG_TAG} (${this.javaClass.name})", "Launching!")

        DynamicColors.applyToActivitiesIfAvailable(this)

        startKoin {
            androidContext(applicationContext)
            modules(ArrayList<Module>().also {
                it.add(dataStores)
                it.add(viewModels)
                it.add(repos)
                it.add(client)
            })
        }
    }
}