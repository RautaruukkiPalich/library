package com.app.modules.auth.filter;

import com.app.core.exception.AuthException;
import com.app.core.utils.PublicEndpointChecker;
import com.app.core.utils.jwt.JWTExtractor;
import com.app.modules.auth.exception.AuthorizationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "true", matchIfMissing = true)
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JWTExtractor jwtExtractor;
    private final PublicEndpointChecker publicEndpointChecker;

    private static final String AUTHORIZATION_PREFIX = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader(AUTHORIZATION_PREFIX);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
//            log.info("no valid authorization header found");
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(BEARER_PREFIX.length());

        boolean isPublic = publicEndpointChecker.isPublic(request);

        try {
            validateAndSetAuthentication(token);
        } catch (AuthException e) {
            log.warn("Authorization failed: {}", e.getMessage());
            if (!isPublic) {
                throw e;
            }
        } catch (Exception e) {
            log.error("JWT authentication failed: {}", e.getMessage());
            if (!isPublic) {
                throw e;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void validateAndSetAuthentication(String token) throws AuthException {
        if (token.isEmpty()) {
            throw AuthorizationException.missingToken();
        }

        final String sub = jwtExtractor.extractSub(token);

        if (sub == null) {
            throw AuthorizationException.invalidToken();
        }

        if (jwtExtractor.isTokenExpired(token)) {
            throw AuthorizationException.tokenExpired();
        }

        Collection<? extends GrantedAuthority> authorities = jwtExtractor
                .extractRoles(token)
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getSpringRole()))
                .toList();

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            Long userId = Long.parseLong(sub);
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            authorities
                    );
            SecurityContextHolder.getContext().setAuthentication(authToken);

            log.debug("User {} authenticated successfully", userId);
        }
    }
}
