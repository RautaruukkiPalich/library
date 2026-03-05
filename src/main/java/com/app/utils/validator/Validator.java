package com.app.utils.validator;

import java.util.Map;

public class Validator {

    private static final String ERROR_NULL = "cant be null";
    private static final String ERROR_BLANK = "cant be blank";
    private static final String ERROR_TOO_SHORT = "cant be shorter %d characters";
    private static final String ERROR_TOO_LONG = "cant be longer %d characters";
    private static final String ERROR_TOO_LOW = "cant be less then %d";
    private static final String ERROR_TOO_HIGH = "cant be greater then %d";


    public static Map<String, String> validateString(String key, String text, Integer minLength, Integer maxLength) {
        if (minLength != null && maxLength != null && minLength > maxLength){
            throw new IllegalArgumentException("min length cant be greater then max length");
        }

        if (text == null) {
            return Map.of(key, ERROR_NULL);
        }
        if (text.isBlank()) {
            return Map.of(key, ERROR_BLANK);
        }
        if (minLength != null && text.length() < minLength) {
            return Map.of(key, String.format(ERROR_TOO_SHORT, minLength));
        }
        if (maxLength != null && text.length() > maxLength) {
            return Map.of(key, String.format(ERROR_TOO_LONG, maxLength));
        }
        return null;
    }

    public static Map<String, String> validateLong(String key, Long val, Long min, Long max){
        if (min != null && max != null && min > max){
            throw new IllegalArgumentException("min value cant be greater then max value");
        }

        if (val == null) {
            return Map.of(key, ERROR_NULL);
        }
        if (min != null && val < min){
            return Map.of(key, String.format(ERROR_TOO_LOW, min));
        }
        if (max != null && val > max){
            return Map.of(key, String.format(ERROR_TOO_HIGH, max));
        }
        return null;
    }
}
