package com.app.repository;

import com.app.model.User;

import java.util.Optional;

public interface IAuthRepository {
    Optional<User> getByEmail(String email);
}
