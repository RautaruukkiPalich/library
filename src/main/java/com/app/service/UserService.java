package com.app.service;

import com.app.auth.IPasswordHasher;
import com.app.dto.UserDTO;
import com.app.exception.duplicate.DuplicateException;
import com.app.exception.notfound.UserNotFoundException;
import com.app.mapper.UserMapper;
import com.app.model.User;
import com.app.repository.IUserRepository;
import com.app.utils.NormalizeSanitizer;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Primary
@Service
@Transactional
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final IPasswordHasher passwordHasher;

    UserService(IUserRepository userRepository, IPasswordHasher passwordHasher) {
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
