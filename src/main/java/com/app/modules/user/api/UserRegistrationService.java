package com.app.modules.user.api;

import com.app.modules.user.dto.RegisterUserDTO;
import org.jspecify.annotations.NonNull;

public interface UserRegistrationService {
    void register(@NonNull RegisterUserDTO dto);

}
