package com.app.modules.user.impl;

import com.app.core.utils.NormalizeSanitizer;
import com.app.modules.user.api.UserAuthQueryService;
import com.app.modules.user.dto.UserAuthInfoDTO;
import com.app.modules.user.mapper.UserMapper;
import com.app.modules.user.repository.UserGetterRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class UserAuthQueryServiceImpl implements UserAuthQueryService {
    private final UserGetterRepository userGetterRepository;

    @Override
    public UserAuthInfoDTO getById(@NonNull Long id) {
        return UserMapper.convertToAuthInfo(userGetterRepository.getById(id));
    }

    @Override
    public UserAuthInfoDTO getByEmail(@NonNull String email) {
        String normalizedEmail = NormalizeSanitizer.normalize(email);

        return UserMapper.convertToAuthInfo(userGetterRepository.getByEmail(normalizedEmail));
    }
}
