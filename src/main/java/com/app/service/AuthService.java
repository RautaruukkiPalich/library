package com.app.service;

import com.app.auth.IPasswordHasher;
import com.app.dto.AuthDTO;
import com.app.dto.TokenPairDTO;
import com.app.dto.UserDTO;
import com.app.exception.auth.AuthenticateException;
import com.app.exception.auth.AuthorizationException;
import com.app.model.RefreshToken;
import com.app.model.User;
import com.app.repository.IAuthRepository;
import com.app.utils.NormalizeSanitizer;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
public class AuthService implements IAuthService {
    private final IUserService userService;
    private final IAuthRepository authRepository;
    private final IRefreshTokenService refreshTokenService;
    private final IJWTService jwtService;
    private final IPasswordHasher passwordHasher;

    public AuthService(
            IUserService userService,
            IAuthRepository authRepository,
            IRefreshTokenService refreshTokenService,
            IJWTService jwtService,
            IPasswordHasher passwordHasher) {
        this.userService = userService;
        this.authRepository = authRepository;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public TokenPairDTO login(AuthDTO dto) {
        Objects.requireNonNull(dto, "dto must not be null");

        String normalizedEmail = NormalizeSanitizer.normalize(dto.email());
        if (normalizedEmail == null) {
            throw AuthenticateException.invalidCredentials();
        }

        User user = authRepository.getByEmail(normalizedEmail)
                .orElseThrow(AuthenticateException::invalidCredentials);

        if (!user.comparePassword(dto.password(), passwordHasher)) {
            throw AuthenticateException.invalidCredentials();
        }

        String accessToken = jwtService.generateToken(String.valueOf(user.getId()));
        RefreshToken refreshToken = refreshTokenService.create(user.getId());

        return TokenPairDTO.builder()
                .access(accessToken)
                .refresh(refreshToken.getToken())
                .build();
    }

    @Override
    public void register(UserDTO dto) {
        Objects.requireNonNull(dto, "dto must not be null");
        this.userService.add(dto);
    }

    @Override
    public TokenPairDTO refreshTokens(String token){
        Objects.requireNonNull(token);

        RefreshToken refreshToken = refreshTokenService.rotate(token);
        String accessToken = jwtService.generateToken(String.valueOf(refreshToken.getUser().getId()));

        return TokenPairDTO.builder()
                .access(accessToken)
                .refresh(refreshToken.getToken())
                .build();
    }

    @Override
    public Long extractToken(String token){
        try{
            String sub = this.jwtService.extractSub(token);
            if (sub == null || this.jwtService.isTokenExpired(token)){
                throw AuthorizationException.tokenExpired();
            }

            return Long.getLong(sub);
        } catch (JwtException e) {
            throw AuthorizationException.invalidToken();
        }
    }
}
