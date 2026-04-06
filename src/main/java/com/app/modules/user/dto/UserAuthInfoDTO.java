package com.app.modules.user.dto;

import com.app.core.security.rbac.Role;
import lombok.Builder;
import org.jspecify.annotations.NonNull;

@Builder
public record UserAuthInfoDTO(
        @NonNull Long id,
        @NonNull Role role
) {
}
