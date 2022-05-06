package com.elseboot3909.GCRClient.Entities

import java.io.Serializable

data class LabelPreview(
        val label: String = "",
        val votes: Int = 0
)  : Serializable
