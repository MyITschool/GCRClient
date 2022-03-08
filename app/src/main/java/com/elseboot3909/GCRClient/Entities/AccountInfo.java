package com.elseboot3909.GCRClient.Entities;

import com.google.gson.annotations.SerializedName;

public class AccountInfo {

    @SerializedName("_account_id")
    private Integer accountId;

    private String name;

    private String email;

    private String providerId;

    private String username;

    @SerializedName("display_name")
    private String displayedName;

    public Integer getAccountId() {
        return accountId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayedName() {
        return displayedName;
    }

}
