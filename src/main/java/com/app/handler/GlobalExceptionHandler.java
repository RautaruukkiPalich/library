package com.app.handler;

import com.app.exception.*;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import com.app.dto.ErrorResponse;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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
        
        HashMap<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            
            // Пытаемся найти @JsonProperty аннотацию
            try {
                Field declaredField = ex.getParameter().getParameterType()
                    .getDeclaredField(field);
                JsonProperty annotation = declaredField.getAnnotation(JsonProperty.class);
                if (annotation != null) {
                    field = annotation.value();
                }
            } catch (NoSuchFieldException ignored) {

            }
        
            validationErrors.put(field, error.getDefaultMessage());
        }
        );
        
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                "validation error",
                "invalid request parameters",
                request.getDescription(false).replace("uri=", "")
        );

        errorResponse.setValidationErrors(validationErrors);
        
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
}