package com.elseboot3909.gcrclient.utils

import kotlinx.serialization.json.Json

object JsonUtils {

    val json = Json { ignoreUnknownKeys = true }

    fun trimJson(json: String?): String {
        return json?.substring(json.indexOf('\n') + 1) ?: ""
    }

}
