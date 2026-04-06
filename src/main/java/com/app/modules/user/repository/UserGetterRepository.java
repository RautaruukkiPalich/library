package com.app.modules.user.repository;

import com.app.modules.user.exception.UserNotFoundException;
import com.app.modules.user.model.User;
import lombok.NonNull;

import java.util.Optional;

public interface UserGetterRepository {
    Optional<User> findById(@NonNull Long id);

    Optional<User> findByEmail(@NonNull String email);

    User getById(@NonNull Long id) throws UserNotFoundException;

    User getByEmail(@NonNull String email) throws UserNotFoundException;

    boolean existsByEmail(@NonNull String email);
}
