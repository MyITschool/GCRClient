package com.elseboot3909.gcrclient.repository

import com.elseboot3909.gcrclient.entity.external.ChangeInfo
import com.elseboot3909.gcrclient.entity.internal.QueryParams
import com.elseboot3909.gcrclient.remote.api.ChangesAPI
import com.elseboot3909.gcrclient.utils.Constants
import com.elseboot3909.gcrclient.utils.JsonUtils
import io.ktor.client.call.body
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString

class ChangesListRepository(
    private val pbRepo: ProgressBarRepository,
    private val spRepo: SearchParamsRepository
) {

    val changesList = MutableStateFlow(ArrayList<ChangeInfo>())

    init {
        CoroutineScope(Dispatchers.IO).launch {
            spRepo.offset.combine(spRepo.queryString) { _, _ -> ""
            }.collect { loadChangesList() }
        }
    }

    fun loadChangesList() {
        CoroutineScope(Dispatchers.IO).launch {
            pbRepo.acquire()
            val response = ChangesAPI.queryChanges(
                QueryParams(
                    q = spRepo.queryString.value,
                    n = Constants.MAX_FETCHED_CHANGES,
                    S = spRepo.offset.value
                )
            )
            if (response.status.value in 200..299) {
                changesList.value = JsonUtils.json.decodeFromString(
                        JsonUtils.trimJson(response.body())
                )
            }
            pbRepo.release()
        }
    }

    fun hardReset() {
        changesList.value = ArrayList()
        loadChangesList()
    }
}