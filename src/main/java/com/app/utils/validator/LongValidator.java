package com.app.utils.validator;


import java.util.Map;

public class LongValidator extends Validator<Long, LongValidator> {
    public static final String ERROR_TOO_LOW = "cant be less then %d";
    public static final String ERROR_TOO_HIGH = "cant be greater then %d";


    public LongValidator(String key, Long value) {
        super(key, value);
    }

    public LongValidator min(Long min) {
        if (min != null) {
            addCheck(() ->
                    value < min ? Map.of(key, String.format(ERROR_TOO_LOW, min)) : null
            );
        }
        return this;
    }

    public LongValidator max(Long max) {
        if (max != null) {
            addCheck(() ->
                    value > max ? Map.of(key, String.format(ERROR_TOO_HIGH, max)) : null
            );
        }
        return this;
    }

}