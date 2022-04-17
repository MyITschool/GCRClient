package com.elseboot3909.GCRClient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.elseboot3909.GCRClient.Entities.ServerData;
import com.elseboot3909.GCRClient.Utils.Constants;
import com.elseboot3909.GCRClient.Utils.ServerDataManager;
import com.elseboot3909.GCRClient.databinding.ActivityMainBinding;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    ArrayAdapter<ServerData> serverListAdapter;

    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        DynamicColors.applyToActivitiesIfAvailable(getApplication());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        ServerDataManager.loadServerDataList(getApplicationContext());
        ServerDataManager.loadSavedPosition(getApplicationContext());

        if (ServerDataManager.selectedPos == -1 && ServerDataManager.serverDataList.size() != 0) {
            ServerDataManager.writeNewPosition(getApplicationContext(), 0);
        }

        if (ServerDataManager.isSharedPreferencesEmpty(getApplicationContext(), Constants.STORED_TOKENS)) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new ChangesListFragment()).commit();
        }

        TextView textView = binding.serverNavSelection.getHeaderView(0).findViewById(R.id.showed_name);
        textView.setText(ServerDataManager.serverDataList.get(ServerDataManager.selectedPos).toString());

        serverListAdapter = new ArrayAdapter<>(this,
                R.layout.server_list,
                ServerDataManager.serverDataList);

        MaterialAlertDialogBuilder materialDialogSwitchServer = new MaterialAlertDialogBuilder(this);
        materialDialogSwitchServer.setTitle("Select server");
        materialDialogSwitchServer.setNeutralButton("Cancel", null);
        materialDialogSwitchServer.setSingleChoiceItems(serverListAdapter, 1, (dialogInterface, i) -> {
            ServerDataManager.writeNewPosition(getApplicationContext(), i);
            finish();
            startActivity(getIntent());
        });

        MaterialAlertDialogBuilder materialAlertDialogExit = new MaterialAlertDialogBuilder(this);
        materialAlertDialogExit.setTitle("Are you sure you want to log out?");
        materialAlertDialogExit.setNeutralButton("Cancel", null);
        materialAlertDialogExit.setPositiveButton("Yes", (dialogInterface, i) -> {
            ServerDataManager.serverDataList.remove((int) ServerDataManager.selectedPos);
            ServerDataManager.writeServerDataList(getApplicationContext());
            ServerDataManager.writeNewPosition(getApplicationContext(), 0);
            finish();
            startActivity(getIntent());
        });

        binding.serverNavSelection.setNavigationItemSelectedListener(item -> {
            mHandler.removeCallbacksAndMessages(null);
            binding.serverNavDL.close();
            switch (item.getItemId()) {
                case R.id.add:
                    mHandler.postDelayed(() -> startActivity(new Intent(MainActivity.this, LoginActivity.class)), Constants.NAV_DRAWER_TIMEOUT);
                    break;
                case R.id.select:
                    mHandler.postDelayed(materialDialogSwitchServer::show, Constants.NAV_DRAWER_TIMEOUT);
                    break;
                case R.id.exit:
                    mHandler.postDelayed(materialAlertDialogExit::show, Constants.NAV_DRAWER_TIMEOUT);
            }
            return false;
        });

        binding.bottomNavMain.setOnItemSelectedListener((item) -> {
            switch(item.getItemId()) {
                case R.id.changes:
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new ChangesListFragment()).commit();
                    return true;
                case R.id.stared:
                    return true;
                case R.id.profile:
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
}