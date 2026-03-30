package com.app.core.utils.validator;


import java.util.Map;

public class NumberValidator<T extends Number & Comparable<T>> extends Validator<T, NumberValidator<T>> {
    public static final String ERROR_TOO_LOW = "cant be less than %s";
    public static final String ERROR_TOO_HIGH = "cant be greater than %s";


    public NumberValidator(String key, T value) {
        super(key, value);
    }

    public NumberValidator<T> min(T min) {
        if (min != null) {
            addCheck(() ->
                    value.compareTo(min) < 0 ? Map.of(key, String.format(ERROR_TOO_LOW, min)) : null
            );
        }
        return this;
    }

    public NumberValidator<T> max(T max) {
        if (max != null) {
            addCheck(() ->
                    value.compareTo(max) > 0 ? Map.of(key, String.format(ERROR_TOO_HIGH, max)) : null
            );
        }
        return this;
    }

}