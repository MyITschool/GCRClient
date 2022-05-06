package com.elseboot3909.GCRClient.UI.Change

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.elseboot3909.GCRClient.Adapter.LabelListAdapter
import com.elseboot3909.GCRClient.Entities.LabelPreview
import com.elseboot3909.GCRClient.ViewModel.ChangeViewModel
import com.elseboot3909.GCRClient.databinding.FragmentVoteBinding
import java.util.*

class VoteFragment : Fragment() {

    private lateinit var binding: FragmentVoteBinding

    private lateinit var id: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVoteBinding.inflate(inflater, container, false)

        id = arguments?.getString("id") ?: ""

        val model: ChangeViewModel by viewModels()

        (activity as ChangeActivity).progressBarManager(true)
        model.getChangeInfo(id).observe(activity as ChangeActivity, { changeInfo ->
            val labels = changeInfo.labels
            val labelsList = ArrayList(labels.keys)
            labelsList.sort()
            val labelsPreview = ArrayList<LabelPreview>()
            for (label in labelsList) {
                val approvalList = labels[label]?.all
                var ret = 0
                if (approvalList != null) {
                    for (approvalInfo in approvalList) {
                        ret += approvalInfo.value
                    }
                }
                labelsPreview.add(LabelPreview(label, ret))
            }

            val labelListAdapter = LabelListAdapter(labelsPreview)
            binding.labelsView.adapter = labelListAdapter
            binding.labelsView.layoutManager = LinearLayoutManager(context)
            binding.labelsView.isNestedScrollingEnabled = true

            binding.mainHidden.visibility = View.VISIBLE
            (activity as ChangeActivity).progressBarManager(false)
        })
        return binding.root
    }
}