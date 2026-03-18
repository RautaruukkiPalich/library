package com.app.exception.auth;

public class AuthenticateException extends AuthException{

    public AuthenticateException(String reason) {
        super(String.format("Authentication failed: %s", reason));
    }

    public AuthenticateException(String reason, Throwable cause) {
        super(String.format("Authentication failed: %s", reason), cause);
    }

    public static AuthenticateException invalidCredentials() {
        return new AuthenticateException("Invalid email or password");
    }
}
