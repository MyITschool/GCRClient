package com.elseboot3909.gcrclient.viewmodel.comments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elseboot3909.gcrclient.entity.external.ChangeInfo
import com.elseboot3909.gcrclient.entity.external.CommentInfo
import com.elseboot3909.gcrclient.remote.api.ChangesAPI
import com.elseboot3909.gcrclient.repository.progress.ProgressBarRepository
import com.elseboot3909.gcrclient.utils.JsonUtils
import com.elseboot3909.gcrclient.viewmodel.change.ChangeInfoRepository
import io.ktor.client.call.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString

class CommentsViewModel(
    private val changeInfoRepo: ChangeInfoRepository,
    private val progressBarRepo: ProgressBarRepository
) : ViewModel() {

    val currentFile = MutableStateFlow("")
    val comments = MutableLiveData<HashMap<String, ArrayList<CommentInfo>>>()

    init {
        viewModelScope.launch {
            changeInfoRepo.changeInfo.collect {
                loadComments(it)
            }
        }
    }

    private fun loadComments(changeInfo: ChangeInfo) {
        viewModelScope.launch {
            progressBarRepo.acquire()
            val response = ChangesAPI.listChangeComments(changeInfo)
            if (response.status.value in 200..299) {
                comments.postValue(
                    JsonUtils.json.decodeFromString<HashMap<String, ArrayList<CommentInfo>>>(
                        JsonUtils.trimJson(response.body())
                    )
                )
            }
            progressBarRepo.release()
        }
    }

}