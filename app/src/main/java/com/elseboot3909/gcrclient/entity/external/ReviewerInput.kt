package com.elseboot3909.gcrclient.entity.external

import kotlinx.serialization.Serializable

@Serializable
data class ReviewerInput(
    val reviewer: String = ""
)