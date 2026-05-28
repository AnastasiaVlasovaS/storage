package com.example.cloud.storage;

import com.example.cloud.storage.exception.UnauthorizedException;
import com.example.cloud.storage.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class AuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

    @Autowired
    private UserRepository userRepository;

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/login", "/register", "/", "/h2-console"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        log.debug("Запрос к пути: {}", path);

        // Пропускаем публичные пути без проверки
        if (PUBLIC_PATHS.contains(path) || path.startsWith("/h2-console")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Проверяем токен
        String token = request.getHeader("auth-token");
        if (token == null || token.isEmpty()) {
            log.warn("Отсутствует auth-token в запросе к {}", path);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Неавторизован\"}");
            return;
        }

        if (userRepository.findByToken(token).isEmpty()) {
            log.warn("Невалидный токен {} в запросе к {}", token, path);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Неавторизован\"}");
            return;
        }

        log.debug("Успешная авторизация для токена {}", token);
        filterChain.doFilter(request, response);
    }
}