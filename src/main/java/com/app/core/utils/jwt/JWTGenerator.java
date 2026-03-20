package com.app.core.utils.jwt;

import org.springframework.stereotype.Component;

@Component
public interface JWTGenerator {
    String generateToken(String sub);
}
