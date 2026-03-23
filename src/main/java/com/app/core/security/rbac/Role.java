package com.app.core.security.rbac;

import java.util.Optional;

public enum Role {
    ROOT(100),
    ADMIN(90),
    MANAGER(75),
    USER(50),
    GUEST(10);

    private final int level;
    private static final String SPRING_ROLE_PREFIX = "ROLE_";

    Role(int level) {
        this.level = level;
    }

    public String getSpringRole() {
        return SPRING_ROLE_PREFIX + this.name();
    }

    public String getAuthority() {
        return this.name();
    }

    public static Optional<Role> extractRole(String authority) {
        try {
            return Optional.of(Role.valueOf(extractRoleName(authority)));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Optional.empty();
        }
    }

    public boolean hasEnoughPermission(Role required) {
        return this.level >= required.level;
    }

    private static String extractRoleName(String authority) {
        if (authority.startsWith(SPRING_ROLE_PREFIX)) {
            return authority.substring(SPRING_ROLE_PREFIX.length());
        }
        return authority;
    }
}
