package com.app.modules.refresh_token.api;


import com.app.modules.refresh_token.dto.RefreshTokenInfoDTO;

public interface RefreshTokenService {
    RefreshTokenInfoDTO create(Long userId);
    RefreshTokenInfoDTO rotate(String token);
    void revokeAllUserTokens(Long id);
}
