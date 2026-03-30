package com.app.core.security.rbac;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class RBACServiceImpl implements RBACService {

    public boolean hasRole(Authentication auth, Role requiredRole) {

        if (requiredRole == null) {
            log.warn("No roles specified in annotation");
            return false;
        }

        if (auth == null || auth instanceof AnonymousAuthenticationToken || !auth.isAuthenticated()) {
            return Role.GUEST.hasEnoughPermission(requiredRole);
        }

        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .anyMatch(r -> Role.extractRole(r)
                        .orElseGet(() -> {
                            log.warn("Unknown role {}", r);
                            return Role.GUEST;
                        })
                        .hasEnoughPermission(requiredRole));
    }
}
