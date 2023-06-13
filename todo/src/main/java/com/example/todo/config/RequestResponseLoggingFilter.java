package com.example.todo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Instant startTime = Instant.now();
        String rId = UUID.randomUUID().toString();
        request.setAttribute("rId", rId);
        log.info("{} request {}:{}", rId, request.getMethod(), request.getRequestURI());
        filterChain.doFilter(request, response);
        Instant endTime = Instant.now();
        log.info("{} response with status: {} --- took: {} ms", rId, response.getStatus(), Duration.between(startTime, endTime).toMillis());
    }
}
