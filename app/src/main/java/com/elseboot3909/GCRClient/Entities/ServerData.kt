package com.elseboot3909.GCRClient.Entities

data class ServerData(
        var username: String = "",
        var password: String = "",
        var serverURL: String = "",
        var prefixURL: String = ""
) {
    override fun toString(): String = serverURL
}
