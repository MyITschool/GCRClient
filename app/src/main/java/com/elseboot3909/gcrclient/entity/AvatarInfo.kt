package com.elseboot3909.gcrclient.entity

import java.io.Serializable

data class AvatarInfo(
    var url: String = "",
    var height: Int = 0,
    var width: Int = 0
) : Serializable
