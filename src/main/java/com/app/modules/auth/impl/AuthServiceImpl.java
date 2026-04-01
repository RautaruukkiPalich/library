package com.app.modules.auth.impl;

import com.app.core.security.rbac.Role;
import com.app.core.utils.NormalizeSanitizer;
import com.app.core.utils.jwt.JWTGenerator;
import com.app.modules.auth.api.AuthService;
import com.app.modules.auth.dto.LoginDTO;
import com.app.modules.auth.dto.RegisterDTO;
import com.app.modules.auth.dto.TokenPairDTO;
import com.app.modules.auth.exception.AuthenticateException;
import com.app.modules.refresh_token.api.RefreshTokenService;
import com.app.modules.refresh_token.dto.RefreshTokenInfoDTO;
import com.app.modules.user.api.UserAuthQueryService;
import com.app.modules.user.api.UserAuthService;
import com.app.modules.user.api.UserPasswordResetService;
import com.app.modules.user.api.UserRegistrationService;
import com.app.modules.user.dto.LoginUserDTO;
import com.app.modules.user.dto.RegisterUserDTO;
import com.app.modules.user.dto.UserAuthInfoDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserAuthService userAuthService;
    private final UserRegistrationService userRegistrationService;
    private final UserAuthQueryService userAuthQueryService;
    private final UserPasswordResetService userPasswordResetService;
    private final RefreshTokenService refreshTokenService;
    private final JWTGenerator jwtGenerator;

    private static final String ROLE_KEY = "role";

    @Override
    public TokenPairDTO login(LoginDTO dto) {
        Objects.requireNonNull(dto, "dto must not be null");

        String normalizedEmail = NormalizeSanitizer.normalize(dto.email());
        if (normalizedEmail == null) {
            throw AuthenticateException.invalidCredentials();
        }

        UserAuthInfoDTO user = userAuthService.checkCredentials(
                        LoginUserDTO.builder()
                                .email(normalizedEmail)
                                .password(dto.password())
                                .build()
                )
                .orElseThrow(AuthenticateException::invalidCredentials);

        return TokenPairDTO.builder()
                .access(generateAccessToken(user.id(), user.role()))
                .refresh(refreshTokenService.create(user.id()).token())
                .build();
    }

    @Override
    public void resetPassword(String email) {
        Objects.requireNonNull(email, "email must not be null");

        UserAuthInfoDTO dto = userAuthQueryService.getByEmail(email);
        userPasswordResetService.resetPassword(email);
        refreshTokenService.revokeAllUserTokens(dto.id());
    }

    @Override
    public void register(RegisterDTO dto) {
        Objects.requireNonNull(dto, "dto must not be null");

        userRegistrationService.register(
                RegisterUserDTO.builder()
                        .firstname(dto.firstname())
                        .lastname(dto.lastname())
                        .surname(dto.surname())
                        .email(dto.email())
                        .password(dto.password())
                        .build()
        );
    }

    @Override
    public TokenPairDTO refreshTokens(String token) {
        Objects.requireNonNull(token, "token must not be null");

        RefreshTokenInfoDTO rt = refreshTokenService.rotate(token);
        UserAuthInfoDTO user = userAuthQueryService.getById(rt.userId());

        return TokenPairDTO.builder()
                .access(generateAccessToken(user.id(), user.role()))
                .refresh(rt.token())
                .build();
    }

    @Override
    public void revokeAllRefreshTokens(Long userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        refreshTokenService.revokeAllUserTokens(userId);
    }

    private String generateAccessToken(Long userId, Role role) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(role, "role must not be null");

        return jwtGenerator.generateToken(
                String.valueOf(userId),
                Map.of(ROLE_KEY, role.getAuthority())
        );
    }
}
