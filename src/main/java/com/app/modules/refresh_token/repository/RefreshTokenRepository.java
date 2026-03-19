package com.app.modules.refresh_token.repository;

import com.app.modules.refresh_token.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {
    Optional<RefreshToken> getByToken(String token);

    RefreshToken save(RefreshToken token);

    void revokeAllUserTokens(Long userId);
}
