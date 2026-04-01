package com.app.modules.user.api;

import com.app.modules.user.dto.LoginUserDTO;
import com.app.modules.user.dto.RegisterUserDTO;
import com.app.modules.user.dto.UserAuthInfoDTO;

import java.util.Optional;

public interface UserAuthService {
    Optional<UserAuthInfoDTO> checkCredentials(LoginUserDTO dto);

    Optional<UserAuthInfoDTO> getById(Long id);
    Optional<UserAuthInfoDTO> getByEmail(String email);

    void register(RegisterUserDTO dto);

    void resetPassword(String email);
}
