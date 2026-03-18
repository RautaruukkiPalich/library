package com.app.service;

import com.app.dto.AuthDTO;
import com.app.dto.TokenPairDTO;
import com.app.dto.UserDTO;

public interface IAuthService {
    TokenPairDTO login(AuthDTO dto);
    TokenPairDTO refreshTokens(String token);
    void register(UserDTO dto);

    Long extractToken(String token);
}
