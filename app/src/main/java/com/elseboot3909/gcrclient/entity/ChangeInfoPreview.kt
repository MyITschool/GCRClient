package com.elseboot3909.gcrclient.entity

import com.elseboot3909.gcrclient.ui.common.changedCountString
import com.elseboot3909.gcrclient.utils.AccountUtils
import com.elseboot3909.gcrclient.utils.DateUtils
import java.text.ParseException

data class ChangeInfoPreview(
    val showedName: String = "",
    val avatarUrl: String = "",
    val dummyAvatar: Int = 0,
    val subject: String = "",
    val project: String = "",
    val branch: String = "",
    val time: String = "",
    val insertions: String = "",
    val deletions: String = ""
)

fun convertInPreview(changeInfo: ChangeInfo): ChangeInfoPreview {
    return ChangeInfoPreview(
        showedName = AccountUtils.getShowedName(changeInfo.owner),
        avatarUrl = getAvatar(changeInfo.owner.avatars),
        dummyAvatar = AccountUtils.getRandomAvatar(),
        subject = changeInfo.subject,
        project = changeInfo.project,
        branch = changeInfo.branch,
        time = dateToString(changeInfo.updated),
        insertions = changedCountString(changeInfo.insertions),
        deletions = changedCountString(changeInfo.deletions)
    )
}

private fun getAvatar(avatars: ArrayList<AvatarInfo>): String {
    avatars.forEach {
        if (it.height >= 46 || it.width >= 46) {
            return it.url
        }
    }
    return if (avatars.size > 0) avatars[avatars.size - 1].url else ""
}

private fun dateToString(value: String): String {
    try {
        val date = DateUtils.dateInputFormat.parse(value.replace(".000000000", ""))
        if (date != null) {
            return when {
                DateUtils.monthOutputFormat.format(date) >= DateUtils.monthOutputFormat.format(
                    DateUtils.currentData
                ) -> {
                    DateUtils.clockOutputFormat.format(date)
                }
                DateUtils.yearOutputFormat.format(date) >= DateUtils.yearOutputFormat.format(
                    DateUtils.currentData
                ) -> {
                    DateUtils.monthOutputFormat.format(date)
                }
                else -> {
                    DateUtils.yearOutputFormat.format(date)
                }
            }
        }
    } catch (ignored: ParseException) {
    }
    return ""
}