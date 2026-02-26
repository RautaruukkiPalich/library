package com.app.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.app.dto.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.app.exception.BookNotFoundException;
import com.app.exception.BookValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private boolean isSwaggerRequest(WebRequest request) {
        String path = request.getDescription(false);
        return path.contains("/v3/api-docs") ||
                path.contains("/swagger") ||
                path.contains("/swagger-ui");
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookNotFound(
            BookNotFoundException ex,
            WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BookValidationException.class)
    public ResponseEntity<ErrorResponse> handleBookValidation(
            BookValidationException ex,
            WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                "Invalid request parameters",
                request.getDescription(false).replace("uri=", ""));

        errorResponse.addValidationError(ex.getField(), ex.getErrorMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
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
                "Internal Server Error",
                "An unexpected error occurred",
                request.getDescription(false).replace("uri=", ""));

        logger.error("Unexpected error", ex);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}