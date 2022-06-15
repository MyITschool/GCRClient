package com.elseboot3909.gcrclient.entity.internal

import com.elseboot3909.gcrclient.entity.external.AvatarInfo
import com.elseboot3909.gcrclient.entity.external.ChangeInfo
import com.elseboot3909.gcrclient.ui.common.changedCountString
import com.elseboot3909.gcrclient.utils.AccountUtils
import com.elseboot3909.gcrclient.utils.DateUtils
import java.text.ParseException

data class ChangeInfoPreview(
    val id: Int = 0,
    val showedName: String = "",
    val avatarUrl: String = "",
    val dummyAvatar: Int = 0,
    val subject: String = "",
    val project: String = "",
    val branch: String = "",
    val time: String = "",
    val insertions: String = "",
    val deletions: String = "",
    val changeInfo: ChangeInfo = ChangeInfo()
)

fun convertInPreview(changeInfo: ChangeInfo, index: Int): ChangeInfoPreview {
    return ChangeInfoPreview(
        id = index,
        showedName = AccountUtils.getShowedName(changeInfo.owner),
        avatarUrl = getAvatar(changeInfo.owner.avatars),
        dummyAvatar = AccountUtils.getAvatarById(changeInfo.owner._account_id),
        subject = changeInfo.subject,
        project = changeInfo.project,
        branch = changeInfo.branch,
        time = dateToString(changeInfo.updated),
        insertions = changedCountString(changeInfo.insertions),
        deletions = changedCountString(changeInfo.deletions),
        changeInfo = changeInfo
    )
}

fun getAvatar(avatars: ArrayList<AvatarInfo>): String {
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
                DateUtils.yearOutputFormat.format(date) < DateUtils.yearOutputFormat.format(DateUtils.currentData) -> {
                    DateUtils.yearOutputFormat.format(date)
                }
                DateUtils.monthOutputFormat.format(date) >= DateUtils.monthOutputFormat.format(
                    DateUtils.currentData
                ) -> {
                    DateUtils.clockOutputFormat.format(date)
                }
                else -> {
                    DateUtils.monthOutputFormat.format(date)
                }
            }
        }
    } catch (ignored: ParseException) {
    }
    return ""
}