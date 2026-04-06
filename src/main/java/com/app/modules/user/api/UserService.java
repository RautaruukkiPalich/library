package com.app.modules.user.api;

import com.app.modules.user.dto.UserDTO;
import lombok.NonNull;

public interface UserService {
    UserDTO getByID(@NonNull Long id);
}
