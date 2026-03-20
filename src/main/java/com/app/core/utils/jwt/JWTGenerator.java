package com.app.core.utils.jwt;

import com.app.core.security.Role;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface JWTGenerator {
    String generateToken(String sub);

    String generateToken(String sub, List<Role> roles);
}
