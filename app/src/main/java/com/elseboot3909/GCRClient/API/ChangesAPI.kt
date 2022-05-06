package com.elseboot3909.GCRClient.API

import java.util.ArrayList

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ChangesAPI {

    @GET("a/changes/")
    fun queryChanges(@Query(value = "q", encoded = true) q: ArrayList<String>, @Query("n") n: Int, @Query("S") S: Int): Call<String>

    @GET("a/changes/{id}/detail")
    fun getChangeDetails(@Path(value = "id", encoded = true) id: String): Call<String>

    @GET("a/changes/{id}/revisions/{revision}/commit")
    fun getCommitInfo(@Path(value = "id", encoded = true) id: String , @Path(value = "revision", encoded = true) revision: String ): Call<String>

}
