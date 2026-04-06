package com.app.modules.refresh_token.api;


import com.app.modules.refresh_token.dto.RefreshTokenInfoDTO;
import org.jspecify.annotations.NonNull;

public interface RefreshTokenService {
    RefreshTokenInfoDTO create(@NonNull Long userId);

    RefreshTokenInfoDTO rotate(@NonNull String token);

    void revokeAllUserTokens(@NonNull Long id);
}
