package com.app.modules.refresh_token.impl;

import com.app.core.config.AuthenticateException;
import com.app.modules.refresh_token.api.RefreshTokenService;
import com.app.modules.refresh_token.dto.RefreshTokenInfoDTO;
import com.app.modules.refresh_token.model.RefreshToken;
import com.app.modules.refresh_token.repository.RefreshTokenRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Objects;

@Service
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-ttl}")
    private long refreshTokenTtl;

    public RefreshTokenServiceImpl(
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public RefreshTokenInfoDTO create(@NonNull Long userId) {
        Objects.requireNonNull(userId, "user id must not be null");
        RefreshToken rt = refreshTokenRepository.save(createRT(userId));
        return RefreshTokenInfoDTO.builder()
                .userId(rt.getUserId())
                .token(rt.getToken())
                .build();
    }

    @Override
    public RefreshTokenInfoDTO rotate(@NonNull String oldToken) throws RuntimeException {
        Objects.requireNonNull(oldToken, "old token must not be null");
        return refreshTokenRepository.findByToken(oldToken)
                .filter(t -> !t.isRevoked())
                .filter(t -> t.getExpiresAt().isAfter(OffsetDateTime.now()))
                .map(t -> {
                    t.setRevoked(true);
                    RefreshToken rt = createRT(t.getUserId());
                    this.refreshTokenRepository.save(t);
                    RefreshToken saved = this.refreshTokenRepository.save(rt);
                    return RefreshTokenInfoDTO.builder()
                            .token(saved.getToken())
                            .userId(saved.getUserId())
                            .build();
                })
                .orElseThrow(AuthenticateException::authRequired);
    }

    @Override
    public void revokeAllUserTokens(@NonNull Long id) {
        Objects.requireNonNull(id, "id must not be null");
        this.refreshTokenRepository.revokeAllUserTokens(id);
    }

    private String generateSecureToken() {
        byte[] randomBytes = new byte[64];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private RefreshToken createRT(@NonNull Long userId) {
        RefreshToken rt = new RefreshToken();
        rt.setToken(generateSecureToken());
        rt.setUserId(userId);
        rt.setRevoked(false);
        rt.setExpiresAt(OffsetDateTime.now().plusSeconds(refreshTokenTtl));
        return rt;
    }
}
