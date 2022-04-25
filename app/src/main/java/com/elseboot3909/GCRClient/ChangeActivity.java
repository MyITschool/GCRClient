package com.elseboot3909.GCRClient;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.elseboot3909.GCRClient.API.AccountAPI;
import com.elseboot3909.GCRClient.Entities.ServerData;
import com.elseboot3909.GCRClient.Utils.ServerDataManager;
import com.elseboot3909.GCRClient.databinding.ActivityChangeBinding;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ChangeActivity extends AppCompatActivity {

    ActivityChangeBinding binding;

    FragmentTransaction fragmentTransaction;
    Fragment currentFragment;
    Fragment searchFragment;
    Bundle bundle;
    String mName;
    String id;
    boolean isStarred;

    Integer mProgressBarRequests = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.enter_from_right, R.anim.quit_to_left);

        binding = ActivityChangeBinding.inflate(getLayoutInflater());

        bundle = getIntent().getExtras();
        id = bundle.getString("id");
        String subject = bundle.getString("subject");
        binding.subject.setText(subject);

        String status = bundle.getString("status");
        switch (status) {
            case "NEW":
                binding.active.status.setText(R.string.active);
                binding.active.status.setVisibility(View.VISIBLE);
                break;
            case "MERGED":
                binding.merged.status.setText(R.string.merged);
                binding.merged.status.setVisibility(View.VISIBLE);
                break;
            case "ABANDONED":
                binding.abandoned.status.setText(R.string.abandoned);
                binding.abandoned.status.setVisibility(View.VISIBLE);
                break;
        }

        if (bundle.getBoolean("work_in_progress")) {
            binding.wip.status.setText(R.string.wip);
            binding.wip.status.setVisibility(View.VISIBLE);
        }

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        mName = ChangeInfoFragment.class.getName();
        searchFragment = getSupportFragmentManager().findFragmentByTag(mName);
        if (searchFragment == null) {
            ChangeInfoFragment fragment = new ChangeInfoFragment();
            fragment.setArguments(bundle);
            fragmentTransaction
                    .replace(R.id.change_container, fragment, mName)
                    .addToBackStack(mName);
        } else {
            fragmentTransaction
                    .replace(R.id.change_container, searchFragment, mName);
        }
        fragmentTransaction.commit();

        binding.exit.setOnClickListener(view -> finish());

        isStarred = bundle.getBoolean("starred");
        if (isStarred) {
            binding.star.setImageResource(R.drawable.ic_baseline_star);
        }
        binding.star.setOnClickListener(view -> updateStarredChange());

        binding.bottomNavMain.setOnItemSelectedListener((item) -> {
            currentFragment = getSupportFragmentManager().findFragmentById(R.id.change_container);
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            switch(item.getItemId()) {
                case R.id.info:
                    if (!(currentFragment instanceof ChangeInfoFragment)) {
                        mName = ChangeInfoFragment.class.getName();
                        searchFragment = getSupportFragmentManager().findFragmentByTag(mName);
                        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.quit_to_right);
                        if (searchFragment == null) {
                            ChangeInfoFragment fragment = new ChangeInfoFragment();
                            fragment.setArguments(bundle);
                            fragmentTransaction
                                    .replace(R.id.change_container, fragment, mName)
                                    .addToBackStack(mName);
                        } else {
                            fragmentTransaction
                                    .replace(R.id.change_container, searchFragment, mName);
                        }
                        fragmentTransaction.commit();
                    }
                    return true;
                case R.id.code:
                    if (!(currentFragment instanceof CodeFragment)) {
                        if (currentFragment instanceof ChangeInfoFragment) {
                            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.quit_to_left);
                        } else {
                            fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.quit_to_right);
                        }
                        mName = CodeFragment.class.getName();
                        searchFragment = getSupportFragmentManager().findFragmentByTag(mName);
                        if (searchFragment == null) {
                            fragmentTransaction
                                    .replace(R.id.change_container, new CodeFragment(), mName)
                                    .addToBackStack(mName);
                        } else {
                            fragmentTransaction
                                    .replace(R.id.change_container, searchFragment, mName);
                        }
                        fragmentTransaction.commit();
                    }
                    return true;
                case R.id.vote:
                    if (!(currentFragment instanceof VoteFragment)) {
                        if (currentFragment instanceof ChangeInfoFragment || currentFragment instanceof CodeFragment) {
                            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.quit_to_left);
                        } else {
                            fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.quit_to_right);
                        }
                        mName = VoteFragment.class.getName();
                        searchFragment = getSupportFragmentManager().findFragmentByTag(mName);
                        if (searchFragment == null) {
                            fragmentTransaction
                                    .replace(R.id.change_container, new VoteFragment(), mName)
                                    .addToBackStack(mName);
                        } else {
                            fragmentTransaction
                                    .replace(R.id.change_container, searchFragment, mName);
                        }
                        fragmentTransaction.commit();
                    }
                    return true;
                case R.id.log:
                    /* TODO */
                    return false;
                case R.id.comment:
                    /* TODO */
                    return false;
                default:
                    return false;
            }
        });

        setContentView(binding.getRoot());
    }

    private void updateStarredChange() {
        binding.star.setClickable(false);
        progressBarManager(true);

        ServerData serverData = ServerDataManager.serverDataList.get(ServerDataManager.selectedPos);
        OkHttpClient client = ServerDataManager.getAuthenticatorClient(serverData.getUsername(), serverData.getPassword());

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(serverData.getServerName() + serverData.getServerNameEnding())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        AccountAPI accountAPI = retrofit.create(AccountAPI.class);
        Call<String> retrofitRequest;
        if (isStarred) {
            retrofitRequest = accountAPI.removeStarredChange(id);
        } else {
            retrofitRequest = accountAPI.putStarredChange(id);
        }

        retrofitRequest.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                progressBarManager(false);
                isStarred = !isStarred;
                if (isStarred) {
                    binding.star.setImageResource(R.drawable.ic_baseline_star);
                } else {
                    binding.star.setImageResource(R.drawable.ic_outline_star);
                }
                binding.star.setClickable(true);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                progressBarManager(false);
                binding.star.setClickable(true);
            }
        });

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.quit_to_right);
    }

    @Override
    public void onBackPressed() {
        currentFragment = getSupportFragmentManager().findFragmentById(R.id.change_container);
        if (currentFragment instanceof ChangeInfoFragment) {
            finish();
        } else {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            mName = ChangesListFragment.class.getName();
            searchFragment = getSupportFragmentManager().findFragmentByTag(mName);
            if (searchFragment != null) {
                fragmentTransaction.replace(R.id.change_container, searchFragment, mName);
            } else {
                finish();
            }
        }
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