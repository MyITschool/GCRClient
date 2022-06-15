package com.elseboot3909.gcrclient.viewmodel.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elseboot3909.gcrclient.entity.external.AccountInfo
import com.elseboot3909.gcrclient.remote.api.AccountAPI
import com.elseboot3909.gcrclient.utils.JsonUtils
import com.elseboot3909.gcrclient.repository.progress.ProgressBarRepository
import io.ktor.client.call.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString

class UsersListViewModel(private val progressBarRepo: ProgressBarRepository) : ViewModel() {
    val users: MutableLiveData<ArrayList<AccountInfo>> by lazy {
        MutableLiveData<ArrayList<AccountInfo>>().also {
            loadUsersList()
        }
    }

    fun refreshUsers() {
        loadUsersList()
    }

    private fun loadUsersList() {
        viewModelScope.launch {
            progressBarRepo.acquire()
            val response = AccountAPI.queryAccount()
            if (response.status.value in 200..299) {
                users.postValue(
                    JsonUtils.json.decodeFromString<ArrayList<AccountInfo>>(
                        JsonUtils.trimJson(response.body())
                    )
                )
            }
            progressBarRepo.release()
        }
    }
}