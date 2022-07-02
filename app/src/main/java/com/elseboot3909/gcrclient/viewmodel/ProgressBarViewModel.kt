package com.elseboot3909.gcrclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elseboot3909.gcrclient.repository.ProgressBarRepository
import kotlinx.coroutines.launch

class ProgressBarViewModel(pbRepo: ProgressBarRepository) : ViewModel() {

    val isVisible = MutableLiveData(false)

    init {
        viewModelScope.launch {
            pbRepo.requestsCounter.collect {
                isVisible.postValue(it != 0)
            }
        }
    }

}