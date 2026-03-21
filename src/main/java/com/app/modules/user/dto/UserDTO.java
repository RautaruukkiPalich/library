package com.app.modules.user.dto;

import com.app.core.security.rbac.Role;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record UserDTO(
        Long id,
        String firstname,
        String lastname,
        String surname,
        String email,
        Role role,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
