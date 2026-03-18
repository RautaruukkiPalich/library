package com.app.modules.auth.repository;

import com.app.modules.user.model.User;

import java.util.Optional;

public interface AuthRepository {
    Optional<User> getByEmail(String email);

    Optional<User> getByID(Long id);
}
