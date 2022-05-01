package com.elseboot3909.GCRClient.ViewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.elseboot3909.GCRClient.API.AccountAPI;
import com.elseboot3909.GCRClient.Entities.AccountInfo;
import com.elseboot3909.GCRClient.Utils.Constants;
import com.elseboot3909.GCRClient.Utils.JsonUtils;
import com.elseboot3909.GCRClient.Utils.NetManager;
import com.google.gson.Gson;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AccountViewModel extends ViewModel {

    private HashMap<String, MutableLiveData<AccountInfo>> accountInfoMap;

    public MutableLiveData<AccountInfo> getAccountInfo(String id) {
        if (accountInfoMap == null) {
            accountInfoMap = new HashMap<>();
        }
        if (!accountInfoMap.containsKey(id)) {
            accountInfoMap.put(id, new MutableLiveData<>());
            loadProfileInfo(id);
        }
        return accountInfoMap.get(id);
    }

    private void loadProfileInfo(String id) {
            Retrofit retrofit = NetManager.getRetrofitConfiguration(null, true);

            AccountAPI accountAPI = retrofit.create(AccountAPI.class);
            Call<String> retrofitRequest = accountAPI.getAccountInfo(id);

            retrofitRequest.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Gson gson = new Gson();
                        MutableLiveData<AccountInfo> accountInfo = accountInfoMap.get(id);
                        if (accountInfo != null) {
                            accountInfo.postValue(gson.fromJson(JsonUtils.TrimJson(response.body()), AccountInfo.class));
                        }
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
