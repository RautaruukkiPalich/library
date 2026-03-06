package com.app.utils.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class Validator<T, V extends Validator<T, V>> {
    protected final String key;
    protected final T value;

    private static final String ERROR_NULL = "cant be null";

    private final List<Supplier<Map<String, String>>> checkFuncs = new ArrayList<>();

    public Validator(String key, T value) {
        this.key = Objects.requireNonNull(key, "key can't be null");
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    protected V self() {
        return (V) this;
    }

    public V notNull() {
        checkFuncs.add(() ->
                value == null ? Map.of(key, ERROR_NULL) : null
        );
        return self();
    }

    public Map<String, String> validate() {
        return checkFuncs.stream()
                .map(Supplier::get)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    protected void addCheck(Supplier<Map<String, String>> check) {
        checkFuncs.add(check);
    }
}

//    public static Map<String, String> validateString(String key, String text, Integer minLength, Integer maxLength) {
//        if (minLength != null && maxLength != null && minLength > maxLength) {
//            throw new IllegalArgumentException("min length cant be greater then max length");
//        }
//
//        if (text == null) {
//            return Map.of(key, ERROR_NULL);
//        }
//        if (text.isBlank()) {
//            return Map.of(key, ERROR_BLANK);
//        }
//        if (minLength != null && text.length() < minLength) {
//            return Map.of(key, String.format(ERROR_TOO_SHORT, minLength));
//        }
//        if (maxLength != null && text.length() > maxLength) {
//            return Map.of(key, String.format(ERROR_TOO_LONG, maxLength));
//        }
//        return null;
//    }

//    public static Map<String, String> validateLong(String key, Long val, Long min, Long max) {
//        if (min != null && max != null && min > max) {
//            throw new IllegalArgumentException("min value cant be greater then max value");
//        }
//
//        if (val == null) {
//            return Map.of(key, ERROR_NULL);
//        }
//        if (min != null && val < min) {
//            return Map.of(key, String.format(ERROR_TOO_LOW, min));
//        }
//        if (max != null && val > max) {
//            return Map.of(key, String.format(ERROR_TOO_HIGH, max));
//        }
//        return null;
//    }
//}
