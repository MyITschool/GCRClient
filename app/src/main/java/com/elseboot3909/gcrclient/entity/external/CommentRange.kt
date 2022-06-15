package com.elseboot3909.gcrclient.entity.external

import kotlinx.serialization.Serializable

@Serializable
data class CommentRange(
    val start_line: Int = 0,
    val end_line: Int = 0
)