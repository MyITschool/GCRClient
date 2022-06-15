package com.elseboot3909.gcrclient.viewmodel.progress

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elseboot3909.gcrclient.repository.progress.ProgressBarRepository
import kotlinx.coroutines.launch

class ProgressBarViewModel(progressBarRepository: ProgressBarRepository) : ViewModel() {

    val isVisible = MutableLiveData(false)

    init {
        viewModelScope.launch {
            progressBarRepository.requestsCounter.collect {
                isVisible.postValue(it != 0)
            }
        }
    }

}