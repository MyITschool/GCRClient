package com.elseboot3909.gcrclient.entity.external

import kotlinx.serialization.Serializable

@Serializable
data class AccountInfo(
    val _account_id: Int = 0,
    val name: String = "",
    val email: String = "",
    val providerId: String = "",
    val username: String = "",
    val display_name: String = "",
    val avatars: ArrayList<AvatarInfo> = ArrayList()
)
