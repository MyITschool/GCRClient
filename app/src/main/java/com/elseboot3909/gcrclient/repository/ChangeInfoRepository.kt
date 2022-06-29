package com.elseboot3909.gcrclient.repository

import com.elseboot3909.gcrclient.entity.external.ChangeInfo
import com.elseboot3909.gcrclient.remote.api.ChangesAPI
import com.elseboot3909.gcrclient.utils.JsonUtils
import io.ktor.client.call.body
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString

class ChangeInfoRepository(
    private val pbRepo: ProgressBarRepository
) {
    val changeInfo = MutableStateFlow(ChangeInfo())

    fun syncChangeWithRemote() {
        CoroutineScope(Dispatchers.IO).launch {
            pbRepo.acquire()
            val response = ChangesAPI.getChange(changeInfo.value)
            if (response.status.value in 200..299) {
                changeInfo.update { JsonUtils.json.decodeFromString(
                    JsonUtils.trimJson(response.body())
                ) }
            }
            pbRepo.release()
        }
    }
}