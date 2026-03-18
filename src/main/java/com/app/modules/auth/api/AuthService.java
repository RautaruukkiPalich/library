package com.app.modules.auth.api;

import com.app.modules.auth.dto.LoginDTO;
import com.app.modules.auth.dto.RegisterDTO;
import com.app.modules.auth.dto.TokenPairDTO;

public interface AuthService {
    TokenPairDTO login(LoginDTO dto);

    TokenPairDTO refreshTokens(String token);

    void register(RegisterDTO dto);
}
