package com.app.modules.auth.api;

import com.app.modules.auth.dto.LoginDTO;
import com.app.modules.auth.dto.RegisterDTO;
import com.app.modules.auth.dto.TokenPairDTO;

public interface AuthService {
    void register(RegisterDTO dto);
    TokenPairDTO login(LoginDTO dto);

    void resetPassword(String email);

    TokenPairDTO refreshTokens(String token);
    void revokeAllRefreshTokens(Long userId);
}
