package com.app.core.security.jwt;

import com.app.core.annotation.public_endpoint.PublicEndpointChecker;
import com.app.core.exception.AuthException;
import com.app.core.security.rbac.Role;
import com.app.core.utils.jwt.JWTExtractor;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "true", matchIfMissing = true)
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JWTExtractor jwtExtractor;
    private final PublicEndpointChecker publicEndpointChecker;

    private static final String AUTHORIZATION_PREFIX = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ROLE_KEY = "role";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader(AUTHORIZATION_PREFIX);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(BEARER_PREFIX.length());

        boolean isPublic = publicEndpointChecker.isPublic(request);

        try {
            validateAndSetAuthentication(token);
        } catch (AuthException e) {
            log.warn("authorization failed for: {} | cause: {}", token, e.getMessage());
            if (!isPublic) {
                throw e;
            }
        }catch (JwtException e) {
            log.error("jwt auth failed for: {} | cause: {}", token, e.getMessage());
            if (!isPublic) {
                throw AuthorizationException.invalidToken();
            }
        }

        filterChain.doFilter(request, response);
    }

    private void validateAndSetAuthentication(String token) throws AuthException {
        if (token.isEmpty()) {
            throw AuthorizationException.missingToken();
        }

        final String sub = jwtExtractor.extractSub(token);

        if (jwtExtractor.isTokenExpired(token)) {
            throw AuthorizationException.tokenExpired();
        }

        String strRole = jwtExtractor.extractClaims(token).get(ROLE_KEY).toString();

        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            Long userId = Long.parseLong(sub);
            Role role = Role.extractRole(strRole)
                    .orElseGet(() -> {
                        log.warn("unknown role: {}", strRole);
                        return Role.GUEST;
                    });

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            List.of(new SimpleGrantedAuthority(role.getSpringRole()))
                    );

            SecurityContextHolder.getContext().setAuthentication(authToken);

            log.debug("user: \"{}\" authenticated successfully with role \"{}\"", userId, role);
        }
    }
}
