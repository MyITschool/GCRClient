package com.elseboot3909.GCRClient.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.elseboot3909.GCRClient.Entities.ChangeInfo
import com.elseboot3909.GCRClient.UI.Main.MainActivity
import com.elseboot3909.GCRClient.Utils.AccountUtils
import com.elseboot3909.GCRClient.ViewModel.AccountViewModel
import com.elseboot3909.GCRClient.databinding.ChangesPreviewListBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ChangePreviewAdapter(private val changesPreview: ArrayList<ChangeInfo>, private val activity: Activity) : RecyclerView.Adapter<ChangePreviewAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ChangesPreviewListBinding) : RecyclerView.ViewHolder(binding.root)

    private val model: AccountViewModel by (activity as MainActivity).viewModels()

    private val dateInputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    private val clockOutputFormat = SimpleDateFormat("HH:mm", Locale.US)
    private val monthOutputFormat = SimpleDateFormat("dd-MM", Locale.US)
    private val yearOutputFormat = SimpleDateFormat("yyyy", Locale.US)
    private val currentData = Calendar.getInstance().time

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChangesPreviewListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val changeInfo = changesPreview[position]
            try {
                val date = dateInputFormat.parse(changeInfo.updated.replace(".000000000", ""))
                if (date != null) {
                    when {
                        monthOutputFormat.format(date) >= monthOutputFormat.format(currentData) -> {
                            binding.time.text = clockOutputFormat.format(date)
                        }
                        yearOutputFormat.format(date) >= yearOutputFormat.format(currentData) -> {
                            binding.time.text = monthOutputFormat.format(date)
                        }
                        else -> {
                            binding.time.text = yearOutputFormat.format(date)
                        }
                    }
                }
            } catch (ignored: ParseException) {
            }
            binding.subject.text = changeInfo.subject
            binding.project.text = changeInfo.project
            binding.branch.text = changeInfo.branch
            binding.insertions.text = changedCountString(changeInfo.insertions)
            binding.deletions.text = changedCountString(changeInfo.deletions)
            binding.profilePic.setImageResource(AccountUtils.getRandomAvatar())
            binding.username.text = AccountUtils.getRandomUsername()
            model.getAccountInfo(changeInfo.owner._account_id.toString())?.observe((activity as MainActivity), { accountInfo ->
                val size = accountInfo.avatars.size
                if (size > 0) AccountUtils.setAvatarDrawable(accountInfo.avatars[size - 1], binding.profilePic)
                binding.username.text = accountInfo.username
            })
        }
    }

    override fun getItemCount(): Int {
        return changesPreview.size
    }

    private fun changedCountString(value: Int): String {
        return if (value > 999) "999+" else value.toString()
    }

}
