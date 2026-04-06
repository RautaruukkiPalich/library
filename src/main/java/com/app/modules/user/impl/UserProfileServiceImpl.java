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
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final PasswordHasher passwordHasher;
    private final UserGetterRepository userGetterRepository;
    private final UserOperations userOperations;

    @Override
    public void editPassword(ProfileDTO.@NonNull EditPassword dto) {
        User u = userGetterRepository.getById(dto.subjectId());
        if (!u.comparePassword(dto.oldPassword(), passwordHasher)) {
            throw new UserValidationException("old password", "invalid");
        }
        u.setPassword(dto.newPassword(), passwordHasher);
        userOperations.validateAndSave(u);
    }

    @Override
    public void editFirstname(ProfileDTO.@NonNull EditFirstname dto) {
        User u = userGetterRepository.getById(dto.subjectId());
        u.setFirstname(NormalizeSanitizer.sanitize(dto.firstname()));
        userOperations.validateAndSave(u);
    }

    @Override
    public void editSurname(ProfileDTO.@NonNull EditSurname dto) {
        User u = userGetterRepository.getById(dto.subjectId());
        u.setSurname(NormalizeSanitizer.sanitize(dto.surname()));
        userOperations.validateAndSave(u);
    }

    @Override
    public void editLastname(ProfileDTO.@NonNull EditLastname dto) {
        User u = userGetterRepository.getById(dto.subjectId());
        u.setLastname(NormalizeSanitizer.sanitize(dto.lastname()));
        userOperations.validateAndSave(u);
    }

    @Override
    public void editEmail(ProfileDTO.@NonNull EditEmail dto) {
        if (userOperations.existsByEmail(dto.email())) {
            throw new DuplicateException("email is already exist");
        }

        User u = userGetterRepository.getById(dto.subjectId());
        u.setEmail(NormalizeSanitizer.normalize(NormalizeSanitizer.sanitize(dto.email())));
        userOperations.validateAndSave(u);
    }
}
