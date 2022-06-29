package com.elseboot3909.gcrclient.repository

import android.net.Uri
import com.elseboot3909.gcrclient.entity.external.DiffInfo
import com.elseboot3909.gcrclient.remote.api.ChangesAPI
import com.elseboot3909.gcrclient.utils.JsonUtils
import io.ktor.client.call.body
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString

class FileDiffRepository(
    private val cfRepo: ChangedFilesRepository,
    private val pbRepo: ProgressBarRepository
) {
    val diffInfo = MutableStateFlow(DiffInfo())
    val fileName = MutableStateFlow("")

    fun loadDiffInfo() {
        CoroutineScope(Dispatchers.IO).launch {
            pbRepo.acquire()
            val response = ChangesAPI.getDiff(
                changeInfo = cfRepo.id.value,
                base = cfRepo.base.value,
                revision = cfRepo.revision.value,
                file = Uri.encode(fileName.value)
            )
            if (response.status.value in 200..299) {
                diffInfo.value = JsonUtils.json.decodeFromString(
                    JsonUtils.trimJson(response.body())
                )
            }
            pbRepo.release()
        }
    }
}