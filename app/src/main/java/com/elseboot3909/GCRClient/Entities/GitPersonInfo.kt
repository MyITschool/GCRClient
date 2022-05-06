package com.elseboot3909.GCRClient.Entities

import java.io.Serializable

data class GitPersonInfo(
        val name: String = "",
        val email: String = "",
        val date: String = "",
        val tz: String = ""
)  : Serializable
