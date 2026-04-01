package com.app.modules.user.impl;

import com.app.core.exception.DuplicateException;
import com.app.core.utils.NormalizeSanitizer;
import com.app.core.utils.passwordHasher.PasswordHasher;
import com.app.modules.email.api.EmailService;
import com.app.modules.email.dto.EmailDTO;
import com.app.modules.user.api.UserAuthService;
import com.app.modules.user.api.UserService;
import com.app.modules.user.dto.LoginUserDTO;
import com.app.modules.user.dto.RegisterUserDTO;
import com.app.modules.user.dto.UserAuthInfoDTO;
import com.app.modules.user.dto.UserDTO;
import com.app.modules.user.exception.UserNotFoundException;
import com.app.modules.user.exception.UserValidationException;
import com.app.modules.user.mapper.UserMapper;
import com.app.modules.user.model.User;
import com.app.modules.user.repository.UserGetterRepository;
import com.app.modules.user.repository.UserPersisterRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Primary
@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService, UserAuthService {

    private final EmailService emailService;
    private final UserGetterRepository userGetterRepository;
    private final UserPersisterRepository userPersisterRepository;
    private final PasswordHasher passwordHasher;

    @Override
    @Transactional(readOnly = true)
    public UserDTO getByID(Long id) {
        User user = this.userGetterRepository.getByID(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return UserMapper.toDTO(user);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(oldPassword, "old password must not be null");
        Objects.requireNonNull(newPassword, "new password must not be null");

        this.userGetterRepository.getByID(userId)
                .ifPresent(u -> {
                    if (!u.comparePassword(oldPassword, passwordHasher)) {
                        throw new UserValidationException("old password", "invalid");
                    }
                    u.setPassword(newPassword, passwordHasher);
                    this.userPersisterRepository.save(u);
                });
    }

    @Override
    public Optional<UserAuthInfoDTO> checkCredentials(LoginUserDTO dto) {
        return this.userGetterRepository.getByEmail(dto.email())
                .filter(u -> u.comparePassword(dto.password(), this.passwordHasher))
                .map(UserMapper::convert);
    }

    @Override
    public Optional<UserAuthInfoDTO> getById(Long id) {
        Objects.requireNonNull(id, "id must not be null");

        return this.userGetterRepository.getByID(id)
                .map(UserMapper::convert);
    }

    @Override
    public Optional<UserAuthInfoDTO> getByEmail(String email) {
        Objects.requireNonNull(email, "email must not be null");
        String normalizedEmail = NormalizeSanitizer.normalize(email);

        return this.userGetterRepository.getByEmail(normalizedEmail)
                .map(UserMapper::convert);
    }

    @Override
    public void register(RegisterUserDTO dto) {
        Objects.requireNonNull(dto, "dto must not be null");
        String normalizedEmail = NormalizeSanitizer.normalize(dto.email());

        if (this.userGetterRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateException("duplicate email");
        }

        User user = new User(dto, this.passwordHasher);
        user.validate();

        User savedUser = this.userPersisterRepository.save(user);
        savedUser.validateStrict();
    }

    @Override
    public void resetPassword(String email) {
        Objects.requireNonNull(email, "email must not be null");
        String normalizedEmail = NormalizeSanitizer.normalize(email);

        userGetterRepository.getByEmail(normalizedEmail).ifPresent(
                u -> {
                    String pwrd = generateRandomPassword();
                    u.setPassword(pwrd, passwordHasher);
                    userPersisterRepository.save(u);

                    emailService.send(
                            EmailDTO.builder()
                                    .to(normalizedEmail)
                                    .subject("reset password")
                                    .body(String.format("new password: '%s'", pwrd))
                                    .build()
                    );
                }
        );
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
