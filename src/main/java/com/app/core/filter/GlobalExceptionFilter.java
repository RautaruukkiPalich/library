package com.app.core.filter;

import com.app.core.exception.AuthException;
import com.app.core.exception.ForbiddenException;
import com.app.core.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@Order(1)
public class GlobalExceptionFilter implements Filter {

    private final ObjectMapper objectMapper;

    public GlobalExceptionFilter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void doFilter(
            @NonNull ServletRequest request,
            @NonNull ServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            filterChain.doFilter(httpRequest, response);
        } catch (AuthException ex) {
            log.info("🔒  Auth: {}", ex.getMessage());
            writeErrorResponse(httpResponse, HttpStatus.UNAUTHORIZED, ex.getMessage(), httpRequest);
        } catch (ForbiddenException ex) {
            log.info("🔒  Forbidden: {}", ex.getMessage());
            writeErrorResponse(httpResponse, HttpStatus.FORBIDDEN, ex.getMessage(), httpRequest);
        } catch (Exception ex) {
            log.error("🔥 Exception: {}", ex.getMessage(), ex);
            writeErrorResponse(httpResponse, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", httpRequest);
        }
    }

    private void writeErrorResponse(
            HttpServletResponse response,
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
    }
}
