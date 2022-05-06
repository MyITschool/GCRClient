package com.elseboot3909.GCRClient.Entities

import java.io.Serializable
import kotlin.collections.HashMap

data class ChangeInfo(
        val id: String = "",
        val project: String = "",
        val branch: String= "",
        val topic: String = "",
        val assignee: String = "",
        val subject: String = "",
        val status: String = "",
        val updated: String= "",
        val starred: Boolean = false,
        val insertions: Int = 0,
        val deletions: Int = 0,
        val owner: AccountInfo = AccountInfo(),
        val labels: HashMap<String, LabelInfo> = HashMap(),
        val reviewers: HashMap<String, ArrayList<AccountInfo>> = HashMap(),
        val work_in_progress: Boolean = false
) : Serializable