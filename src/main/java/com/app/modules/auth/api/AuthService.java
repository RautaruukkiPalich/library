package com.app.modules.auth.api;

import com.app.modules.auth.dto.LoginDTO;
import com.app.modules.auth.dto.RegisterDTO;
import com.app.modules.auth.dto.TokenPairDTO;
import lombok.NonNull;

public interface AuthService {
    void register(@NonNull RegisterDTO dto);

    TokenPairDTO login(@NonNull LoginDTO dto);

    void resetPassword(@NonNull String email);

    TokenPairDTO refreshTokens(@NonNull String token);

    void revokeAllRefreshTokens(@NonNull Long userId);
}
