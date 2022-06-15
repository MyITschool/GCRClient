package com.elseboot3909.gcrclient.entity.external

import kotlinx.serialization.Serializable

@Serializable
data class LabelInfo(
    val all: ArrayList<ApprovalInfo> = ArrayList(),
    val rejected: AccountInfo = AccountInfo(),
    val approved: AccountInfo = AccountInfo(),
    val recommended: AccountInfo = AccountInfo(),
    val disliked: AccountInfo = AccountInfo(),
    val values: HashMap<String, String> = HashMap()
)
