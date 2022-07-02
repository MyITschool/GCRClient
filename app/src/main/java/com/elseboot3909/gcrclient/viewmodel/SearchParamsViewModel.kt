package com.elseboot3909.gcrclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.elseboot3909.gcrclient.repository.SearchParamsRepository
import kotlinx.coroutines.launch

class SearchParamsViewModel(
    spRepo: SearchParamsRepository
) : ViewModel() {

    val usersCounter = spRepo.selectedUsersCounter.asLiveData()
    val projectsCounter = spRepo.selectedUsersCounter.asLiveData()

    val searchString = spRepo.searchString.asLiveData()
    val oldSearchString = spRepo.oldSearchString.asLiveData()

    val queryString = spRepo.queryString.asLiveData()

    val offset = spRepo.offset.asLiveData()

}