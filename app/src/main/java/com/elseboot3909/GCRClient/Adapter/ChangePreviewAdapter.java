package com.elseboot3909.GCRClient.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elseboot3909.GCRClient.Entities.ChangeInfo;
import com.elseboot3909.GCRClient.databinding.ChangesPreviewListBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ChangePreviewAdapter extends RecyclerView.Adapter<ChangePreviewAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ChangesPreviewListBinding binding;

        public ViewHolder(ChangesPreviewListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private final ArrayList<ChangeInfo> changesPreview;
    private final AdapterCallback callback;

    private final DateFormat dateInputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private final DateFormat clockOutputFormat = new SimpleDateFormat("HH:mm", Locale.US);
    private final DateFormat monthOutputFormat = new SimpleDateFormat("dd-MM", Locale.US);
    private final DateFormat yearOutputFormat = new SimpleDateFormat("yyyy", Locale.US);
    private final Date currentData = Calendar.getInstance().getTime();

    public ChangePreviewAdapter(ArrayList<ChangeInfo> changesPreview, AdapterCallback callback) {
        this.changesPreview = changesPreview;
        this.callback = callback;
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
        try {
            Date date = dateInputFormat.parse(changeInfo.getUpdated().replace(".000000000", ""));
            if (monthOutputFormat.format(date).compareTo(monthOutputFormat.format(currentData)) >= 0) {
                holder.binding.time.setText(clockOutputFormat.format(date));
            } else if (yearOutputFormat.format(date).compareTo(yearOutputFormat.format(currentData)) >= 0) {
                holder.binding.time.setText(monthOutputFormat.format(date));
            } else {
                holder.binding.time.setText(yearOutputFormat.format(date));
            }
        } catch (ParseException ignored) { }
        holder.binding.subject.setText(changeInfo.getSubject());
        holder.binding.project.setText(changeInfo.getProject());
        holder.binding.branch.setText(changeInfo.getBranch());
        holder.binding.insertions.setText(changedCountString(changeInfo.getInsertions()));
        holder.binding.deletions.setText(changedCountString(changeInfo.getDeletions()));
        callback.setProfileInfoCallback(holder.binding, changeInfo.getOwner().get_account_id());
    }

    @Override
    public int getItemCount() {
        return changesPreview.size();
    }

    private String changedCountString(Integer value) {
        if (value > 999) return "999+";
        else return String.valueOf(value);
    }

    public interface AdapterCallback {
        void setProfileInfoCallback(ChangesPreviewListBinding binding, Integer accountId);
    }

}