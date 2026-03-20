package com.app.core.utils;


import com.app.core.annotation.PublicMethod;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublicEndpointChecker {

    private final RequestMappingHandlerMapping handlerMapping;
    private static final List<String> PUBLIC_PATTERNS = List.of(
            "/swagger-ui/**",
            "/api-docs/**",
            "/v3/api-docs/**"
    );

    public boolean isPublic(HttpServletRequest request) {
        String uri = request.getRequestURI();

        if (matchesPublicPattern(uri)) {
            return true;
        }

        return hasPublicAnnotation(request);
    }

    private boolean matchesPublicPattern(String uri) {
        return PUBLIC_PATTERNS.stream().anyMatch(pattern -> matchesPattern(uri, pattern));
    }

    private boolean matchesPattern(String uri, String pattern) {
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return uri.startsWith(prefix);
        }
        return uri.equals(pattern);
    }

    private boolean hasPublicAnnotation(HttpServletRequest request) {

        try {
            HandlerExecutionChain handler = handlerMapping.getHandler(request);
            if (handler == null) {
                return false;
            }

            Object handlerObj = handler.getHandler();

            if (handlerObj instanceof HandlerMethod handlerMethod) {
                return handlerMethod.hasMethodAnnotation(PublicMethod.class) ||
                        handlerMethod.getBeanType().isAnnotationPresent(PublicMethod.class);
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
