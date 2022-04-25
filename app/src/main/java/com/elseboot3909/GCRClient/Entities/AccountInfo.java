package com.elseboot3909.GCRClient.Entities;

import java.util.ArrayList;

public class AccountInfo {

    private Integer _account_id;
    private String name;
    private String email;
    private String providerId;
    private String username;
    private String display_name;
    private ArrayList<AvatarInfo> avatars;

    public Integer get_account_id() { return _account_id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getProviderId() { return providerId; }
    public String getUsername() { return username; }
    public String getDisplay_name() { return display_name; }
    public ArrayList<AvatarInfo> getAvatars() { return avatars; }
}
