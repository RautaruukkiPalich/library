package com.app.modules.user.impl;

import com.app.core.security.rbac.Role;
import com.app.core.utils.NormalizeSanitizer;
import com.app.core.utils.passwordHasher.PasswordHasher;
import com.app.modules.user.api.UserProfileService;
import com.app.modules.user.exception.UserValidationException;
import com.app.modules.user.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@AllArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final PasswordHasher passwordHasher;
    private final UserOperations userOperations;

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(oldPassword, "old password must not be null");
        Objects.requireNonNull(newPassword, "new password must not be null");

        User u = userOperations.getOrThrowNotFound(userId);
        if (!u.comparePassword(oldPassword, passwordHasher)) {
            throw new UserValidationException("old password", "invalid");
        }
        u.setPassword(newPassword, passwordHasher);
        userOperations.validateAndSave(u);
    }

    @Override
    public void changeFirstname(Long userId, String firstname) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(firstname, "firstname must not be null");

        User u = userOperations.getOrThrowNotFound(userId);
        u.setFirstname(NormalizeSanitizer.sanitize(firstname));
        userOperations.validateAndSave(u);
    }

    @Override
    public void changeSurname(Long userId, String surname) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(surname, "surname must not be null");

        User u = userOperations.getOrThrowNotFound(userId);
        u.setSurname(NormalizeSanitizer.sanitize(surname));
        userOperations.validateAndSave(u);
    }

    @Override
    public void changeLastname(Long userId, String lastname) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(lastname, "surname must not be null");

        User u = userOperations.getOrThrowNotFound(userId);
        u.setLastname(NormalizeSanitizer.sanitize(lastname));
        userOperations.validateAndSave(u);
    }

    @Override
    public void changeEmail(Long userId, String email) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(email, "surname must not be null");

        User u = userOperations.getOrThrowNotFound(userId);
        u.setEmail(NormalizeSanitizer.normalize(NormalizeSanitizer.sanitize(email)));
        userOperations.validateAndSave(u);
    }

    @Override
    public void changeRole(Long userId, Role role) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(role, "role must not be null");

        User u = userOperations.getOrThrowNotFound(userId);
        u.setRole(role);
        userOperations.validateAndSave(u);
    }
}
