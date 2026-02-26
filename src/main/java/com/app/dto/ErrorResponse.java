package com.app.dto;

import java.time.LocalDateTime;
import java.util.HashMap;

public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private HashMap<String, String> validationErrors;
    
    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public HashMap<String, String> getValidationErrors() { return validationErrors; }
    
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setStatus(int status) { this.status = status; }
    public void setError(String error) { this.error = error; }
    public void setMessage(String message) { this.message = message; }
    public void setPath(String path) { this.path = path; }
    public void setValidationErrors(HashMap<String, String> validationErrors) { 
        this.validationErrors = validationErrors; 
    }
    
    public void addValidationError(String field, String message) {
        if (validationErrors == null) {
            validationErrors = new HashMap<>();
        }
        validationErrors.put(field, message);
    }
}