package com.elseboot3909.gcrclient.entity

data class ServerData(
    var username: String = "",
    var password: String = "",
    var serverURL: String = "",
) {
    override fun toString(): String = serverURL
}
