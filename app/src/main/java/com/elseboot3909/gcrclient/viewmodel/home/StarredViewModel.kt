package com.elseboot3909.gcrclient.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elseboot3909.gcrclient.entity.external.ChangeInfo
import com.elseboot3909.gcrclient.entity.internal.QueryParams
import com.elseboot3909.gcrclient.remote.api.ChangesAPI
import com.elseboot3909.gcrclient.utils.JsonUtils
import com.elseboot3909.gcrclient.repository.progress.ProgressBarRepository
import io.ktor.client.call.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent

class StarredViewModel(private val progressBarRepo: ProgressBarRepository) : ViewModel(), KoinComponent {

    private val starredList: MutableLiveData<ArrayList<ChangeInfo>> by lazy {
        MutableLiveData<ArrayList<ChangeInfo>>().also {
            loadStarredChanges()
        }
    }

    fun getStarredList(): LiveData<ArrayList<ChangeInfo>> {
        return starredList
    }

    private val json = Json { ignoreUnknownKeys = true }

    fun loadStarredChanges() {
        viewModelScope.launch {
            progressBarRepo.acquire()
            val response = ChangesAPI.queryChanges(
                QueryParams(q = "is:starred")
            )
            if (response.status.value in 200..299) {
                starredList.postValue(
                    json.decodeFromString<ArrayList<ChangeInfo>>(
                        JsonUtils.trimJson(response.body())
                    )
                )
            }
            progressBarRepo.release()
        }
    }
}