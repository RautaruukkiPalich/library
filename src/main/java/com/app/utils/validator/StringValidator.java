package com.app.utils.validator;

import java.util.Map;

public class StringValidator extends Validator<String, StringValidator> {
    private static final String ERROR_BLANK = "cant be blank";
    private static final String ERROR_TOO_SHORT = "cant be shorter %d characters";
    private static final String ERROR_TOO_LONG = "cant be longer %d characters";


    public StringValidator(String key, String value) {
        super(key, value);
    }

    public StringValidator notBlank() {
        addCheck(() ->
                value.isBlank() ? Map.of(key, ERROR_BLANK) : null
        );
        return this;
    }

    public StringValidator minLength(int minLength) {
        addCheck(() ->
                value.length() < minLength ?
                        Map.of(key, String.format(ERROR_TOO_SHORT, minLength)) :
                        null
        );
        return this;
    }

    public StringValidator maxLength(int maxLength) {
        addCheck(() ->
                value.length() > maxLength ?
                        Map.of(key, String.format(ERROR_TOO_LONG, maxLength)) :
                        null
        );
        return this;
    }
}
