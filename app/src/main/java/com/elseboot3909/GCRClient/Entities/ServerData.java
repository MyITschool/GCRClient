package com.elseboot3909.GCRClient.Entities;

import androidx.annotation.NonNull;

public class ServerData {

    private final String username;

    private final String password;

    private final String serverURL;

    private final String prefixURL;

    public ServerData(String username, String password, String serverURL, String prefixURL) {
        this.username = username;
        this.password = password;
        this.serverURL = serverURL;
        this.prefixURL = prefixURL;
    }

    @NonNull
    @Override
    public String toString() {
        return serverURL;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getServerURL() {
        return serverURL;
    }

    public String getURL() {
        return serverURL + prefixURL;
    }

}
