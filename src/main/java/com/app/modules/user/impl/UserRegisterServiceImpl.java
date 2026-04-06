package com.app.modules.user.impl;

import com.app.core.exception.DuplicateException;
import com.app.core.utils.NormalizeSanitizer;
import com.app.core.utils.passwordHasher.PasswordHasher;
import com.app.modules.user.api.UserRegistrationService;
import com.app.modules.user.dto.RegisterUserDTO;
import com.app.modules.user.model.User;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class UserRegisterServiceImpl implements UserRegistrationService {
    private final UserOperations userOperations;
    private final PasswordHasher passwordHasher;

    @Override
    public void register(@NonNull RegisterUserDTO dto) {
        String normalizedEmail = NormalizeSanitizer.normalize(dto.email());

        if (userOperations.existsByEmail(normalizedEmail)) {
            throw new DuplicateException("duplicate email");
        }

        User user = new User(dto, passwordHasher);
        User savedUser = userOperations.validateAndSave(user);

        savedUser.validateStrict();
    }
}
