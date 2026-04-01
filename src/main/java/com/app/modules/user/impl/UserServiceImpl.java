package com.app.modules.user.impl;

import com.app.core.utils.passwordHasher.PasswordHasher;
import com.app.modules.user.api.UserAuthService;
import com.app.modules.user.api.UserService;
import com.app.modules.user.dto.LoginUserDTO;
import com.app.modules.user.dto.UserAuthInfoDTO;
import com.app.modules.user.dto.UserDTO;
import com.app.modules.user.mapper.UserMapper;
import com.app.modules.user.model.User;
import com.app.modules.user.repository.UserGetterRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Primary
@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService, UserAuthService {

    private final UserGetterRepository userGetterRepository;
    private final PasswordHasher passwordHasher;
    private final UserOperations userOperations;


    @Override
    @Transactional(readOnly = true)
    public UserDTO getByID(Long userId) {
        User u = userOperations.getOrThrowNotFound(userId);

        u.validateStrict();
        return UserMapper.toDTO(u);
    }

    @Override
    public Optional<UserAuthInfoDTO> checkCredentials(LoginUserDTO dto) {
        return userGetterRepository.getByEmail(dto.email())
                .filter(u -> u.comparePassword(dto.password(), passwordHasher))
                .map(UserMapper::convertToAuthInfo);
    }
}
