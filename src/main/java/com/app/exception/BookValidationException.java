package com.app.exception;

public class BookValidationException extends RuntimeException {
    private final String field;
    private final String message;
    
    public BookValidationException(String field, String message) {
        super(String.format("Validation error on field '%s': %s", field, message));
        this.field = field;
        this.message = message;
    }
    
    public String getField() { return field; }
    public String getErrorMessage() { return message; }
}