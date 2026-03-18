package com.app.repository;

import com.app.model.User;

import java.util.Optional;

public interface IUserRepository {
    Optional<User> getByID(Long id);

    boolean existsByEmail(String email);

    User save(User user);

    void delete(User user);
}
