package com.app.modules.auth.impl;

import com.app.core.utils.NormalizeSanitizer;
import com.app.core.utils.jwt.JWTGenerator;
import com.app.modules.auth.api.AuthService;
import com.app.modules.auth.dto.LoginDTO;
import com.app.modules.auth.dto.RegisterDTO;
import com.app.modules.auth.dto.TokenPairDTO;
import com.app.modules.auth.exception.AuthenticateException;
import com.app.modules.refresh_token.api.RefreshTokenService;
import com.app.modules.refresh_token.dto.RefreshTokenInfoDTO;
import com.app.modules.user.api.UserAuthService;
import com.app.modules.user.dto.LoginUserDTO;
import com.app.modules.user.dto.RegisterUserDTO;
import com.app.modules.user.dto.UserAuthInfoDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private final UserAuthService userAuthService;
    private final RefreshTokenService refreshTokenService;
    private final JWTGenerator jwtGenerator;

    private static final String ROLE_KEY = "role";

    public AuthServiceImpl(
            UserAuthService userAuthService,
            RefreshTokenService refreshTokenService,
            JWTGenerator jwtGenerator) {
        this.userAuthService = userAuthService;
        this.refreshTokenService = refreshTokenService;
        this.jwtGenerator = jwtGenerator;
    }

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
                .access(generateAccessToken(user))
                .refresh(refreshTokenService.create(user.id()).token())
                .build();
    }

    @Override
    public void register(RegisterDTO dto) {
        Objects.requireNonNull(dto, "dto must not be null");
        this.userAuthService.register(
                RegisterUserDTO.builder()
                        .firstname(dto.firstname())
                        .lastname(dto.lastname())
                        .surname(dto.lastname())
                        .email(dto.email())
                        .password(dto.password())
                        .build()
        );
    }

    @Override
    public TokenPairDTO refreshTokens(String token) {
        Objects.requireNonNull(token);

        RefreshTokenInfoDTO rt = refreshTokenService.rotate(token);

        UserAuthInfoDTO user = userAuthService.getById(rt.userId())
                .orElseThrow(AuthenticateException::invalidCredentials);

        return TokenPairDTO.builder()
                .access(generateAccessToken(user))
                .refresh(rt.token())
                .build();
    }

    private String generateAccessToken(UserAuthInfoDTO u) {
        Objects.requireNonNull(u, "dto must not be null");
        return jwtGenerator.generateToken(
                String.valueOf(u.id()),
                Map.of(ROLE_KEY, u.role().getAuthority())
        );
    }
}
