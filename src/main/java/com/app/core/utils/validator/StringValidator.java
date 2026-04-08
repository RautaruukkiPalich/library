package com.app.core.utils.validator;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

public class StringValidator extends Validator<String, StringValidator> {
    public static final String ERROR_BLANK = "must not be blank";
    public static final String ERROR_TOO_SHORT = "must not be shorter %d characters";
    public static final String ERROR_TOO_LONG = "must not be longer %d characters";


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

    public StringValidator match(Pattern pattern) {
        return match(pattern, "invalid pattern. expected '%s'".formatted(pattern));
    }

    public StringValidator match(Pattern pattern, String desc) {
        addCheck(() ->
                !pattern.matcher(value).matches() ?
                        Map.of(key, desc) :
                        null
        );
        return this;
    }

    public StringValidator contains(String substring) {
        return contains(substring, "must contains %s".formatted(substring));
    }

    public StringValidator contains(String substring, String desc) {
        addCheck(() ->
                !value.contains(substring) ?
                        Map.of(key, desc) :
                        null
        );
        return this;
    }

    public StringValidator in(String[] patterns) {
        return in(patterns, "not contains in available patterns");
    }

    public StringValidator in(String[] patterns, String desc) {
        addCheck(() ->
                !Arrays.asList(patterns).contains(value) ?
                        Map.of(key, desc) :
                        null
        );
        return this;
    }

    public StringValidator notIn(String[] patterns) {
        return notIn(patterns, "contains in not available patterns");
    }

    public StringValidator notIn(String[] patterns, String desc) {
        addCheck(() ->
                Arrays.asList(patterns).contains(value) ?
                        Map.of(key, desc) :
                        null
        );
        return this;
    }

    public StringValidator containsAny(String[] patterns) {
        return containsAny(patterns, "contains not available elements");
    }

    public StringValidator containsAny(String[] patterns, String desc) {
        addCheck(() ->
                 Arrays.stream(patterns).noneMatch(value::contains) ? Map.of(key, desc) : null
        );
        return this;
    }

    public StringValidator notContainsAny(String[] patterns) {
        return notContainsAny(patterns, "not contains available elements");
    }

    public StringValidator notContainsAny(String[] patterns, String desc) {
        addCheck(() ->
                Arrays.stream(patterns).anyMatch(value::contains) ? Map.of(key, desc) : null
        );
        return this;
    }
}
