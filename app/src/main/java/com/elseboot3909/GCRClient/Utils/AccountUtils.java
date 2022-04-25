package com.elseboot3909.GCRClient.Utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.viewbinding.ViewBinding;

import com.elseboot3909.GCRClient.API.AccountAPI;
import com.elseboot3909.GCRClient.Entities.AccountInfo;
import com.elseboot3909.GCRClient.Entities.AvatarInfo;
import com.elseboot3909.GCRClient.Entities.ServerData;
import com.elseboot3909.GCRClient.R;
import com.google.android.material.chip.Chip;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.Random;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AccountUtils {

    public static HashMap<String, HashMap<Integer, AccountInfo>> savedAccountInfo = new HashMap<>();

    static Random random = new Random();

    public static final int[] dummyAvatars = new int[]{R.drawable.ic_dummy_avatar_1, R.drawable.ic_dummy_avatar_2, R.drawable.ic_dummy_avatar_3, R.drawable.ic_dummy_avatar_4, R.drawable.ic_dummy_avatar_5};

    public static int getRandomAvatar() {
        return dummyAvatars[random.nextInt(dummyAvatars.length)];
    }

    public static final String[] dummyUsernames = new String[]{"John", "Laurel", "Luke", "Jack", "Dexter", "Henry", "Dale", "Elbert"};

    public static String getRandomUsername() {
        return dummyUsernames[random.nextInt(dummyUsernames.length)];
    }

    public static void setAvatarDrawable(AvatarInfo avatarInfo, View view)  {
        Picasso picasso = Picasso.get();
        picasso.load(avatarInfo.getUrl()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(Resources.getSystem(), bitmap);
                roundedBitmapDrawable.setCircular(true);
                if (view instanceof Chip) {
                    ((Chip) view).setChipIcon(roundedBitmapDrawable);
                } else if (view instanceof ImageView) {
                    ((ImageView) view).setImageDrawable(roundedBitmapDrawable);
                } else {
                    Log.e(Constants.LOG_TAG, "(" + this.getClass() + ") setAvatarDrawable: Check type of view object!");
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) { }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) { }
        });
    }

    public static void loadProfileInfo(Integer accountId, ViewBinding binding, AccountUtilsCallback callback) {
        HashMap<Integer, AccountInfo> selectedMap = new HashMap<>();
        ServerData serverData = ServerDataManager.serverDataList.get(ServerDataManager.selectedPos);
        if (savedAccountInfo.containsKey(serverData.getServerName())) {
            selectedMap = savedAccountInfo.get(serverData.getServerName());
        }
        if (selectedMap.containsKey(accountId)) {
            callback.loadProfileInfoCallback(selectedMap.get(accountId), binding);
        } else {
            OkHttpClient client = ServerDataManager.getAuthenticatorClient(serverData.getUsername(), serverData.getPassword());

            Retrofit retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(serverData.getServerName() + serverData.getServerNameEnding())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();

            AccountAPI accountAPI = retrofit.create(AccountAPI.class);
            Call<String> retrofitRequest = accountAPI.getAccountInfo(String.valueOf(accountId));

            HashMap<Integer, AccountInfo> copySelectedMap = selectedMap;
            retrofitRequest.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Gson gson = new Gson();
                        AccountInfo accountInfo = gson.fromJson(JsonUtils.TrimJson(response.body()), AccountInfo.class);
                        copySelectedMap.put(accountInfo.get_account_id(), accountInfo);
                        savedAccountInfo.put(serverData.getServerName(), copySelectedMap);
                        callback.loadProfileInfoCallback(accountInfo, binding);
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

    public interface AccountUtilsCallback {
        void loadProfileInfoCallback(AccountInfo accountInfo, ViewBinding binding);
    }

}
