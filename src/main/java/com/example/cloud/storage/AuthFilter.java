package com.example.cloud.storage;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthFilter extends OncePerRequestFilter {

    // Хранилище активных токенов (просто для примера)
    private static final Set<String> validTokens = ConcurrentHashMap.newKeySet();

    public static void addToken(String token) {
        validTokens.add(token);
    }

    public static void removeToken(String token) {
        validTokens.remove(token);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws jakarta.servlet.ServletException, IOException {
        String token = request.getHeader("auth-token");
        String path = request.getRequestURI();

        if (path.equals("/login") || path.equals("/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (token != null && validTokens.contains(token)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Нет авторизации");
        }
    }
}