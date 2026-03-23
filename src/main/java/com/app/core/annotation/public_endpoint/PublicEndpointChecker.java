package com.app.core.annotation.public_endpoint;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublicEndpointChecker implements ApplicationListener<ContextRefreshedEvent> {

    private final RequestMappingHandlerMapping handlerMapping;
    private final Map<PathPattern, Set<HttpMethod>> publicEndpoints = new ConcurrentHashMap<>();
    private final PathPatternParser pathPatternParser = new PathPatternParser();

    private static final List<String> PUBLIC_PATTERNS = List.of(
            "/swagger-ui/**",
            "/api-docs/**",
            "/v3/api-docs/**"
    );

    public boolean isPublic(HttpServletRequest request) {
        return matchesPublicEndpoint(request.getRequestURI(), request.getMethod());
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        addStaticEndpoints();
        addPublicEndpoints();
    }

    private void addStaticEndpoints() {
        PUBLIC_PATTERNS.forEach(p -> {
            publicEndpoints.computeIfAbsent(
                    pathPatternParser.parse(p),
                    k -> new HashSet<>());
        });
    }

    private void addPublicEndpoints() {
        handlerMapping.getHandlerMethods().entrySet().stream()
                .filter(e -> isPublicEndpointAnnotation(e.getValue()))
                .forEach(e -> {
                    RequestMappingInfo info = e.getKey();

                    Set<HttpMethod> httpMethods = collectHttpMethods(info);

                    info.getPatternValues().forEach(p -> {
                        publicEndpoints.computeIfAbsent(
                                        pathPatternParser.parse(p),
                                        k -> new HashSet<>())
                                .addAll(httpMethods);
                    });
                });
    }

    private boolean isPublicEndpointAnnotation(HandlerMethod h) {
        return h.hasMethodAnnotation(PublicEndpoint.class) ||
                h.getBeanType().isAnnotationPresent(PublicEndpoint.class);
    }

    private Set<HttpMethod> collectHttpMethods(RequestMappingInfo info) {
        return info.getMethodsCondition()
                .getMethods()
                .stream()
                .map(rm -> HttpMethod.valueOf(rm.name()))
                .collect(Collectors.toSet());
    }

    private boolean matchesPublicEndpoint(String uri, String method) {
        return publicEndpoints.entrySet()
                .stream()
                .anyMatch(entry -> {
                    PathPattern pattern = entry.getKey();
                    Set<HttpMethod> allowedMethods = entry.getValue();

                    return pattern != null && pattern.matches(PathContainer.parsePath(uri)) &&
                            (allowedMethods.isEmpty() || allowedMethods.contains(HttpMethod.valueOf(method)));
                });
    }
}
