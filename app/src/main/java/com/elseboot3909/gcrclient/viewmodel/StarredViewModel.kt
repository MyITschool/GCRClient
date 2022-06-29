package com.elseboot3909.gcrclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elseboot3909.gcrclient.entity.external.ChangeInfo
import com.elseboot3909.gcrclient.repository.StarredRepository
import kotlinx.coroutines.launch

class StarredViewModel(
    private val starredRepo: StarredRepository
) : ViewModel() {
    val starredList: MutableLiveData<ArrayList<ChangeInfo>> by lazy {
        MutableLiveData<ArrayList<ChangeInfo>>().also {
            starredRepo.loadStarredChanges()
        }
    }

    init {
        viewModelScope.launch {
            starredRepo.starredList.collect {
                starredList.postValue(it)
            }
        }
    }
}