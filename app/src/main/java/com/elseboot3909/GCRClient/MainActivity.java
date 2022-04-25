package com.elseboot3909.GCRClient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.elseboot3909.GCRClient.Entities.ServerData;
import com.elseboot3909.GCRClient.Utils.Constants;
import com.elseboot3909.GCRClient.Utils.ServerDataManager;
import com.elseboot3909.GCRClient.databinding.ActivityMainBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    ArrayAdapter<ServerData> serverListAdapter;
    Handler mHandler = new Handler();
    FragmentTransaction fragmentTransaction;
    Fragment currentFragment;
    Fragment searchFragment;
    String mName;
    Integer mProgressBarRequests = 0;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() > 0) {
                    reloadActivity();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        ServerDataManager.loadServerDataList(getApplicationContext());
        ServerDataManager.loadSavedPosition(getApplicationContext());

        if (ServerDataManager.selectedPos == -1 && ServerDataManager.serverDataList.isEmpty()) {
            ServerDataManager.writeNewPosition(getApplicationContext(), 0);
        }

        if (ServerDataManager.serverDataList.isEmpty()) {
            activityResultLauncher.launch(new Intent(MainActivity.this, LoginActivity.class));
        } else {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            mName = ChangesListFragment.class.getName();
            searchFragment = getSupportFragmentManager().findFragmentByTag(mName);
            if (searchFragment == null) {
                fragmentTransaction
                        .replace(R.id.main_container, new ChangesListFragment(), mName)
                        .addToBackStack(mName);
            } else {
                fragmentTransaction
                        .replace(R.id.main_container, searchFragment, mName);
            }
            fragmentTransaction.commit();
        }

        TextView textView = binding.serverNavSelection.getHeaderView(0).findViewById(R.id.showed_name);
        if (!ServerDataManager.serverDataList.isEmpty())  {
            textView.setText(ServerDataManager.serverDataList.get(ServerDataManager.selectedPos).toString());
        }

        serverListAdapter = new ArrayAdapter<>(this,
                R.layout.server_list,
                ServerDataManager.serverDataList);

        MaterialAlertDialogBuilder materialDialogSwitchServer = new MaterialAlertDialogBuilder(this);
        materialDialogSwitchServer.setTitle("Select server");
        materialDialogSwitchServer.setNeutralButton("Cancel", null);
        materialDialogSwitchServer.setSingleChoiceItems(serverListAdapter, 1, (dialogInterface, i) -> {
            ServerDataManager.writeNewPosition(getApplicationContext(), i);
            reloadActivity();
        });

        MaterialAlertDialogBuilder materialAlertDialogExit = new MaterialAlertDialogBuilder(this);
        materialAlertDialogExit.setTitle("Are you sure you want to log out?");
        materialAlertDialogExit.setNeutralButton("Cancel", null);
        materialAlertDialogExit.setPositiveButton("Yes", (dialogInterface, i) -> {
            ServerDataManager.serverDataList.remove((int) ServerDataManager.selectedPos);
            ServerDataManager.writeServerDataList(getApplicationContext());
            ServerDataManager.writeNewPosition(getApplicationContext(), 0);
            reloadActivity();
        });

        binding.serverNavSelection.setNavigationItemSelectedListener(item -> {
            mHandler.removeCallbacksAndMessages(null);
            binding.serverNavDL.close();
            switch (item.getItemId()) {
                case R.id.add:
                    mHandler.postDelayed(() -> activityResultLauncher.launch(new Intent(MainActivity.this, LoginActivity.class)), Constants.NAV_DRAWER_TIMEOUT);
                    break;
                case R.id.select:
                    mHandler.postDelayed(() -> {
                        if (ServerDataManager.serverDataList.size() > 1) {
                            materialDialogSwitchServer.show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Nothing to choose from", Toast.LENGTH_SHORT).show();
                        }
                    }, Constants.NAV_DRAWER_TIMEOUT);
                    break;
                case R.id.exit:
                    mHandler.postDelayed(materialAlertDialogExit::show, Constants.NAV_DRAWER_TIMEOUT);
            }
            return false;
        });

        binding.bottomNavMain.setOnItemSelectedListener((item) -> {
            currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            switch(item.getItemId()) {
                case R.id.changes:
                    if (!(currentFragment instanceof ChangesListFragment)) {
                        mName = ChangesListFragment.class.getName();
                        searchFragment = getSupportFragmentManager().findFragmentByTag(mName);
                        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.quit_to_right);
                        if (searchFragment == null) {
                            fragmentTransaction
                                    .replace(R.id.main_container, new ChangesListFragment(), mName)
                                    .addToBackStack(mName);
                        } else {
                            fragmentTransaction
                                    .replace(R.id.main_container, searchFragment, mName);
                        }
                        fragmentTransaction.commit();
                    }
                    return true;
                case R.id.stared:
                    if (!(currentFragment instanceof StarredListFragment)) {
                        if (currentFragment instanceof ChangesListFragment) {
                            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.quit_to_left);
                        } else if (currentFragment instanceof ProfileFragment) {
                            fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.quit_to_right);
                        }
                        mName = StarredListFragment.class.getName();
                        searchFragment = getSupportFragmentManager().findFragmentByTag(mName);
                        if (searchFragment == null) {
                            fragmentTransaction
                                    .replace(R.id.main_container, new StarredListFragment(), mName)
                                    .addToBackStack(mName);
                        } else {
                            fragmentTransaction
                                    .replace(R.id.main_container, searchFragment, mName);
                        }
                        fragmentTransaction.commit();
                    }
                    return true;
                case R.id.profile:
                    if (!(currentFragment instanceof ProfileFragment)) {
                        mName = ProfileFragment.class.getName();
                        searchFragment = getSupportFragmentManager().findFragmentByTag(mName);
                        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.quit_to_left);
                        if (searchFragment == null) {
                            fragmentTransaction
                                    .replace(R.id.main_container, new ProfileFragment(), mName)
                                    .addToBackStack(mName);
                        } else {
                            fragmentTransaction
                                    .replace(R.id.main_container, searchFragment, mName);
                        }
                        fragmentTransaction.commit();
                    }
                    return true;
                default:
                    return false;
            }
        });

        setContentView(binding.getRoot());
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void reloadActivity() {
        finish();
        startActivity(getIntent());
    }

    public void progressBarManager(boolean enabled) {
        if (enabled) {
            mProgressBarRequests++;
        } else {
            mProgressBarRequests--;
        }
        if (mProgressBarRequests > 0 && binding.progressBar.getVisibility() != View.VISIBLE) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else if (mProgressBarRequests <= 0 && binding.progressBar.getVisibility() == View.VISIBLE) {
            binding.progressBar.setVisibility(View.GONE);
        }
    }

}