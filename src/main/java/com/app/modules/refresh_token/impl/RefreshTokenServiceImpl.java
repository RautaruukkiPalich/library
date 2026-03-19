package com.app.modules.refresh_token.impl;

import com.app.modules.refresh_token.api.RefreshTokenService;
import com.app.modules.refresh_token.dto.RefreshTokenInfoDTO;
import com.app.modules.refresh_token.model.RefreshToken;
import com.app.modules.refresh_token.repository.RefreshTokenRepository;
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

    @Value("${jwt.refresh-ttl}")
    private long refreshTokenTtl;

    public RefreshTokenServiceImpl(
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public RefreshTokenInfoDTO create(Long userId) {
        RefreshToken rt = refreshTokenRepository.save(createRT(userId));
        return RefreshTokenInfoDTO.builder()
                .userId(rt.getUserId())
                .token(rt.getToken())
                .build();
    }

    @Override
    public RefreshTokenInfoDTO rotate(String oldToken) throws RuntimeException {
        return refreshTokenRepository.getByToken(oldToken)
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

    private RefreshToken createRT(Long userId) {
        RefreshToken rt = new RefreshToken();
        rt.setToken(generateSecureToken());
        rt.setUserId(userId);
        rt.setRevoked(false);
        rt.setExpiresAt(OffsetDateTime.now().plusSeconds(refreshTokenTtl));
        return rt;
    }
}
