package com.nguyenthanhbang.Social_media.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class InternalAuthFilter extends OncePerRequestFilter {
    private static final String INTERNAL_HEADER = "X-Internal-Secret";
    private static final String INTERNAL_PATTERN = "/api/v1/internal/**";

    private final String expectedSecret;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public InternalAuthFilter(String expectedSecret) {
        this.expectedSecret = expectedSecret == null ? "" : expectedSecret;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (!pathMatcher.match(INTERNAL_PATTERN, path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String providedSecret = request.getHeader(INTERNAL_HEADER);
        if (expectedSecret.isBlank() || providedSecret == null || !expectedSecret.equals(providedSecret)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Unauthorized");
            return;
        }

        filterChain.doFilter(request, response);
    }
}

