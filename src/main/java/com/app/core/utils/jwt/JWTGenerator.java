package com.app.core.utils.jwt;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface JWTGenerator {
    String generateToken(String sub);

    String generateToken(String sub, Map<String, Object> claims);
}
