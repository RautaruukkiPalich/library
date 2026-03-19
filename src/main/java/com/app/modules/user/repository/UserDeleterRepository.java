package com.app.modules.user.repository;

import com.app.modules.user.model.User;

public interface UserDeleterRepository {
    void delete(User user);
}
