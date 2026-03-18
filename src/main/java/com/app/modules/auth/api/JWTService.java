package com.app.modules.auth.api;

public interface JWTService {
    String generateToken(String sub);

    String extractSub(String token);

    boolean isTokenExpired(String token);
}
