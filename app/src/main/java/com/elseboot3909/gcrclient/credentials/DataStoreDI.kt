package com.elseboot3909.gcrclient.credentials

import com.elseboot3909.gcrclient.credentials.dataStore.CredentialsDataStore
import com.elseboot3909.gcrclient.credentials.dataStore.SelectedDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * This Koin module generates singleton of classes with DataStore
 */
var dataStores = module {
    single { SelectedDataStore(androidContext()) }
    single { CredentialsDataStore(androidContext()) }
}
