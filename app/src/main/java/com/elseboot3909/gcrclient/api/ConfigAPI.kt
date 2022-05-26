package com.elseboot3909.gcrclient.api

import retrofit2.Call
import retrofit2.http.GET

interface ConfigAPI {

    @GET("config/server/version")
    fun getVersion(): Call<String>

}
