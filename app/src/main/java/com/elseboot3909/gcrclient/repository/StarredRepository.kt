package com.elseboot3909.gcrclient.repository

import com.elseboot3909.gcrclient.entity.external.ChangeInfo
import com.elseboot3909.gcrclient.entity.internal.QueryParams
import com.elseboot3909.gcrclient.remote.api.ChangesAPI
import com.elseboot3909.gcrclient.utils.JsonUtils
import com.elseboot3909.gcrclient.utils.JsonUtils.json
import io.ktor.client.call.body
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString

class StarredRepository(
    private val pbRepo: ProgressBarRepository
) {
    val starredList = MutableStateFlow<ArrayList<ChangeInfo>>(ArrayList())

    fun loadStarredChanges() {
        CoroutineScope(Dispatchers.IO).launch {
            pbRepo.acquire()
            val response = ChangesAPI.queryChanges(
                QueryParams(q = "is:starred")
            )
            if (response.status.value in 200..299) {
                starredList.value = (
                    json.decodeFromString(
                        JsonUtils.trimJson(response.body())
                    )
                )
            }
            pbRepo.release()
        }
    }

    fun addStarredChange(changeInfo: ChangeInfo) {
        CoroutineScope(Dispatchers.IO).launch {
            starredList.value.add(changeInfo)
        }
    }

    fun removeStarredChange(changeInfo: ChangeInfo) {
        CoroutineScope(Dispatchers.IO).launch {
            starredList.value.remove(changeInfo)
        }
    }
}