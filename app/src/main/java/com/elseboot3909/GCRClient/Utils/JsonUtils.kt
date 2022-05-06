package com.elseboot3909.GCRClient.Utils

class JsonUtils {

    companion object {
        fun trimJson(json: String?) : String {
            return json?.substring(json.indexOf('\n') + 1) ?: ""
        }
    }

}
