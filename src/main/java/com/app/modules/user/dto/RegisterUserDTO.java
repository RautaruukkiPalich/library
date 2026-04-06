package com.app.modules.user.dto;

import lombok.Builder;
import org.jspecify.annotations.NonNull;

@Builder
public record RegisterUserDTO(
        @NonNull String firstname,
        @NonNull String lastname,
        @NonNull String surname,
        @NonNull String email,
        @NonNull String password
) {
}
