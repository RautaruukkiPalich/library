package com.app.core.utils.validator;

import com.app.core.exception.ValidationException;

import java.util.Map;

@FunctionalInterface
public interface ValidationExceptionFactory {
    ValidationException create(Map<String, String> errors);
}
