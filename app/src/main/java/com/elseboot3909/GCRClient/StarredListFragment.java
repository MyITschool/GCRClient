package com.elseboot3909.GCRClient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elseboot3909.GCRClient.API.AccountAPI;
import com.elseboot3909.GCRClient.Adapter.ChangePreviewAdapter;
import com.elseboot3909.GCRClient.Entities.ChangeInfo;
import com.elseboot3909.GCRClient.Entities.ServerData;
import com.elseboot3909.GCRClient.Utils.AccountUtils;
import com.elseboot3909.GCRClient.Utils.Constants;
import com.elseboot3909.GCRClient.Utils.JsonUtils;
import com.elseboot3909.GCRClient.Utils.ServerDataManager;
import com.elseboot3909.GCRClient.databinding.ChangesPreviewListBinding;
import com.elseboot3909.GCRClient.databinding.FragmentStarredListBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Objects;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class StarredListFragment extends Fragment {

    FragmentStarredListBinding binding;
    private final ArrayList<ChangeInfo> changesList = new ArrayList<>();
    private ChangePreviewAdapter changePreviewAdapter;
    private GestureDetector mGestureDetector;
    private Integer starredCount;
    private boolean isLoading = false;

    private final AccountUtils.AccountUtilsCallback accountUtilsCallback = (accountInfo, binding) -> {
        ChangesPreviewListBinding listBinding = (ChangesPreviewListBinding) binding;
        listBinding.username.setText(accountInfo.getUsername());
        int listSize = accountInfo.getAvatars().size();
        if (listSize != 0) {
            AccountUtils.setAvatarDrawable(accountInfo.getAvatars().get(listSize - 1), listBinding.profilePic);
        }
    };

    private final ChangePreviewAdapter.AdapterCallback adapterCallback = (binding, accountId) -> {
        binding.profilePic.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), AccountUtils.getRandomAvatar(), null));
        binding.username.setText(AccountUtils.getRandomUsername());
        AccountUtils.loadProfileInfo(accountId, binding, accountUtilsCallback);
    };

    public StarredListFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStarredListBinding.inflate(inflater, container, false);

        binding.ctlMenu.setOnClickListener((view) -> {
            DrawerLayout serverNavDL = Objects.requireNonNull(getActivity()).findViewById(R.id.serverNavDL);
            serverNavDL.openDrawer(GravityCompat.START);
        });

        if (starredCount == null) {
            starredCount = 0;
            getChangesList();
        }
        updateTotalCount(starredCount);

        changePreviewAdapter = new ChangePreviewAdapter(changesList, adapterCallback);
        binding.changesView.setAdapter(changePreviewAdapter);
        binding.changesView.setLayoutManager(new LinearLayoutManager(getContext()));

        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        binding.changesView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (mGestureDetector.onTouchEvent(e) && child != null) {
                    ChangeInfo changeInfo = changesList.get(rv.getChildAdapterPosition(child));
                    Intent intent = new Intent(getActivity(), ChangeActivity.class);
                    intent.putExtra("id", changeInfo.getId());
                    intent.putExtra("subject", changeInfo.getSubject());
                    intent.putExtra("starred", changeInfo.getStarred());
                    intent.putExtra("status", changeInfo.getStatus());
                    intent.putExtra("work_in_progress", changeInfo.getWork_in_progress());
                    startActivity(intent);
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) { }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) { }
        });

        binding.refreshChangesView.setOnRefreshListener(() -> {
            int size = changesList.size();
            changesList.clear();
            changePreviewAdapter.notifyItemRangeRemoved(0, size);
            updateTotalCount(0);
            getChangesList();
            binding.refreshChangesView.setRefreshing(false);
        });

        return binding.getRoot();
    }

    private void getChangesList() {
        if (!isLoading) {
            isLoading = true;
        } else {
            return;
        }
        ((MainActivity) Objects.requireNonNull(getActivity())).progressBarManager(true);

        ServerData serverData = ServerDataManager.serverDataList.get(ServerDataManager.selectedPos);
        OkHttpClient client = ServerDataManager.getAuthenticatorClient(serverData.getUsername(), serverData.getPassword());

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(serverData.getServerName() + serverData.getServerNameEnding())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        AccountAPI accountAPI = retrofit.create(AccountAPI.class);
        Call<String> retrofitRequest = accountAPI.getStarredChanges();

        retrofitRequest.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Gson gson = new Gson();
                    changesList.addAll(gson.fromJson(JsonUtils.TrimJson(response.body()),
                            new TypeToken<ArrayList<ChangeInfo>>() {}.getType()));
                    Log.e(Constants.LOG_TAG, String.valueOf(changesList.size()));
                    updateTotalCount(changesList.size());
                    changePreviewAdapter.notifyItemRangeChanged(0, changesList.size());
                } else {
                    Log.e(Constants.LOG_TAG, "onResponse: Not successful");
                }
                isLoading = false;
                ((MainActivity) Objects.requireNonNull(getActivity())).progressBarManager(false);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                isLoading = false;
                ((MainActivity) Objects.requireNonNull(getActivity())).progressBarManager(false);
                Log.e(Constants.LOG_TAG, "onFailure: Not successful");
            }
        });
    }

    private void updateTotalCount(int count) {
        starredCount = count;
        binding.totalCount.setText(getString(R.string.total_starred_changes, starredCount));
    }

}