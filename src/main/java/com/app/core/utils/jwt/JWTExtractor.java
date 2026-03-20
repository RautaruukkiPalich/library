package com.app.core.utils.jwt;

import org.springframework.stereotype.Component;

@Component
public interface JWTExtractor {
    String extractSub(String token);
    boolean isTokenExpired(String token);
}
