package com.elseboot3909.gcrclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elseboot3909.gcrclient.entity.external.DiffInfo
import com.elseboot3909.gcrclient.repository.FileDiffRepository
import kotlinx.coroutines.launch

class FileDiffViewModel(
    fdRepo: FileDiffRepository
) : ViewModel() {

    val diffInfo = MutableLiveData(DiffInfo())
    val fileName = MutableLiveData("")

    init {
        viewModelScope.launch {
            fdRepo.diffInfo.collect {
                diffInfo.postValue(it)
            }
        }
        viewModelScope.launch {
            fdRepo.fileName.collect {
                fileName.postValue(it)
                fdRepo.loadDiffInfo()
            }
        }
    }
}