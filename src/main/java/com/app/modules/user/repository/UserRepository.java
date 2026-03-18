package com.app.modules.user.repository;

import com.app.modules.user.model.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> getByID(Long id);

    boolean existsByEmail(String email);

    User save(User user);

    void delete(User user);
}
