package com.elseboot3909.gcrclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elseboot3909.gcrclient.entity.external.ProjectInfo
import com.elseboot3909.gcrclient.repository.ProjectsListRepository
import kotlinx.coroutines.launch

class ProjectsListViewModel(
    private val plRepo: ProjectsListRepository
) : ViewModel() {
    val projectsList: MutableLiveData<HashMap<String, ProjectInfo>> by lazy {
        MutableLiveData<HashMap<String, ProjectInfo>>().also {
            plRepo.loadProjectList()
        }
    }

    init {
        viewModelScope.launch {
            plRepo.projectsList.collect {
                projectsList.postValue(it)
            }
        }
    }

}