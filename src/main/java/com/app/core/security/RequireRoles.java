package com.app.core.security;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RequireRoles {
    Role[] roles() default {Role.ADMIN};

    Logical logical() default Logical.ANY;
}