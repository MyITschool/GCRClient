package com.elseboot3909.gcrclient.entity.external

import kotlinx.serialization.Serializable

@Serializable
data class CommentInfo(
    val id: String = "",
    val in_reply_to: String = "",
    val author: AccountInfo = AccountInfo(),
    val message: String = "",
    val updated: String= "",
    val context_lines: ArrayList<ContextLine> = ArrayList(),
    val range: CommentRange = CommentRange(),
    val line: Int = 0
)