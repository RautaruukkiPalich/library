package com.app.utils.MapUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MapUtils {

    @SafeVarargs
    public static Map<String, String> merge(Map<String, String>... maps) {
        return Arrays.stream(maps)
                .filter(Objects::nonNull)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> v1 + "; " + v2
                ));
    }

}
