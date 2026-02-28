package com.app.handler;

import com.app.dto.ErrorResponse;
import com.app.exception.NotFoundException;
import com.app.exception.ValidationException;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private boolean isSwaggerRequest(WebRequest request) {
        String path = request.getDescription(false);
        return path.contains("/v3/api-docs") ||
                path.contains("/swagger") ||
                path.contains("/swagger-ui");
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            NotFoundException ex,
            WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "not found",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            ValidationException ex,
            WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "validation error",
                "invalid request parameters",
                request.getDescription(false).replace("uri=", ""));

        errorResponse.setValidationErrors(ex.getErrorsMap());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = ex.getBindingResult().
                getFieldErrors().
                stream().
                collect(Collectors.toMap(
                        field -> getJsonPropertyName(field, ex),
                        field -> Objects.toString(field.getDefaultMessage(), "argument not valid"),
                        (v1, v2) -> v1 + "; " + v2));

        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                "validation error",
                "invalid argument parameter",
                request.getDescription(false).replace("uri=", ""),
                errors
        );

        return handleExceptionInternal(ex, errorResponse, headers, status, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) throws Exception {

        if (isSwaggerRequest(request)) {
            throw ex;
        }

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "internal server error",
                "an unexpected error occurred",
                request.getDescription(false).replace("uri=", ""));

        logger.error("unexpected error", ex);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getJsonPropertyName(FieldError error, MethodArgumentNotValidException ex) {
        String errField = error.getField();

        return Optional.ofNullable(ex.getBindingResult().getTarget())
                .map(Object::getClass)
                .map(cls -> findField(cls, errField))
                .map(field -> field.getAnnotation(JsonProperty.class))
                .map(JsonProperty::value)
                .filter(value -> !value.isEmpty())
                .orElse(errField);
    }

    private Field findField(Class<?> cls, String fieldName){
        if (cls == null || cls == Object.class) {
            return null;
        }

        try {
            return cls.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return findField(cls.getSuperclass(), fieldName);
        }
    }
}