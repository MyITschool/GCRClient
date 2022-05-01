package com.elseboot3909.GCRClient.ViewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.elseboot3909.GCRClient.API.ChangesAPI;
import com.elseboot3909.GCRClient.Entities.ChangeInfo;
import com.elseboot3909.GCRClient.Entities.CommitInfo;
import com.elseboot3909.GCRClient.Utils.Constants;
import com.elseboot3909.GCRClient.Utils.JsonUtils;
import com.elseboot3909.GCRClient.Utils.NetManager;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChangeViewModel extends ViewModel {

    private String id;

    private MutableLiveData<ChangeInfo> changeInfo;
    private MutableLiveData<CommitInfo> commitInfo;

    private final Gson gson = new Gson();

    public LiveData<ChangeInfo> getChangeInfo(String id) {
        if (changeInfo == null || !id.equals(this.id)) {
            this.id = id;
            changeInfo = new MutableLiveData<>();
            loadChangeInfo();
        }
        return changeInfo;
    }

    public LiveData<CommitInfo> getCommitInfo(String id) {
        if (commitInfo == null || !id.equals(this.id)) {
            this.id = id;
            commitInfo = new MutableLiveData<>();
            loadCommitInfo();
        }
        return commitInfo;
    }

    private void loadChangeInfo() {
            Retrofit retrofit = NetManager.getRetrofitConfiguration(null, true);

            ChangesAPI changesAPI = retrofit.create(ChangesAPI.class);
            Call<String> retrofitRequest = changesAPI.getChangeDetails(id);

            retrofitRequest.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        changeInfo.postValue(gson.fromJson(JsonUtils.TrimJson(response.body()), ChangeInfo.class));
                    } else {
                        Log.e(Constants.LOG_TAG, "onResponse: Not successful");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.e(Constants.LOG_TAG, "onFailure: Not successful");
                }
            });
    }


    private void loadCommitInfo() {
        Retrofit retrofit = NetManager.getRetrofitConfiguration(null, true);

        ChangesAPI changesAPI = retrofit.create(ChangesAPI.class);
        Call<String> retrofitRequest = changesAPI.getCommitInfo(id, "current");

        retrofitRequest.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    commitInfo.postValue(gson.fromJson(JsonUtils.TrimJson(response.body()), CommitInfo.class));
                } else {
                    Log.e(Constants.LOG_TAG, "onResponse: Not successful");
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(Constants.LOG_TAG, "onFailure: Not successful");
            }
        });
    }

}
