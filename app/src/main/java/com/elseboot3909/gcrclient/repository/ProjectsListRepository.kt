package com.elseboot3909.gcrclient.repository

import com.elseboot3909.gcrclient.entity.external.ProjectInfo
import com.elseboot3909.gcrclient.remote.api.ProjectsAPI
import com.elseboot3909.gcrclient.utils.JsonUtils
import io.ktor.client.call.body
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString

class ProjectsListRepository(
    private val pbRepo: ProgressBarRepository,
) {

    val projectsList = MutableStateFlow(HashMap<String, ProjectInfo>())

    fun loadProjectList() {
        CoroutineScope(Dispatchers.IO).launch {
            pbRepo.acquire()
            val response = ProjectsAPI.listProjects()
            if (response.status.value in 200..299) {
                projectsList.value = JsonUtils.json.decodeFromString(
                        JsonUtils.trimJson(response.body())
                    )
            }
            pbRepo.release()
        }
    }

}