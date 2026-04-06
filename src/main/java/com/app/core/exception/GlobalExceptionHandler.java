package com.app.core.exception;

import com.app.core.response.ErrorResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private boolean isSwaggerRequest(WebRequest request) {
        String path = request.getDescription(false);
        return path.contains("/v3/api-docs") ||
                path.contains("/swagger") ||
                path.contains("/swagger-ui");
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuth(
            AuthException ex,
            WebRequest request) {

        log.info("Auth: {}", ex.getMessage());

        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
            ForbiddenException ex,
            WebRequest request) {

        log.info("Forbidden: {}", ex.getMessage());

        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            NotFoundException ex,
            WebRequest request) {

        log.info("Not found: {}", ex.getMessage());

        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(
            DuplicateException ex,
            WebRequest request) {

        log.info("Duplicate: {}", ex.getMessage());

        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            ValidationException ex,
            WebRequest request) {

        log.info("Validation: {}", ex.getMessage());

        return buildResponseWithDetails(HttpStatus.BAD_REQUEST, ex.getMessage(), request, ex.getErrorsMap());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) throws Exception {

        if (isSwaggerRequest(request)) {
            throw ex;
        }

        log.info("unexpected error", ex);

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "an unexpected error occurred", request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            HttpStatusCode status,
            @NonNull WebRequest request) {

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
                getPath(request),
                errors
        );

        return handleExceptionInternal(ex, errorResponse, headers, status, request);
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

    private Field findField(Class<?> cls, String fieldName) {
        if (cls == null || cls == Object.class) {
            return null;
        }

        try {
            return cls.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return findField(cls.getSuperclass(), fieldName);
        }
    }

    private String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status, String message, WebRequest request) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(
                        status.value(),
                        status.getReasonPhrase(),
                        message,
                        getPath(request)));
    }

    private ResponseEntity<ErrorResponse> buildResponseWithDetails(
            HttpStatus status, String message, WebRequest request, Map<String, String> details) {
        return buildResponseWithDetails(status, message, status.getReasonPhrase(), request, details);
    }

    private ResponseEntity<ErrorResponse> buildResponseWithDetails(
            HttpStatus status, String message, String reason, WebRequest request, Map<String, String> details) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(
                        status.value(),
                        reason,
                        message,
                        getPath(request),
                        details));
    }
}