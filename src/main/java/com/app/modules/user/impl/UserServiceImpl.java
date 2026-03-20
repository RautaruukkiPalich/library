package com.app.modules.user.impl;

import com.app.core.exception.DuplicateException;
import com.app.core.utils.NormalizeSanitizer;
import com.app.core.utils.passwordHasher.PasswordHasher;
import com.app.modules.user.api.UserAuthService;
import com.app.modules.user.api.UserService;
import com.app.modules.user.dto.LoginUserDTO;
import com.app.modules.user.dto.RegisterUserDTO;
import com.app.modules.user.dto.UserAuthInfoDTO;
import com.app.modules.user.dto.UserDTO;
import com.app.modules.user.exception.UserNotFoundException;
import com.app.modules.user.mapper.UserMapper;
import com.app.modules.user.model.User;
import com.app.modules.user.repository.UserGetterRepository;
import com.app.modules.user.repository.UserPersisterRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Primary
@Service
@Transactional
public class UserServiceImpl implements UserService, UserAuthService {

    private final UserGetterRepository userGetterRepository;
    private final UserPersisterRepository userPersisterRepository;
    private final PasswordHasher passwordHasher;

    UserServiceImpl(
            UserGetterRepository userGetterRepository,
            UserPersisterRepository userPersisterRepository,
            PasswordHasher passwordHasher) {
        this.userGetterRepository = userGetterRepository;
        this.userPersisterRepository = userPersisterRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getByID(Long id) {
        User user = this.userGetterRepository.getByID(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return UserMapper.toDTO(user);
    }

    @Override
    public Optional<UserAuthInfoDTO> checkCredentials(LoginUserDTO dto) {
        return this.userGetterRepository.getByEmail(dto.email())
                .filter(u -> u.comparePassword(dto.password(), this.passwordHasher))
                .map(UserMapper::convert);
    }

    @Override
    public Optional<UserAuthInfoDTO> getById(Long id) {
        return this.userGetterRepository.getByID(id)
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
}
