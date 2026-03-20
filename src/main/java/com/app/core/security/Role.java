package com.app.core.security;

public enum Role{
    ADMIN,
    MANAGER,
    USER,
    GUEST;

    public String getSpringRole() {
        return "ROLE_" + this.name();
    }

    public String getAuthority() {
        return this.name();
    }
}
