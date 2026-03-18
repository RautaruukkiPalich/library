package com.app.modules.auth.dto;

import lombok.Builder;

@Builder
public record RegisterDTO(
        String firstname,
        String lastname,
        String surname,
        String email,
        String rawPassword
) {
}
