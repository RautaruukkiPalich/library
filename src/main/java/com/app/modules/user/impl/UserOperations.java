package com.app.modules.user.impl;

import com.app.core.exception.NotFoundException;
import com.app.modules.user.exception.UserNotFoundException;
import com.app.modules.user.model.User;
import com.app.modules.user.repository.UserGetterRepository;
import com.app.modules.user.repository.UserPersisterRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserOperations {
    private final UserGetterRepository userGetterRepository;
    private final UserPersisterRepository userPersisterRepository;

    public UserOperations(UserGetterRepository userGetterRepository,
                          UserPersisterRepository userPersisterRepository) {
        this.userGetterRepository = userGetterRepository;
        this.userPersisterRepository = userPersisterRepository;
    }

    public User validateAndSave(User u) {
        Objects.requireNonNull(u, "user must not be null");

        u.validate();
        return userPersisterRepository.save(u);
    }

    public User getOrThrowNotFound(Long userId) throws NotFoundException {
        Objects.requireNonNull(userId, "userID must not be null");

        return userGetterRepository.getByID(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public User getOrThrowNotFound(String email) throws NotFoundException {
        Objects.requireNonNull(email, "email must not be null");

        return userGetterRepository.getByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("email", email));
    }

    public boolean existsByEmail(String email){
        Objects.requireNonNull(email, "email must not be null");
        return userGetterRepository.existsByEmail(email);
    }
}
