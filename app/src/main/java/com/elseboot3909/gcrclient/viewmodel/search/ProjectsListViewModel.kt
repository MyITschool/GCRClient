package com.elseboot3909.gcrclient.viewmodel.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elseboot3909.gcrclient.entity.external.ProjectInfo
import com.elseboot3909.gcrclient.remote.api.ProjectsAPI
import com.elseboot3909.gcrclient.repository.progress.ProgressBarRepository
import com.elseboot3909.gcrclient.utils.JsonUtils
import io.ktor.client.call.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString

class ProjectsListViewModel(private val progressBarRepo: ProgressBarRepository) : ViewModel() {
    val projects: MutableLiveData<HashMap<String, ProjectInfo>> by lazy {
        MutableLiveData<HashMap<String, ProjectInfo>>().also {
            loadProjectList()
        }
    }

    fun refreshProjects() {
        loadProjectList()
    }

    private fun loadProjectList() {
        viewModelScope.launch {
            progressBarRepo.acquire()
            val response = ProjectsAPI.listProjects()
            if (response.status.value in 200..299) {
                projects.postValue(
                    JsonUtils.json.decodeFromString<HashMap<String, ProjectInfo>>(
                        JsonUtils.trimJson(response.body())
                    )
                )
            }
            progressBarRepo.release()
        }
    }
}