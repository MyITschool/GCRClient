package com.elseboot3909.GCRClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.elseboot3909.GCRClient.Utils.Transitions;
import com.google.android.material.transition.MaterialSharedAxis;

import com.elseboot3909.GCRClient.Utils.Constants;
import com.elseboot3909.GCRClient.Utils.TokenManager;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

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
                //currentFragment.setEnterTransition(Transitions.getBackwardTransition());
                //currentFragment.setExitTransition(Transitions.getForwardTransition());
            }
        });

        // https://developer.android.com/training/gestures/edge-to-edge#java
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

//        Window window = this.getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
//                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION |
//                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_login);
        if (TokenManager.isSharedPreferencesEmpty(getApplicationContext())) {
            getSupportFragmentManager().beginTransaction().add(R.id.login_container, new HelloLoginFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.login_container, new ServerInputFragment()).commit();
        }
    }

    @Override
    public void onBackPressed() {
//        currentFragment = getSupportFragmentManager().findFragmentById(R.id.login_container);
//        if (currentFragment instanceof HelloLoginFragment) {
//            moveTaskToBack(true);
//        } else if (currentFragment instanceof ServerInputFragment &&
//                TokenManager.isSharedPreferencesEmpty(getApplicationContext())) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.login_container, new HelloLoginFragment()).commit();
//            moveTaskToBack(false);
//        } else if (currentFragment instanceof LoginFragment) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.login_container, new ServerInputFragment()).commit();
//            moveTaskToBack(false);
//        }
        //moveTaskToBack(!(getSupportFragmentManager().findFragmentById(R.id.login_container) instanceof HelloLoginFragment));

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (currentFragment instanceof HelloLoginFragment) {
            moveTaskToBack(true);
        } else {
            super.onBackPressed();
        }
    }
}