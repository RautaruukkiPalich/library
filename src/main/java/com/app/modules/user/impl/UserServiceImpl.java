package com.app.modules.user.impl;

import com.app.core.exception.DuplicateException;
import com.app.core.utils.NormalizeSanitizer;
import com.app.core.utils.passwordHasher.PasswordHasher;
import com.app.modules.user.api.UserService;
import com.app.modules.user.dto.UserDTO;
import com.app.modules.user.exception.UserNotFoundException;
import com.app.modules.user.mapper.UserMapper;
import com.app.modules.user.model.User;
import com.app.modules.user.repository.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Primary
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    UserServiceImpl(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getByID(Long id) {
        User user = this.userRepository.getByID(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return UserMapper.toDTO(user);
    }

    @Override
    public Long add(UserDTO dto) {
        Objects.requireNonNull(dto, "dto must not be null");
        String normalizedEmail = NormalizeSanitizer.normalize(dto.email());

        if (this.userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateException("duplicate email");
        }

        User user = new User(dto, this.passwordHasher);
        user.validate();

        User savedUser = this.userRepository.save(user);
        savedUser.validateStrict();

        return savedUser.getId();
    }
}
