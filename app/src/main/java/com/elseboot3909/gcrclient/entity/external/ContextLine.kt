package com.elseboot3909.gcrclient.entity.external

import kotlinx.serialization.Serializable

@Serializable
data class ContextLine(
    val line_number: Int = 0,
    val context_line: String = ""
)