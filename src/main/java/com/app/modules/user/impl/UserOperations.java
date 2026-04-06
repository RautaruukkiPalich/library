package com.app.modules.user.impl;

import com.app.modules.user.model.User;
import com.app.modules.user.repository.UserGetterRepository;
import com.app.modules.user.repository.UserPersisterRepository;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@AllArgsConstructor
public class UserOperations {
    private final UserGetterRepository userGetterRepository;
    private final UserPersisterRepository userPersisterRepository;

    public User validateAndSave(@NonNull User u) {
        Objects.requireNonNull(u, "user must not be null");

        u.validate();
        return userPersisterRepository.save(u);
    }

    public boolean existsByEmail(@NonNull String email) {
        Objects.requireNonNull(email, "email must not be null");
        return userGetterRepository.existsByEmail(email);
    }
}
