package com.app.modules.user.api;

import org.jspecify.annotations.NonNull;

public interface UserPasswordResetService {

    void resetPassword(@NonNull String email);
}
