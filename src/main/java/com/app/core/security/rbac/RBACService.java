package com.app.core.security.rbac;

import org.springframework.security.core.Authentication;

public interface RBACService {
    boolean hasRole(Authentication auth, Role requiredRole);
}
