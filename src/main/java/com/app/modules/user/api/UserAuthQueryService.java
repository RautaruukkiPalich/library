package com.app.modules.user.api;

import com.app.modules.user.dto.UserAuthInfoDTO;
import com.app.modules.user.exception.UserNotFoundException;
import lombok.NonNull;

public interface UserAuthQueryService {
    UserAuthInfoDTO getById(@NonNull Long id) throws UserNotFoundException;

    UserAuthInfoDTO getByEmail(@NonNull String email) throws UserNotFoundException;

}
