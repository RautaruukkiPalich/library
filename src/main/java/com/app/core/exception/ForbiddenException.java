package com.app.core.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ForbiddenException insufficientPermissions() {
        return new ForbiddenException("Insufficient permissions");
    }
}
