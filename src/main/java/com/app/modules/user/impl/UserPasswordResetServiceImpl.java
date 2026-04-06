package com.app.modules.user.impl;

import com.app.core.exception.ValidationException;
import com.app.core.utils.NormalizeSanitizer;
import com.app.core.utils.passwordHasher.PasswordHasher;
import com.app.modules.email.api.EmailPreparerService;
import com.app.modules.email.dto.EmailDTO;
import com.app.modules.user.api.UserPasswordResetService;
import com.app.modules.user.model.User;
import com.app.modules.user.repository.UserGetterRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Random;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class UserPasswordResetServiceImpl implements UserPasswordResetService {
    private final UserGetterRepository userGetterRepository;
    private final UserOperations userOperations;
    private final PasswordHasher passwordHasher;
    private final EmailPreparerService emailPreparerService;

    @Override
    public void resetPassword(@NonNull String email) {
        Objects.requireNonNull(email, "email must not be null");
        String normalizedEmail = NormalizeSanitizer.normalize(email);

        User u = userGetterRepository.getByEmail(normalizedEmail);

        String temporaryPassword = generateAndValidatePassword(u);

        emailPreparerService.prepare(buildResetPasswordMessage(u.getEmail(), temporaryPassword));
    }

    private EmailDTO buildResetPasswordMessage(@NonNull String email, @NonNull String newPassword) {
        return EmailDTO.builder()
                .to(email)
                .subject("reset password")
                .body(String.format("new password: '%s'", newPassword))
                .build();
    }

    private String generateAndValidatePassword(@NonNull User user) {
        int maxAttempts = 10;
        int attempt = 0;

        while (attempt < maxAttempts) {
            try {
                String password = generateRandomPassword(10);
                user.setPassword(password, passwordHasher);
                userOperations.validateAndSave(user);
                log.debug("generated valid password after {} attempts", attempt + 1);
                return password;
            } catch (ValidationException e) {
                attempt++;
                log.debug("generated invalid password (attempt {}): {}", attempt, e.getErrorsMap());
            }
        }

        throw new IllegalStateException(
                String.format("failed to generate valid password after %d attempts", maxAttempts)
        );
    }

    //TODO: change realization
    public String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            password.append(chars.charAt(index));
        }

        return password.toString();
    }
}
