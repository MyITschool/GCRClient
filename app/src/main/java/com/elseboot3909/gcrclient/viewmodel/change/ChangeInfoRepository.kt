package com.elseboot3909.gcrclient.viewmodel.change

import com.elseboot3909.gcrclient.entity.external.ChangeInfo
import com.elseboot3909.gcrclient.remote.api.ChangesAPI
import com.elseboot3909.gcrclient.utils.JsonUtils
import com.elseboot3909.gcrclient.repository.diff.DiffRepository
import com.elseboot3909.gcrclient.repository.progress.ProgressBarRepository
import io.ktor.client.call.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString

class ChangeInfoRepository(
    private val progressBarRepo: ProgressBarRepository,
    private val diffRepository: DiffRepository
) {
    val changeInfo = MutableStateFlow(ChangeInfo())

    fun syncChangeWithRemote() {
        CoroutineScope(Dispatchers.IO).launch {
            progressBarRepo.acquire()
            val response = ChangesAPI.getChange(changeInfo.value)
            if (response.status.value in 200..299) {
                changeInfo.update { JsonUtils.json.decodeFromString(
                    JsonUtils.trimJson(response.body())
                ) }
                diffRepository.revision.value = changeInfo.value.let { it.revisions.keys.sortedWith(compareBy { i -> it.revisions[i]?._number }) }.last()
                diffRepository.base.value = 0
            }
            progressBarRepo.release()
        }
    }
}