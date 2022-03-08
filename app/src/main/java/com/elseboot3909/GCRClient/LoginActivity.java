package com.elseboot3909.GCRClient;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.elseboot3909.GCRClient.Utils.Constants;
import com.elseboot3909.GCRClient.Utils.TokenManager;

public class LoginActivity extends AppCompatActivity {

    Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(Constants.LOG_TAG, "Launching LoginActivity!");

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                currentFragment = getSupportFragmentManager().findFragmentById(R.id.login_container);
            }
        });

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_login);
        if (TokenManager.isSharedPreferencesEmpty(getApplicationContext())) {
            getSupportFragmentManager().beginTransaction().add(R.id.login_container, new HelloLoginFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.login_container, new ServerInputFragment()).commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (currentFragment instanceof HelloLoginFragment) {
            moveTaskToBack(true);
        } else {
            super.onBackPressed();
        }
    }
}