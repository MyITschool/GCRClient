package com.elseboot3909.GCRClient.UI.Change;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.elseboot3909.GCRClient.Entities.AccountInfo;
import com.elseboot3909.GCRClient.Utils.AccountUtils;
import com.elseboot3909.GCRClient.ViewModel.ChangeViewModel;
import com.elseboot3909.GCRClient.databinding.FragmentChangeInfoBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class ChangeInfoFragment extends Fragment {

    private final DateFormat dateInputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private final DateFormat dateOutputFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.US);
    FragmentChangeInfoBinding binding;
    private String id;

    public ChangeInfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChangeInfoBinding.inflate(inflater, container, false);

        Activity activity = getActivity();
        id = getArguments().getString("id");

        ChangeViewModel model = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(ChangeViewModel.class);
        LifecycleOwner lifecycleOwner = getActivity();

        if (activity != null) ((ChangeActivity) activity).progressBarManager(true);
        model.getChangeInfo(id).observe(lifecycleOwner, changeInfo -> model.getCommitInfo(id).observe(lifecycleOwner, commitInfo -> {
                    try {
                        Date date = dateInputFormat.parse(changeInfo.getUpdated().replace(".000000000", ""));
                        if (date != null) binding.editTime.setText(dateOutputFormat.format(date));
                    } catch (ParseException ignored) {
                    }

                    /* Set owner params */
                    binding.chipOwner.infoChip.setText(changeInfo.getOwner().getName());
                    binding.chipOwner.infoChip.setChipIconResource(AccountUtils.getRandomAvatar());
                    if (changeInfo.getOwner().getAvatars() != null) {
                        int listSize = changeInfo.getOwner().getAvatars().size();
                        if (listSize != 0) {
                            AccountUtils.setAvatarDrawable(changeInfo.getOwner().getAvatars().get(listSize - 1), binding.chipOwner.infoChip);
                        }
                    }

                    binding.chipProject.infoChip.setText(changeInfo.getProject());

                    binding.chipBranch.infoChip.setText(changeInfo.getBranch());

                    /* Set topic params if exists */
                    String topic = changeInfo.getTopic();
                    if (topic != null && !topic.isEmpty()) {
                        binding.topicLayout.setVisibility(View.VISIBLE);
                        binding.chipTopic.infoChip.setText(topic);
                    }

                    /* Set reviewers params */
                    HashMap<String, ArrayList<AccountInfo>> reviewers = changeInfo.getReviewers();
                    ArrayList<AccountInfo> subArray;
                    if (reviewers != null) {
                        if (reviewers.containsKey("REVIEWER")) {
                            subArray = reviewers.get("REVIEWER");
                            if (subArray != null && subArray.size() > 0) {
                                binding.chipReviewer.infoChip.setText(subArray.get(0).getName());
                                binding.chipReviewer.infoChip.setChipIconResource(AccountUtils.getRandomAvatar());
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
                                binding.chipCC.infoChip.setChipIconResource(AccountUtils.getRandomAvatar());
                                if (subArray.get(0).getAvatars() != null) {
                                    int listSize = subArray.get(0).getAvatars().size();
                                    if (listSize != 0) {
                                        AccountUtils.setAvatarDrawable(subArray.get(0).getAvatars().get(listSize - 1), binding.chipCC.infoChip);
                                    }
                                }
                                binding.CCLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    /* Set message of commit */
                    String description = commitInfo.getMessage();
                    if (!description.isEmpty()) {
                        binding.description.setText(description);
                        binding.description.setVisibility(View.VISIBLE);
                    }

                    if (activity != null) ((ChangeActivity) activity).progressBarManager(false);
                    binding.hideFragment.setVisibility(View.VISIBLE);
                })
        );

        return binding.getRoot();
    }

}