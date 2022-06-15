package com.elseboot3909.gcrclient.viewmodel.diff

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elseboot3909.gcrclient.entity.external.ChangeInfo
import com.elseboot3909.gcrclient.entity.external.FileInfo
import com.elseboot3909.gcrclient.remote.api.ChangesAPI
import com.elseboot3909.gcrclient.repository.diff.DiffRepository
import com.elseboot3909.gcrclient.utils.Constants
import com.elseboot3909.gcrclient.utils.JsonUtils
import com.elseboot3909.gcrclient.viewmodel.change.ChangeInfoRepository
import com.elseboot3909.gcrclient.repository.progress.ProgressBarRepository
import io.ktor.client.call.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString

class FilesViewModel(
    private val progressBarRepo: ProgressBarRepository,
    diffRepo: DiffRepository,
    changeInfoRepo: ChangeInfoRepository
) : ViewModel() {

    val filesList: MutableLiveData<HashMap<String, FileInfo>> by lazy {
        MutableLiveData<HashMap<String, FileInfo>>()
    }

    private var _base = 0
    private var _revision = ""
    private var _id = ""

//    init {
//        viewModelScope.launch {
//            combine(diffRepo.base, diffRepo.revision, changeInfoRepo.changeInfo) { base, revision, changeInfo ->
//                loadFilesList(changeInfo = changeInfo, revision = revision, base = base)
//            }.collect {}
//        }
//    }

    fun loadFilesList(revision: String, base: Int, changeInfo: ChangeInfo) {
        Log.e(Constants.LOG_TAG, "$_base $base $_revision $revision $_id ${changeInfo.id}")
        if (_base == base && revision == _revision && changeInfo.id == _id) return
        _base = base; _revision = revision; _id = changeInfo.id
        Log.e(Constants.LOG_TAG, "HERE!")
        viewModelScope.launch {
            Log.e(Constants.LOG_TAG, "HERE1!")
            progressBarRepo.acquire()
            Log.e(Constants.LOG_TAG, "HERE2!")
            val response = ChangesAPI.listFiles(changeInfo, revision, base)
            Log.e(Constants.LOG_TAG, response.body())
            if (response.status.value in 200..299) {
                filesList.postValue(
                    JsonUtils.json.decodeFromString<HashMap<String, FileInfo>>(
                        JsonUtils.trimJson(response.body())
                    )
                )
            }
            progressBarRepo.release()
        }
    }

}