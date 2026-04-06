package com.app.modules.user.impl;

import com.app.modules.user.model.User;
import com.app.modules.user.repository.UserGetterRepository;
import com.app.modules.user.repository.UserPersisterRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserOperations {
    private final UserGetterRepository userGetterRepository;
    private final UserPersisterRepository userPersisterRepository;

    public User validateAndSave(@NonNull User user) {
        user.validate();
        return userPersisterRepository.save(user);
    }

    public boolean existsByEmail(@NonNull String email) {
        return userGetterRepository.existsByEmail(email);
    }
}
