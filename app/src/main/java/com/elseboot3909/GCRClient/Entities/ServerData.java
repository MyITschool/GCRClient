package com.elseboot3909.GCRClient.Entities;

public class ServerData {

    private final String Username;

    private final String Password;

    private final String ServerName;

    private final String ServerNameEnding;

    private final String AccessToken;

    public ServerData(String username, String password, String serverName, String serverNameEnding, String accessToken) {
        Username = username;
        Password = password;
        ServerName = serverName;
        ServerNameEnding = serverNameEnding;
        AccessToken = accessToken;
    }

    @Override
    public String toString() {
        return ServerName;
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }

    public String getServerName() {
        return ServerName;
    }

    public String getServerNameEnding() {
        return ServerNameEnding;
    }

}
