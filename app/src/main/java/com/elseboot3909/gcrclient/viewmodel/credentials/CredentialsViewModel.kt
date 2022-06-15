package com.elseboot3909.gcrclient.viewmodel.credentials

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.elseboot3909.gcrclient.credentials.dataStore.CredentialsDataStore
import com.elseboot3909.gcrclient.credentials.dataStore.SelectedDataStore
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class CredentialsViewModel : ViewModel(), KoinComponent {
    val selected = get<SelectedDataStore>().selected.asLiveData()
    val serversList = get<CredentialsDataStore>().serversList.asLiveData()

    val isInitialized = MutableLiveData(false)
}