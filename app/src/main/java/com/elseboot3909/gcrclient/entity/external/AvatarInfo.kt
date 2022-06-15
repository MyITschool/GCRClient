package com.elseboot3909.gcrclient.entity.external

import kotlinx.serialization.Serializable

@Serializable

data class AvatarInfo(
    var url: String = "",
    var height: Int = 0,
    var width: Int = 0
)
