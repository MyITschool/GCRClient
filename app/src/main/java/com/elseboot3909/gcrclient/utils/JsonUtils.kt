package com.elseboot3909.gcrclient.utils

class JsonUtils {

    companion object {
        fun trimJson(json: String?): String {
            return json?.substring(json.indexOf('\n') + 1) ?: ""
        }
    }

}
