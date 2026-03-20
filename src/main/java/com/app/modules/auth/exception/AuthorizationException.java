package com.app.modules.auth.exception;

import com.app.core.exception.AuthException;

public class AuthorizationException extends AuthException {

    public AuthorizationException(String reason) {
        super(String.format("Authorization failed: %s", reason));
    }

    public AuthorizationException(String reason, Throwable cause) {
        super(String.format("Authorization failed: %s", reason), cause);
    }

    public static AuthorizationException tokenExpired() {
        return new AuthorizationException("Token expired");
    }

    public static AuthorizationException invalidToken() {
        return new AuthorizationException("Invalid token");
    }

    public static AuthorizationException missingToken() {
        return new AuthorizationException("Missing authorization token");
    }
}