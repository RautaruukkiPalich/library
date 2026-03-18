package com.app.repository;

import com.app.model.RefreshToken;

import java.util.Optional;

public interface IRefreshTokenRepository {
    Optional<RefreshToken> getByToken(String token);
    RefreshToken save(RefreshToken token);
    void revokeAllUserTokens(Long userId);
}
