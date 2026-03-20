package com.app.core.utils;


import com.app.core.annotation.PublicMethod;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublicEndpointChecker {

    private final RequestMappingHandlerMapping handlerMapping;

    public boolean isPublic(HttpServletRequest request) {
        try {
            Object handler = Objects.requireNonNull(handlerMapping.getHandler(request)).getHandler();

            if (handler instanceof HandlerMethod handlerMethod) {
                log.info("method: {}", handlerMethod);
                return handlerMethod.hasMethodAnnotation(PublicMethod.class) ||
                        handlerMethod.getBeanType().isAnnotationPresent(PublicMethod.class);
            }

            log.info("request is not instance of HandlerMethod: {}", request.getRequestURI());

            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
