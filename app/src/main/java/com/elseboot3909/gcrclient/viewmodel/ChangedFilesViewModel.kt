package com.elseboot3909.gcrclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elseboot3909.gcrclient.entity.external.FileInfo
import com.elseboot3909.gcrclient.repository.ChangedFilesRepository
import kotlinx.coroutines.launch

class ChangedFilesViewModel(
    private val cfRepo: ChangedFilesRepository
) : ViewModel() {
    val changedFiles: MutableLiveData<HashMap<String, FileInfo>> = MutableLiveData()

    val base = MutableLiveData(0)
    val revision = MutableLiveData("")

    init {
        viewModelScope.launch {
            cfRepo.base.collect {
                base.postValue(it)
            }
        }
        viewModelScope.launch {
            cfRepo.revision.collect {
                revision.postValue(it)
            }
        }
        viewModelScope.launch {
            cfRepo.changedFiles.collect {
                changedFiles.postValue(it)
            }
        }
    }
}