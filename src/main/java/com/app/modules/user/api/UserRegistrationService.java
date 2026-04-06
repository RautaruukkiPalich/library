package com.app.modules.user.api;

import com.app.modules.user.dto.RegisterUserDTO;
import lombok.NonNull;

public interface UserRegistrationService {
    void register(@NonNull RegisterUserDTO dto);

}
