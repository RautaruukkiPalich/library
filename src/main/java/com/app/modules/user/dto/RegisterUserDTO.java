package com.app.modules.user.dto;

import lombok.Builder;

@Builder
public record RegisterUserDTO(
        String firstname,
        String lastname,
        String surname,
        String email,
        String password
) {
}
