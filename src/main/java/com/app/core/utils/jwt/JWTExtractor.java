package com.app.core.utils.jwt;

public interface JWTExtractor {
    String extractSub(String token);
    boolean isTokenExpired(String token);
}
