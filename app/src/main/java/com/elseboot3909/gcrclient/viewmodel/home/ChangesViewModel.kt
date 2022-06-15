package com.elseboot3909.gcrclient.viewmodel.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elseboot3909.gcrclient.entity.external.ChangeInfo
import com.elseboot3909.gcrclient.entity.internal.QueryParams
import com.elseboot3909.gcrclient.remote.api.ChangesAPI
import com.elseboot3909.gcrclient.utils.Constants
import com.elseboot3909.gcrclient.utils.JsonUtils.json
import com.elseboot3909.gcrclient.utils.JsonUtils.trimJson
import com.elseboot3909.gcrclient.repository.progress.ProgressBarRepository
import com.elseboot3909.gcrclient.repository.search.SearchParamsRepository
import io.ktor.client.call.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString

class ChangesViewModel(
    private val progressBarRepo: ProgressBarRepository,
    private val searchParamsRepo: SearchParamsRepository
) : ViewModel() {

    val changesList: MutableLiveData<ArrayList<ChangeInfo>> by lazy {
        MutableLiveData<ArrayList<ChangeInfo>>()
    }

    init {
        viewModelScope.launch {
            combine(searchParamsRepo.queryString, searchParamsRepo.offset) { params, offset ->
                loadChangesList(params, offset)
            }.collect {}
        }
    }

//    fun erase() {
//        changesList.value?.clear()
//        searchParamsRepo.erase()
//        loadChangesList()
//    }

    fun loadChangesList(
        params: String = searchParamsRepo.queryString.value,
        offset: Int = searchParamsRepo.offset.value
    ) {
        viewModelScope.launch {
            progressBarRepo.acquire()
            val response = ChangesAPI.queryChanges(
                QueryParams(
                    q = params,
                    n = Constants.MAX_FETCHED_CHANGES,
                    S = offset
                )
            )
            if (response.status.value in 200..299) {
                changesList.postValue(
                    json.decodeFromString<ArrayList<ChangeInfo>>(
                        trimJson(response.body())
                    )
                )
            }
            progressBarRepo.release()
        }
    }

}