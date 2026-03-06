package com.app.utils.map;

import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MapUtils<K, V> {

    public enum MatchStrategy {
        ALL_MATCH {
            @Override
            <T> boolean match(Stream<T> stream, Predicate<T> predicate) {
                return stream.allMatch(predicate);
            }
        },
        ANY_MATCH {
            @Override
            <T> boolean match(Stream<T> stream, Predicate<T> predicate) {
                return stream.anyMatch(predicate);
            }
        },
        NONE_MATCH {
            @Override
            <T> boolean match(Stream<T> stream, Predicate<T> predicate) {
                return stream.noneMatch(predicate);
            }
        };

        abstract <T> boolean match(Stream<T> stream, Predicate<T> predicate);
    }

    public static <K, V> boolean anyMatch(Map<K, V> actual, Map<K, V> expected, Comparator<V> comparator) {
        return match(actual, expected, MatchStrategy.ANY_MATCH, comparator);
    }

    public static <K, V> boolean anyMatch(Map<K, V> actual, Map<K, V> expected) {
        return anyMatch(actual, expected, defaultComparator());
    }

    public static <K, V> boolean allMatch(Map<K, V> actual, Map<K, V> expected, Comparator<V> comparator) {
        return match(actual, expected, MatchStrategy.ALL_MATCH, comparator);
    }

    public static <K, V> boolean allMatch(Map<K, V> actual, Map<K, V> expected) {
        return allMatch(actual, expected, defaultComparator());
    }

    public static <K, V> boolean match(Map<K, V> actual, Map<K, V> expected, MatchStrategy strategy, Comparator<V> comparator) {
        return strategy.match(
                expected.entrySet().stream(),
                entry -> comparator.compare(entry.getValue(), actual.get(entry.getKey()))
        );
    }

    private static <V> Comparator<V> defaultComparator() {
        return (exp, act) -> {
            if (exp == act) return true;
            if (exp == null || act == null) return false;
            return exp.equals(act);
        };
    }
}
