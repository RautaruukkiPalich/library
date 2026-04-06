package com.app.modules.user.dto;

import com.app.core.security.rbac.Role;
import lombok.Builder;

@Builder
public record UserAuthInfoDTO(
        Long id,
        Role role
) {
}
