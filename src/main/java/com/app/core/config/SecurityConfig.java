package com.app.core.config;

import com.app.core.filter.GlobalExceptionFilter;
import com.app.core.utils.PublicEndpointChecker;
import com.app.modules.auth.exception.AuthenticateException;
import com.app.modules.auth.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Objects;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "true", matchIfMissing = true)
public class SecurityConfig {

    private final PublicEndpointChecker publicEndpointChecker;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final GlobalExceptionFilter globalExceptionFilter;


    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .anyRequest()
                        .access(
                                (sup, ctx) -> {
                                    Objects.requireNonNull(ctx, "context cant be null");
                                    if (publicEndpointChecker.isPublic(ctx.getRequest())) {
                                        return new AuthorizationDecision(true);
                                    }

                                    Authentication auth = sup.get();
                                    if (auth == null || auth instanceof AnonymousAuthenticationToken || !auth.isAuthenticated()) {
                                        throw AuthenticateException.authRequired();
                                    }

                                    return new AuthorizationDecision(true);
                                })
                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(globalExceptionFilter, jwtAuthenticationFilter.getClass());

        return http.build();
    }
}
