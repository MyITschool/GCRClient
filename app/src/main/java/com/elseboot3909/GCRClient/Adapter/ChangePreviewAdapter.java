package com.elseboot3909.GCRClient.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elseboot3909.GCRClient.Entities.ChangeInfo;
import com.elseboot3909.GCRClient.databinding.ChangesPreviewListBinding;

import java.util.ArrayList;

public class ChangePreviewAdapter extends RecyclerView.Adapter<ChangePreviewAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ChangesPreviewListBinding binding;

        public ViewHolder(ChangesPreviewListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private ArrayList<ChangeInfo> changesPreview;

    public ChangePreviewAdapter(ArrayList<ChangeInfo> changesPreview) {
        this.changesPreview = changesPreview;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChangesPreviewListBinding binding = ChangesPreviewListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChangeInfo changeInfo = changesPreview.get(position);
        holder.binding.subject.setText(changeInfo.getSubject());
        holder.binding.project.setText(changeInfo.getProject());
        holder.binding.branch.setText(changeInfo.getBranch());
    }

    @Override
    public int getItemCount() {
        return changesPreview.size();
    }

}
