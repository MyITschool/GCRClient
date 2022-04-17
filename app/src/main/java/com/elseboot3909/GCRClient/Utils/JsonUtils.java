package com.elseboot3909.GCRClient.Utils;

public class JsonUtils {

    public static String TrimJson(String json) {
        return json.substring(json.indexOf('\n') + 1);
    }

}
