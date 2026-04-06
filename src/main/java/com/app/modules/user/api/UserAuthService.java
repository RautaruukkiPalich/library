package com.app.modules.user.api;

import com.app.modules.user.dto.LoginUserDTO;
import com.app.modules.user.dto.UserAuthInfoDTO;
import lombok.NonNull;

import java.util.Optional;

public interface UserAuthService {
    Optional<UserAuthInfoDTO> checkCredentials(@NonNull LoginUserDTO dto);
}
