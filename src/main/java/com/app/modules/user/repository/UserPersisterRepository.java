package com.app.modules.user.repository;

import com.app.modules.user.model.User;
import lombok.NonNull;

public interface UserPersisterRepository {
    User save(@NonNull User user);
}
