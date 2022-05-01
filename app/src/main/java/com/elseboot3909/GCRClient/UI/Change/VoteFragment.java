package com.elseboot3909.GCRClient.UI.Change;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.elseboot3909.GCRClient.API.ChangesAPI;
import com.elseboot3909.GCRClient.Entities.ChangeInfo;
import com.elseboot3909.GCRClient.Entities.LabelInfo;
import com.elseboot3909.GCRClient.Utils.Constants;
import com.elseboot3909.GCRClient.Utils.JsonUtils;
import com.elseboot3909.GCRClient.Utils.NetManager;
import com.elseboot3909.GCRClient.databinding.FragmentVoteBinding;
import com.google.gson.Gson;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class VoteFragment extends Fragment {

    FragmentVoteBinding binding;

    private final Gson gson = new Gson();

    private ChangeInfo changeInfo;
    private boolean isReady;
    private String id;

    public VoteFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVoteBinding.inflate(inflater, container, false);

//        labelListAdapter = new LabelListAdapter(changesList, adapterCallback);
//        binding.changesView.setAdapter(changePreviewAdapter);
//        binding.changesView.setLayoutManager(new LinearLayoutManager(getContext()));
//        binding.changesView.setNestedScrollingEnabled(true);

        if (!isReady) {
            id = getArguments().getString("id");
            loadChangeInfo();
            isReady = true;
        } else {
            setLabelsInfo();
        }

        return binding.getRoot();
    }

    private void loadChangeInfo() {
        Activity activity = getActivity();
        if (activity != null) ((ChangeActivity) activity).progressBarManager(true);

        Retrofit retrofit = NetManager.getRetrofitConfiguration(null, true);

        ChangesAPI changesAPI = retrofit.create(ChangesAPI.class);
        Call<String> retrofitRequest = changesAPI.getChangeDetails(id);

        retrofitRequest.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    changeInfo = gson.fromJson(JsonUtils.TrimJson(response.body()), ChangeInfo.class);
                    setLabelsInfo();
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

    private void setLabelsInfo() {
        HashMap<String, LabelInfo> labels = changeInfo.getLabels();


    }

}