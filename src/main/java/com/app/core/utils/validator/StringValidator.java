package com.app.core.utils.validator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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

    private static final String IN_DEFAULT_MESSAGE = "not contains in available patterns";

    public StringValidator in(String[] patterns) {
        return in(patterns, IN_DEFAULT_MESSAGE);
    }

    public StringValidator in(String[] patterns, String desc) {
        return inBase(Arrays.stream(patterns), desc);
    }

    public StringValidator in(Collection<String> patterns) {
        return in(patterns, IN_DEFAULT_MESSAGE);
    }

    public StringValidator in(Collection<String> patterns, String desc) {
        return inBase(patterns.stream(), desc);
    }

    StringValidator inBase(Stream<String> stream, String desc) {
        addCheck(() ->
                stream.noneMatch(p -> p.contains(value)) ?
                        Map.of(key, desc) :
                        null
        );
        return this;
    }

    private static final String NOT_IN_DEFAULT_MESSAGE = "contains in not available patterns";

    public StringValidator notIn(String[] patterns) {
        return notIn(patterns, NOT_IN_DEFAULT_MESSAGE);
    }

    public StringValidator notIn(String[] patterns, String desc) {
        return notInBase(Arrays.stream(patterns), desc);
    }

    public StringValidator notIn(Collection<String> patterns) {
        return notIn(patterns, NOT_IN_DEFAULT_MESSAGE);
    }

    public StringValidator notIn(Collection<String> patterns, String desc) {
        return notInBase(patterns.stream(), desc);
    }

    StringValidator notInBase(Stream<String> stream, String desc) {
        addCheck(() ->
                stream.anyMatch(p -> p.contains(value)) ?
                        Map.of(key, desc) :
                        null
        );
        return this;
    }

    private static final String CONTAINS_ANY_DEFAULT_MESSAGE = "contains not available elements";

    public StringValidator containsAny(String[] patterns) {
        return containsAny(patterns, CONTAINS_ANY_DEFAULT_MESSAGE);
    }

    public StringValidator containsAny(String[] patterns, String desc) {
        return containsAnyBase(Arrays.stream(patterns), desc);
    }

    public StringValidator containsAny(Collection<String> patterns) {
        return containsAny(patterns, CONTAINS_ANY_DEFAULT_MESSAGE);
    }

    public StringValidator containsAny(Collection<String> patterns, String desc) {
        return containsAnyBase(patterns.stream(), desc);
    }

    StringValidator containsAnyBase(Stream<String> stream, String desc) {
        addCheck(() ->
                stream.noneMatch(value::contains) ? Map.of(key, desc) : null
        );
        return this;
    }

    private static final String NOT_CONTAINS_ANY_DEFAULT_MESSAGE = "not contains available elements";

    public StringValidator notContainsAny(String[] patterns) {
        return notContainsAny(patterns, NOT_CONTAINS_ANY_DEFAULT_MESSAGE);
    }

    public StringValidator notContainsAny(String[] patterns, String desc) {
        return notContainsAnyBase(Arrays.stream(patterns), desc);
    }

    public StringValidator notContainsAny(Collection<String> patterns) {
        return notContainsAny(patterns, NOT_CONTAINS_ANY_DEFAULT_MESSAGE);
    }

    public StringValidator notContainsAny(Collection<String> patterns, String desc) {
        return notContainsAnyBase(patterns.stream(), desc);
    }

    StringValidator notContainsAnyBase(Stream<String> stream, String desc) {
        addCheck(() ->
                stream.anyMatch(value::contains) ? Map.of(key, desc) : null
        );
        return this;
    }
}
