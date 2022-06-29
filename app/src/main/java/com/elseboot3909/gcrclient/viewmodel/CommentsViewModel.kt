package com.elseboot3909.gcrclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elseboot3909.gcrclient.entity.external.CommentInfo
import com.elseboot3909.gcrclient.repository.CommentsRepository
import kotlinx.coroutines.launch

class CommentsViewModel(
    private val cRepo: CommentsRepository
) : ViewModel() {

    val comments = MutableLiveData<HashMap<String, ArrayList<CommentInfo>>>()
    val currentFile = MutableLiveData("")

    init {
        viewModelScope.launch {
            cRepo.commentsMap.collect {
                comments.postValue(it)
            }
        }
        viewModelScope.launch {
            cRepo.currentFile.collect {
                currentFile.postValue(it)
            }
        }
    }

}