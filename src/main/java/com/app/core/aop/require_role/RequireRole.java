package com.app.core.aop.require_role;

import com.app.core.security.rbac.Role;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RequireRole {
    Role value() default Role.ADMIN;
}