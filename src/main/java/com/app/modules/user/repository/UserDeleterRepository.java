package com.app.modules.user.repository;

import com.app.modules.user.model.User;
import org.jspecify.annotations.NonNull;

public interface UserDeleterRepository {
    void delete(@NonNull User user);
}
