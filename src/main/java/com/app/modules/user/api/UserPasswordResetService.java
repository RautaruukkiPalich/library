package com.app.modules.user.api;

import lombok.NonNull;

public interface UserPasswordResetService {

    void resetPassword(@NonNull String email);
}
