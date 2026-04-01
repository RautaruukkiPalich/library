package com.app.modules.user.api;

import com.app.core.security.rbac.Role;

public interface UserProfileService {

    void changePassword(Long userId, String oldPassword, String newPassword);

    void changeFirstname(Long userId, String firstname);

    void changeSurname(Long userId, String surname);

    void changeLastname(Long userId, String lastname);

    void changeEmail(Long userId, String email);

    void changeRole(Long userId, Role role);
}
