package com.app.core.utils.passwordHasher;

public interface PasswordHasher {
    String encode(String rawPassword);

    boolean matches(String rawPassword, String hashedPassword);
}
