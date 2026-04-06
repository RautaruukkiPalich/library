package com.app.modules.user.repository;

import com.app.modules.user.model.User;
import lombok.NonNull;

public interface UserDeleterRepository {
    void delete(@NonNull User user);
}
