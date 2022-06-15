package com.elseboot3909.gcrclient.entity.external

import kotlinx.serialization.Serializable

@Serializable
data class RevisionInfo(
    val _number: Int = 0,
    val commit: CommitInfo = CommitInfo(),
)