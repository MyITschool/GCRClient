package com.elseboot3909.GCRClient.UI.Main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elseboot3909.GCRClient.API.ChangesAPI;
import com.elseboot3909.GCRClient.Adapter.ChangePreviewAdapter;
import com.elseboot3909.GCRClient.Entities.ChangeInfo;
import com.elseboot3909.GCRClient.R;
import com.elseboot3909.GCRClient.UI.Change.ChangeActivity;
import com.elseboot3909.GCRClient.UI.Search.SearchActivity;
import com.elseboot3909.GCRClient.Utils.Constants;
import com.elseboot3909.GCRClient.Utils.JsonUtils;
import com.elseboot3909.GCRClient.Utils.NetManager;
import com.elseboot3909.GCRClient.databinding.FragmentChangesListBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChangesListFragment extends Fragment {

    FragmentChangesListBinding binding;
    private final ArrayList<ChangeInfo> changesList = new ArrayList<>();
    private ArrayList<String> queryParams = new ArrayList<>();
    private ChangePreviewAdapter changePreviewAdapter;
    private GestureDetector mGestureDetector;
    private boolean isFrozen = false;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Constants.SEARCH_ACQUIRED) {
                    queryParams.clear();
                    String search = result.getData().getStringExtra("search_string");
                    if (!search.isEmpty()) {
                        queryParams.add(search);
                        binding.searchBar.setText(search);
                    } else {
                        queryParams.add("status:open");
                        binding.searchBar.setText("");
                    }
                    int oldSize = changesList.size();
                    changesList.clear();
                    changePreviewAdapter.notifyItemRangeRemoved(0, oldSize);
                }
            }
    );

    public ChangesListFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentChangesListBinding.inflate(inflater, container, false);

        binding.ctlMenu.setOnClickListener((view) -> {
            DrawerLayout serverNavDL = Objects.requireNonNull(getActivity()).findViewById(R.id.serverNavDL);
            serverNavDL.openDrawer(GravityCompat.START);
        });

        if (queryParams.isEmpty()) {
            queryParams.add("status:open");
        }
        binding.searchBar.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            intent.putExtra("search_string", binding.searchBar.getText().toString().trim());
            activityResultLauncher.launch(intent);
        });

        if (changesList.isEmpty()) {
            getChangesList();
        }

        changePreviewAdapter = new ChangePreviewAdapter(changesList, this);
        binding.changesView.setAdapter(changePreviewAdapter);
        binding.changesView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.changesView.setNestedScrollingEnabled(true);

        binding.changesView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)){
                    getChangesList();
                }
            }
        });

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
                return isFrozen;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) { }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) { }
        });

        binding.refreshChangesView.setOnRefreshListener(() -> {
            int oldSize = changesList.size();
            changesList.clear();
            changePreviewAdapter.notifyItemRangeRemoved(0, oldSize);
            binding.refreshChangesView.setRefreshing(false);
        });

        return binding.getRoot();
    }

    private void getChangesList() {
        ((MainActivity) Objects.requireNonNull(getActivity())).progressBarManager(true);
        isFrozen = true;

        int oldSize = changesList.size();

        Retrofit retrofit = NetManager.getRetrofitConfiguration(null, true);

        ChangesAPI changesAPI = retrofit.create(ChangesAPI.class);
        Call<String> retrofitRequest = changesAPI.queryChanges(queryParams, 20, oldSize);

        retrofitRequest.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Gson gson = new Gson();
                    changesList.addAll(gson.fromJson(JsonUtils.TrimJson(response.body()),
                            new TypeToken<ArrayList<ChangeInfo>>() {
                    }.getType()));
                    Log.e(Constants.LOG_TAG, String.valueOf(changesList.size()));
                    changePreviewAdapter.notifyItemRangeChanged(oldSize, 20);
                } else {
                    Log.e(Constants.LOG_TAG, "onResponse: Not successful");
                }
                ((MainActivity) Objects.requireNonNull(getActivity())).progressBarManager(false);
                isFrozen = false;
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                ((MainActivity) Objects.requireNonNull(getActivity())).progressBarManager(false);
                isFrozen = false;
                Log.e(Constants.LOG_TAG, "onFailure: Not successful");
            }
        });
    }

}