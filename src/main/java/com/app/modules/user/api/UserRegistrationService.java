package com.app.modules.user.api;

import com.app.modules.user.dto.RegisterUserDTO;

public interface UserRegistrationService {
    void register(RegisterUserDTO dto);

}
