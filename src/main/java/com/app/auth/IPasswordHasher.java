package com.app.auth;

public interface IPasswordHasher {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String hashedPassword);
}
