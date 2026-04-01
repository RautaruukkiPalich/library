package com.app.modules.user.impl;

import com.app.core.utils.NormalizeSanitizer;
import com.app.modules.user.api.UserAuthQueryService;
import com.app.modules.user.dto.UserAuthInfoDTO;
import com.app.modules.user.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@AllArgsConstructor
public class UserAuthQueryServiceImpl implements UserAuthQueryService {
    private final UserOperations userOperations;

    @Override
    public UserAuthInfoDTO getById(Long id) {
        Objects.requireNonNull(id, "id must not be null");

        return UserMapper.convertToAuthInfo(userOperations.getOrThrowNotFound(id));
    }

    @Override
    public UserAuthInfoDTO getByEmail(String email) {
        Objects.requireNonNull(email, "email must not be null");
        String normalizedEmail = NormalizeSanitizer.normalize(email);

        return UserMapper.convertToAuthInfo(userOperations.getOrThrowNotFound(normalizedEmail));
    }
}
