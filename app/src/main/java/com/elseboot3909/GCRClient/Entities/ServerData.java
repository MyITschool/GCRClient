package com.elseboot3909.GCRClient.Entities;

public class ServerData {

    private final String Username;

    private final String ServerName;

    private final String ServerNameEnding;

    private final String AccessToken;

    public ServerData(String username, String serverName, String serverNameEnding, String accessToken) {
        Username = username;
        ServerName = serverName;
        ServerNameEnding = serverNameEnding;
        AccessToken = accessToken;
    }

    @Override
    public String toString() {
        return ServerName;
    }

    public String getServerName() {
        return ServerName;
    }

    public String getServerNameEnding() {
        return ServerNameEnding;
    }

    public String getAccessToken() {
        return AccessToken;
    }

}
