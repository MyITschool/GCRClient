package com.elseboot3909.GCRClient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.elseboot3909.GCRClient.API.ChangesAPI;
import com.elseboot3909.GCRClient.Adapter.ChangePreviewAdapter;
import com.elseboot3909.GCRClient.Entities.ChangeInfo;
import com.elseboot3909.GCRClient.Entities.ServerData;
import com.elseboot3909.GCRClient.Utils.Constants;
import com.elseboot3909.GCRClient.Utils.JsonUtils;
import com.elseboot3909.GCRClient.Utils.ServerDataManager;
import com.elseboot3909.GCRClient.databinding.FragmentChangesListBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ChangesListFragment extends Fragment {

    FragmentChangesListBinding binding;
    ArrayList<ChangeInfo> changesList = new ArrayList<>();
    ChangePreviewAdapter changePreviewAdapter;
    Integer offset;

    public ChangesListFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        offset = 0;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentChangesListBinding.inflate(inflater, container, false);

        binding.progressBar.setVisibility(View.VISIBLE);

        binding.ctlMenu.setOnClickListener((view) -> {
            DrawerLayout serverNavDL = getActivity().findViewById(R.id.serverNavDL);
            serverNavDL.openDrawer(GravityCompat.START);
        });

        binding.searchBar.setOnClickListener(view -> startActivity(new Intent(getActivity(), SearchActivity.class)));

        getChangesList(offset, ServerDataManager.serverDataList.get(ServerDataManager.selectedPos));

        Log.e(Constants.LOG_TAG, String.valueOf(changesList.size()));

        changePreviewAdapter = new ChangePreviewAdapter(changesList);
        binding.changesView.setAdapter(changePreviewAdapter);
        binding.changesView.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.changesView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)){
                    binding.progressBar.setVisibility(View.VISIBLE);
                    offset += 20;
                    getChangesList(offset, ServerDataManager.serverDataList.get(ServerDataManager.selectedPos));
                }
            }
        });

        binding.refreshChangesView.setOnRefreshListener(() -> {
            offset = 0;
            int size = changesList.size();
            changesList.clear();
            changePreviewAdapter.notifyItemRangeRemoved(offset, size);
            binding.refreshChangesView.setRefreshing(false);
        });

        return binding.getRoot();
    }

    private void getChangesList(Integer offset, ServerData serverData) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverData.getServerName() + serverData.getServerNameEnding())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        ChangesAPI changesAPI = retrofit.create(ChangesAPI.class);
        Call<String> retrofitRequest = changesAPI.queryChanges("status:open", 20, offset, serverData.getAccessToken());

        retrofitRequest.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    changesList.addAll(gson.fromJson(JsonUtils.TrimJson(response.body()),
                            new TypeToken<ArrayList<ChangeInfo>>() {}.getType()));
                    Log.e(Constants.LOG_TAG, String.valueOf(changesList.size()));
                    changePreviewAdapter.notifyItemRangeChanged(offset, 20);
                } else {
                    Log.e(Constants.LOG_TAG, "onResponse: Not successful");
                }
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Log.e(Constants.LOG_TAG, "onFailure: Not successful");
            }
        });
    }

}