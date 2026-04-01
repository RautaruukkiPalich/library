package com.app.modules.user.impl;

import com.app.core.utils.NormalizeSanitizer;
import com.app.core.utils.passwordHasher.PasswordHasher;
import com.app.modules.email.api.EmailPreparerService;
import com.app.modules.email.dto.EmailDTO;
import com.app.modules.user.api.UserPasswordResetService;
import com.app.modules.user.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Random;

@Service
@Transactional
@AllArgsConstructor
public class UserPasswordResetServiceImpl implements UserPasswordResetService {
    private final UserOperations userOperations;
    private final PasswordHasher passwordHasher;
    private final EmailPreparerService emailPreparerService;

    @Override
    public void resetPassword(String email) {
        Objects.requireNonNull(email, "email must not be null");
        String normalizedEmail = NormalizeSanitizer.normalize(email);

        User u = userOperations.getOrThrowNotFound(normalizedEmail);

        String pwrd = generateRandomPassword();
        u.setPassword(pwrd, passwordHasher);
        userOperations.validateAndSave(u);

        emailPreparerService.prepare(buildResetPasswordMessage(u.getEmail(), pwrd));
    }

    private EmailDTO buildResetPasswordMessage(String email, String newPassword) {
        return EmailDTO.builder()
                .to(email)
                .subject("reset password")
                .body(String.format("new password: '%s'", newPassword))
                .build();
    }

    //TODO: change realization
    public String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(chars.length());
            password.append(chars.charAt(index));
        }

        return password.toString();
    }
}
