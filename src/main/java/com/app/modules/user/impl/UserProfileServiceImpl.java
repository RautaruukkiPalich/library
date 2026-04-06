package com.app.modules.user.impl;

import com.app.core.exception.DuplicateException;
import com.app.core.utils.NormalizeSanitizer;
import com.app.core.utils.passwordHasher.PasswordHasher;
import com.app.modules.user.api.UserProfileService;
import com.app.modules.user.dto.ProfileDTO;
import com.app.modules.user.exception.UserValidationException;
import com.app.modules.user.model.User;
import com.app.modules.user.repository.UserGetterRepository;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@AllArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final PasswordHasher passwordHasher;
    private final UserGetterRepository userGetterRepository;
    private final UserOperations userOperations;

    @Override
    public void editPassword(ProfileDTO.@NonNull EditPassword dto) {
        Objects.requireNonNull(dto, "dto must not be null");

        User u = userGetterRepository.getById(dto.subjectId());
        if (!u.comparePassword(dto.oldPassword(), passwordHasher)) {
            throw new UserValidationException("old password", "invalid");
        }
        u.setPassword(dto.newPassword(), passwordHasher);
        userOperations.validateAndSave(u);
    }

    @Override
    public void editFirstname(ProfileDTO.@NonNull EditFirstname dto) {
        Objects.requireNonNull(dto, "dto must not be null");

        User u = userGetterRepository.getById(dto.subjectId());
        u.setFirstname(NormalizeSanitizer.sanitize(dto.firstname()));
        userOperations.validateAndSave(u);
    }

    @Override
    public void editSurname(ProfileDTO.@NonNull EditSurname dto) {
        Objects.requireNonNull(dto, "dto must not be null");

        User u = userGetterRepository.getById(dto.subjectId());
        u.setSurname(NormalizeSanitizer.sanitize(dto.surname()));
        userOperations.validateAndSave(u);
    }

    @Override
    public void editLastname(ProfileDTO.@NonNull EditLastname dto) {
        Objects.requireNonNull(dto, "dto must not be null");

        User u = userGetterRepository.getById(dto.subjectId());
        u.setLastname(NormalizeSanitizer.sanitize(dto.lastname()));
        userOperations.validateAndSave(u);
    }

    @Override
    public void editEmail(ProfileDTO.@NonNull EditEmail dto) {
        Objects.requireNonNull(dto, "dto must not be null");

        if (userOperations.existsByEmail(dto.email())) {
            throw new DuplicateException("email is already exist");
        }

        User u = userGetterRepository.getById(dto.subjectId());
        u.setEmail(NormalizeSanitizer.normalize(NormalizeSanitizer.sanitize(dto.email())));
        userOperations.validateAndSave(u);
    }
}
