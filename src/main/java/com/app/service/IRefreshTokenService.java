package com.app.service;

import com.app.model.RefreshToken;

public interface IRefreshTokenService {
    RefreshToken create(Long userId);

    RefreshToken rotate(String token);

    void revokeAllUserTokens(Long id);
}
