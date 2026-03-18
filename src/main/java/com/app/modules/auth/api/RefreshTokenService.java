package com.app.modules.auth.api;

import com.app.modules.auth.model.RefreshToken;

public interface RefreshTokenService {
    RefreshToken create(Long userId);

    RefreshToken rotate(String token);

    void revokeAllUserTokens(Long id);
}
