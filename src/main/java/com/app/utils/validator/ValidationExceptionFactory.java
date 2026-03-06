package com.app.utils.validator;

import com.app.exception.validation.ValidationException;

import java.util.Map;

@FunctionalInterface
public interface ValidationExceptionFactory {
    ValidationException create(Map<String, String> errors);
}
