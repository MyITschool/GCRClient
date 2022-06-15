package com.elseboot3909.gcrclient.repository.diff

import android.util.Log
import com.elseboot3909.gcrclient.entity.external.ChangeInfo
import com.elseboot3909.gcrclient.entity.external.FileInfo
import com.elseboot3909.gcrclient.remote.api.ChangesAPI
import com.elseboot3909.gcrclient.utils.Constants
import com.elseboot3909.gcrclient.utils.JsonUtils
import com.elseboot3909.gcrclient.viewmodel.change.ChangeInfoRepository
import com.elseboot3909.gcrclient.repository.progress.ProgressBarRepository
import io.ktor.client.call.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString

class FilesRepository(
    private val progressBarRepo: ProgressBarRepository,
    diffRepo: DiffRepository,
    changeInfoRepo: ChangeInfoRepository
) {
    val filesList = MutableStateFlow<HashMap<String, FileInfo>>(HashMap())

    init {
        CoroutineScope(Dispatchers.IO).launch {
            combine(diffRepo.base, diffRepo.revision, changeInfoRepo.changeInfo) { base, revision, changeInfo ->
                loadFilesList(changeInfo = changeInfo, revision = revision, base = base)
            }.collect {}
        }
    }

    private fun loadFilesList(revision: String, base: Int, changeInfo: ChangeInfo) {
        CoroutineScope(Dispatchers.IO).launch {
            progressBarRepo.acquire()
            val response = ChangesAPI.listFiles(changeInfo, revision, base)
            Log.e(Constants.LOG_TAG, response.body())
            if (response.status.value in 200..299) {
                filesList.value = (
                    JsonUtils.json.decodeFromString(
                        JsonUtils.trimJson(response.body())
                    )
                )
            }
            progressBarRepo.release()
        }
    }

}