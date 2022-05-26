package com.elseboot3909.gcrclient.entity

import java.io.Serializable

data class ChangeInfo(
    val id: String = "",
    val project: String = "",
    val branch: String = "",
    val topic: String = "",
    val assignee: String = "",
    val subject: String = "",
    val status: String = "",
    val updated: String = "",
    val starred: Boolean = false,
    val insertions: Int = 0,
    val deletions: Int = 0,
    val owner: AccountInfo = AccountInfo(),
    val labels: HashMap<String, LabelInfo> = HashMap(),
    val reviewers: HashMap<String, ArrayList<AccountInfo>> = HashMap(),
    val work_in_progress: Boolean = false,
    val current_revision: String = "",
    val revisions: HashMap<String, RevisionInfo> = HashMap()
) : Serializable