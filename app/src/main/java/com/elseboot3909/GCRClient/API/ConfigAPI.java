package com.elseboot3909.GCRClient.API;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ConfigAPI {

    @GET("/config/server/version")
    Call<String> getVersion();

}
