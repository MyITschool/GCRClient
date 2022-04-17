package com.elseboot3909.GCRClient.Entities;

import com.google.gson.annotations.SerializedName;

public class OAuthTokenInfo {
    private String username;

    @SerializedName("resource_host")
    private String host;

    @SerializedName("access_token")
    private String token;

    @SerializedName("provider_id")
    private String providerId;

    @SerializedName("expires_at")
    private String expires;

    private String type;

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

}
