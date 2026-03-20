package com.app.modules.user.dto;

import com.app.core.security.Role;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record UserDTO(
        Long id,
        String firstname,
        String lastname,
        String surname,
        String email,
        List<Role> roles,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
