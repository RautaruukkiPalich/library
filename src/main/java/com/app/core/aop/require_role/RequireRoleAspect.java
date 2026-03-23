package com.app.core.aop.require_role;


import com.app.core.exception.ForbiddenException;
import com.app.core.security.rbac.RBACService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.aop.roles.enabled", havingValue = "true", matchIfMissing = true)
public class RequireRoleAspect {

    private final RBACService rbacService;

    @Before("@annotation(requireRole)")
    public void checkRoles(JoinPoint joinPoint, RequireRole requireRole) {
        if (!rbacService.hasRole(
                SecurityContextHolder.getContext().getAuthentication(),
                requireRole.value())
        ) {
            throw ForbiddenException.insufficientPermissions();
        }
    }
}
