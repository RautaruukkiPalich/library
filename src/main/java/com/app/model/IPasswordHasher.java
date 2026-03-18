package com.app.model;

public interface IPasswordHasher {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String hashedPassword);
}
