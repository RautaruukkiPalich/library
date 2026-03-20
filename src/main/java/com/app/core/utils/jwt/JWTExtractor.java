package com.app.core.utils.jwt;

import com.app.core.security.Role;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface JWTExtractor {
    String extractSub(String token);

    List<Role> extractRoles(String token);

    boolean isTokenExpired(String token);
}
