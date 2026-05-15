package org.example.promtdeck.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthCookieOriginFilter extends OncePerRequestFilter {

    private final Set<String> allowedOrigins;

    public AuthCookieOriginFilter(@Value("${auth.allowed-origins:http://localhost:5173}") String allowedOrigins) {
        this.allowedOrigins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (requiresOriginCheck(request) && !isTrustedOrigin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid request origin");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean requiresOriginCheck(HttpServletRequest request) {
        if (!HttpMethod.POST.matches(request.getMethod())) {
            return false;
        }

        String uri = request.getRequestURI();
        return "/api/auth/refresh".equals(uri) || "/api/auth/logout".equals(uri);
    }

    private boolean isTrustedOrigin(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        if (StringUtils.hasText(origin)) {
            return allowedOrigins.contains(origin);
        }

        String referer = request.getHeader("Referer");
        if (!StringUtils.hasText(referer)) {
            return false;
        }

        try {
            URI uri = URI.create(referer);
            String refererOrigin = uri.getScheme() + "://" + uri.getAuthority();
            return allowedOrigins.contains(refererOrigin);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
