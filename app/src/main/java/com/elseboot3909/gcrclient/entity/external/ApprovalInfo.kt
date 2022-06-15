package com.elseboot3909.gcrclient.entity.external

import kotlinx.serialization.Serializable

@Serializable
data class ApprovalInfo(
    var _account_id: Int = 0,
    var name: String = "",
    var email: String = "",
    var providerId: String = "",
    var username: String = "",
    var display_name: String = "",
    var avatars: ArrayList<AvatarInfo> = ArrayList(),
    var value: Int = 0
)
