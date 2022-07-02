package com.elseboot3909.gcrclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.elseboot3909.gcrclient.repository.CredentialsRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CredentialsViewModel(
    private val cRepo: CredentialsRepository
) : ViewModel() {
    val selected = cRepo.selected.asLiveData()
    val serversList = cRepo.serversList.asLiveData()
    val currentServerData = cRepo.currentServerData.asLiveData()

    val isInitialized = MutableLiveData(false)

    init {
        viewModelScope.launch {
            cRepo.serversList.combine(cRepo.selected) {
                    lst, i -> if (lst.size == 1) lst[0].serverURL.isNotEmpty() else i != 0
            }.collect {
                if (isInitialized.value != true) isInitialized.value = it
            }
        }
    }

}