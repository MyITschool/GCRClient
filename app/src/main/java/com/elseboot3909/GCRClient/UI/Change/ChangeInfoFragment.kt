package com.elseboot3909.GCRClient.UI.Change

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.elseboot3909.GCRClient.Utils.AccountUtils
import com.elseboot3909.GCRClient.ViewModel.ChangeViewModel
import com.elseboot3909.GCRClient.databinding.FragmentChangeInfoBinding
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ChangeInfoFragment : Fragment() {

    private lateinit var binding: FragmentChangeInfoBinding

    private val dateInputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    private val dateOutputFormat: DateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.US)
    private lateinit var id: String
    private var listSize = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentChangeInfoBinding.inflate(inflater, container, false)

        id = arguments?.getString("id") ?: ""
        val model: ChangeViewModel by viewModels()

        (activity as ChangeActivity).progressBarManager(true)
        model.getChangeInfo(id).observe(activity as ChangeActivity, { changeInfo ->
            model.getCommitInfo(id).observe(activity as ChangeActivity, { commitInfo ->
                try {
                    val date = dateInputFormat.parse(changeInfo.updated.replace(".000000000", ""))
                    if (date != null) binding.editTime.text = dateOutputFormat.format(date)
                } catch (ignored: ParseException) {
                }

                /* Set owner params */
                binding.chipOwner.infoChip.text = changeInfo.owner.name
                binding.chipOwner.infoChip.setChipIconResource(AccountUtils.getRandomAvatar())
                listSize = changeInfo.owner.avatars.size
                if (listSize != 0) {
                    AccountUtils.setAvatarDrawable(changeInfo.owner.avatars[listSize - 1], binding.chipOwner.infoChip)
                }

                binding.chipProject.infoChip.text = changeInfo.project

                binding.chipBranch.infoChip.text = changeInfo.branch

                /* Set topic params if exists */
                val topic = changeInfo.topic
                if (topic.isNotEmpty()) {
                    binding.topicLayout.visibility = View.VISIBLE
                    binding.chipTopic.infoChip.text = topic
                }

                /* Set reviewers params */
                val reviewers = changeInfo.reviewers
                if (reviewers.containsKey("REVIEWER")) {
                    val subArray = reviewers["REVIEWER"]
                    if (subArray != null && subArray.size > 0) {
                        binding.chipReviewer.infoChip.text = subArray[0].name
                        binding.chipReviewer.infoChip.setChipIconResource(AccountUtils.getRandomAvatar())
                        listSize = subArray[0].avatars.size
                        if (listSize != 0) {
                            AccountUtils.setAvatarDrawable(subArray[0].avatars[listSize - 1], binding.chipReviewer.infoChip)
                        }
                        binding.reviewerLayout.visibility = View.VISIBLE
                    }
                }
                if (reviewers.containsKey("CC")) {
                    val subArray = reviewers["CC"]
                    if (subArray != null && subArray.size > 0) {
                        binding.chipCC.infoChip.text = subArray[0].name
                        binding.chipCC.infoChip.setChipIconResource(AccountUtils.getRandomAvatar())
                        listSize = subArray[0].avatars.size
                        if (listSize != 0) {
                            AccountUtils.setAvatarDrawable(subArray[0].avatars[listSize - 1], binding.chipCC.infoChip)
                        }
                        binding.CCLayout.visibility = View.VISIBLE
                    }
                }

                /* Set message of commit */
                val description = commitInfo.message
                if (description.isNotEmpty()) {
                    binding.description.text = description
                    binding.description.visibility = View.VISIBLE
                }

                (activity as ChangeActivity).progressBarManager(false)
                binding.hideFragment.visibility = View.VISIBLE
            })
        })
        return binding.root
    }
}