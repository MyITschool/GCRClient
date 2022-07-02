package com.elseboot3909.gcrclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elseboot3909.gcrclient.entity.external.ChangeInfo
import com.elseboot3909.gcrclient.repository.CredentialsRepository
import com.elseboot3909.gcrclient.repository.StarredRepository
import kotlinx.coroutines.launch

/**
 * This ViewModel contains list of starred changes.
 * List can be observed by [starredList].
 * To manage this list use [StarredRepository] class.
 */
class StarredViewModel(
    private val sRepo: StarredRepository
) : ViewModel() {
    val starredList: MutableLiveData<ArrayList<ChangeInfo>> by lazy {
        MutableLiveData<ArrayList<ChangeInfo>>().also {
            sRepo.loadStarredChanges()
        }
    }

    init {
        viewModelScope.launch {
            sRepo.starredList.collect {
                starredList.postValue(it)
            }
        }
    }
}