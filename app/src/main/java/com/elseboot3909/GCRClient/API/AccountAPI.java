package com.elseboot3909.GCRClient.API;

import com.elseboot3909.GCRClient.Entities.AccountInfo;
import com.elseboot3909.GCRClient.Entities.OAuthTokenInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface AccountAPI {

    @GET("/accounts/{user}")
    Call<String> getAccountInfo(@Path("user") String user);

    @GET("/a/accounts/self/oauthtoken")
    Call<String> getOAuthToken();

}
