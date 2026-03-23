package com.app.core.middleware;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class ProceedMW implements Filter {

    private static final String USER_AGENT_HEADER = "User-Agent";

    private static final String STATUS_FORMATTER = " | Status: %d";
    private static final String IP_FORMATTER = " | IP: %s";
    private static final String USER_AGENT_FORMATTER = " | User-Agent: %s";
    private static final String DURATION_FORMATTER = " | Duration: %dms";
    private static final String USER_ID_FORMATTER = " | UserId: %s";
    private static final String ROLE_FORMATTER = " | Role: %s";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        long startTime = System.currentTimeMillis();

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpRequest, 0);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(httpResponse);

        try {
            chain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            String queryString = httpRequest.getQueryString();
            int status = responseWrapper.getStatus();
            String userId = getUserIdFromRequest(httpRequest);

            StringBuilder logMessage = new StringBuilder();
            logMessage.append(String.format(
                    "%s %s",
                    httpRequest.getMethod(),
                    httpRequest.getRequestURI()));

            if (queryString != null && !queryString.isEmpty()) {
                logMessage.append("?").append(queryString);
            }

            logMessage.append(String.format(STATUS_FORMATTER, status));
            logMessage.append(String.format(IP_FORMATTER, getClientIp(httpRequest)));
            logMessage.append(String.format(USER_AGENT_FORMATTER, httpRequest.getHeader(USER_AGENT_HEADER)));
            logMessage.append(String.format(DURATION_FORMATTER, duration));

            if (userId != null) {
                logMessage.append(String.format(USER_ID_FORMATTER, userId));
            }

            Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                    .map(Authentication::getAuthorities)
                    .ifPresent(a -> logMessage.append(String.format(ROLE_FORMATTER, a)));

            if (status >= 500) {
                log.error(logMessage.toString());
            } else if (status >= 400) {
                log.warn(logMessage.toString());
            } else {
                log.info(logMessage.toString());
            }

            responseWrapper.copyBodyToResponse();
        }
    }

    private static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";
    private static final String X_REAL_IP_HEADER = "X-Real-IP";
    private static final String X_USER_ID_HEADER = "X-User-Id";

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader(X_FORWARDED_FOR_HEADER);
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader(X_REAL_IP_HEADER);
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    private String getUserIdFromRequest(HttpServletRequest request) {
        String userId = request.getHeader(X_USER_ID_HEADER);
        if (userId != null) {
            return userId;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null &&
                auth.isAuthenticated() &&
                !(auth instanceof AnonymousAuthenticationToken)
        ) {
            Object principal = auth.getPrincipal();
            if (principal != null) {
                return principal.toString();
            }
        }

        return null;
    }
}
