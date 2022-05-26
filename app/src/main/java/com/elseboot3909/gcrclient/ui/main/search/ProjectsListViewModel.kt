package com.elseboot3909.gcrclient.ui.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elseboot3909.gcrclient.api.ProjectsAPI
import com.elseboot3909.gcrclient.entity.ProjectInfo
import com.elseboot3909.gcrclient.utils.JsonUtils
import com.elseboot3909.gcrclient.utils.NetManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProjectsListViewModel : ViewModel() {

    private val projects: MutableLiveData<HashMap<String, ProjectInfo>> by lazy {
        MutableLiveData<HashMap<String, ProjectInfo>>().also {
            loadProjectList()
        }
    }

    fun getProjects(): LiveData<HashMap<String, ProjectInfo>> {
        return projects
    }

    fun refreshProjects() {
        loadProjectList()
    }

    private fun loadProjectList() {
        val retrofit = NetManager.getRetrofitConfiguration(null, true)

        retrofit.create(ProjectsAPI::class.java).getListProjects()
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful && response.body() != null) {
                        projects.postValue(
                            Gson().fromJson(
                                JsonUtils.trimJson(response.body()),
                                object : TypeToken<HashMap<String, ProjectInfo>>() {}.type
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                }
            })
    }

}