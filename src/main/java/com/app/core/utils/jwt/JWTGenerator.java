package com.app.core.utils.jwt;

public interface JWTGenerator {
    String generateToken(String sub);
}
