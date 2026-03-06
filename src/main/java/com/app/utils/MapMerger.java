package com.app.utils;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class MapMerger<K, V> {
    private final Map<K, V>[] maps;
    private BinaryOperator<V> mergeFunc = (v1, v2) -> v2; //default use last value

    @SafeVarargs
    public MapMerger(Map<K, V>... maps) {
        this.maps = maps;
    }

    public MapMerger<K, V> withMergeFunc(BinaryOperator<V> func) {
        this.mergeFunc = Objects.requireNonNull(func, "merge func must not be null");
        return this;
    }

    public final Map<K, V> merge() {
        return Arrays.stream(this.maps)
                .filter(Objects::nonNull)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        mergeFunc
                ));

    }
}
