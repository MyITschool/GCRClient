package com.elseboot3909.GCRClient.API;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChangesAPI {

    @GET("changes/")
    Call<String> queryChanges(@Query(value = "q", encoded = true) String q, @Query("n") Integer n, @Query("S") Integer S, @Query("access_token") String token);

}
