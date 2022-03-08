package com.elseboot3909.GCRClient.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class TokenManager {

    static SharedPreferences sharedPreferences = null;

    public static SharedPreferences getSharedPreferences(Context context) {
        if (sharedPreferences != null) {
            return sharedPreferences;
        }

        String masterKeyAlias;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return null;
        }

        SharedPreferences sharedPreferences = null;
        try {
            sharedPreferences = EncryptedSharedPreferences.create(
                    Constants.STORED_TOKENS,
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

    public static boolean isSharedPreferencesEmpty(Context context) {
        return TokenManager.getSharedPreferences(context) == null || TokenManager.getSharedPreferences(context).getString(Constants.STORED_TOKENS, "").equals("");
    }

}
