package com.app.utils;

import java.util.Map;

public class MapUtils {
    public static boolean anyMatch(Map<String, String> actual, Map<String, String> expected) {
        return expected.entrySet().stream().anyMatch(
                entry -> entry.getValue().equals(actual.get(entry.getKey()))
        );
    }

    public static boolean allMatch(Map<String, String> actual, Map<String, String> expected) {
        return expected.entrySet().stream().allMatch(
                entry -> entry.getValue().equals(actual.get(entry.getKey()))
        );
    }
}
