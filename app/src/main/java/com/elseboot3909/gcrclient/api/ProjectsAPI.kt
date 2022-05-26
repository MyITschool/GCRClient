package com.elseboot3909.gcrclient.api

import retrofit2.Call
import retrofit2.http.GET

interface ProjectsAPI {

    @GET("a/projects/?d&all")
    fun getListProjects(): Call<String>

}