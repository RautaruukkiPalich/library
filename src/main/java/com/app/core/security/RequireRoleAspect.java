package com.app.core.security;


import com.app.core.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.aop.roles.enabled", havingValue = "true", matchIfMissing = true)
public class RequireRoleAspect {

    private final RBACService rbacService;

    @Before("@annotation(requireRoles)")
    public void checkRoles(JoinPoint joinPoint, RequireRoles requireRoles) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Role[] requiredRoles = requireRoles.roles();
        Logical logical = requireRoles.logical();

//        log.info("Checking roles: {}", Arrays.toString(requiredRoles));

        if (!rbacService.hasRoles(auth, requiredRoles, logical)) {
            throw ForbiddenException.insufficientPermissions();
        }
    }
}
