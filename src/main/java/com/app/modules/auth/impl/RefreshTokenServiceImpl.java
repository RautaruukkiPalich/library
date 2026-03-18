package com.app.modules.auth.impl;

import com.app.modules.auth.api.RefreshTokenService;
import com.app.modules.auth.exception.AuthenticateException;
import com.app.modules.auth.model.RefreshToken;
import com.app.modules.auth.repository.AuthRepository;
import com.app.modules.auth.repository.RefreshTokenRepository;
import com.app.modules.user.api.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;

@Service
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthRepository authRepository;

    @Value("${jwt.refresh-ttl}")
    private long refreshTokenTtl;

    public RefreshTokenServiceImpl(
            RefreshTokenRepository refreshTokenRepository, UserService userService,
            AuthRepository authRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.authRepository = authRepository;
    }

    @Override
    public RefreshToken create(Long userId) {
        return this.authRepository.getByID(userId)
                .map(u -> {
                    RefreshToken rt = new RefreshToken();
                    rt.setToken(generateSecureToken());
                    rt.setUser(u);
                    rt.setRevoked(false);
                    rt.setExpiresAt(OffsetDateTime.now().plusSeconds(refreshTokenTtl));
                    return refreshTokenRepository.save(rt);
                })
                .orElseThrow(AuthenticateException::invalidCredentials);
    }

    @Override
    public RefreshToken rotate(String oldToken) throws RuntimeException {
        return refreshTokenRepository.getByToken(oldToken)
                .filter(t -> !t.isRevoked())
                .filter(t -> t.getExpiresAt().isAfter(OffsetDateTime.now()))
                .map(t -> {
                    t.setRevoked(true);

                    RefreshToken rt = new RefreshToken();
                    rt.setToken(generateSecureToken());
                    rt.setUser(t.getUser());
                    rt.setRevoked(false);
                    rt.setExpiresAt(OffsetDateTime.now().plusSeconds(refreshTokenTtl));

                    this.refreshTokenRepository.save(t);
                    return this.refreshTokenRepository.save(rt);
                })
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public void revokeAllUserTokens(Long id) {
        this.refreshTokenRepository.revokeAllUserTokens(id);
    }

    private String generateSecureToken() {
        byte[] randomBytes = new byte[64];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
