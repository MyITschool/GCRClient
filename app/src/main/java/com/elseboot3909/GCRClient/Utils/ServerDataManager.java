package com.elseboot3909.GCRClient.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.elseboot3909.GCRClient.Entities.ServerData;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class ServerDataManager {

    public static ArrayList<ServerData> serverDataList = new ArrayList<>();
    public static Integer selectedPos = 0;

    static final Gson gson = new Gson();

    public static SharedPreferences getSharedPreferences(Context context, String name) {
        SharedPreferences sharedPreferences = null;
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            sharedPreferences = EncryptedSharedPreferences.create(
                    name,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        return sharedPreferences;
    }

    @SuppressLint("ApplySharedPref")
    public static void writeServerDataList(Context context) {
        JsonArray jsonArray = (JsonArray) gson.toJsonTree(serverDataList,
                new TypeToken<List<ServerData>>() {
                }.getType());
        getSharedPreferences(context, Constants.SERVERS_DATA).edit().putString(Constants.SERVERS_DATA, jsonArray.toString()).commit();
    }

    public static void loadServerDataList(Context context) {
        String mValue = Constants.SERVERS_DATA;
        if (getSharedPreferences(context, mValue) != null && !getSharedPreferences(context, mValue).getString(mValue, "").isEmpty()) {
            try {
                serverDataList = gson.fromJson(getSharedPreferences(context, mValue).getString(mValue, ""),
                        new TypeToken<ArrayList<ServerData>>() {}.getType());
            } catch (JsonSyntaxException ignored) {
                serverDataList.clear();
                serverDataList.add(gson.fromJson(getSharedPreferences(context, mValue).getString(mValue, ""), ServerData.class));
            }
        } else {
            serverDataList = new ArrayList<>();
        }
    }

    @SuppressLint("ApplySharedPref")
    public static void writeNewPosition(Context context, Integer pos) {
        if (pos >= 0 && serverDataList.size() > pos) {
            selectedPos = pos;
            getSharedPreferences(context, Constants.SELECTED_SERVER).edit().putInt(Constants.SELECTED_SERVER, pos).commit();
        } else {
            Log.e(Constants.LOG_TAG, "Failed to writeNewPosition(), pos=" + pos + ", selectedPos=" + selectedPos + ", serverDataList.size()=" + serverDataList.size());
        }
    }

    public static void loadSavedPosition(Context context) {
        selectedPos = getSharedPreferences(context, Constants.SELECTED_SERVER).getInt(Constants.SELECTED_SERVER, -1);
    }

}
