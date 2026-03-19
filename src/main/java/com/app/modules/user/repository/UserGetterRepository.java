package com.app.modules.user.repository;

import com.app.modules.user.model.User;

import java.util.Optional;

public interface UserGetterRepository {
    Optional<User> getByID(Long id);
    Optional<User> getByEmail(String email);

    boolean existsByEmail(String email);
}
