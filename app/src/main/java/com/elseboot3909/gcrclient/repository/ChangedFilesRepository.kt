package com.elseboot3909.gcrclient.repository

import com.elseboot3909.gcrclient.entity.external.ChangeInfo
import com.elseboot3909.gcrclient.entity.external.FileInfo
import com.elseboot3909.gcrclient.remote.api.ChangesAPI
import com.elseboot3909.gcrclient.utils.JsonUtils
import io.ktor.client.call.body
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString

class ChangedFilesRepository(
    private val pbRepo: ProgressBarRepository,
    private val ciRepo: ChangeInfoRepository
) {
    val changedFiles = MutableStateFlow<HashMap<String, FileInfo>>(HashMap())

    val base = MutableStateFlow(0)
    val revision = MutableStateFlow("")
    val id = MutableStateFlow(ChangeInfo())

    init {
        CoroutineScope(Dispatchers.IO).launch {
            ciRepo.changeInfo.collect {
                base.value = 0
                revision.value = it.let { it.revisions.keys.sortedWith(compareBy { i -> it.revisions[i]?._number }) }.last()
                id.value = it
                loadChangedFiles()
            }
        }
    }

    fun loadChangedFiles() {
        CoroutineScope(Dispatchers.IO).launch {
            pbRepo.acquire()
            val response = ChangesAPI.listFiles(id.value, revision.value, base.value)
            if (response.status.value in 200..299) {
                changedFiles.value = (JsonUtils.json.decodeFromString(
                    JsonUtils.trimJson(response.body())
                ))
            }
            pbRepo.release()
        }
    }
}