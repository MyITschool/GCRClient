package com.elseboot3909.gcrclient.entity.external

import kotlinx.serialization.Serializable

@Serializable
data class ProjectInfo(
    val id: String = "",
    val description: String = "",
    val state: String = ""
)