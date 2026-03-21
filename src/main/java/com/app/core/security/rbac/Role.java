package com.app.core.security.rbac;

import java.util.Optional;

public enum Role {
    ADMIN(100),
    MANAGER(75),
    USER(50),
    GUEST(10);

    private final int level;

    Role(int level) {
        this.level = level;
    }

    public String getSpringRole() {
        return "ROLE_" + this.name();
    }

    public String getAuthority() {
        return this.name();
    }

    public static Optional<Role> extractRole(String authority) {
        try {
            return Optional.of(Role.valueOf(authority));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Optional.empty();
        }
    }

    public boolean hasEnoughPermission(Role required) {
        return this.level >= required.level;
    }
}
