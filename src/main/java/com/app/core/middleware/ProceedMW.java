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

@Slf4j
@Component
public class ProceedMW implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        long startTime = System.currentTimeMillis();

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpRequest, 0);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(httpResponse);

        try {
            chain.doFilter(request, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            String method = httpRequest.getMethod();
            String path = httpRequest.getRequestURI();
            String queryString = httpRequest.getQueryString();
            int status = responseWrapper.getStatus();
            String remoteAddr = getClientIp(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");
            String userId = getUserIdFromRequest(httpRequest);

            StringBuilder logMessage = new StringBuilder();
            logMessage.append(String.format("%s %s", method, path));

            if (queryString != null && !queryString.isEmpty()) {
                logMessage.append("?").append(queryString);
            }

            logMessage.append(String.format(" | Status: %d", status));
            logMessage.append(String.format(" | IP: %s", remoteAddr));
            logMessage.append(String.format(" | User-Agent: %s", userAgent));
            logMessage.append(String.format(" | Duration: %dms", duration));

            if (userId != null) {
                logMessage.append(String.format(" | UserId: %s", userId));
            }

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

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    private String getUserIdFromRequest(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        if (userId != null) {
            return userId;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            Object principal = auth.getPrincipal();
            if (principal != null) {
                return principal.toString();
            }
        }

        return null;
    }
}
