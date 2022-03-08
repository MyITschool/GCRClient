package com.elseboot3909.GCRClient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.elseboot3909.GCRClient.Utils.Constants;
import com.elseboot3909.GCRClient.Utils.TokenManager;

public class MainActivity extends AppCompatActivity {

    //private static final String GERRIT_URL = "gerrit.twrp.me/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = TokenManager.getSharedPreferences(getApplicationContext());

        Intent intent = null;
        if (TokenManager.isSharedPreferencesEmpty(getApplicationContext())) {
            Log.e(Constants.LOG_TAG, Constants.STORED_TOKENS + " is empty!");
            intent = new Intent(MainActivity.this, LoginActivity.class);
        } else {
            Log.e(Constants.LOG_TAG, sharedPreferences.getString(Constants.STORED_TOKENS, ""));
        }
        startActivity(intent);
        //setContentView(R.layout.activity_main);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}