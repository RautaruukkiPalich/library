package com.app.modules.user.api;

import com.app.modules.user.dto.UserAuthInfoDTO;
import com.app.modules.user.exception.UserNotFoundException;

public interface UserAuthQueryService {
    UserAuthInfoDTO getById(Long id) throws UserNotFoundException;

    UserAuthInfoDTO getByEmail(String email) throws UserNotFoundException;

}
