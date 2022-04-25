package com.elseboot3909.GCRClient.API;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChangesAPI {

    @GET("a/changes/")
    Call<String> queryChanges(@Query(value = "q", encoded = true) ArrayList<String> q, @Query("n") Integer n, @Query("S") Integer S);

    @GET("a/changes/{id}/detail")
    Call<String> getChangeDetails(@Path(value = "id", encoded = true) String id);

    @GET("a/changes/{id}/revisions/{revision}/commit")
    Call<String> getCommitInfo(@Path(value = "id", encoded = true) String id, @Path(value = "revision", encoded = true) String revision);

}
