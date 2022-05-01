package com.elseboot3909.GCRClient.API;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AccountAPI {

    @GET("accounts/{user}")
    Call<String> getAccountInfo(@Path("user") String user);

    @GET("a/accounts/self/detail")
    Call<String> getSelfAccountDetails();

    @GET("a/accounts/self/starred.changes")
    Call<String> getStarredChanges();

    @PUT("a/accounts/self/starred.changes/{id}")
    Call<String> putStarredChange(@Path(value = "id", encoded = true) String id);

    @DELETE("a/accounts/self/starred.changes/{id}")
    Call<String> removeStarredChange(@Path(value = "id", encoded = true) String id);

}
