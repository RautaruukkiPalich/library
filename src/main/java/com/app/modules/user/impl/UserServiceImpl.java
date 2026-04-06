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
import lombok.NonNull;
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

    @Override
    @Transactional(readOnly = true)
    public UserDTO getByID(@NonNull Long userId) {
        User u = userGetterRepository.getById(userId);

        u.validateStrict();
        return UserMapper.toDTO(u);
    }

    @Override
    public Optional<UserAuthInfoDTO> checkCredentials(@NonNull LoginUserDTO dto) {
        return userGetterRepository.findByEmail(dto.email())
                .filter(u -> u.comparePassword(dto.password(), passwordHasher))
                .map(UserMapper::convertToAuthInfo);
    }
}
