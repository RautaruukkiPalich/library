package com.app.modules.user.dto;

import com.app.core.security.Role;
import lombok.Builder;

import java.util.List;

@Builder
public record UserAuthInfoDTO(
        Long id,
        List<Role> roles
) {
}
