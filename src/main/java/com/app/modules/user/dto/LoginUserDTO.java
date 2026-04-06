package com.app.modules.user.dto;

import org.jspecify.annotations.NonNull;

public record LoginUserDTO(
        @NonNull String email,
        @NonNull String password
) {
}
