package com.elseboot3909.gcrclient.api

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface AccountAPI {

    @GET("accounts/{user}")
    fun getAccountInfo(@Path("user") user: String): Call<String>

    @GET("a/accounts/self/detail")
    fun getSelfAccountDetails(): Call<String>

    @GET("a/accounts/self/starred.changes")
    fun getStarredChanges(): Call<String>

    @PUT("a/accounts/self/starred.changes/{id}")
    fun putStarredChange(@Path(value = "id", encoded = true) id: String): Call<String>

    @DELETE("a/accounts/self/starred.changes/{id}")
    fun removeStarredChange(@Path(value = "id", encoded = true) id: String): Call<String>

}
