package com.app.modules.user.api;

import com.app.modules.user.dto.UserDTO;

public interface UserService {
    UserDTO getByID(Long id);
    void changePassword(Long userId, String oldPassword, String newPassword);
}
