package com.elseboot3909.GCRClient.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elseboot3909.GCRClient.Entities.LabelPreview
import com.elseboot3909.GCRClient.R
import com.elseboot3909.GCRClient.databinding.LabelsListBinding
import java.util.*

class LabelListAdapter(private var labelsPreview: ArrayList<LabelPreview>) : RecyclerView.Adapter<LabelListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: LabelsListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LabelsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val labelPreview = labelsPreview[position]
        holder.binding.label.text = labelPreview.label
        val total = labelPreview.votes
        when {
            total < 0 -> {
                holder.binding.total.text = "-$total"
                holder.binding.statusPic.setImageResource(R.drawable.ic_no_votes)
            }
            total > 0 -> {
                holder.binding.total.text = "+$total"
                holder.binding.statusPic.setImageResource(R.drawable.ic_vote_good)
            }
            else -> {
                holder.binding.total.text = "No votes"
                holder.binding.statusPic.setImageResource(R.drawable.ic_no_votes)
            }
        }
    }

    override fun getItemCount(): Int = labelsPreview.size

}
