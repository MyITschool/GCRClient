package com.elseboot3909.gcrclient.entity

import java.io.Serializable

data class LabelInfo(
    val all: ArrayList<ApprovalInfo> = ArrayList(),
    val rejected: AccountInfo = AccountInfo(),
    val approved: AccountInfo = AccountInfo(),
    val recommended: AccountInfo = AccountInfo(),
    val disliked: AccountInfo = AccountInfo()
) : Serializable
