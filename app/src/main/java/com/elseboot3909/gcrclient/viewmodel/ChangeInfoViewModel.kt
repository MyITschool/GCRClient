package com.elseboot3909.gcrclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elseboot3909.gcrclient.entity.external.ChangeInfo
import com.elseboot3909.gcrclient.repository.ChangeInfoRepository
import kotlinx.coroutines.launch

class ChangeInfoViewModel(
    private val ciRepo: ChangeInfoRepository
) : ViewModel() {

    val changeInfo = MutableLiveData(ChangeInfo())

    init {
        viewModelScope.launch {
            ciRepo.changeInfo.collect {
                changeInfo.postValue(it)
            }
        }
    }

}