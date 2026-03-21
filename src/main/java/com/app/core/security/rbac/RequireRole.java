package com.app.core.security.rbac;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RequireRole {
    Role value() default Role.ADMIN;
}