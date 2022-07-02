package com.elseboot3909.gcrclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elseboot3909.gcrclient.entity.external.ChangeInfo
import com.elseboot3909.gcrclient.repository.ChangesListRepository
import com.elseboot3909.gcrclient.repository.SearchParamsRepository
import com.elseboot3909.gcrclient.utils.Constants
import kotlinx.coroutines.launch

class ChangesListViewModel(
    private val clRepo: ChangesListRepository,
    private val spRepo: SearchParamsRepository
) : ViewModel() {

    val changesList: MutableLiveData<ArrayList<ChangeInfo>> by lazy {
        MutableLiveData<ArrayList<ChangeInfo>>().also {
            loadChangesList()
        }
    }

    val showNextBtn = MutableLiveData(false)
    val showPrevBtn = MutableLiveData(false)

    init {
        viewModelScope.launch {
            clRepo.changesList.collect {
                changesList.postValue(it)
                showNextBtn.postValue(it.size >= Constants.MAX_FETCHED_CHANGES)
                showPrevBtn.postValue(spRepo.offset.value != 0)
            }
        }
    }

    fun loadChangesList() {
        clRepo.loadChangesList()
    }

}