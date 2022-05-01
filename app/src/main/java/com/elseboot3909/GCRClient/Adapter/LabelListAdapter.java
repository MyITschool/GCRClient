package com.elseboot3909.GCRClient.Adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elseboot3909.GCRClient.databinding.LabelsListBinding;

public class LabelListAdapter extends RecyclerView.Adapter<LabelListAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final LabelsListBinding binding;

        public ViewHolder(LabelsListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public LabelListAdapter() {

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }



}
