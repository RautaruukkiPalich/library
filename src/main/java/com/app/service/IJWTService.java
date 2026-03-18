package com.app.service;

public interface IJWTService {
    String generateToken(String sub);
    String extractSub(String token);
    boolean isTokenExpired(String token);
}
