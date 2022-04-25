package com.elseboot3909.GCRClient;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.elseboot3909.GCRClient.API.ChangesAPI;
import com.elseboot3909.GCRClient.Entities.AccountInfo;
import com.elseboot3909.GCRClient.Entities.ChangeInfo;
import com.elseboot3909.GCRClient.Entities.CommitInfo;
import com.elseboot3909.GCRClient.Entities.ServerData;
import com.elseboot3909.GCRClient.Utils.AccountUtils;
import com.elseboot3909.GCRClient.Utils.Constants;
import com.elseboot3909.GCRClient.Utils.JsonUtils;
import com.elseboot3909.GCRClient.Utils.ServerDataManager;
import com.elseboot3909.GCRClient.databinding.FragmentChangeInfoBinding;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ChangeInfoFragment extends Fragment {

    FragmentChangeInfoBinding binding;

    private final Integer chipMaxSize = 22 + 8 * 2 + 4 + 16;

    DateFormat dateInputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    DateFormat dateOutputFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.US);

    private final Gson gson = new Gson();

    private ChangeInfo changeInfo;
    private CommitInfo commitInfo;
    private boolean isReady;
    private String id;

    public ChangeInfoFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChangeInfoBinding.inflate(inflater, container, false);

        if (!isReady) {
            id = getArguments().getString("id");
            loadChangeInfo();
            isReady = true;
        } else {
            setChangeInfo();
        }

        return binding.getRoot();
    }

    private void loadChangeInfo() {
        Activity activity = getActivity();
        if (activity != null) ((ChangeActivity) activity).progressBarManager(true);

        ServerData serverData = ServerDataManager.serverDataList.get(ServerDataManager.selectedPos);
        OkHttpClient client = ServerDataManager.getAuthenticatorClient(serverData.getUsername(), serverData.getPassword());

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(serverData.getServerName() + serverData.getServerNameEnding())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        ChangesAPI changesAPI = retrofit.create(ChangesAPI.class);
        Call<String> retrofitRequest = changesAPI.getChangeDetails(id);

        retrofitRequest.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    changeInfo = gson.fromJson(JsonUtils.TrimJson(response.body()), ChangeInfo.class);

                    Call<String> retrofitRequest = changesAPI.getCommitInfo(id, "current");

                    retrofitRequest.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                commitInfo = gson.fromJson(JsonUtils.TrimJson(response.body()), CommitInfo.class);
                                setChangeInfo();
                            } else {
                                Log.e(Constants.LOG_TAG, "onResponse: Not successful");
                            }
                            if (activity != null) ((ChangeActivity) activity).progressBarManager(false);
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            if (activity != null) ((ChangeActivity) activity).progressBarManager(false);
                            Log.e(Constants.LOG_TAG, "onFailure: Not successful");
                        }
                    });
                } else {
                    Log.e(Constants.LOG_TAG, "onResponse: Not successful");
                    if (activity != null) ((ChangeActivity) activity).progressBarManager(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                if (activity != null) ((ChangeActivity) activity).progressBarManager(false);
                Log.e(Constants.LOG_TAG, "onFailure: Not successful");
            }
        });
    }

    private void setChangeInfo() {
        DisplayMetrics displayMetrics;
        try {
            displayMetrics = getContext().getResources().getDisplayMetrics();
        } catch (NullPointerException ignored) { return; }

        try {
            Date date = dateInputFormat.parse(changeInfo.getUpdated().replace(".000000000", ""));
            if (date != null) binding.editTime.setText(dateOutputFormat.format(date));
        } catch (ParseException ignored) { }

        /* Set owner params */
        binding.chipOwner.infoChip.setText(changeInfo.getOwner().getName());
        try {
            binding.chipOwner.infoChip.setChipIcon(ContextCompat.getDrawable(getContext(), AccountUtils.getRandomAvatar()));
        } catch (NullPointerException ignored) { }
        if (changeInfo.getOwner().getAvatars() != null) {
            int listSize = changeInfo.getOwner().getAvatars().size();
            if (listSize != 0) {
                AccountUtils.setAvatarDrawable(changeInfo.getOwner().getAvatars().get(listSize - 1), binding.chipOwner.infoChip);
            }
        }

        /* Set project params */
        binding.chipProject.infoChip.setText(changeInfo.getProject());
        binding.chipProject.infoChip.post(() -> binding.projectText.post(() -> {
            int mWidth = (int) (chipMaxSize * displayMetrics.density + binding.projectText.getWidth());
            if (displayMetrics.widthPixels - mWidth < binding.chipProject.infoChip.getWidth()) {
                binding.chipProject.infoChip.setMaxWidth(displayMetrics.widthPixels - mWidth);
                binding.chipProject.infoChip.invalidate();
            }
        }));

        /* Set branch params */
        binding.chipBranch.infoChip.setText(changeInfo.getBranch());
        binding.chipBranch.infoChip.post(() -> binding.branchText.post(() -> {
            int mWidth = (int) (chipMaxSize * displayMetrics.density + binding.branchText.getWidth());
            if (displayMetrics.widthPixels - mWidth < binding.chipBranch.infoChip.getWidth()) {
                binding.chipBranch.infoChip.setMaxWidth(displayMetrics.widthPixels - mWidth);
                binding.chipBranch.infoChip.invalidate();
            }
        }));

        /* Set topic params if exists */
        String topic = changeInfo.getTopic();
        if (topic != null && !topic.isEmpty()) {
            binding.topicLayout.setVisibility(View.VISIBLE);
            binding.chipTopic.infoChip.setText(topic);
            binding.chipTopic.infoChip.post(() -> binding.topicText.post(() -> {
                int mWidth = (int) (chipMaxSize * displayMetrics.density + binding.topicText.getWidth());
                if (displayMetrics.widthPixels - mWidth < binding.chipTopic.infoChip.getWidth()) {
                    binding.chipTopic.infoChip.setMaxWidth(displayMetrics.widthPixels - mWidth);
                    binding.chipTopic.infoChip.invalidate();
                }
            }));
        }

        /* Set reviewers params */
        HashMap<String, ArrayList<AccountInfo>> reviewers = changeInfo.getReviewers();
        ArrayList<AccountInfo> subArray;
        if (reviewers != null) {
            if (reviewers.containsKey("REVIEWER")) {
                subArray = reviewers.get("REVIEWER");
                if (subArray != null && subArray.size() > 0) {
                    binding.chipReviewer.infoChip.setText(subArray.get(0).getName());
                    try {
                        binding.chipReviewer.infoChip.setChipIcon(ContextCompat.getDrawable(getContext(), AccountUtils.getRandomAvatar()));
                    } catch (NullPointerException ignored) { }
                    if (subArray.get(0).getAvatars() != null) {
                        int listSize = subArray.get(0).getAvatars().size();
                        if (listSize != 0) {
                            AccountUtils.setAvatarDrawable(subArray.get(0).getAvatars().get(listSize - 1), binding.chipReviewer.infoChip);
                        }
                    }
                    binding.reviewerLayout.setVisibility(View.VISIBLE);
                }
            }
            if (reviewers.containsKey("CC")) {
                subArray = reviewers.get("CC");
                if (subArray != null && subArray.size() > 0) {
                    binding.chipCC.infoChip.setText(subArray.get(0).getName());
                    try {
                        binding.chipCC.infoChip.setChipIcon(ContextCompat.getDrawable(getContext(), AccountUtils.getRandomAvatar()));
                    } catch (NullPointerException ignored) { }
                    if (subArray.get(0).getAvatars() != null) {
                        int listSize = subArray.get(0).getAvatars().size();
                        if (listSize != 0) {
                            AccountUtils.setAvatarDrawable(subArray.get(0).getAvatars().get(listSize - 1), binding.chipCC.infoChip);
                        }
                    }
                    binding.CCLayout.setVisibility(View.VISIBLE);
                }
            }

            /* Set message of commit */
            String description = commitInfo.getMessage();
            if (!description.isEmpty()) {
                binding.description.setText(description);
                binding.description.setVisibility(View.VISIBLE);
            }
        }
        binding.hideFragment.setVisibility(View.VISIBLE);
    }

}