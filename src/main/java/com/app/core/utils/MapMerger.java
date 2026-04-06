package com.app.core.utils;

import lombok.NonNull;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class MapMerger<K, V> {
    private BinaryOperator<V> mergeFunc = (v1, v2) -> v2; //default use last value

    public MapMerger() {
    }

    public MapMerger<K, V> withMergeFunc(@NonNull BinaryOperator<V> func) {
        this.mergeFunc = func;
        return this;
    }

    public Map<K, V> merge(Map<K, V>[] maps) {
        if (maps == null) {
            return Map.of();
        }

        return Arrays.stream(maps)
                .filter(Objects::nonNull)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        mergeFunc,
                        HashMap::new
                ));
    }

    public Map<K, V> merge(List<Map<K, V>> maps) {
        if (maps == null) {
            return Map.of();
        }

        return maps.stream()
                .filter(Objects::nonNull)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        mergeFunc,
                        HashMap::new
                ));
    }
}
