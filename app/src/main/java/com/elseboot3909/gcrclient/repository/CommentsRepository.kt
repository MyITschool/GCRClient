package com.elseboot3909.gcrclient.repository

import com.elseboot3909.gcrclient.entity.external.CommentInfo
import com.elseboot3909.gcrclient.remote.api.ChangesAPI
import com.elseboot3909.gcrclient.utils.JsonUtils
import io.ktor.client.call.body
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString

class CommentsRepository(
    private val ciRepo: ChangeInfoRepository,
    private val pbRepo: ProgressBarRepository
) {
    val commentsMap = MutableStateFlow<HashMap<String, ArrayList<CommentInfo>>>(HashMap())
    val currentFile = MutableStateFlow("")

    init {
        CoroutineScope(Dispatchers.IO).launch {
            ciRepo.changeInfo.collect {
                loadComments()
            }
        }
    }

    private fun loadComments() {
        CoroutineScope(Dispatchers.IO).launch {
            pbRepo.acquire()
            val response = ChangesAPI.listChangeComments(ciRepo.changeInfo.value)
            if (response.status.value in 200..299) {
                commentsMap.value = JsonUtils.json.decodeFromString(
                    JsonUtils.trimJson(response.body())
                )
            }
            pbRepo.release()
        }
    }

}