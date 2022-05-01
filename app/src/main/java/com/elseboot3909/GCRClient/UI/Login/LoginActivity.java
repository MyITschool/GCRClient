package com.elseboot3909.GCRClient.UI.Login;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;

import com.elseboot3909.GCRClient.R;
import com.elseboot3909.GCRClient.Utils.ServerDataManager;
import com.elseboot3909.GCRClient.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        overridePendingTransition(R.anim.enter_from_right, R.anim.quit_to_left);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_login);
        if (ServerDataManager.serverDataList.isEmpty()) {
            getSupportFragmentManager().beginTransaction().add(R.id.login_container, new HelloLoginFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.login_container, new ServerInputFragment()).commit();
        }

        setContentView(binding.getRoot());
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.login_container);
        if (currentFragment instanceof HelloLoginFragment) {
            moveTaskToBack(true);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.quit_to_right);
    }

}