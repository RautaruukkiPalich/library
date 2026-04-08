package com.app.core.utils.validator;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Validator<T, V extends Validator<T, V>> {
    protected final String key;
    protected final T value;

    public static final String ERROR_NULL = "must not be null";

    private final List<Supplier<Map<String, String>>> checkFuncs = new ArrayList<>();

    public Validator(@NonNull String key, T value) {
        this.key = key;
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

    public V custom(Predicate<T> predicate, String desc) {
        checkFuncs.add(() ->
                value != null && !predicate.test(value) ? Map.of(key, desc) : null
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
