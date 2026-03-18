package com.app.modules.auth.impl;

import com.app.core.utils.NormalizeSanitizer;
import com.app.core.utils.passwordHasher.PasswordHasher;
import com.app.modules.auth.api.AuthService;
import com.app.modules.auth.api.JWTService;
import com.app.modules.auth.api.RefreshTokenService;
import com.app.modules.auth.dto.LoginDTO;
import com.app.modules.auth.dto.RegisterDTO;
import com.app.modules.auth.dto.TokenPairDTO;
import com.app.modules.auth.exception.AuthenticateException;
import com.app.modules.auth.mapper.AuthMapper;
import com.app.modules.auth.model.RefreshToken;
import com.app.modules.auth.repository.AuthRepository;
import com.app.modules.user.api.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final AuthRepository authRepository;
    private final RefreshTokenService refreshTokenService;
    private final JWTService jwtService;
    private final PasswordHasher passwordHasher;

    public AuthServiceImpl(
            UserService userService,
            AuthRepository authRepository,
            RefreshTokenService refreshTokenService,
            JWTService jwtService,
            PasswordHasher passwordHasher) {
        this.userService = userService;
        this.authRepository = authRepository;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public TokenPairDTO login(LoginDTO dto) {
        Objects.requireNonNull(dto, "dto must not be null");

        String normalizedEmail = NormalizeSanitizer.normalize(dto.email());
        if (normalizedEmail == null) {
            throw AuthenticateException.invalidCredentials();
        }

        Long userId = authRepository.getByEmail(normalizedEmail)
                .filter(u -> u.comparePassword(dto.password(), passwordHasher))
                .map(u -> u.getId())
                .orElseThrow(AuthenticateException::invalidCredentials);

        return TokenPairDTO.builder()
                .access(jwtService.generateToken(String.valueOf(userId)))
                .refresh(refreshTokenService.create(userId).getToken())
                .build();
    }

    @Override
    public void register(RegisterDTO dto) {
        Objects.requireNonNull(dto, "dto must not be null");
        this.userService.add(AuthMapper.toUserDTO(dto));
    }

    @Override
    public TokenPairDTO refreshTokens(String token) {
        Objects.requireNonNull(token);

        RefreshToken refreshToken = refreshTokenService.rotate(token);
        String accessToken = jwtService.generateToken(String.valueOf(refreshToken.getUser().getId()));

        return TokenPairDTO.builder()
                .access(accessToken)
                .refresh(refreshToken.getToken())
                .build();
    }
}
