package com.app.modules.user.repository;

import com.app.modules.user.model.User;

public interface UserPersisterRepository {
    User save(User user);
}
