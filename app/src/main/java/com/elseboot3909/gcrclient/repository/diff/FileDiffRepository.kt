package com.elseboot3909.gcrclient.repository.diff

import android.net.Uri
import com.elseboot3909.gcrclient.entity.external.DiffInfo
import com.elseboot3909.gcrclient.remote.api.ChangesAPI
import com.elseboot3909.gcrclient.utils.JsonUtils
import com.elseboot3909.gcrclient.viewmodel.change.ChangeInfoRepository
import com.elseboot3909.gcrclient.repository.progress.ProgressBarRepository
import io.ktor.client.call.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString

class FileDiffRepository(
    private val progressBarRepo: ProgressBarRepository,
    private val diffRepo: DiffRepository,
    private val changeInfoRepo: ChangeInfoRepository
) {

    val diffInfo = MutableStateFlow(DiffInfo())

    init {
        CoroutineScope(Dispatchers.IO).launch {
            diffRepo.file.collect {
                loadDiffInfo(it)
            }
        }
    }

    private fun loadDiffInfo(file: String) {
        CoroutineScope(Dispatchers.IO).launch {
            progressBarRepo.acquire()
            val response = ChangesAPI.getDiff(
                changeInfo = changeInfoRepo.changeInfo.value,
                base = diffRepo.base.value,
                revision = diffRepo.revision.value,
                file = Uri.encode(file)
            )
            if (response.status.value in 200..299) {
                diffInfo.value = (JsonUtils.json.decodeFromString(
                        JsonUtils.trimJson(response.body())
                    )
                )
            }
            progressBarRepo.release()
        }
    }

}