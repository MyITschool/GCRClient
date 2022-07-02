package com.elseboot3909.gcrclient.repository

import android.util.Log
import com.elseboot3909.gcrclient.ServerData
import com.elseboot3909.gcrclient.credentials.dataStore.CredentialsDataStore
import com.elseboot3909.gcrclient.credentials.dataStore.SelectedDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class CredentialsRepository : KoinComponent {
    val currentServerData = MutableStateFlow(ServerData.getDefaultInstance())

    val selected = MutableStateFlow(-1)
    val serversList = MutableStateFlow(ArrayList<ServerData>()).also {
        it.value.add(ServerData.getDefaultInstance())
    }

    val dummyTrigger = MutableStateFlow(false)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            get<SelectedDataStore>().selected.collect {
                selected.value = it
                getCurrentServerData()
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            get<CredentialsDataStore>().serversList.collect {
                serversList.value = ArrayList(it.serverDataList.filterNotNull())
                getCurrentServerData()
            }
        }
    }

    private fun getCurrentServerData() {
        try {
            currentServerData.value = serversList.value[selected.value]
        } catch (e: IndexOutOfBoundsException) { }
        dummyTrigger.value = !dummyTrigger.value
    }
}