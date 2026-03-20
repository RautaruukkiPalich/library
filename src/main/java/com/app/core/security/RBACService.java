package com.app.core.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service("rbacService")
public class RBACService {

    public Role[] toRoleArray(Role[] roles) {
        return roles;
    }

    public boolean hasRoles(Authentication auth, Role[] requiredRoles, Logical logical) {

        if (requiredRoles == null || requiredRoles.length == 0) {
            log.warn("No roles specified in annotation");
            return false;
        }

        Set<Role> requiredRoleNames = Set.of(requiredRoles);

        if (auth == null || auth instanceof AnonymousAuthenticationToken || !auth.isAuthenticated()) {
            return handleGuestAccess(requiredRoleNames, logical);
        }

        Set<Role> userRoles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .map(r -> Role.valueOf(extractRoleName(r)))
                .collect(Collectors.toSet());

        return checkRoleAccess(userRoles, requiredRoleNames, logical);
    }

    private boolean handleGuestAccess(Set<Role> requiredRoles, Logical logical) {
        boolean hasGuest = requiredRoles.contains(Role.GUEST);

        return switch (logical) {
            case ANY -> hasGuest;
            case ALL -> hasGuest && requiredRoles.size() == 1;
        };
    }

    private boolean checkRoleAccess(Set<Role> userRoles, Set<Role> requiredRoles, Logical logical) {
        return switch (logical) {
            case ALL -> {
                boolean hasAll = userRoles.containsAll(requiredRoles);
                if (!hasAll) {
                    log.debug("User missing required roles. Required: {}, User: {}", requiredRoles, userRoles);
                }
                yield hasAll;
            }
            case ANY -> {
                boolean hasAny = requiredRoles.stream().anyMatch(userRoles::contains);
                if (!hasAny) {
                    log.debug("User has none of required roles. Required: {}, User: {}", requiredRoles, userRoles);
                }
                yield hasAny;
            }
        };
    }

    private String extractRoleName(String authority) {
        final String ROLE_PREFIX = "ROLE_";

        if (authority.startsWith(ROLE_PREFIX)) {
            return authority.substring(ROLE_PREFIX.length());
        }
        return authority;
    }
}
