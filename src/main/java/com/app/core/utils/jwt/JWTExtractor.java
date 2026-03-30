package com.app.core.utils.jwt;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface JWTExtractor {
    String extractSub(String token);

    Map<String, Object> extractClaims(String token);

    boolean isTokenExpired(String token);
}
