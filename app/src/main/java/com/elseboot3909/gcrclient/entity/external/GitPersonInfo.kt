package com.elseboot3909.gcrclient.entity.external

import kotlinx.serialization.Serializable

@Serializable
data class GitPersonInfo(
    val name: String = "",
    val email: String = "",
    val date: String = "",
    val tz: Int = 0
)
