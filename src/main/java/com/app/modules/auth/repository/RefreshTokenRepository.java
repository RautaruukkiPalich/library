package com.app.modules.auth.repository;

import com.app.modules.auth.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {
    Optional<RefreshToken> getByToken(String token);

    RefreshToken save(RefreshToken token);

    void revokeAllUserTokens(Long userId);
}
