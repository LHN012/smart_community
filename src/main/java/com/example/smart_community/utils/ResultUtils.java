package com.example.smart_community.utils;

import java.util.HashMap;
import java.util.Map;

public class ResultUtils {

    public static Map<String, Object> success(String msg) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", msg);
        return result;
    }

    public static Map<String, Object> error(String msg) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("msg", msg);
        return result;
    }

    public static Map<String, Object> data(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", data);
        return result;
    }
}    