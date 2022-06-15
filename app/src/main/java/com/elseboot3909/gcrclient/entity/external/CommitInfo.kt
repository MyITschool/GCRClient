package com.elseboot3909.gcrclient.entity.external

import kotlinx.serialization.Serializable

@Serializable
data class CommitInfo(
    val commit: String = "",
    val author: GitPersonInfo = GitPersonInfo(),
    val committer: GitPersonInfo = GitPersonInfo(),
    val subject: String = "",
    val message: String = ""
)
