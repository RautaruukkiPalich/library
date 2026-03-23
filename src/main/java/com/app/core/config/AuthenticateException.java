package com.app.core.config;

import com.app.core.exception.AuthException;

public class AuthenticateException extends AuthException {

    public AuthenticateException(String reason) {
        super(String.format("Authentication failed: %s", reason));
    }

    public AuthenticateException(String reason, Throwable cause) {
        super(String.format("Authentication failed: %s", reason), cause);
    }

    public static AuthenticateException authRequired() {
        return new AuthenticateException("Authentication required");
    }
}
