package com.elseboot3909.GCRClient;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;

import com.elseboot3909.GCRClient.Utils.Constants;
import com.elseboot3909.GCRClient.Utils.ServerDataManager;

public class LoginActivity extends AppCompatActivity {

    Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(Constants.LOG_TAG, "Launching LoginActivity!");

        overridePendingTransition(R.anim.enter_from_right, R.anim.quit_to_left);

        getSupportFragmentManager().addOnBackStackChangedListener(() -> currentFragment = getSupportFragmentManager().findFragmentById(R.id.login_container));

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_login);
        if (ServerDataManager.isSharedPreferencesEmpty(getApplicationContext(), Constants.STORED_TOKENS)) {
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.quit_to_right);
    }

}