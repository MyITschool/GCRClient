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

/**
 * This repository manages list of changes, that user marked as starred.
 * To observe changes in [starredList] use StarredViewModel class.
 */
class StarredRepository(
    private val pbRepo: ProgressBarRepository
) {
    val starredList = MutableStateFlow<ArrayList<ChangeInfo>>(ArrayList())

    /**
     * This function fetches all starred changes with server.
     * Should be called only once on app startup or after switching server.
     * In other cases [addStarredChange] or [removeStarredChange] should be used.
     */
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


    /**
     * Restore repository to default values.
     * Should be used when server data configuration has been changed.
     */
    fun hardReset() {
        starredList.value = ArrayList()
        loadStarredChanges()
    }

    /**
     * Add any change to starred list.
     */
    fun addStarredChange(changeInfo: ChangeInfo) {
        CoroutineScope(Dispatchers.IO).launch {
            starredList.value.add(changeInfo)
        }
    }

    /**
     * Remove any change from starred list.
     */
    fun removeStarredChange(changeInfo: ChangeInfo) {
        CoroutineScope(Dispatchers.IO).launch {
            starredList.value.remove(changeInfo)
        }
    }
}