package com.app.core.middleware;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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

        ContentCachingResponseWrapper responseWrapper =
                new ContentCachingResponseWrapper(httpResponse);

        try {
            chain.doFilter(request, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            String path = httpRequest.getRequestURI();
            String method = httpRequest.getMethod();
            int code = responseWrapper.getStatus();
            String remoteAddr = request.getRemoteAddr();

            log.info("{} {} | Code: {} | IP: {} | Elapsed: {}ms",
                    method, path,
                    code,
                    remoteAddr,
                    duration
            );

            responseWrapper.copyBodyToResponse();
        }
    }
}
